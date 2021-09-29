package com.coyni.android.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.coyni.android.R;
import com.coyni.android.utils.MyApplication;
import com.coyni.android.view.MainActivity;

public class IssuingCardFragment extends Fragment {
    View view;
    static Context context;
    ImageView imgClose;
    MyApplication objMyApplication;

    public IssuingCardFragment() {
    }

    public static IssuingCardFragment newInstance(Context cxt) {
        IssuingCardFragment fragment = new IssuingCardFragment();
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
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.issuinglayout, container, false);
        try {
            imgClose = view.findViewById(R.id.imgIClose);
            objMyApplication = (MyApplication) context.getApplicationContext();
            ((MainActivity) context).enablePayment();
            imgClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    objMyApplication.setToken(true);
                    ((MainActivity) context).loadToken();
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return view;
    }
}
