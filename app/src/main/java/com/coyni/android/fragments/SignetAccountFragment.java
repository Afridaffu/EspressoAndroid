package com.coyni.android.fragments;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.coyni.android.model.bank.Banks;
import com.coyni.android.viewmodel.BuyViewModel;
import com.coyni.android.adapters.StatesListAdapter;
import com.coyni.android.model.APIError;
import com.coyni.android.model.bank.BanksDataItem;
import com.coyni.android.model.signet.SignetEditResponse;
import com.coyni.android.model.signet.SignetRequest;
import com.coyni.android.model.signet.SignetResponse;
import com.coyni.android.utils.Utils;
import com.coyni.android.viewmodel.SignetViewModel;
import com.google.android.material.textfield.TextInputEditText;
import com.coyni.android.R;
import com.coyni.android.model.States;
import com.coyni.android.utils.MyApplication;
import com.coyni.android.view.MainActivity;

import java.util.ArrayList;
import java.util.List;

public class SignetAccountFragment extends Fragment {
    View view;
    static Context context;
    LinearLayout layoutConfirm;
    MyApplication objMyApplication;
    TextInputEditText etAddress1, etAddress2, etCountry, etCity, etZipCode, etStateRegion, etNameOnSignetAcc, etSignetWalletId;
    SignetViewModel signetViewModel;
    BuyViewModel buyViewModel;
    ProgressDialog dialog;
    Toolbar toolbar;
    BanksDataItem selectedBank;
    RelativeLayout layoutSuccess, layoutHead;
    TextView tvMessage, tvSubMsg, tvSignetHead;
    ImageView imgPaste;
    static String strFrom = "", strScreen = "";
    Dialog popupStates;
    StatesListAdapter statesListAdapter;
    List<States> listStates = new ArrayList<>();

    public SignetAccountFragment() {
    }

    public static SignetAccountFragment newInstance(Context cxt, String from, String screen) {
        SignetAccountFragment fragment = new SignetAccountFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        context = cxt;
        strFrom = from;
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
        ((MainActivity) context).setToolbar(toolbar, true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_signet_account, container, false);
        try {
            toolbar = (Toolbar) view.findViewById(R.id.toolbar_sent);
            signetViewModel = new ViewModelProvider(this).get(SignetViewModel.class);
            buyViewModel = new ViewModelProvider(this).get(BuyViewModel.class);
            objMyApplication = (MyApplication) context.getApplicationContext();
            if (Build.VERSION.SDK_INT >= 23) {
                getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                        WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            }
            getActivity().findViewById(R.id.layoutMenu).setVisibility(View.GONE);
            layoutConfirm = (LinearLayout) view.findViewById(R.id.layoutConfirm);
            etAddress1 = (TextInputEditText) view.findViewById(R.id.etAddress1);
            etAddress2 = (TextInputEditText) view.findViewById(R.id.etAddress2);
            etCountry = (TextInputEditText) view.findViewById(R.id.etCountry);
            etCity = (TextInputEditText) view.findViewById(R.id.etCity);
            etZipCode = (TextInputEditText) view.findViewById(R.id.etZipCode);
            etStateRegion = (TextInputEditText) view.findViewById(R.id.etStateRegion);
            etNameOnSignetAcc = (TextInputEditText) view.findViewById(R.id.etNameOnSignetAcc);
            etSignetWalletId = (TextInputEditText) view.findViewById(R.id.etSignetWalletId);
            layoutHead = (RelativeLayout) view.findViewById(R.id.layoutHead);
            layoutSuccess = (RelativeLayout) view.findViewById(R.id.layoutSuccess);
            tvMessage = (TextView) view.findViewById(R.id.tvMessage);
            tvSubMsg = (TextView) view.findViewById(R.id.tvSubMsg);
            tvSignetHead = (TextView) view.findViewById(R.id.tvSignetHead);
            imgPaste = (ImageView) view.findViewById(R.id.imgPaste);
            LinearLayout layoutStateArrow = (LinearLayout) view.findViewById(R.id.layoutStateArrow);
//            etNameOnSignetAcc.setText(Utils.capitalize(objMyApplication.getStrUser()));
//            etNameOnSignetAcc.setEnabled(false);
            tvSignetHead.setText("Add Signet Account");
            imgPaste.setEnabled(true);
            if (strFrom != null && strFrom.equals("Edit")) {
                selectedBank = objMyApplication.getEditSignet();
                etAddress1.setText(selectedBank.getAddressLine1());
                etAddress2.setText(selectedBank.getAddressLine2());
                etCountry.setText(selectedBank.getCountry());
                etCity.setText(selectedBank.getCity());
                etZipCode.setText(selectedBank.getZipCode());
                etStateRegion.setText(selectedBank.getState());
                etNameOnSignetAcc.setText(Utils.capitalize(selectedBank.getAccountName()));
                tvSignetHead.setText("Edit Signet Account");
                etSignetWalletId.setText(selectedBank.getAccoutNumber());
                etSignetWalletId.setEnabled(false);
                etNameOnSignetAcc.setEnabled(false);
                imgPaste.setEnabled(false);
            }

            imgPaste.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        ClipboardManager clipboardManager = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                        ClipData pData = clipboardManager.getPrimaryClip();
                        ClipData.Item item = pData.getItemAt(0);
                        etSignetWalletId.setText(item.getText().toString().trim());
                        etSignetWalletId.setSelection(item.getText().toString().trim().length());
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });

