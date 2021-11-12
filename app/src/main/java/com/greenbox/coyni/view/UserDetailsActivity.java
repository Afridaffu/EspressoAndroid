package com.greenbox.coyni.view;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.greenbox.coyni.R;
import com.greenbox.coyni.model.profile.Profile;
import com.greenbox.coyni.utils.MyApplication;

public class UserDetailsActivity extends AppCompatActivity {

    ImageView editProfileIV, userProfileIV;
    TextView userAddressTV,userPhoneNumTV,userEmailIdTV,imageTextTV;
    MyApplication myApplicationObj;
    LinearLayout emailLL,phoneLL,addressLL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_user_details);
            initFields();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void initFields(){
        try {
            myApplicationObj = (MyApplication) getApplicationContext();
            editProfileIV = findViewById(R.id.editProfileIV);
            userProfileIV = findViewById(R.id.userProfileIV);
            userAddressTV = findViewById(R.id.userAddressTV);
            userPhoneNumTV = findViewById(R.id.userPhoneNumTV);
            userEmailIdTV = findViewById(R.id.userEmailIdTV);
            imageTextTV = findViewById(R.id.imageTextTV);
            emailLL = findViewById(R.id.emailLL);
            phoneLL = findViewById(R.id.phoneLL);
            addressLL = findViewById(R.id.addressLL);
            Profile profile = myApplicationObj.getMyProfile();

            if(myApplicationObj.getMyProfile().getData().getFirstName()!=null){
                bindImage(myApplicationObj.getMyProfile().getData().getImage());
                userEmailIdTV.setText(profile.getData().getEmail());
                userPhoneNumTV.setText("+" + profile.getData().getPhoneNumber().replace(" ", " - "));
                if (profile.getData().getAddressLine2() == null || profile.getData().getAddressLine2().trim().equals("")) {
                    userAddressTV.setText(profile.getData().getAddressLine1() + ", " + profile.getData().getCity() + ", "
                            + profile.getData().getState() + ", " + "United States" + ", " + profile.getData().getZipCode());
                } else {
                    userAddressTV.setText(profile.getData().getAddressLine1() + ", " + profile.getData().getAddressLine2() + ", "
                            + profile.getData().getCity() + ", " + profile.getData().getState()
                            + ", " + "United States" + ", " + profile.getData().getZipCode());
                }

                emailLL.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(UserDetailsActivity.this, PINActivity.class)
                        .putExtra("TYPE","ENTER")
                        .putExtra("screen","EditEmail"));
                    }
                });
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void bindImage(String imageString) {
        try {

            if (imageString != null && !imageString.trim().equals("")) {
                userProfileIV.setVisibility(View.VISIBLE);
                imageTextTV.setVisibility(View.GONE);
                Glide.with(this)
                        .load(imageString)
                        .into(userProfileIV);
            } else {
                userProfileIV.setVisibility(View.GONE);
                imageTextTV.setVisibility(View.VISIBLE);
                String imageText ="";
                imageText = imageText+myApplicationObj.getMyProfile().getData().getFirstName().substring(0,1).toUpperCase()+
                        myApplicationObj.getMyProfile().getData().getLastName().substring(0,1).toUpperCase();
                imageTextTV.setText(imageText);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}