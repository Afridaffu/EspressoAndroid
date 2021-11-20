package com.greenbox.coyni.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.greenbox.coyni.R;
import com.greenbox.coyni.adapters.AgreeListAdapter;
import com.greenbox.coyni.model.Agreements;
import com.greenbox.coyni.model.AgreementsData;
import com.greenbox.coyni.model.AgreementsPdf;
import com.greenbox.coyni.model.Item;
import com.greenbox.coyni.utils.MyApplication;
import com.greenbox.coyni.viewmodel.DashboardViewModel;

import java.util.List;

public class AgreementsActivity extends AppCompatActivity {
    DashboardViewModel dashboardViewModel;
    RecyclerView recyclerView;
    AgreeListAdapter adapter;
    LinearLayoutManager linearLayoutManager;
    AgreeListAdapter.RecyclerClickListener listener;
    String status;
    String privacyURL = "https://crypto-resources.s3.amazonaws.com/Greenbox+POS+GDPR+Privacy+Policy.pdf";
    String tosURL = "https://crypto-resources.s3.amazonaws.com/Gen+3+V1+TOS+v6.pdf";
    Agreements agreements;
    int i=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dashboardViewModel=new ViewModelProvider(this).get(DashboardViewModel.class);
        setContentView(R.layout.activity_agreements);
        recyclerView=findViewById(R.id.recyclerview);
        RecyclerView.LayoutManager layoutManager=new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        initObserver();
        dashboardViewModel.meAgreementsById();
        setOnClickListener();

    }

    private void initObserver() {
        try {

            dashboardViewModel.getAgreementsMutableLiveData().observe(this, new Observer<Agreements>() {
                @Override
                public void onChanged(Agreements agreements) {
                    Log.e("act", agreements.getStatus());
                    if (agreements.getStatus().contains("SUCCESS")) {
                       adapter=new AgreeListAdapter(AgreementsActivity.this,agreements,dashboardViewModel,listener);
                        recyclerView.setAdapter(adapter);
                    }
                }
            });
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
    }

    private void setOnClickListener() {
        try {

            listener = (view, position) -> {
                if (position==1) {
                    Intent inte = new Intent(Intent.ACTION_VIEW);
                    inte.setDataAndType(
                            Uri.parse(tosURL),
                            "application/pdf");
                    startActivity(inte);
                }
                else if (position==0){
                    Intent inte = new Intent(Intent.ACTION_VIEW);
                    inte.setDataAndType(
                            Uri.parse(privacyURL),
                            "application/pdf");
                    startActivity(inte);

                }
            };
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
    }
}