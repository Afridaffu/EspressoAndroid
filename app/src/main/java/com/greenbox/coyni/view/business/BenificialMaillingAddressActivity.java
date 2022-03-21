package com.greenbox.coyni.view.business;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.greenbox.coyni.R;
import com.greenbox.coyni.utils.Utils;

public class BenificialMaillingAddressActivity extends AppCompatActivity {
    TextInputLayout mailadressTIL, mailadressTIL2, mailcityTIL, mailstateTIL, mailzipcodeTIL;
    TextInputEditText mailadressET, mailadressET2, mailcityET, mailstateET, mailzipcodeET;
    LinearLayout mailadresErrorLL, mailAdressErrorLL2, mailcityErrorLL, mailstateErrorLL, mailzipErrorLL;
    TextView mailAdressTV, mailAdressTV2, mailcityTV, mailstateTV, mailzipTV;
    public static int focusedID = 0;

    ImageView cancelIV;
    public CardView doneCV;

    public boolean isMailAdress = false, isCity = false, isState = false, iszipcode = false, isNextEnabled = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_benificial_mailling_address);
        initfields();
        focusWatchers();
        textWatchers();

        cancelIV = findViewById(R.id.cancelIV);
        cancelIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BenificialMaillingAddressActivity.this, AddBeneficialOwnerActivity.class);
                startActivity(intent);
            }
        });
    }

    protected void onResume() {
        super.onResume();

        if (mailadressET.getId() == focusedID) {
            mailadressET.requestFocus();
        } else if (mailadressET2.getId() == focusedID) {
            mailadressET2.requestFocus();
        } else if (mailcityET.getId() == focusedID) {
            mailcityET.requestFocus();
        } else if (mailstateET.getId() == focusedID) {
            mailstateET.requestFocus();
        } else if (mailzipcodeET.getId() == focusedID) {
            mailzipcodeET.requestFocus();
        } else {
            mailadressET.requestFocus();
        }
        Log.e("ID", "" + focusedID);
    }

    private void initfields() {
        mailadressTIL = findViewById(R.id.mailadressTIL);
        mailadressTIL2 = findViewById(R.id.mailadressTIL2);
        mailcityTIL = findViewById(R.id.mailcityTIL);
        mailstateTIL = findViewById(R.id.mailstateTIL);
        mailzipcodeTIL = findViewById(R.id.mailzipcodeTIL);

        mailadressET = findViewById(R.id.mailadressET);
        mailadressET2 = findViewById(R.id.mailadressET2);
        mailcityET = findViewById(R.id.mailcityET);
        mailstateET = findViewById(R.id.mailstateET);
        mailzipcodeET = findViewById(R.id.mailzipcodeET);

        mailadresErrorLL = findViewById(R.id.mailadresErrorLL);
        mailAdressErrorLL2 = findViewById(R.id.mailAdressErrorLL2);
        mailcityErrorLL = findViewById(R.id.mailcityErrorLL);
        mailstateErrorLL = findViewById(R.id.mailstateErrorLL);
        mailzipErrorLL = findViewById(R.id.mailzipErrorLL);

        mailAdressTV = findViewById(R.id.mailAdressTV);
        mailAdressTV2 = findViewById(R.id.mailAdressTV2);
        mailcityTV = findViewById(R.id.mailcityTV);
        mailstateTV = findViewById(R.id.mailstateTV);
        mailzipTV = findViewById(R.id.mailzipTV);

        doneCV = findViewById(R.id.Donecv);
    }

    private void focusWatchers() {
        mailadressET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    mailadressET.setHint("");
                    if (mailadressET.getText().toString().trim().length() > 1) {
                        mailadressTIL.setBoxStrokeColorStateList(Utils.getNormalColorState());
                        mailadressET.setHintTextColor(getColor(R.color.light_gray));
                        Utils.setUpperHintColor(mailadressTIL, getColor(R.color.primary_black));
                        mailadresErrorLL.setVisibility(GONE);

                    } else if (mailadressET.getText().toString().trim().length() == 1) {
                        mailadressTIL.setBoxStrokeColor(getResources().getColor(R.color.error_red));
                        mailadressET.setHintTextColor(getColor(R.color.light_gray));
                        Utils.setUpperHintColor(mailadressTIL, getColor(R.color.error_red));
                        mailadresErrorLL.setVisibility(VISIBLE);
                        mailAdressTV.setText("Minimum 2 Characters Required");
                    } else {
                        mailadressTIL.setBoxStrokeColorStateList(Utils.getErrorColorState());
                        Utils.setUpperHintColor(mailadressTIL, getColor(R.color.light_gray));
                        mailadresErrorLL.setVisibility(VISIBLE);
                        mailAdressTV.setText("Field Required");
                    }
                } else {
                    mailadressET.setHint("Street Address");
                    mailadressTIL.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
                    Utils.setUpperHintColor(mailadressTIL, getColor(R.color.primary_green));
                    mailadresErrorLL.setVisibility(GONE);

                }
            }
        });

        mailadressET2.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    mailadressET2.setHint("");
                    mailadressTIL2.setBoxStrokeColorStateList(Utils.getNormalColorState());
                    Utils.setUpperHintColor(mailadressTIL2, getColor(R.color.light_gray));
                } else {
                    mailadressET2.setHint("Apt#, Suit, Floor ");
                    mailadressTIL2.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
                    Utils.setUpperHintColor(mailadressTIL2, getColor(R.color.primary_green));
                    mailAdressErrorLL2.setVisibility(GONE);
                }
            }
        });

        mailcityET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    mailcityET.setHint("");
                    if (mailcityET.getText().toString().trim().length() > 0) {
                        mailcityErrorLL.setVisibility(GONE);
                        mailcityTIL.setBoxStrokeColorStateList(Utils.getNormalColorState());
                        Utils.setUpperHintColor(mailcityTIL, getColor(R.color.primary_black));

                    } else {
                        mailcityTIL.setBoxStrokeColorStateList(Utils.getErrorColorState());
                        Utils.setUpperHintColor(mailcityTIL, getColor(R.color.light_gray));
                        mailcityErrorLL.setVisibility(VISIBLE);
                        mailcityTV.setText("Field Required");
                    }
                } else {
                    mailcityET.setHint("City");
                    mailcityTIL.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
                    Utils.setUpperHintColor(mailcityTIL, getColor(R.color.primary_green));
                    mailcityErrorLL.setVisibility(GONE);
                }
            }
        });

        mailstateET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    mailstateET.setHint("");
                    if (mailstateET.getText().toString().trim().length() > 0) {
                        mailstateErrorLL.setVisibility(GONE);
                        mailstateTIL.setBoxStrokeColorStateList(Utils.getNormalColorState());
                        Utils.setUpperHintColor(mailstateTIL, getColor(R.color.primary_black));

                    } else {
                        mailstateTIL.setBoxStrokeColorStateList(Utils.getErrorColorState());
                        Utils.setUpperHintColor(mailstateTIL, getColor(R.color.light_gray));
                        mailstateErrorLL.setVisibility(VISIBLE);
                        mailstateTV.setText("Field Required");
                    }
                } else {
                    mailstateET.setHint("State");
                    mailstateTIL.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
                    Utils.setUpperHintColor(mailstateTIL, getColor(R.color.primary_green));
                    mailstateErrorLL.setVisibility(GONE);
                }
            }
        });

        mailzipcodeET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    mailzipcodeET.setHint("");
                    if (mailzipcodeET.getText().toString().trim().length() > 0 && mailzipcodeET.getText().toString().trim().length() == 5) {
                        mailzipcodeTIL.setVisibility(GONE);
                        mailzipcodeTIL.setBoxStrokeColorStateList(Utils.getNormalColorState());
                        Utils.setUpperHintColor(mailzipcodeTIL, getColor(R.color.primary_black));

                    } else if (mailzipcodeET.getText().toString().trim().length() >= 1 && mailzipcodeET.getText().toString().trim().length() <= 4) {
                        mailzipcodeTIL.setBoxStrokeColorStateList(Utils.getErrorColorState());
                        Utils.setUpperHintColor(mailzipcodeTIL, getColor(R.color.error_red));
                        mailzipErrorLL.setVisibility(VISIBLE);
                        mailzipTV.setText("Minimum 5 Digits Required");
                    } else {
                        mailzipcodeTIL.setBoxStrokeColorStateList(Utils.getErrorColorState());
                        Utils.setUpperHintColor(mailzipcodeTIL, getColor(R.color.light_gray));
                        mailzipErrorLL.setVisibility(VISIBLE);
                        mailzipTV.setText("Field Required");
                    }
                } else {
                    mailzipcodeET.setHint("Zipcode");
                    mailzipcodeTIL.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
                    Utils.setUpperHintColor(mailzipcodeTIL, getColor(R.color.primary_green));
                    mailzipErrorLL.setVisibility(GONE);
                }
            }
        });
    }

    private void textWatchers() {

        mailadressET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() > 2) {
                    isMailAdress = true;
                    mailadressTIL.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
                    Utils.setUpperHintColor(mailadressTIL, getResources().getColor(R.color.primary_green));
                } else {
                    mailAdressTV.setText("Field Required");
                    isMailAdress = false;
                }
                enableOrDisableNext();
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        mailcityET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() > 0) {
                    isCity = true;
                    mailcityTIL.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
                    Utils.setUpperHintColor(mailcityTIL, getResources().getColor(R.color.primary_green));
                } else {
                    isCity = false;
                    mailcityErrorLL.setVisibility(VISIBLE);
                    mailcityTV.setText("Field Required");
                }
                enableOrDisableNext();
            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    String str = mailcityET.getText().toString();
                    if (str.length() > 0 && str.substring(0).equals(" ")) {
                        mailcityET.setText("");
                        mailcityET.setSelection(mailcityET.getText().length());
                    } else if (str.length() > 0 && str.contains(".")) {
                        mailcityET.setText(mailcityET.getText().toString());
                        mailcityET.setSelection(mailcityET.getText().length());
                    }

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        mailstateET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() > 0) {
                    isState = true;
                } else {
                    isState = false;
                }
                enableOrDisableNext();
            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
        mailzipcodeET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() > 2 && charSequence.toString().trim().length() == 5) {
                    iszipcode = true;
                    mailzipErrorLL.setVisibility(GONE);
                    mailzipcodeTIL.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
                    Utils.setUpperHintColor(mailzipcodeTIL, getResources().getColor(R.color.primary_green));
                } else {
                    iszipcode = false;
                }
                enableOrDisableNext();
            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    private void enableOrDisableNext() {
        try {
            if (isMailAdress && isCity && isState && iszipcode) {
                isNextEnabled = true;
                doneCV.setCardBackgroundColor(getResources().getColor(R.color.primary_color));

                Log.e("All boolean", isMailAdress + " " + isCity + " " + isState + " " + iszipcode);
            } else {

                Log.e("All boolean", isMailAdress + " " + isCity + " " + isState + " " + iszipcode);

                isNextEnabled = false;
                doneCV.setCardBackgroundColor(getResources().getColor(R.color.inactive_color));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        if (isNextEnabled == true) {
            doneCV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(BenificialMaillingAddressActivity.this, SignupMaillingAddresActivity.class);
                    startActivity(intent);
                }
            });
        }
    }
}