package com.greenbox.coyni.view.business;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.greenbox.coyni.R;
import com.greenbox.coyni.utils.Utils;
import com.greenbox.coyni.view.BaseActivity;

public class OpenNewAccount extends BaseActivity {
    LinearLayout newdbaLL,newcomapnyLL;
    ImageView closeback;
    Dialog entity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_new_account);

        newcomapnyLL = findViewById(R.id.newcompanyLL);
        newcomapnyLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(OpenNewAccount.this,BusinessRegistrationTrackerActivity.class);
                startActivity(i);
            }
        });

        closeback = findViewById(R.id.backCloseIV);
        closeback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(OpenNewAccount.this,BusinessOnboardingOpenNewAccount.class);
                startActivity(i);
            }
        });


        newdbaLL = findViewById(R.id.newdbaLL);
        newdbaLL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showProgressDialog();
                    dbaPopup();

                }
        });
    }
    private void dbaPopup() {
        try {
            entity = new Dialog(OpenNewAccount.this);
            entity.requestWindowFeature(Window.FEATURE_NO_TITLE);
            entity.setContentView(R.layout.open_account_new_dba_btmshet);
            entity.setCancelable(true);
            Window window = entity.getWindow();
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
            entity.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            WindowManager.LayoutParams wlp = window.getAttributes();

            wlp.gravity = Gravity.BOTTOM;
            wlp.flags &= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
            window.setAttributes(wlp);
            entity.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
            entity.show();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
