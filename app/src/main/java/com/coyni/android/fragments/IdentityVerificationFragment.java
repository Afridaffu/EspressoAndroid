package com.coyni.android.fragments;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.coyni.android.R;
import com.coyni.android.utils.MyApplication;

import static com.coyni.android.view.MainActivity.bottomSheetBehavior;
import static com.coyni.android.view.MainActivity.viewBack;
import static com.coyni.android.view.MainActivity.viewBottomSheet;

public class IdentityVerificationFragment extends Fragment {
    View view;
    static Context context;
    ImageView imgBack;
    MyApplication objMyApplication;

    public IdentityVerificationFragment() {
    }

    public static IdentityVerificationFragment newInstance(Context cxt) {
        IdentityVerificationFragment fragment = new IdentityVerificationFragment();
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
        view = inflater.inflate(R.layout.fragment_identity_verification, container, false);
        try {
            if (Build.VERSION.SDK_INT >= 23) {
                //getActivity().getWindow().setStatusBarColor(getActivity().getColor(R.color.statusbar));
                getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                        WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            }
            getActivity().findViewById(R.id.layoutMenu).setVisibility(View.GONE);
            imgBack = (ImageView) view.findViewById(R.id.imgBack);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        /*imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserAgreementsFragment profileFragment = UserAgreementsFragment.newInstance(getActivity());
                try {
                    openFragment(profileFragment, "profile");
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });*/

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
}
