package com.greenbox.coyni.dialogs;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;

import com.bumptech.glide.util.Util;
import com.google.android.material.textfield.TextInputLayout;
import com.greenbox.coyni.R;
import com.greenbox.coyni.utils.Utils;
import com.greenbox.coyni.view.business.BusinessAdditionalActionRequiredActivity;

public class AddCommentsDialog extends BaseDialog {

    private Context context;
    private String comment;
    private EditText addNoteET;
    private CardView doneBtn;
    private TextInputLayout addNoteTIL;
    private LinearLayout cancelBtn;

    public AddCommentsDialog(@NonNull Context context, String comment) {
        super(context);
        this.context = context;
        this.comment = comment;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_note_layout);

        addNoteET = findViewById(R.id.addNoteET);
        doneBtn = findViewById(R.id.doneBtn);
        addNoteTIL = findViewById(R.id.etlMessage);
        cancelBtn = findViewById(R.id.cancelBtn);

        doneBtn.setCardBackgroundColor(context.getResources().getColor(R.color.inactive_color));
        doneBtn.setEnabled(false);
        addNoteET.requestFocus();
        addNoteET.setHint(R.string.reason);
        if(!Utils.isKeyboardVisible) {
            Utils.shwForcedKeypad(context);
        }

        if (comment != null && !comment.trim().equals("")) {
            addNoteET.setText(comment.trim());
            addNoteET.setSelection(comment.trim().length());
        }
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Utils.isKeyboardVisible) {
                    Utils.hideKeypad(context);
                }
                dismiss();
            }
        });


        doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    getOnDialogClickListener().onDialogClicked(Utils.COMMENT_ACTION, addNoteET.getText().toString().trim());
                    dismiss();
                    if (Utils.isKeyboardVisible) {
                        Utils.hideKeypad(context);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        addNoteET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() == 0) {
                    addNoteTIL.setCounterEnabled(false);
                    doneBtn.setEnabled(false);
                    doneBtn.setCardBackgroundColor(context.getResources().getColor(R.color.inactive_color));
                } else {
                    addNoteTIL.setCounterEnabled(true);
                    doneBtn.setEnabled(true);
                    doneBtn.setCardBackgroundColor(context.getResources().getColor(R.color.primary_color));
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {
                try {
                    String str = addNoteET.getText().toString();
                    if (str.length() > 0 && str.substring(0, 1).equals(" ")) {
                        addNoteET.setText("");
                        addNoteET.setSelection(addNoteET.getText().length());

                    } else if (str.length() > 0 && str.contains(".")) {
                        addNoteET.setText(addNoteET.getText().toString().replaceAll("\\.", ""));
                        addNoteET.setSelection(addNoteET.getText().length());

                    } else if (str.length() > 0 && str.contains("http") || str.length() > 0 && str.contains("https")) {
                        addNoteET.setText("");
                        addNoteET.setSelection(addNoteET.getText().length());
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

}
