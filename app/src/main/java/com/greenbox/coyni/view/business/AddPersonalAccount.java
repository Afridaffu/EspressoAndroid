package com.greenbox.coyni.view.business;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.greenbox.coyni.R;
import com.greenbox.coyni.view.BaseActivity;

public class AddPersonalAccount extends BaseActivity {
    ImageView closeIv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_personal_account);

        closeIv =findViewById(R.id.closeBackIV);
        closeIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(AddPersonalAccount.this,BusinessOnboardingOpenNewAccount.class);
                startActivity(i);
            }
        });

    }
}
