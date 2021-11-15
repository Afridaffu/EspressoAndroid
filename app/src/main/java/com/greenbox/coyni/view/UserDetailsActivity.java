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
import com.google.gson.reflect.TypeToken;
import com.greenbox.coyni.R;
import com.greenbox.coyni.model.States;
import com.greenbox.coyni.model.profile.Profile;
import com.greenbox.coyni.utils.MyApplication;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.List;

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
            getStates();
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
                String phoneNumber  = profile.getData().getPhoneNumber().split(" ")[1];
                String phoneFormat = "("+phoneNumber.substring(0, 3)+") "+phoneNumber.substring(3, 6)+"-"+phoneNumber.substring(6, 10);

                Log.e("Phone",phoneNumber);
                bindImage(myApplicationObj.getMyProfile().getData().getImage());
                userEmailIdTV.setText(profile.getData().getEmail());

                userPhoneNumTV.setText(phoneFormat);
//                phoneNumber.substring(1, 4) + phoneNumberET.getText().toString().substring(6, 9) + phoneNumberET.getText().toString().substring(10, phoneNumberET.getText().length());

                String addressFormatted = "";
                if(profile.getData().getAddressLine1()!=null && !profile.getData().getAddressLine1().equals("")){
                    addressFormatted = addressFormatted + profile.getData().getAddressLine1()+", ";
                }
                if(profile.getData().getAddressLine2()!=null && !profile.getData().getAddressLine2().equals("")){
                    addressFormatted = addressFormatted + profile.getData().getAddressLine2()+", ";
                }
                if(profile.getData().getCity()!=null && !profile.getData().getCity().equals("")){
                    addressFormatted = addressFormatted + profile.getData().getCity()+", ";
                }
                if(profile.getData().getState()!=null && !profile.getData().getState().equals("")){
                    addressFormatted = addressFormatted + profile.getData().getState()+", ";
                }

                addressFormatted = addressFormatted + "United States, ";

                if(profile.getData().getZipCode()!=null && !profile.getData().getZipCode().equals("")){
                    addressFormatted = addressFormatted + profile.getData().getZipCode()+", ";
                }

                userAddressTV.setText(addressFormatted);

                emailLL.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(UserDetailsActivity.this, PINActivity.class)
                        .putExtra("TYPE","ENTER")
                        .putExtra("screen","UserDetails"));
                    }
                });

                addressLL.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(UserDetailsActivity.this, EditAddressActivity.class));
                    }
                });

                phoneLL.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(UserDetailsActivity.this, EditPhoneActivity.class));
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

    private void getStates() {
        String json = null;
        try {
            InputStream is = getAssets().open("states.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
            Gson gson = new Gson();
            Type type = new TypeToken<List<States>>() {
            }.getType();
            List<States> listStates = gson.fromJson(json, type);
            myApplicationObj.setListStates(listStates);
            Log.e("list states", listStates.size()+"");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}