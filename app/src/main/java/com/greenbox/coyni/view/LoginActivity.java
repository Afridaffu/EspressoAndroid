package com.greenbox.coyni.view;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.greenbox.coyni.R;
import com.greenbox.coyni.fragments.FaceIdNotAvailable_BottomSheet;
import com.greenbox.coyni.fragments.Login_EmPaIncorrect_BottomSheet;
import com.greenbox.coyni.utils.outline_et.OutLineBoxEditTextIV;

public class LoginActivity extends AppCompatActivity {
    OutLineBoxEditTextIV emailET,passwordET;
    TextView mailpassIncorrect,faceidNotAvail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        passwordET=findViewById(R.id.passwordET);
        emailET=findViewById(R.id.emailET);
        emailET.setField("Email","Coyni@example.com","",false);
        passwordET.setField("Password","************","", false);
        passwordET.setPasswordType(false);
        passwordET.setInputLengthPwd();
        mailpassIncorrect=findViewById(R.id.mailpassIncorrect);
        faceidNotAvail=findViewById(R.id.faceidNotAvaiSheet);
        mailpassIncorrect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Login_EmPaIncorrect_BottomSheet emailpass_incorrect = new Login_EmPaIncorrect_BottomSheet();
                emailpass_incorrect.show(getSupportFragmentManager(),emailpass_incorrect.getTag());

            }
        });
        faceidNotAvail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FaceIdNotAvailable_BottomSheet faceIdNotAvailable_bottomSheet = new FaceIdNotAvailable_BottomSheet();
                faceIdNotAvailable_bottomSheet.show(getSupportFragmentManager(),faceIdNotAvailable_bottomSheet.getTag());
            }
        });

    }
}