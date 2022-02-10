package com.greenbox.coyni.view.business;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.greenbox.coyni.R;
import com.greenbox.coyni.model.signedagreements.AgreementsRequest;
import com.greenbox.coyni.model.signedagreements.SignedAgreementResponse;
import com.greenbox.coyni.utils.LogUtils;
import com.greenbox.coyni.utils.Utils;
import com.greenbox.coyni.view.BaseActivity;
import com.greenbox.coyni.viewmodel.BusinessDashboardViewModel;

import java.io.File;

public class MerchantsAgrementActivity extends BaseActivity {
    public CardView doneCV;
    LinearLayout signatureEditLl;
    ImageView mIVSignature;
    CheckBox agreeCb;

    BusinessDashboardViewModel businessDashboardViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_merchants_agrement);
        businessDashboardViewModel = new ViewModelProvider(this).get(BusinessDashboardViewModel.class);
        AgreementsRequest request = new AgreementsRequest();
        request.setAgreementType(5);
        request.setIdentityFile("");
        businessDashboardViewModel.signedAgreement(request);
        initObservers();

        doneCV = findViewById(R.id.AgreeDoneCv);
        signatureEditLl = findViewById(R.id.signatureEditLL);
        mIVSignature = findViewById(R.id.signatureEditIV);
        agreeCb = findViewById(R.id.agreementCB);

        agreeCb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(agreeCb.isEnabled()){
                    doneCV.setCardBackgroundColor(getResources().getColor(R.color.primary_color));
                }
                else {
                    doneCV.setCardBackgroundColor(getResources().getColor(R.color.inactive_color));
                }
            }
        });

//        doneCV.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(MerchantsAgrementActivity.this, GetstartedSuccessAcivity.class);
//                activityResultLauncher.launch(intent);
//            }
//        });


        signatureEditLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchsignature();
            }
        });

    }
    private void launchsignature() {
        Intent inSignature = new Intent(MerchantsAgrementActivity.this, SignatureActivity.class);
        activityResultLauncher.launch(inSignature);
    }

    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    getSignature(result.getData());
                }
            });

    private void getSignature(Intent data) {
        if (data != null) {
            String filePath = data.getStringExtra(Utils.DATA);
            File targetFile = new File(filePath);
            if (targetFile.exists()) {
                Bitmap myBitmap = BitmapFactory.decodeFile(targetFile.getAbsolutePath());
                LogUtils.v(TAG, "file size " + myBitmap.getByteCount());
                mIVSignature.setImageBitmap(myBitmap);
            }
        }
    }

    private void initObservers() {

        businessDashboardViewModel.getSignedAgreementResponseMutableLiveData().observe(this, new Observer<SignedAgreementResponse>() {
            @Override
            public void onChanged(SignedAgreementResponse signedAgreementResponse) {
                try {
                    if (signedAgreementResponse != null && signedAgreementResponse.getStatus().equalsIgnoreCase("Success")) {

                                Intent intent = new Intent(MerchantsAgrementActivity.this, GetstartedSuccessAcivity.class);
                                activityResultLauncher.launch(intent);

                    }
                }catch (Exception ex){
                    ex.printStackTrace();
                }
            }
            });
        }
}

