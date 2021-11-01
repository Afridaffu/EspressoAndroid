package com.greenbox.coyni.view;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.github.angads25.toggle.widget.LabeledSwitch;
import com.greenbox.coyni.R;
import com.greenbox.coyni.fragments.FaceIdSetupBottomSheet;

public class CustomerProfileActivity extends AppCompatActivity {
    LabeledSwitch labeledSwitch;
    View viewFaceBottom;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_customer_profile);
            labeledSwitch=findViewById(R.id.switchbtn);
            viewFaceBottom=findViewById(R.id.viewSetupFaceBottom);
            viewFaceBottom.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FaceIdSetupBottomSheet faceIdSetupBottomSheet = new FaceIdSetupBottomSheet();
                    faceIdSetupBottomSheet.show(getSupportFragmentManager(),faceIdSetupBottomSheet.getTag());


                }
            });
            labeledSwitch.setOnToggledListener((labeledSwitch, isOn) -> {
                   });
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
        }
}