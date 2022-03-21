package com.greenbox.coyni.view.business;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.android.material.chip.Chip;
import com.greenbox.coyni.R;
import com.greenbox.coyni.adapters.OnItemClickListener;
import com.greenbox.coyni.adapters.ReserveReleasesRollingAdapter;
import com.greenbox.coyni.dialogs.DialogReserveReleasesRollingList;
import com.greenbox.coyni.utils.LogUtils;
import com.greenbox.coyni.view.BaseActivity;

public class ReserveReleasesActivity extends BaseActivity {

    ImageView ivFilterIcon;
    LinearLayout closeBtnIV, rollingLL;
    RecyclerView rrTransListRV;
    boolean isFilter = false ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reserve_releases);

        closeBtnIV = findViewById(R.id.closeBtnIV);
        ivFilterIcon = findViewById(R.id.ivFilterIcon);
        rollingLL = findViewById(R.id.rollingLL);
        rrTransListRV = findViewById(R.id.rrTransListRV);

        ReserveReleasesRollingAdapter adapter = new ReserveReleasesRollingAdapter(this);
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                LogUtils.v(TAG, "position clicked " + position);
            }
        });

        rrTransListRV.setAdapter(adapter);
        rrTransListRV.setLayoutManager(new LinearLayoutManager(this));
        closeBtnIV.setOnClickListener(view -> onBackPressed());
        ivFilterIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                showFiltersPopup();
                DialogReserveReleasesRollingList showReserveReleaseDialog = new DialogReserveReleasesRollingList(ReserveReleasesActivity.this);
                showReserveReleaseDialog.show();
            }
        });
        rollingLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog = new Dialog(ReserveReleasesActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.getWindow().setBackgroundDrawableResource(R.color.mb_transparent);
                dialog.setContentView(R.layout.reserve_manual_dialog);
                Window window = dialog.getWindow();
                window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
                WindowManager.LayoutParams wl = window.getAttributes();
                wl.gravity = Gravity.BOTTOM;
                wl.flags &= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
                window.setAttributes(wl);
                dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
                dialog.setCanceledOnTouchOutside(true);
                dialog.show();

                LinearLayout llRolling = dialog.findViewById(R.id.llRolling);
                LinearLayout llManualLL = dialog.findViewById(R.id.llManual);

                llRolling.setOnClickListener(view ->
                        onBackPressed()
//                        startActivity(new Intent(ReserveReleasesActivity.this, ReserveReleasesActivity.class))
                );
                llManualLL.setOnClickListener(view ->
//                        finish()
                        startActivity(new Intent(ReserveReleasesActivity.this, ManualReleasesActivity.class))
                );

            }
        });

    }

//    private void showFiltersPopup() {
//        Dialog dialog = new Dialog(ReserveReleasesActivity.this);
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialog.getWindow().setBackgroundDrawableResource(R.color.mb_transparent);
//        dialog.setContentView(R.layout.activity_reserve_filter);
//        Window window = dialog.getWindow();
//        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
//        WindowManager.LayoutParams wl = window.getAttributes();
//        wl.gravity = Gravity.BOTTOM;
//        wl.flags &= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
//        window.setAttributes(wl);
//        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
//        dialog.setCanceledOnTouchOutside(true);
//        dialog.show();
//
//        Chip OpenC = dialog.findViewById(R.id.OpenC);
//        Chip releasedC = dialog.findViewById(R.id.releasedC);
//        Chip onHoldC = dialog.findViewById(R.id.onHoldC);
//        Chip canceledC = dialog.findViewById(R.id.canceledC);
//    }
}