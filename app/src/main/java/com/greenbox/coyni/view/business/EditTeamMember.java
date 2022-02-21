package com.greenbox.coyni.view.business;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.greenbox.coyni.R;
import com.greenbox.coyni.utils.Utils;

public class EditTeamMember extends AppCompatActivity {
    TextInputLayout editfnametil,editlnametil,editemailtil;
    TextInputEditText editfnameET,editlnameET,editemailET;
    LinearLayout editfnameLL,editlnameLL,editemailLL,editphoneLL;
    TextView editfnameTV,editlnameTV,editemailTV,editphoneTV;
    public static int focusedID = 0;
    public CardView sendcv;
    public boolean isFirstName = false, isLastName = false, isEmail = false, isPhoneNumber = false,isNextEnabled=false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_team_member);

        initfields();
        focusWatchers();
        textWatchers();

    }

    private void initfields() {
        editfnametil = findViewById(R.id.editfnametil);
        editlnametil = findViewById(R.id.editlnametil);
        editemailtil = findViewById(R.id.editemailtil);

        editfnameET = findViewById(R.id.editfnameET);
        editlnameET = findViewById(R.id.editlnameET);
        editemailET = findViewById(R.id.editemailET);

        editfnameLL = findViewById(R.id.editfirstNameErrorLL);
        editlnameLL = findViewById(R.id.editlastNameErrorLL);
        editemailLL = findViewById(R.id.editemailErrorLL);
        editphoneLL = findViewById(R.id.editphoneErrorLL);

        editfnameTV = findViewById(R.id.editfirstNameErrorTV);
        editlnameTV = findViewById(R.id.editlastNameErrorTV);
        editemailTV = findViewById(R.id.editemailErrorTV);
        editphoneTV = findViewById(R.id.editphoneErrorTV);

        sendcv = findViewById(R.id.sendCV);

    }
    private void focusWatchers() {
        try {

            editfnameET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    if (!b) {
                        editfnameET.setHint("");
                        if (editfnameET.getText().toString().trim().length() > 1) {
                            editfnameLL.setVisibility(GONE);
                            editfnametil.setBoxStrokeColorStateList(Utils.getNormalColorState(getApplicationContext()));
                            Utils.setUpperHintColor(editfnametil, getColor(R.color.primary_black));

                        } else if (editfnameET.getText().toString().trim().length() == 1) {
                            editfnametil.setBoxStrokeColorStateList(Utils.getErrorColorState(getApplicationContext()));
                            Utils.setUpperHintColor(editfnametil, getColor(R.color.error_red));
                            editfnameLL.setVisibility(VISIBLE);
                            editfnameTV.setText("Minimum 2 Characters Required");
                        } else {
                            editfnametil.setBoxStrokeColorStateList(Utils.getErrorColorState(getApplicationContext()));
                            Utils.setUpperHintColor(editfnametil, getColor(R.color.light_gray));
                            editfnameLL.setVisibility(VISIBLE);
                            editfnameTV.setText("Field Required");
                        }
                    } else {
                        if (!Utils.isKeyboardVisible)
                            Utils.shwForcedKeypad(EditTeamMember.this);
                        focusedID = editfnameET.getId();
                        editfnameET.setHint("First Name");
                        editfnametil.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
                        Utils.setUpperHintColor(editfnametil, getColor(R.color.primary_green));
                    }
                }
            });

            editlnameET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    if (!b) {
                        editlnameET.setHint("");
                        if (editlnameET.getText().toString().trim().length() > 1) {
                            editlnameLL.setVisibility(GONE);
                            editlnametil.setBoxStrokeColorStateList(Utils.getNormalColorState(getApplicationContext()));
                            Utils.setUpperHintColor(editlnametil, getColor(R.color.primary_black));

                        } else if (editlnameET.getText().toString().trim().length() == 1) {
                            editlnametil.setBoxStrokeColorStateList(Utils.getErrorColorState(getApplicationContext()));
                            Utils.setUpperHintColor(editlnametil, getColor(R.color.error_red));
                            editlnameLL.setVisibility(VISIBLE);
                            editlnameTV.setText("Minimum 2 Characters Required");
                        } else {
                            editlnametil.setBoxStrokeColorStateList(Utils.getErrorColorState(getApplicationContext()));
                            Utils.setUpperHintColor(editlnametil, getColor(R.color.light_gray));
                            editlnameLL.setVisibility(VISIBLE);
                            editlnameTV.setText("Field Required");
                        }
                    } else {
                        if (!Utils.isKeyboardVisible)
                            Utils.shwForcedKeypad(EditTeamMember.this);
                        focusedID = editlnameET.getId();
                        editlnameET.setHint("Last Name");
                        editlnametil.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
                        Utils.setUpperHintColor(editlnametil, getColor(R.color.primary_green));
                    }
                }
            });

            editemailET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    if (!b) {
                        editemailET.setHint("");
                        if (editemailET.getText().toString().trim().length() > 5 && !Utils.isValidEmail(editemailET.getText().toString().trim())) {
                            editemailtil.setBoxStrokeColorStateList(Utils.getErrorColorState(getApplicationContext()));
                            Utils.setUpperHintColor(editemailtil, getColor(R.color.error_red));
                            editemailLL.setVisibility(VISIBLE);
                            editemailTV.setText("Please Enter a valid Email");
                        } else if (editemailET.getText().toString().trim().length() > 5 && Utils.isValidEmail(editemailET.getText().toString().trim())) {
                            editemailtil.setBoxStrokeColorStateList(Utils.getNormalColorState(getApplicationContext()));
                            Utils.setUpperHintColor(editemailtil, getColor(R.color.primary_black));
                            editemailLL.setVisibility(GONE);
                        } else if (editemailET.getText().toString().trim().length() > 0 && editemailET.getText().toString().trim().length() <= 5) {
                            editemailtil.setBoxStrokeColorStateList(Utils.getErrorColorState(getApplicationContext()));
                            Utils.setUpperHintColor(editemailtil, getColor(R.color.error_red));
                            editemailLL.setVisibility(VISIBLE);
                            editemailTV.setText("Field Required");
                        } else {
                            editemailtil.setBoxStrokeColorStateList(Utils.getErrorColorState(getApplicationContext()));
                            Utils.setUpperHintColor(editemailtil, getColor(R.color.light_gray));
                            editemailLL.setVisibility(VISIBLE);
                            editemailTV.setText("Field Required");
                        }
                    } else {
                        if (!Utils.isKeyboardVisible)
                            Utils.shwForcedKeypad(EditTeamMember.this);
                        focusedID = editemailET.getId();
                        editemailET.setHint("Email");
                        editemailtil.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
                        Utils.setUpperHintColor(editemailtil, getColor(R.color.primary_green));
                    }
                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }
    }
    private void textWatchers() {
        editfnameET.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() > 1 && charSequence.toString().trim().length() < 31) {
                    isFirstName = true;
                    editfnameLL.setVisibility(GONE);
                    editfnametil.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
                    Utils.setUpperHintColor(editfnametil, getResources().getColor(R.color.primary_green));
                } else if (editfnameET.getText().toString().trim().length() == 0) {
                    editfnameLL.setVisibility(VISIBLE);
                    editfnameTV.setText("Field Required");
                } else {
                    isFirstName = false;
                }
                enableOrDisableNext();
            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    String str = editfnameET.getText().toString();
                    if (str.length() > 0 && str.substring(0, 1).equals(" ")) {
                        editfnameET.setText("");
                        editfnameET.setSelection(editfnameET.getText().length());
                    } else if (str.length() > 0 && str.contains(".")) {
                        editfnameET.setText(editfnameET.getText().toString().replaceAll("\\.", ""));
                        editfnameET.setSelection(editfnameET.getText().length());
                    } else if (str.length() > 0 && str.contains("http") || str.length() > 0 && str.contains("https")) {
                        editfnameET.setText("");
                        editfnameET.setSelection(editfnameET.getText().length());
                    }

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        editlnameET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (charSequence.toString().trim().length() > 1 && charSequence.toString().trim().length() < 31) {
                    isLastName = true;
                    editlnameLL.setVisibility(GONE);
                    editlnametil.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
                    Utils.setUpperHintColor(editlnametil, getResources().getColor(R.color.primary_green));
                } else if (editlnameET.getText().toString().trim().length() == 0) {
                    editlnameLL.setVisibility(VISIBLE);
                    editlnameTV.setText("Field Required");
                } else {
                    isLastName = false;
                }
                enableOrDisableNext();
            }

            @Override
            public void afterTextChanged(Editable editable) {
                try {
                    String str = editlnameET.getText().toString();
                    if (str.length() > 0 && str.substring(0).equals(" ")) {
                        editlnameET.setText(editlnameET.getText().toString().replaceAll(" ", ""));
                        editlnameET.setSelection(editlnameET.getText().length());
                    } else if (str.length() > 0 && str.substring(str.length() - 1).equals(".")) {
                        editlnameET.setText(editlnameET.getText().toString().replaceAll(".", ""));
                        editlnameET.setSelection(editlnameET.getText().length());
                    }

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        editemailET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (charSequence.length() > 5 && Utils.isValidEmail(charSequence.toString().trim())) {
                    isEmail = false;
                    editemailLL.setVisibility(GONE);
                    editemailtil.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
//                        emailTIL.setHintTextColor(colorState);
                    Utils.setUpperHintColor(editemailtil, getResources().getColor(R.color.primary_green));

                } else if (editemailET.getText().toString().trim().length() == 0) {
                    editemailLL.setVisibility(VISIBLE);
                    editemailTV.setText("Field Required");
                }
                if (Utils.isValidEmail(charSequence.toString().trim()) && charSequence.toString().trim().length() > 5) {
                    isEmail = true;
                } else {
                    isEmail = false;
                }
                enableOrDisableNext();
            }

            @Override
            public void afterTextChanged(Editable editable) {
                try {
                    String str = editemailET.getText().toString();
                    if (str.length() > 0 && str.substring(0).equals(" ") || (str.length() > 0 && str.contains(" "))) {
                        editemailET.setText(editemailET.getText().toString().replaceAll(" ", ""));
                        editemailET.setSelection(editemailET.getText().length());
                    }

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });


    }

    private void enableOrDisableNext() {
        try {
            if (isFirstName && isLastName && isEmail ) {
                isNextEnabled = true;
                sendcv.setCardBackgroundColor(getResources().getColor(R.color.primary_color));

                Log.e("All boolean", isFirstName + " " + isLastName + " " + isEmail + " " );
            } else {

                Log.e("All boolean", isFirstName + " " + isLastName + " " + isEmail + " " + isPhoneNumber + " ");

                isNextEnabled = false;
                sendcv.setCardBackgroundColor(getResources().getColor(R.color.inactive_color));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}