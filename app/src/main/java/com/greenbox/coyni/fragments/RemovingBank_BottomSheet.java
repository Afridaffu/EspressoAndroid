package com.greenbox.coyni.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.greenbox.coyni.R;
import com.greenbox.coyni.model.APIError;
import com.greenbox.coyni.model.bank.BankDeleteResponseData;
import com.greenbox.coyni.model.paymentmethods.PaymentsList;
import com.greenbox.coyni.utils.Utils;
import com.greenbox.coyni.view.PaymentMethodsActivity;
import com.greenbox.coyni.viewmodel.PaymentMethodsViewModel;

public class RemovingBank_BottomSheet extends BottomSheetDialogFragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    static PaymentsList objPayment;
    static PaymentMethodsViewModel paymentMethodsViewModel;
    static Context context;
    ProgressDialog progressDialog;

    public RemovingBank_BottomSheet() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment FaceIdNotAvailable_BottomSheet.
     */
    // TODO: Rename and change types and number of parameters
    public static RemovingBank_BottomSheet newInstance(PaymentsList paymentsList, Context objContext) {
        RemovingBank_BottomSheet fragment = new RemovingBank_BottomSheet();
        try {
            Bundle args = new Bundle();
            objPayment = paymentsList;
            context = objContext;
            paymentMethodsViewModel = new ViewModelProvider((PaymentMethodsActivity)context).get(PaymentMethodsViewModel.class);
            fragment.setArguments(args);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_removing_btm_sheet, container, false);
        TextView tvBankName = view.findViewById(R.id.tvBankName);
        TextView tvAccount = view.findViewById(R.id.tvAccount);
        TextView tvNo = view.findViewById(R.id.tvNo);
        TextView tvYes = view.findViewById(R.id.tvYes);
        initObserver();
        if (objPayment != null) {
            tvBankName.setText(objPayment.getBankName());
            if (objPayment.getAccountNumber() != null && objPayment.getAccountNumber().length() > 4) {
                tvAccount.setText("**** " + objPayment.getAccountNumber().substring(objPayment.getAccountNumber().length() - 4));
            } else {
                tvAccount.setText(objPayment.getAccountNumber());
            }
        }
        tvNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        tvYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    progressDialog = Utils.showProgressDialog(context);
                    paymentMethodsViewModel.deleteBanks(String.valueOf(objPayment.getId()));
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
        return view;
    }

    private void initObserver() {
        paymentMethodsViewModel.getDelBankResponseMutableLiveData().observe(this, new Observer<BankDeleteResponseData>() {
            @Override
            public void onChanged(BankDeleteResponseData bankDeleteResponseData) {
                progressDialog.dismiss();
                if (bankDeleteResponseData != null) {
                    ((PaymentMethodsActivity) context).getPaymentMethods();
                }
            }
        });

        paymentMethodsViewModel.getApiErrorMutableLiveData().observe(this, new Observer<APIError>() {
            @Override
            public void onChanged(APIError apiError) {
                try {
                    progressDialog.dismiss();
                    if (apiError != null) {
                        if (!apiError.getError().getErrorDescription().equals("")) {
                            Utils.displayAlert(apiError.getError().getErrorDescription(), getActivity(), "");
                        } else {
                            Utils.displayAlert(apiError.getError().getFieldErrors().get(0), getActivity(), "");
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

}

