package com.greenbox.coyni.view;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.greenbox.coyni.R;

public class UserDetailsActivity extends AppCompatActivity {

    ImageView editProfileIV, userProfileIV;
    TextView userAddressTV,userPhoneNumTV,userEmailIdTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_details);
        initFields();
    }

    public void initFields(){
        editProfileIV = findViewById(R.id.editProfileIV);
        userProfileIV = findViewById(R.id.userProfileIV);
        userAddressTV = findViewById(R.id.userAddressTV);
        userPhoneNumTV = findViewById(R.id.userPhoneNumTV);
        userEmailIdTV = findViewById(R.id.userEmailIdTV);
    }


}