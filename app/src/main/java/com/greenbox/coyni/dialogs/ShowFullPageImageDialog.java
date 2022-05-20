package com.greenbox.coyni.dialogs;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.greenbox.coyni.R;
import com.greenbox.coyni.utils.DisplayImageUtility;

public class ShowFullPageImageDialog extends BaseDialog {
    private ImageView back, setImg;
    private Context context;
    private String imageKey;

    public ShowFullPageImageDialog(@NonNull Context context, String imageKey) {
        super(context, R.style.DialogTheme);
        this.context = context;
        this.imageKey = imageKey;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_image_actionrequired);
        back = findViewById(R.id.ivBack);
        setImg = findViewById(R.id.setImageIV);

        DisplayImageUtility utility = DisplayImageUtility.getInstance(context);
        utility.addImage(imageKey, setImg, 0);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }
}
