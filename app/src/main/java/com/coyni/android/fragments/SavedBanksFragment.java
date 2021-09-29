package com.coyni.android.fragments;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.coyni.android.R;
import com.coyni.android.adapters.SavedBanksListAdapter;
import com.coyni.android.model.APIError;
import com.coyni.android.model.bank.BankDeleteResponseData;
import com.coyni.android.model.bank.Banks;
import com.coyni.android.model.bank.BanksDataItem;
import com.coyni.android.model.bank.SignOn;
import com.coyni.android.model.bank.SignOnData;
import com.coyni.android.model.bank.SyncAccount;
import com.coyni.android.utils.MyApplication;
import com.coyni.android.utils.Utils;
import com.coyni.android.view.WebViewActivity;
import com.coyni.android.viewmodel.BuyViewModel;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.List;

public class SavedBanksFragment extends Fragment {
    View view;
    static Context context;
    MyApplication objMyApplication;
    CardView cvAddSignet;
    TextView tvHead, tvAdd, tvTitle;
    RecyclerView rvBanks;
    ImageView imgBack;
    BuyViewModel buyViewModel;
    List<BanksDataItem> listBanks = new ArrayList<>();
    List<BanksDataItem> listSignetBanks = new ArrayList<>();
    Dialog popupDelete;
    ProgressDialog dialog;
    static String strScreen = "", strSignOn = "";
    SignOnData signOnData;

    public SavedBanksFragment() {
    }

