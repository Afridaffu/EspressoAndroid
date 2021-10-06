package com.coyni.android.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.coyni.android.R;
import com.coyni.android.model.APIError;
import com.coyni.android.model.bank.Banks;
import com.coyni.android.model.bank.BanksDataItem;
import com.coyni.android.model.bank.SignOn;
import com.coyni.android.model.bank.SignOnData;
import com.coyni.android.model.bank.SyncAccount;
import com.coyni.android.model.cards.Cards;
import com.coyni.android.model.cards.CardsDataItem;
import com.coyni.android.utils.MyApplication;
import com.coyni.android.utils.Utils;
import com.coyni.android.view.AddCardActivity;
import com.coyni.android.view.BuyTokenActivityProfile;
import com.coyni.android.view.WebViewActivity;
import com.coyni.android.viewmodel.BuyViewModel;

import java.util.ArrayList;
import java.util.List;

public class PaymentMethodsBuyFragment extends Fragment {
    BuyViewModel buyViewModel;
    MyApplication objMyApplication;
    String strSignOn = "";
    RelativeLayout rlSignetAccount, rlBank, rlDebitCreditCard;
    static Context context;
    View view;
    ImageView imgBack;
    Bundle bundle;
    SignOnData signOnData;
    List<BanksDataItem> listBanks = new ArrayList<>();
    List<BanksDataItem> listSignetBanks = new ArrayList<>();
    ProgressDialog dialog;
    List<CardsDataItem> listAllCards = new ArrayList<>();
    Boolean isBank = false;

    public PaymentMethodsBuyFragment() {
    }

