package com.coyni.android.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.coyni.android.R;
import com.coyni.android.model.APIError;
import com.coyni.android.model.kycchecks.KYC_ChecksResponse;
import com.coyni.android.model.login.LoginRequest;
import com.coyni.android.model.shuftipro.ShuftiProResponse;
import com.coyni.android.model.usertracker.UserTracker;
import com.coyni.android.utils.MyApplication;
import com.coyni.android.utils.Utils;
import com.coyni.android.view.MainActivity;
import com.coyni.android.viewmodel.DashboardViewModel;
import com.coyni.android.viewmodel.LoginViewModel;
import com.coyni.android.viewmodel.SignetViewModel;
import com.shutipro.sdk.Shuftipro;
import com.shutipro.sdk.listeners.ShuftiVerifyListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class AccountActivatedFragment extends Fragment {
    LoginViewModel loginViewModel;
    SignetViewModel signetViewModel;
    DashboardViewModel dashboardViewModel;
    MyApplication objMyApplication;
    String strPassword = "";
    RelativeLayout rlMailingAddress, rlIdentityVerification, rlPaymentMethods;
    static Context context;
    View view;
    Bundle bundle;
    UserTracker userTracker;
    ProgressDialog dialog;
    ImageView ivNextBasicVerification, ivNextMailingAddress, ivNextIdVerification;
    CardView cvBasicVerification, cvIdVerification, cvPaymentMethods, cvMailingAddress;

    public AccountActivatedFragment() {
    }

    public static AccountActivatedFragment newInstance(Context cxt) {
        AccountActivatedFragment fragment = new AccountActivatedFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
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

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_account_activated, container, false);
        try {
            if (Build.VERSION.SDK_INT >= 23) {
                getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                        WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            }
            getActivity().findViewById(R.id.layoutMenu).setVisibility(View.GONE);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        initialization();
        initObserver();

        return view;
    }

    private void initialization() {
        try {
            bundle = getArguments();
            CardView cvStart = (CardView) view.findViewById(R.id.cvStart);
            TextView tvSkip = (TextView) view.findViewById(R.id.tvSkip);
            rlMailingAddress = (RelativeLayout) view.findViewById(R.id.rlMailingAddress);
            rlIdentityVerification = (RelativeLayout) view.findViewById(R.id.rlIdentityVerification);
            rlPaymentMethods = (RelativeLayout) view.findViewById(R.id.rlPaymentMethods);
            ivNextBasicVerification = (ImageView) view.findViewById(R.id.ivNextBasicVerification);
            ivNextMailingAddress = (ImageView) view.findViewById(R.id.ivNextMailingAddress);
            ivNextIdVerification = (ImageView) view.findViewById(R.id.ivNextIdVerification);
            cvBasicVerification = (CardView) view.findViewById(R.id.cvBasicVerification);
            cvMailingAddress = (CardView) view.findViewById(R.id.cvMailingAddress);
            cvIdVerification = (CardView) view.findViewById(R.id.cvIdVerification);
            cvPaymentMethods = (CardView) view.findViewById(R.id.cvPaymentMethods);
            Utils.statusBar(getActivity());
            objMyApplication = (MyApplication) context.getApplicationContext();
            loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);
            signetViewModel = new ViewModelProvider(this).get(SignetViewModel.class);
            dashboardViewModel = new ViewModelProvider(this).get(DashboardViewModel.class);
            tvSkip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (getActivity().getIntent().getStringExtra("screen") != null && !getActivity().getIntent().getStringExtra("screen").equals("")) {
                        getActivity().onBackPressed();
                    } else {
                        Intent i = new Intent(getActivity(), MainActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(i);
                    }
                }
            });

            cvStart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        if (userTracker != null && !userTracker.getData().getAddressAvailable()) {
                            UserAddressFragment profileFragment = UserAddressFragment.newInstance(getActivity());
                            openFragment(profileFragment, "userAddress");
                        } else if (userTracker != null && !userTracker.getData().getPersonIdentified()) {
                            shuftipro();
                        } else if (userTracker != null && !userTracker.getData().getPaymentModeAdded()) {
                            Bundle bundle = new Bundle();
                            bundle.putString("screen", "tracker");
                            PaymentMethodsBuyFragment paymentMethodsBuyFragment = PaymentMethodsBuyFragment.newInstance(getActivity());
                            paymentMethodsBuyFragment.setArguments(bundle);
                            openFragment(paymentMethodsBuyFragment, "PaymentMethodsBuy");
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });

            if (getActivity().getIntent().getStringExtra("password") != null && !getActivity().getIntent().getStringExtra("password").equals("")) {
                strPassword = getActivity().getIntent().getStringExtra("password");
                LoginRequest loginRequest = new LoginRequest();
                loginRequest.setEmail(objMyApplication.getStrEmail().trim());
                loginRequest.setPassword(strPassword);
                loginViewModel.login(loginRequest);
            }

            bindUserTracker();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        rlMailingAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (userTracker != null && !userTracker.getData().getAddressAvailable()) {
                        UserAddressFragment profileFragment = UserAddressFragment.newInstance(getActivity());
                        openFragment(profileFragment, "userAddress");
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        rlPaymentMethods.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userTracker != null && userTracker.getData().getAddressAvailable() && userTracker.getData().getProfileVerified()
                        && userTracker.getData().getPersonIdentified()) {
                    Bundle bundle = new Bundle();
                    bundle.putString("screen", "tracker");
                    PaymentMethodsBuyFragment paymentMethodsBuyFragment = PaymentMethodsBuyFragment.newInstance(getActivity());
                    try {
                        openFragment(paymentMethodsBuyFragment, "PaymentMethodsBuy");
                        paymentMethodsBuyFragment.setArguments(bundle);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                } else {
                    Utils.displayAlert("Please complete your Profile Account before adding payment methods", getActivity());
                }
            }
        });

        rlIdentityVerification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
