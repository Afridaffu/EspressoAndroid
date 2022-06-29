package com.greenbox.coyni.view.business;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.greenbox.coyni.R;
import com.greenbox.coyni.model.CompanyInfo.CompanyInfoResp;
import com.greenbox.coyni.model.States;
import com.greenbox.coyni.utils.MyApplication;
import com.greenbox.coyni.utils.Utils;
import com.greenbox.coyni.view.BaseActivity;
import com.greenbox.coyni.view.BusinessUserDetailsPreviewActivity;
import com.greenbox.coyni.viewmodel.BusinessIdentityVerificationViewModel;

import java.util.List;

public class CompanyInfoDetails extends BaseActivity {
    private LinearLayout closeLL, emailLL, phoneLL;
    private TextView mEmailTx, mPhoneNumberTx, mAddressTx, nameTX, mBusinessEntity;
    private String companyEmail = "", companyPhone = "", companyCountryCode = "";
    private BusinessIdentityVerificationViewModel businessIdentityVerificationViewModel;
    private int companyId = 0;
   public static MyApplication myApplication;
    private final String strName = "";
    private static String state = "";
    private ImageView emailIconIV, phoneIconIV;

    public static String StateName = "";
    public static String StateCode = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        setContentView(R.layout.activity_company_info_details);
        initFields();
        initObservers();

    }


    private void initFields() {
        closeLL = findViewById(R.id.bpCloseLL);
        mEmailTx = (TextView) findViewById(R.id.emailTx);
        mPhoneNumberTx = (TextView) findViewById(R.id.phoneNumberTx);
        mAddressTx = (TextView) findViewById(R.id.addressTx);
        nameTX = (TextView) findViewById(R.id.name_id);
        mBusinessEntity = (TextView) findViewById(R.id.business_entity);
        emailLL = (LinearLayout) findViewById(R.id.emailLL);
        phoneLL = (LinearLayout) findViewById(R.id.phoneLL);
        emailIconIV = (ImageView) findViewById(R.id.emailIconIV);
        phoneIconIV = (ImageView) findViewById(R.id.phoneIconIV);
        myApplication = (MyApplication) getApplicationContext();
        if (myApplication.getAccountType() == Utils.SHARED_ACCOUNT) {
            emailIconIV.setVisibility(View.GONE);
            phoneIconIV.setVisibility(View.GONE);
            emailLL.setEnabled(false);
            phoneLL.setEnabled(false);
        } else {
            emailIconIV.setVisibility(View.VISIBLE);
            phoneIconIV.setVisibility(View.VISIBLE);
            emailLL.setEnabled(true);
            phoneLL.setEnabled(true);
        }
        businessIdentityVerificationViewModel = new ViewModelProvider(this).get(BusinessIdentityVerificationViewModel.class);

        closeLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        nameTX.setOnClickListener(view -> {

            try {
                if (nameTX.getText().toString().contains("...") && myApplication.getCompanyInfoResp().getData().getName().length() >= 21) {
                    nameTX.setText(myApplication.getCompanyInfoResp().getData().getName());

                } else if (myApplication.getCompanyInfoResp().getData().getName().length() >= 21) {
                    nameTX.setText(myApplication.getCompanyInfoResp().getData().getName().substring(0, 20) + "...");
                } else {
                    nameTX.setText(myApplication.getCompanyInfoResp().getData().getName());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        if (myApplication.getDbaOwnerId() == null || myApplication.getDbaOwnerId() == 0) {
            emailLL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(CompanyInfoDetails.this, BusinessUserDetailsPreviewActivity.class).putExtra("screen", "CompanyInfo").putExtra("action", "EditEmailCompany").putExtra("value", companyEmail));
                }
            });
            phoneLL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(CompanyInfoDetails.this, BusinessUserDetailsPreviewActivity.class).putExtra("screen", "CompanyInfo").putExtra("action", "EditPhoneCompany").putExtra("value", companyPhone));
                }
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        showProgressDialog();
        businessIdentityVerificationViewModel.getCompanyInfo();
    }

    private void initObservers() {
        try {
            businessIdentityVerificationViewModel.getGetCompanyInfoResponse().observe(this, new Observer<CompanyInfoResp>() {
                @Override
                public void onChanged(CompanyInfoResp companyInfoResp) {
                    dismissDialog();
                    if (companyInfoResp != null) {
                        if (companyInfoResp.getStatus().equalsIgnoreCase(Utils.SUCCESS)) {
                            try {
                                CompanyInfoResp.Data cir = companyInfoResp.getData();
                                myApplication.setCompanyInfoResp(companyInfoResp);
                                String str = cir.getName();
//                                fullname = cir.getName().substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
                                if (str != null && !str.equals("")) {
                                    if (str.length() > 20) {
                                        nameTX.setText(Utils.getCapsSentences(str).substring(0, 20) + "...");
                                    } else {
                                        nameTX.setText(Utils.getCapsSentences(str));
                                    }
                                }
                                if (cir.getBusinessEntity() != null && !cir.getBusinessEntity().equals("")) {
                                    mBusinessEntity.setText(cir.getBusinessEntity());
                                }

                                if (cir.getEmail() != null && !cir.getEmail().equals("")) {
                                    mEmailTx.setText(cir.getEmail());
                                    companyEmail = cir.getEmail();
                                }

                                if (cir.getPhoneNumberDto().getPhoneNumber() != null && !cir.getPhoneNumberDto().getPhoneNumber().equals("")) {
                                    mPhoneNumberTx.setText("(" + cir.getPhoneNumberDto().getPhoneNumber().substring(0, 3) + ") " + cir.getPhoneNumberDto().getPhoneNumber().substring(3, 6) + "-" + cir.getPhoneNumberDto().getPhoneNumber().substring(6, 10));
                                    companyPhone = mPhoneNumberTx.getText().toString();
                                }

                                if (cir.getPhoneNumberDto().getCountryCode() != null && !cir.getPhoneNumberDto().getCountryCode().equals("")) {
                                    companyCountryCode = cir.getPhoneNumberDto().getCountryCode();
                                }
                                if (cir.getAddressLine1() != null && !cir.getAddressLine1().equals("")) {
                                    mAddressTx.setText(cir.getAddressLine1());
                                }
                                if (cir.getAddressLine2() != null && !cir.getAddressLine2().equals("")) {
                                    mAddressTx.append(", " + cir.getAddressLine2());
                                }
                                if (cir.getCity() != null && !cir.getCity().equals("")) {
                                    mAddressTx.append(", " + cir.getCity());
                                }
//                                if (cir.getState() != null && !cir.getState().equals("")) {
//                                    mAddressTx.append(", " + cir.getState());
//                                }
                              state = cir.getState().toLowerCase();
                                String stateCode = Utils.getStateCode(state, myApplication.getListStates());

                                if (stateCode != null && !stateCode.equals("")) {
                                    mAddressTx.append(", " + stateCode);
                                }

                                if (cir.getCountry() != null && !cir.getCountry().equals("")) {
                                    mAddressTx.append(", " + cir.getCountry());
                                }
                                if (cir.getZipCode() != null && !cir.getZipCode().equals("")) {
                                    mAddressTx.append(", " + cir.getZipCode() + ".");
                                }

                                companyId = cir.getId();

                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}