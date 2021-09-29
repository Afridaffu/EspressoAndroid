package com.coyni.android.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.coyni.android.view.MainActivity;
import com.coyni.android.viewmodel.DashboardViewModel;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.coyni.android.R;
import com.coyni.android.model.user.Agreements;
import com.coyni.android.model.user.AgreementsDataItems;
import com.coyni.android.utils.MyApplication;
import com.coyni.android.utils.Utils;

import java.util.List;

import static com.coyni.android.view.MainActivity.bottomSheetBehavior;
import static com.coyni.android.view.MainActivity.viewBack;
import static com.coyni.android.view.MainActivity.viewBottomSheet;

public class UserAgreementsFragment extends Fragment {
    View view;
    static Context context;
    ImageView imgBack;
    RelativeLayout rlTerms, rlPrivacyPolicy;
    MyApplication objMyApplication;
    ProgressDialog dialog;
    DashboardViewModel dashboardViewModel;

    public UserAgreementsFragment() {
    }

    public static UserAgreementsFragment newInstance(Context cxt) {
        UserAgreementsFragment fragment = new UserAgreementsFragment();
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
        if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            bottomSheetBehavior.setHideable(true);
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            if (viewBottomSheet.getVisibility() == View.VISIBLE) {
                viewBottomSheet.setVisibility(View.GONE);
            }
            viewBack.setVisibility(View.GONE);
        }
        getActivity().findViewById(R.id.layoutMenu).setVisibility(View.GONE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_user_agreement, container, false);
        try {
            objMyApplication = (MyApplication) context.getApplicationContext();
            dashboardViewModel = new ViewModelProvider(this).get(DashboardViewModel.class);
            if (Build.VERSION.SDK_INT >= 23) {
                //getActivity().getWindow().setStatusBarColor(getActivity().getColor(R.color.statusbar));
                getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                        WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            }
            getActivity().findViewById(R.id.layoutMenu).setVisibility(View.GONE);
            imgBack = (ImageView) view.findViewById(R.id.imgBack);
            rlPrivacyPolicy = (RelativeLayout) view.findViewById(R.id.rlPrivacyPolicy);
            rlTerms = (RelativeLayout) view.findViewById(R.id.rlTerms);

            if (Utils.checkInternet(context)) {
//                dialog = new ProgressDialog(context, R.style.MyAlertDialogStyle);
//                dialog.setIndeterminate(false);
//                dialog.setMessage("Please wait...");
//                dialog.getWindow().setGravity(Gravity.CENTER);
//                dialog.show();
                dashboardViewModel.meAgreements();

            } else {
                Utils.displayAlert(getString(R.string.internet), getActivity());
            }
            initObservables();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) context).loadProfile();
            }
        });
        rlTerms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TermsServicesFragment profileFragment = TermsServicesFragment.newInstance(getActivity());
                try {
                    openFragment(profileFragment, "termsofservice");
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
        rlPrivacyPolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PrivacyPolicyFragment profileFragment = PrivacyPolicyFragment.newInstance(getActivity());
                try {
                    openFragment(profileFragment, "privacypolicy");
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        return view;
    }

    private void openFragment(Fragment fragment, String tag) {
        try {
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.container, fragment, tag).addToBackStack(tag);
//            transaction.replace(R.id.container, fragment, tag);
            transaction.commit();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void initObservables() {
        dashboardViewModel.getUserAgreementsMutableLiveData().observe(getActivity(), new Observer<Agreements>() {
            @Override
            public void onChanged(Agreements user) {
                //dialog.dismiss();

                String strAmount = "";
                List<AgreementsDataItems> agreementsInfo = user.getData().getItems();
                objMyApplication.setAgreements(user);
                if (agreementsInfo != null && agreementsInfo.size() > 0) {
                    for (int i = 0; i < agreementsInfo.size(); i++) {
                        objMyApplication.setSignatureType(agreementsInfo.get(i).getSignatureType());
                        objMyApplication.setRefId(agreementsInfo.get(i).getRefId());
                        objMyApplication.setAgreementsId(agreementsInfo.get(i).getId());
                        objMyApplication.setAgreementsUserId(agreementsInfo.get(i).getUserId());
                        objMyApplication.setAgreementsSignature(agreementsInfo.get(i).getSignature());
                        objMyApplication.setAgreementsIpAddress(agreementsInfo.get(i).getIpAddress());
                        objMyApplication.setAgreementsSignedOn(agreementsInfo.get(i).getSignedOn());
                    }
                }
            }
        });
    }
}


