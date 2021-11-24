package com.greenbox.coyni.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.text.Html;
import android.widget.TextView;

import com.greenbox.coyni.R;
import com.greenbox.coyni.viewmodel.DashboardViewModel;

public class PdfViewAgreementsActivity extends AppCompatActivity {

    DashboardViewModel dashboardViewModel;

    TextView  pdfagreementsTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_view_agreements);

        pdfagreementsTV = findViewById(R.id.pdfagreementsTV);

        dashboardViewModel=new ViewModelProvider(this).get(DashboardViewModel.class);

        if(getIntent().getStringExtra("content")!=""){
            pdfagreementsTV.setText(Html.fromHtml(getIntent().getStringExtra("content")));
        }
    }
}