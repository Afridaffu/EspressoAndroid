package com.coyni.mapp.custom_camera;

import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.coyni.mapp.R;

public class CameraActivity extends AppCompatActivity {

    public static CameraActivity cameraActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        setContentView(R.layout.activity_camera);

        cameraActivity = this;
        try {
            Fragment cameraFragment = CameraFragment.newInstance();
            Bundle args = new Bundle();
            args.putString("FROM", getIntent().getStringExtra("FROM"));
            cameraFragment.setArguments(args);
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, cameraFragment, CameraFragment.TAG)
                    .commit();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