    public static SavedBanksFragment newInstance(Context cxt, String screen) {
        SavedBanksFragment fragment = new SavedBanksFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        context = cxt;
        strScreen = screen;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onResume() {
        super.onResume();
        if (!objMyApplication.getStrSignet().equals("")) {
            objMyApplication.setStrSignet("");
            buyViewModel.meBanks();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        try {
            if (requestCode == 1 && data == null) {
                if (objMyApplication.getStrFiservError() != null && objMyApplication.getStrFiservError().toLowerCase().equals("cancel")) {
                    Utils.displayAlert("Bank integration has been cancelled", getActivity());
                } else {
                    dialog = new ProgressDialog(context, R.style.MyAlertDialogStyle);
                    dialog.setIndeterminate(false);
                    dialog.setMessage("Please wait...");
                    dialog.getWindow().setGravity(Gravity.CENTER);
                    dialog.show();
                    buyViewModel.meSyncAccount();
                }
            } else {
                super.onActivityResult(requestCode, resultCode, data);
            }
        } catch (Exception ex) {
            super.onActivityResult(requestCode, resultCode, data);
            ex.printStackTrace();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_saved_banks, container, false);
        try {
            tvTitle = (TextView) view.findViewById(R.id.tvTitle);
            tvHead = (TextView) view.findViewById(R.id.tvHead);
            tvAdd = (TextView) view.findViewById(R.id.tvAdd);
            cvAddSignet = (CardView) view.findViewById(R.id.cvAddSignet);
            rvBanks = (RecyclerView) view.findViewById(R.id.rvBanks);
            imgBack = (ImageView) view.findViewById(R.id.imgBack);
            objMyApplication = (MyApplication) context.getApplicationContext();
            buyViewModel = new ViewModelProvider(this).get(BuyViewModel.class);
            if (strScreen.equals("bank")) {
                tvTitle.setText("Bank Accounts");
                tvHead.setText("Bank Account");
                tvAdd.setText("Add New Bank Account");
                listBanks = objMyApplication.getListBanks();
                bindBanks();
            } else {
                tvTitle.setText("Signet Accounts");
                tvHead.setText("Signet Account");
                tvAdd.setText("Add New Signet Account");
                listSignetBanks = objMyApplication.getSignetBanks();
                bindSignetBanks();
            }
            if (Build.VERSION.SDK_INT >= 23) {
                getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                        WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            }
            getActivity().findViewById(R.id.layoutMenu).setVisibility(View.GONE);
            cvAddSignet.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (strScreen.equals("bank")) {
                        strSignOn = objMyApplication.getStrSignOnError();
                        signOnData = objMyApplication.getSignOnData();
                        if (strSignOn.equals("") && signOnData != null && signOnData.getUrl() != null) {
                            Intent i = new Intent(context, WebViewActivity.class);
                            i.putExtra("signon", signOnData);
                            startActivityForResult(i, 1);
                        } else {
                            Utils.displayAlert(strSignOn, getActivity());
                        }
                    } else {
                        openFragment(SignetAccountFragment.newInstance(context, "Add", "payment"), "signet");
                    }
                }
            });

            imgBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PaymentMethodsBuyFragment paymentMethodsBuyFragment = PaymentMethodsBuyFragment.newInstance(getActivity());
                    openFragment(paymentMethodsBuyFragment, "PaymentMethodsBuy");
                }
            });
            initObserver();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return view;
    }

    private void openFragment(Fragment fragment, String tag) {
        try {
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.container, fragment, tag).addToBackStack(tag);
            transaction.commit();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void bindBanks() {
        SavedBanksListAdapter banksListAdapter;
        try {
            if (listBanks != null && listBanks.size() > 0) {
                rvBanks.setVisibility(View.VISIBLE);
                banksListAdapter = new SavedBanksListAdapter(listBanks, context, "bank", this);
                LinearLayoutManager mLayoutManager = new LinearLayoutManager(context);
                rvBanks.setLayoutManager(mLayoutManager);
                rvBanks.setItemAnimator(new DefaultItemAnimator());
                rvBanks.setAdapter(banksListAdapter);
            } else {
                rvBanks.setVisibility(View.GONE);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void bindSignetBanks() {
        SavedBanksListAdapter banksListAdapter;
        try {
            if (listSignetBanks != null && listSignetBanks.size() > 0) {
                rvBanks.setVisibility(View.VISIBLE);
                banksListAdapter = new SavedBanksListAdapter(listSignetBanks, context, "signet", this);
                LinearLayoutManager mLayoutManager = new LinearLayoutManager(context);
                rvBanks.setLayoutManager(mLayoutManager);
                rvBanks.setItemAnimator(new DefaultItemAnimator());
                rvBanks.setAdapter(banksListAdapter);
            } else {
                rvBanks.setVisibility(View.GONE);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void showBankDeletePopup(BanksDataItem objData) {
        try {
            ImageView imgClose;
            TextView tvHead, tvMessage, tvCancel;
            CardView cvRemove;
            objMyApplication.setStrSignet("delete");
            popupDelete = new Dialog(context, R.style.DialogTheme);
            popupDelete.requestWindowFeature(Window.FEATURE_NO_TITLE);
            popupDelete.setContentView(R.layout.deletepopup);
            Window window = popupDelete.getWindow();
            window.setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            window.setGravity(Gravity.CENTER);
            window.setBackgroundDrawableResource(android.R.color.transparent);

            WindowManager.LayoutParams lp = window.getAttributes();
            lp.dimAmount = 0.7f;
            lp.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
            popupDelete.getWindow().setAttributes(lp);
            popupDelete.show();
            imgClose = (ImageView) popupDelete.findViewById(R.id.imgClose);
            tvHead = (TextView) popupDelete.findViewById(R.id.tvHead);
            tvMessage = (TextView) popupDelete.findViewById(R.id.tvMessage);
            tvCancel = (TextView) popupDelete.findViewById(R.id.tvCancel);
            cvRemove = (CardView) popupDelete.findViewById(R.id.cvRemove);
            tvHead.setText("Remove " + objData.getAccountCategory().replace("Account", "") + " Account");
            tvMessage.setText("Are you sure want to remove this " + objData.getAccountCategory().replace("Account", "") + " Account");
            imgClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popupDelete.dismiss();
                }
            });
            tvCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popupDelete.dismiss();
                }
            });
            cvRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    buyViewModel.deleteBanks(String.valueOf(objData.getId()));
                    dialog = new ProgressDialog(context, R.style.MyAlertDialogStyle);
                    dialog.setIndeterminate(false);
                    dialog.setMessage("Please wait...");
                    dialog.getWindow().setGravity(Gravity.CENTER);
                    dialog.show();
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void initObserver() {
        buyViewModel.getBanksMutableLiveData().observe(getViewLifecycleOwner(), new Observer<Banks>() {
            @Override
            public void onChanged(Banks banks) {
                try {
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                    if (banks != null) {
                        List<BanksDataItem> lstBanks = new ArrayList<>();
                        List<BanksDataItem> listSigBanks = new ArrayList<>();
                        List<BanksDataItem> listItems = banks.getData().getItems();
                        for (int i = 0; i < listItems.size(); i++) {
                            if (listItems.get(i).getAccountCategory() != null && listItems.get(i).getAccountCategory().toLowerCase().equals("bank") && listItems.get(i).getArchived() != null && !listItems.get(i).getArchived()) {
                                lstBanks.add(banks.getData().getItems().get(i));
                            } else {
                                if (listItems.get(i).getAccountCategory() != null && listItems.get(i).getAccountCategory().toLowerCase().equals("signet") && listItems.get(i).getArchived() != null && !listItems.get(i).getArchived()) {
                                    listSigBanks.add(banks.getData().getItems().get(i));
                                }
                            }
                        }
                        listBanks = lstBanks;
                        objMyApplication.setListBanks(lstBanks);
                        objMyApplication.setSignetBanks(listSigBanks);
                        if (!strScreen.equals("bank")) {
                            if (listSigBanks != null && listSigBanks.size() > 0) {
                                listSignetBanks = listSigBanks;
                                bindSignetBanks();
                            } else {
                                getActivity().onBackPressed();
                            }
                        } else {
                            bindBanks();
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        buyViewModel.getDelBankResponseMutableLiveData().observe(getViewLifecycleOwner(), new Observer<BankDeleteResponseData>() {
            @Override
            public void onChanged(BankDeleteResponseData bankDeleteResponseData) {
                if (dialog != null) {
                    dialog.dismiss();
                }
                popupDelete.dismiss();
                if (getViewLifecycleOwner().getLifecycle().getCurrentState() == Lifecycle.State.RESUMED) {
                    if (bankDeleteResponseData != null) {
                        Context context = new ContextThemeWrapper(getActivity(), R.style.Theme_QuickCard);
                        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);

                        builder.setTitle(R.string.app_name);
                        builder.setMessage(bankDeleteResponseData.getData());
                        AlertDialog dilog = builder.show();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                dilog.dismiss();
                                if (Utils.checkInternet(context)) {
                                    dialog = new ProgressDialog(context, R.style.MyAlertDialogStyle);
                                    dialog.setIndeterminate(false);
                                    dialog.setMessage("Please wait...");
                                    dialog.getWindow().setGravity(Gravity.CENTER);
                                    dialog.show();
                                    buyViewModel.meBanks();
                                } else {
                                    Utils.displayAlert(getString(R.string.internet), getActivity());
                                }
                            }
                        }, Integer.parseInt(context.getString(R.string.closealert)));
                    }
                }
            }
        });

        buyViewModel.getSignOnMutableLiveData().observe(getViewLifecycleOwner(), new Observer<SignOn>() {
            @Override
            public void onChanged(SignOn signOn) {
                if (dialog != null) {
                    dialog.dismiss();
                }
                if (signOn != null) {
                    if (signOn.getStatus().toUpperCase().equals("SUCCESS")) {
                        strSignOn = "";
                        signOnData = signOn.getData();
                        objMyApplication.setSignOnData(signOnData);
                        objMyApplication.setStrSignOnError("");
                        if (objMyApplication.getResolveUrl()) {
                            callResolveFlow();
                        }
                    } else {
                        strSignOn = signOn.getError().getErrorDescription();
                    }
                }
            }
        });

        buyViewModel.getSyncAccountMutableLiveData().observe(getViewLifecycleOwner(), new Observer<SyncAccount>() {
            @Override
            public void onChanged(SyncAccount syncAccount) {
                if (dialog != null) {
                    dialog.dismiss();
                }
                if (syncAccount != null) {
                    if (syncAccount.getStatus().toLowerCase().equals("success")) {
                        dialog = new ProgressDialog(context, R.style.MyAlertDialogStyle);
                        dialog.setIndeterminate(false);
                        dialog.setMessage("Please wait...");
                        dialog.getWindow().setGravity(Gravity.CENTER);
                        dialog.show();
                        buyViewModel.meBanks();
//                        Utils.displayAlert("You added a new Bank Account to your profile!", getActivity());
                        Utils.displayCloseAlert("You added a new Bank Account to your profile!", getActivity());
                    }
                }
            }
        });

        buyViewModel.getApiErrorMutableLiveData().observe(getViewLifecycleOwner(), new Observer<APIError>() {
            @Override
            public void onChanged(APIError apiError) {
                try {
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                    if (apiError != null) {
                        if (apiError.getError().getErrorCode().equals(getString(R.string.error_code)) && !objMyApplication.getResolveUrl()) {
                            objMyApplication.setResolveUrl(true);
                            buyViewModel.meSignOn();
                        } else {
                            if (apiError.getError().getFieldErrors() != null) {
                                Utils.displayAlert(apiError.getError().getFieldErrors().get(0), getActivity());
                            } else {
                                if (apiError.getError().getErrorDescription().toLowerCase().contains("expire") || apiError.getError().getErrorDescription().toLowerCase().contains("invalid token")) {
                                    objMyApplication.displayAlert(getActivity(), context.getString(R.string.session));
                                } else {
                                    Utils.displayAlert(apiError.getError().getErrorDescription(), getActivity());
                                }
                            }
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    private void callResolveFlow() {
        try {
            if (strSignOn.equals("") && signOnData != null && signOnData.getUrl() != null) {
                Intent i = new Intent(context, WebViewActivity.class);
                i.putExtra("signon", signOnData);
                startActivityForResult(i, 1);
            } else {
                Utils.displayAlert(strSignOn, getActivity());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void editSignet(BanksDataItem item) {
        try {
            objMyApplication.setEditSignet(item);
            openFragment(SignetAccountFragment.newInstance(context, "Edit", "payment"), "signet");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
