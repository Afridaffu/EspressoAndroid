package com.greenbox.coyni.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.greenbox.coyni.R;

public class BusinessTransactionsFragment extends BaseFragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View currentView = inflater.inflate(R.layout.fragment_business_transactions, container, false);
        return currentView;
    }
}
