package com.coyni.mapp.dialogs;


import static com.coyni.mapp.custom_camera.CameraUtility.BROWSE;
import static com.coyni.mapp.custom_camera.CameraUtility.CHOOSE_LIBRARY;
import static com.coyni.mapp.custom_camera.CameraUtility.TAKE_PHOTO;

import android.content.Context;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import com.coyni.mapp.R;
import com.coyni.mapp.databinding.DialogFilePickerBinding;


public class FilePickerDialog extends BaseDialog implements View.OnClickListener {

    private DialogFilePickerBinding binding;
    private boolean showBrowseOption = false;
    private Long mLastClickTime = 0L;

    public FilePickerDialog(@NonNull Context context) {
        super(context);
    }

    public FilePickerDialog(@NonNull Context context, boolean showBrowseOption) {
        super(context);
        this.showBrowseOption = showBrowseOption;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_file_picker);

        binding = DataBindingUtil
                .inflate(LayoutInflater.from(getContext()), R.layout.dialog_file_picker, null, false);
        setContentView(binding.getRoot());

        binding.llBrowseLayout.setVisibility(showBrowseOption ? View.VISIBLE : View.GONE);
        binding.browseFileTV.setOnClickListener(this);
        binding.tvChooseLibrary.setOnClickListener(this);
        binding.tvTakePhoto.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();
        switch (view.getId()) {
            case R.id.tvChooseLibrary:
                if (getOnDialogClickListener() != null) {
                    getOnDialogClickListener().onDialogClicked(CHOOSE_LIBRARY, null);
                }
                dismiss();
                break;
            case R.id.tvTakePhoto:
                if (getOnDialogClickListener() != null) {
                    getOnDialogClickListener().onDialogClicked(TAKE_PHOTO, null);
                }
                dismiss();
                break;
            case R.id.browseFileTV:
                if (getOnDialogClickListener() != null) {
                    getOnDialogClickListener().onDialogClicked(BROWSE, null);
                }
                dismiss();
                break;
        }
    }
}
