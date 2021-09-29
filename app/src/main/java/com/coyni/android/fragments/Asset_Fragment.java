package com.coyni.android.fragments;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.coyni.android.R;
import com.coyni.android.utils.MyApplication;
import com.coyni.android.view.MainActivity;

public class Asset_Fragment extends Fragment {
    View view;
    static Context context;
    ImageView imgClose;
    MyApplication objMyApplication;

    public Asset_Fragment() {
    }

    public static Asset_Fragment newInstance(Context cxt) {
        Asset_Fragment fragment = new Asset_Fragment();
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
        view = inflater.inflate(R.layout.asset_layout, container, false);
        try {
            imgClose = view.findViewById(R.id.imgAClose);
            objMyApplication = (MyApplication) context.getApplicationContext();
            ((MainActivity) context).enableAsset();
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
