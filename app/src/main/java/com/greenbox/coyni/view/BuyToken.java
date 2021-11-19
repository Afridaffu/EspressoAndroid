package com.greenbox.coyni.view;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.greenbox.coyni.R;
import com.greenbox.coyni.utils.keyboards.CustomKeyboard;

public class BuyToken extends AppCompatActivity {
   EditText inputEV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy_token);

        EditText inputEV = findViewById(R.id.inputEV);
        CustomKeyboard ckb =(CustomKeyboard) findViewById(R.id.ckb);
        inputEV.setRawInputType(InputType.TYPE_CLASS_TEXT);
        inputEV.setTextIsSelectable(true);

        InputConnection ic = inputEV.onCreateInputConnection(new EditorInfo());
        ckb.setInputConnection(ic);


        inputEV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputEV.clearFocus();

            }
        });
    }
}