    public static PaymentMethodsBuyFragment newInstance(Context cxt) {
        PaymentMethodsBuyFragment fragment = new PaymentMethodsBuyFragment();
        context = cxt;
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onResume() {
        super.onResume();
        ((AppCompatActivity) getActivity()).getSupportActionBar().show();
        isBank = false;

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        try {
            if (requestCode == 1 && data == null) {
                if (objMyApplication.getStrFiservError() != null && objMyApplication.getStrFiservError().toLowerCase().equals("cancel")) {
                    Utils.displayAlert("Bank integration has been cancelled", getActivity());
                } else {
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
        view = inflater.inflate(R.layout.payment_methods_buy_fragment, container, false);

        try {
            if (Build.VERSION.SDK_INT >= 23) {
                getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                        WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                getActivity().findViewById(R.id.layoutMenu).setVisibility(View.GONE);
                initialization();
                initObserver();
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return view;
    }

    private void initialization() {
        try {
            bundle = getArguments();
            buyViewModel = new ViewModelProvider(this).get(BuyViewModel.class);
            rlSignetAccount = (RelativeLayout) view.findViewById(R.id.rlSignetAccount);
            rlBank = (RelativeLayout) view.findViewById(R.id.rlBank);
            rlDebitCreditCard = (RelativeLayout) view.findViewById(R.id.rlDebitCreditCard);
            imgBack = (ImageView) view.findViewById(R.id.imgBack);
            objMyApplication = (MyApplication) context.getApplicationContext();
            if (Utils.checkInternet(context)) {
//                dialog = new ProgressDialog(context, R.style.MyAlertDialogStyle);
//                dialog.setIndeterminate(false);
//                dialog.setMessage("Please wait...");
//                dialog.getWindow().setGravity(Gravity.CENTER);
//                dialog.show();
                if (objMyApplication.getSignOnData() == null || objMyApplication.getSignOnData().getUrl() == null) {
                    buyViewModel.meSignOn();
                } else {
                    strSignOn = objMyApplication.getStrSignOnError();
                    signOnData = objMyApplication.getSignOnData();
                }
                buyViewModel.meCards();
            } else {
                Utils.displayAlert(getString(R.string.internet), getActivity());
            }
            listBanks = objMyApplication.getListBanks();
            listSignetBanks = objMyApplication.getSignetBanks();
            listAllCards = objMyApplication.getListCards();
            if (getArguments() != null && getArguments().getString("screen") != null) {
                objMyApplication.setFromWhichFragment(getArguments().getString("screen"));
            } else {
                objMyApplication.setFromWhichFragment("fromProfileDebitCreditCards");
            }
            rlDebitCreditCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        if (listAllCards != null && listAllCards.size() == 0) {
                            Intent i = new Intent(getActivity(), AddCardActivity.class);
                            i.putExtra("from", "add");
                            i.putExtra("fromProfilePaymentMethods", "fromProfilePaymentMethods");
                            startActivity(i);
                        } else {
                            Intent i = new Intent(getActivity(), BuyTokenActivityProfile.class);
                            i.putExtra("from", "add");
                            startActivity(i);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });

            rlSignetAccount.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    objMyApplication.setFromWhichFragment("signet");
                    if (listSignetBanks != null && listSignetBanks.size() > 0) {
                        isBank = true;
                        openFragment(SavedBanksFragment.newInstance(context, "signet"), "signet");
                    } else {
                        SignetAccountFragment profileFragment = SignetAccountFragment.newInstance(getActivity(), "Add", "payment");
                        try {
                            openFragment(profileFragment, "signetAccAddress");
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                }
            });

            rlBank.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        if (listBanks != null && listBanks.size() > 0) {
                            isBank = true;
                            openFragment(SavedBanksFragment.newInstance(context, "bank"), "bank");
                        } else {
                            if (strSignOn.equals("") && signOnData != null && signOnData.getUrl() != null) {
                                Intent i = new Intent(context, WebViewActivity.class);
                                i.putExtra("signon", signOnData);
                                startActivityForResult(i, 1);
                            } else {
                                Utils.displayAlert(strSignOn, getActivity());
                            }
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });

            imgBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getActivity().onBackPressed();
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
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

    private void initObserver() {
        buyViewModel.getCardsMutableLiveData().observe(getActivity(), new Observer<Cards>() {
            @Override
            public void onChanged(Cards cards) {
//                if (dialog != null) {
//                    dialog.dismiss();
//                }
                try {
                    if (cards != null && !isBank) {
                        listAllCards = cards.getData().getItems();
                        objMyApplication.setListCards(listAllCards);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        buyViewModel.getSignOnMutableLiveData().observe(getActivity(), new Observer<SignOn>() {
            @Override
            public void onChanged(SignOn signOn) {
                //dialog.dismiss();
                try {
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
                            objMyApplication.setSignOnData(null);
                            objMyApplication.setStrSignOnError(strSignOn);
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        buyViewModel.getSyncAccountMutableLiveData().observe(getActivity(), new Observer<SyncAccount>() {
            @Override
            public void onChanged(SyncAccount syncAccount) {
                try {
                    if (syncAccount != null) {
                        if (syncAccount.getStatus().toLowerCase().equals("success")) {
                            buyViewModel.meBanks();
//                        Utils.displayAlert("You added a new Bank Account to your profile!", getActivity());
                            Utils.displayCloseAlert("You added a new Bank Account to your profile!", getActivity());
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        buyViewModel.getBanksMutableLiveData().observe(getActivity(), new Observer<Banks>() {
            @Override
            public void onChanged(Banks banks) {
                try {
                    //dialog.dismiss();
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
                        listSignetBanks = listSigBanks;
                        objMyApplication.setListBanks(lstBanks);
                        objMyApplication.setSignetBanks(listSigBanks);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        buyViewModel.getApiErrorMutableLiveData().observe(getActivity(), new Observer<APIError>() {
            @Override
            public void onChanged(APIError apiError) {
                try {
                    if (apiError != null) {
                        if (apiError.getError().getErrorCode().equals(getString(R.string.error_code)) && !objMyApplication.getResolveUrl()) {
                            objMyApplication.setResolveUrl(true);
                            buyViewModel.meSignOn();
                        } else if (!apiError.getError().getErrorDescription().equals("")) {
                            if (apiError.getError().getErrorDescription().toLowerCase().contains("token expired") || apiError.getError().getErrorDescription().toLowerCase().contains("invalid token")) {
                                objMyApplication.displayAlert(getActivity(), context.getString(R.string.session));
                            } else {
                                Utils.displayAlert(apiError.getError().getErrorDescription(), getActivity());
                            }
                        } else {
                            Utils.displayAlert(apiError.getError().getFieldErrors().get(0), getActivity());
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
}