//                    if (userTracker != null && !userTracker.getData().getPersonIdentified()) {
//                        shuftipro();
//                    }
                    if (userTracker != null && userTracker.getData().getAddressAvailable()) {
                        if (userTracker != null && !userTracker.getData().getPersonIdentified()) {
                            shuftipro();
                        }
                    } else {
                        Utils.displayAlert("Please complete your Mailing Address before adding Identity Verification", getActivity());
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

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
        signetViewModel.getKycChecksResponseMutableLiveData().observe(getActivity(), new Observer<KYC_ChecksResponse>() {
            @Override
            public void onChanged(KYC_ChecksResponse kyc_checksResponse) {
                if (dialog != null) {
                    dialog.dismiss();
                }
                if (kyc_checksResponse != null && kyc_checksResponse.getStatus().toUpperCase().equals("SUCCESS")) {
//                    Utils.displayAlert("KYC succeddfully updated.", getActivity());
                    Utils.displayCloseAlert("KYC succeddfully updated.", getActivity());
                    dialog = new ProgressDialog(context, R.style.MyAlertDialogStyle);
                    dialog.setIndeterminate(false);
                    dialog.setMessage("Please wait...");
                    dialog.getWindow().setGravity(Gravity.CENTER);
                    dialog.show();
                    dashboardViewModel.meTracker();
                }
            }
        });

        dashboardViewModel.getUserTrackerMutableLiveData().observe(getActivity(), new Observer<UserTracker>() {
            @Override
            public void onChanged(UserTracker userTracker) {
                if (dialog != null) {
                    dialog.dismiss();
                }
                if (userTracker != null) {
                    objMyApplication.setUserTracker(userTracker);
                    bindUserTracker();
                }
            }
        });

        signetViewModel.getApiErrorMutableLiveData().observe(getActivity(), new Observer<APIError>() {
            @Override
            public void onChanged(APIError apiError) {
                dialog.dismiss();
                if (apiError != null) {
                    if (!apiError.getError().getErrorDescription().equals("")) {
                        if (apiError.getError().getErrorDescription().toLowerCase().contains("token expired") || apiError.getError().getErrorDescription().toLowerCase().contains("invalid token")) {
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

        signetViewModel.getShuftiProResponseMutableLiveData().observe(getActivity(), new Observer<ShuftiProResponse>() {
            @Override
            public void onChanged(ShuftiProResponse shuftiProResponse) {
                if (dialog != null) {
                    dialog.dismiss();
                }
                if (shuftiProResponse != null && shuftiProResponse.getStatus().equals("ERROR")) {
                    Utils.displayAlert(shuftiProResponse.getData().getDeclined_reason(), getActivity());
                }
            }
        });
    }

    private void shuftipro() {
        try {
            Shuftipro shuftipro = Shuftipro.getInstance();
            JSONObject AuthKeys = new JSONObject();
            AuthKeys.put("auth_type", "basic_auth");
            AuthKeys.put("client_Id", Utils.shuftiClientID);
            AuthKeys.put("secret_key", Utils.shuftiSecretKey);
            JSONObject Config = new JSONObject();

            Config.put("open_webview", false);
            Config.put("asyncRequest", false);
            Config.put("captureEnabled", false);

            JSONObject requestObject = new JSONObject();
            requestObject.put("reference", randomNumber());
            requestObject.put("country", "US");
//            requestObject.put("country", "IN");
            requestObject.put("language", "EN");
            requestObject.put("email", "");
            requestObject.put("callback_url", "");
            requestObject.put("redirect_url", "");
            requestObject.put("verification_mode", "image_only");
            requestObject.put("show_privacy_policy", "1");
            requestObject.put("show_results", "1");
            requestObject.put("show_consent", "1");


            //Creating Face object
            JSONObject faceObject = new JSONObject();
            faceObject.put("proof", "");
            requestObject.put("face", faceObject);

            //Creating Document object

            JSONObject documentObject = new JSONObject();
            ArrayList<String> doc_supported_types = new ArrayList<String>();

            doc_supported_types.add("passport");
            doc_supported_types.add("id_card");
            doc_supported_types.add("driving_license");

            documentObject.put("proof", "");
            documentObject.put("additional_proof", "");
            documentObject.put("name", "");
            documentObject.put("dob", "");
            documentObject.put("document_number", "");
            documentObject.put("expiry_date", "");
            documentObject.put("issue_date", "");
            documentObject.put("backside_proof_required", "0");
            documentObject.put("supported_types", new JSONArray(doc_supported_types));

            requestObject.put("document", documentObject);

            //Creating BGC object
            requestObject.put("background_checks", "");

            shuftipro.shuftiproVerification(requestObject, AuthKeys, Config, getActivity(),
                    new ShuftiVerifyListener() {
                        @Override
                        public void verificationStatus(HashMap<String, String> responseSet) {
                            try {
                                //Utils.copyAlert(responseSet.toString(), getActivity());
                                Log.e("Response", responseSet.toString());
                                if (responseSet != null) {
                                    if (responseSet.get("message") != null) {
                                        Utils.displayAlert(responseSet.get("message"), getActivity());
                                    } else if (responseSet.get("reference") != null) {
                                        dialog = new ProgressDialog(context, R.style.MyAlertDialogStyle);
                                        dialog.setIndeterminate(false);
                                        dialog.setMessage("Please wait...");
                                        dialog.getWindow().setGravity(Gravity.CENTER);
                                        dialog.show();
                                        signetViewModel.updateKyc(responseSet.get("reference"));
                                    }

                                }
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }

                        }
                    });

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private String randomNumber() {
        String strNo = "";
        try {
            long timeSeed = System.nanoTime(); // to get the current date time value

            double randSeed = Math.random() * 1000; // random number generation

            long midSeed = (long) (timeSeed * randSeed); // mixing up the time and

            String s = midSeed + "";
            strNo = s.substring(0, 10);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return strNo;
    }

    private void bindUserTracker() {
        try {
            userTracker = objMyApplication.getUserTracker();
            if (userTracker != null && userTracker.getData().getAddressAvailable()) {
                ImageView imgMailingAddrPending = (ImageView) view.findViewById(R.id.imgMailingAddrPending);
                imgMailingAddrPending.setImageResource(R.drawable.ic_active_done);
                ivNextMailingAddress.setVisibility(View.GONE);
                cvIdVerification.setCardElevation(15f);
            } else {
                ImageView imgMailingAddrPending = (ImageView) view.findViewById(R.id.imgMailingAddrPending);
                imgMailingAddrPending.setImageResource(R.drawable.ic_active_pending);
                cvMailingAddress.setCardElevation(15f);
                ivNextMailingAddress.setVisibility(View.VISIBLE);
            }
            if (userTracker != null && userTracker.getData().getProfileVerified()) {
                ImageView imgPending = (ImageView) view.findViewById(R.id.imgDone);
                imgPending.setImageResource(R.drawable.ic_active_done);
                ivNextBasicVerification.setVisibility(View.GONE);
            } else {
                ImageView imgPending = (ImageView) view.findViewById(R.id.imgDone);
                imgPending.setImageResource(R.drawable.ic_active_pending);
                ivNextBasicVerification.setVisibility(View.VISIBLE);
            }
            if (userTracker != null && userTracker.getData().getPersonIdentified()) {
                cvPaymentMethods.setCardElevation(15f);
                cvIdVerification.setCardElevation(0);
                ImageView imgPending = (ImageView) view.findViewById(R.id.imgKYCPending);
                imgPending.setImageResource(R.drawable.ic_active_done);
                ivNextIdVerification.setVisibility(View.GONE);
            } else {
                ImageView imgPending = (ImageView) view.findViewById(R.id.imgKYCPending);
                imgPending.setImageResource(R.drawable.ic_active_pending);
                ivNextIdVerification.setVisibility(View.VISIBLE);
            }
            if (userTracker != null && userTracker.getData().getPaymentModeAdded()) {
                ImageView imgPayPending = (ImageView) view.findViewById(R.id.imgPayPending);
                imgPayPending.setImageResource(R.drawable.ic_active_done);
            } else {
                ImageView imgMailingAddrPending = (ImageView) view.findViewById(R.id.imgPayPending);
                imgMailingAddrPending.setImageResource(R.drawable.ic_active_pending);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
