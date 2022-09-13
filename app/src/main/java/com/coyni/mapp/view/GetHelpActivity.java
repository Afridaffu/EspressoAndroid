package com.coyni.mapp.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;
import android.text.Html;

import com.coyni.mapp.R;
import com.coyni.mapp.databinding.ActivityGetHelpBinding;

public class GetHelpActivity extends AppCompatActivity {

    private ActivityGetHelpBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_get_help);
        setContentView(R.layout.activity_get_help);

        binding.procedureForSupportTV.setText(Html.fromHtml("&#x2022; Name\\n&#x2022; coyni email\\n&#x2022;  Account ID number (located in your user details) \\n&#x2022; A detailed description of your question, feedback, or issue"));
    }
}