            layoutStateArrow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    statesPopup();
                }
            });
            layoutConfirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Utils.hideKeypad(getActivity(), v);
                        if (validation()) {
                            dialog = new ProgressDialog(context, R.style.MyAlertDialogStyle);
                            dialog.setIndeterminate(false);
                            dialog.setMessage("Please wait...");
                            dialog.getWindow().setGravity(Gravity.CENTER);
                            dialog.show();
                            if (strFrom != null && strFrom.equals("Add")) {
                                objMyApplication.setStrSignet("add");
                                SignetRequest obj = new SignetRequest();
                                obj.setAccountCategory("Signet");
                                obj.setAccountName(etNameOnSignetAcc.getText().toString());
                                obj.setAccountNumber(etSignetWalletId.getText().toString());
                                obj.setAccountType("Savings");
                                obj.setAddressLine1(etAddress1.getText().toString().trim());
                                obj.setAddressLine2(etAddress2.getText().toString().trim());
                                obj.setBankAccountName("Signet");
                                obj.setBankName("Signet");
//                                obj.setCountry(etCountry.getText().toString().trim());
                                obj.setCountry("US");
                                obj.setState(etStateRegion.getText().toString().trim());
                                obj.setCity(etCity.getText().toString().trim());
                                obj.setZipCode(etZipCode.getText().toString().trim());
                                obj.setDefault(true);
                                if (Utils.checkInternet(context)) {
                                    signetViewModel.saveBank(obj);
                                } else {
                                    //Toast.makeText(context, getString(R.string.internet), Toast.LENGTH_LONG).show();
                                    Utils.displayAlert(getString(R.string.internet), getActivity());
                                }
                            } else {
                                objMyApplication.setStrSignet("edit");
                                SignetRequest obj = new SignetRequest();
                                obj.setAccountName(etNameOnSignetAcc.getText().toString());
                                obj.setAccountNumber(etSignetWalletId.getText().toString());
                                obj.setAddressLine1(etAddress1.getText().toString().trim());
                                obj.setAddressLine2(etAddress2.getText().toString().trim());
//                                obj.setCountry(etCountry.getText().toString().trim());
                                obj.setCountry("US");
                                obj.setState(etStateRegion.getText().toString().trim());
                                obj.setCity(etCity.getText().toString().trim());
                                obj.setZipCode(etZipCode.getText().toString().trim());
                                obj.setDefault(true);
                                if (Utils.checkInternet(context)) {
                                    signetViewModel.editBank(obj, String.valueOf(selectedBank.getId()));
                                } else {
                                    Utils.displayAlert(getString(R.string.internet), getActivity());
                                }
                            }
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }

                }
            });
            initObservables();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return view;
    }

    private void initObservables() {
        signetViewModel.getSignetResponseMutableLiveData().observe(getActivity(), new Observer<SignetResponse>() {
            @Override
            public void onChanged(SignetResponse signetResponse) {
                dialog.dismiss();
                if (signetResponse != null && signetResponse.getStatus().toUpperCase().equals("SUCCESS")) {
                    ((MainActivity) context).setToolbar(toolbar, false);
                    layoutHead.setVisibility(View.GONE);
                    tvMessage.setText("Added Successfully");
                    tvSubMsg.setText("You added a new Signet Account to your profile!");
                    layoutSuccess.setVisibility(View.VISIBLE);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (strScreen.equals("payment")) {
//                            dialog = new ProgressDialog(context, R.style.MyAlertDialogStyle);
//                            dialog.setIndeterminate(false);
//                            dialog.setMessage("Please wait...");
//                            dialog.getWindow().setGravity(Gravity.CENTER);
//                            dialog.show();
                                buyViewModel.meBanks();
                            } else {
                                getActivity().onBackPressed();
                            }
                        }
                    }, 2000);
                }
            }
        });

        signetViewModel.getSignetEditResponseMutableLiveData().observe(getActivity(), new Observer<SignetEditResponse>() {
            @Override
            public void onChanged(SignetEditResponse signetEditResponse) {
                dialog.dismiss();
                if (signetEditResponse != null && signetEditResponse.getStatus().toUpperCase().equals("SUCCESS")) {
                    ((MainActivity) context).setToolbar(toolbar, false);
                    layoutHead.setVisibility(View.GONE);
                    tvMessage.setText("Updated Successfully");
                    tvSubMsg.setText("Signet account information edited successfully!");
                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    params.addRule(RelativeLayout.CENTER_HORIZONTAL);
                    params.addRule(RelativeLayout.BELOW, tvMessage.getId());
                    params.setMargins(0, Utils.convertPxtoDP(20), 0, 0);
                    tvSubMsg.setLayoutParams(params);
                    layoutSuccess.setVisibility(View.VISIBLE);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            getActivity().onBackPressed();
                        }
                    }, 1000);
                }
            }
        });

        signetViewModel.getApiErrorMutableLiveData().observe(getActivity(), new Observer<APIError>() {
            @Override
            public void onChanged(APIError apiError) {
                dialog.dismiss();
                if (apiError != null) {
                    if (!apiError.getError().getErrorDescription().equals("")) {
                        if (apiError.getError().getErrorDescription().toLowerCase().contains("expire") || apiError.getError().getErrorDescription().toLowerCase().contains("invalid token")) {
                            objMyApplication.displayAlert(getActivity(), context.getString(R.string.session));
                        } else {
                            Utils.displayAlert(apiError.getError().getErrorDescription(), getActivity());
                        }
                    } else {
                        Utils.displayAlert(apiError.getError().getFieldErrors().get(0), getActivity());
                    }
                }
            }
        });

        buyViewModel.getBanksMutableLiveData().observe(getActivity(), new Observer<Banks>() {
            @Override
            public void onChanged(Banks banks) {
                try {
                    dialog.dismiss();
                    if (banks != null) {
                        List<BanksDataItem> listBanks = new ArrayList<>();
                        List<BanksDataItem> listSigBanks = new ArrayList<>();
                        List<BanksDataItem> listItems = banks.getData().getItems();
                        for (int i = 0; i < listItems.size(); i++) {
                            if (listItems.get(i).getAccountCategory() != null && listItems.get(i).getAccountCategory().toLowerCase().equals("bank") && listItems.get(i).getArchived() != null && !listItems.get(i).getArchived()) {
                                listBanks.add(banks.getData().getItems().get(i));
                            } else {
                                if (listItems.get(i).getAccountCategory() != null && listItems.get(i).getAccountCategory().toLowerCase().equals("signet") && listItems.get(i).getArchived() != null && !listItems.get(i).getArchived()) {
                                    listSigBanks.add(banks.getData().getItems().get(i));
                                }
                            }
                        }
                        objMyApplication.setListBanks(listBanks);
                        objMyApplication.setSignetBanks(listSigBanks);
                        openFragment(SavedBanksFragment.newInstance(context, "signet"), "signet");
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    private Boolean validation() {
        Boolean value = true;
        try {
            if (etNameOnSignetAcc.getText().toString().equals("")) {
                etNameOnSignetAcc.requestFocus();
                Utils.displayAlert("Name is required", getActivity());
                return value = false;
            } else if (etSignetWalletId.getText().toString().equals("")) {
                etSignetWalletId.requestFocus();
                Utils.displayAlert("Signet Wallet Address is required", getActivity());
                return value = false;
            } else if (etAddress1.getText().toString().equals("")) {
                etAddress1.requestFocus();
                Utils.displayAlert("Address Line 1 required", getActivity());
                return value = false;
            } else if (etCountry.getText().toString().equals("")) {
                Utils.displayAlert("Please select Country", getActivity());
                etCountry.requestFocus();
                return value = false;
            } else if (etStateRegion.getText().toString().equals("")) {
                etStateRegion.requestFocus();
                Utils.displayAlert("State is required", getActivity());
                return value = false;
            } else if (etCity.getText().toString().equals("")) {
                etCity.requestFocus();
                Utils.displayAlert("City is required", getActivity());
                return value = false;
            } else if (etZipCode.getText().toString().equals("")) {
                etZipCode.requestFocus();
                Utils.displayAlert("Zip Code / Postal Code is required", getActivity());
                return value = false;
            } else if (!etZipCode.getText().toString().equals("") && etZipCode.getText().toString().length() < 5) {
                etZipCode.requestFocus();
                Utils.displayAlert("Zip Code / Postal Code must have at least 5 numbers", getActivity());
                return value = false;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return value;
    }

    private void statesPopup() {
        try {
            popupStates = new Dialog(context, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
            popupStates.requestWindowFeature(Window.FEATURE_NO_TITLE);
            popupStates.setContentView(R.layout.countrieslist);
            popupStates.setCanceledOnTouchOutside(false);
            popupStates.setCancelable(false);
            popupStates.show();
            bindStates();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void bindStates() {
        RecyclerView rvCountries;
        ImageView imgBack;
        EditText etSearch;
        TextView tvNoResults;
        statesListAdapter = new StatesListAdapter(null, context, "signet", this, null);
        try {
            imgBack = popupStates.findViewById(R.id.imgBack);
            etSearch = popupStates.findViewById(R.id.etSearch);
            tvNoResults = popupStates.findViewById(R.id.tvNoResults);
            rvCountries = popupStates.findViewById(R.id.rvCountries);
            listStates = objMyApplication.getListStates();
            if (listStates != null && listStates.size() > 0) {
                statesListAdapter = new StatesListAdapter(listStates, context, "signet", this, null);
                LinearLayoutManager mLayoutManager = new LinearLayoutManager(context);
                rvCountries.setLayoutManager(mLayoutManager);
                rvCountries.setItemAnimator(new DefaultItemAnimator());
                rvCountries.setAdapter(statesListAdapter);
            }
            imgBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popupStates.dismiss();
                }
            });
            etSearch.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    try {
                        String search_key = s.toString();
                        List<States> filterList = new ArrayList<>();
                        int sIndex = 0;
                        if (listStates != null && listStates.size() > 0) {
                            for (int i = 0; i < listStates.size(); i++) {
                                sIndex = listStates.get(i).getName().toLowerCase().indexOf(search_key.toLowerCase());
                                if (sIndex == 0) {
                                    filterList.add(listStates.get(i));
                                }
                            }
                            if (filterList != null && filterList.size() > 0) {
                                statesListAdapter.updateList(filterList);
                                rvCountries.setVisibility(View.VISIBLE);
                                tvNoResults.setVisibility(View.GONE);
                            } else {
                                rvCountries.setVisibility(View.GONE);
                                tvNoResults.setVisibility(View.VISIBLE);
                            }
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void populateState(String strState) {
        try {
            popupStates.dismiss();
            etStateRegion.setText(strState);
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
}

