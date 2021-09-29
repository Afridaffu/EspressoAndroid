package com.coyni.android.fragments;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.coyni.android.R;

public class WalletDetailsFragment extends Fragment {
    View view;
    static Context context;
    ImageView imgCurrency,imgCurrency1;
    TextView tvWallet,tvPrice,tvBalHead,tvWalletBal,tvValue;

    public WalletDetailsFragment() {
    }

    public static WalletDetailsFragment newInstance(Context cxt) {
        WalletDetailsFragment fragment = new WalletDetailsFragment();
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
        view = inflater.inflate(R.layout.walletdetails, container, false);
        try {
            if (Build.VERSION.SDK_INT >= 23) {
                getActivity().getWindow().setStatusBarColor(getActivity().getColor(R.color.white));
            }
            imgCurrency = view.findViewById(R.id.imgCurrency);
            imgCurrency1 = view.findViewById(R.id.imgCurrency1);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return view;
    }
}

