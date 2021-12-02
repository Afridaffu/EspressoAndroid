package com.greenbox.coyni.view;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.greenbox.coyni.R;
import com.greenbox.coyni.fragments.AddNoteBottomSheet;
public class PayRequestTransactionActivity extends AppCompatActivity {
    EditText payreqET;
    LinearLayout addNoteClick;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addNoteClick=findViewById(R.id.addNoteClickLL);

        addNoteClick.setOnClickListener(view -> {
            AddNoteBottomSheet addNoteBottomSheet = new AddNoteBottomSheet();
            addNoteBottomSheet.show(getSupportFragmentManager(), addNoteBottomSheet.getTag());
        });
        setContentView(R.layout.activity_pay_request_transaction);
        initialization();
        TextWathcers();
    }

    private void initialization() {
        payreqET=findViewById(R.id.payrequestET);
    }
    public void TextWathcers(){
        payreqET.addTextChangedListener(new TextWatcher() {
            boolean hint;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length()>3){
                    hint=true;
                    payreqET.setTextSize(32);
                }
                else {
                    hint=false;
                    payreqET.setTextSize(54);

                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }
}