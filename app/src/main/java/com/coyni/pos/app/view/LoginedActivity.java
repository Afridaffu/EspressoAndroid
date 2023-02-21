package com.coyni.pos.app.view;

import android.os.Bundle;

import androidx.annotation.Nullable;

import com.coyni.pos.app.baseclass.BaseActivity;
import com.coyni.pos.app.databinding.ActivityLoginBinding;

public class LoginedActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityLoginBinding binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

    }
}
