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
import com.greenbox.coyni.model.DBAInfo.DBAInfoRequest;
import com.greenbox.coyni.model.register.PhNoWithCountryCode;
import com.greenbox.coyni.model.team.PhoneNumberTeam;
import com.greenbox.coyni.model.team.TeamRequest;
import com.greenbox.coyni.utils.Utils;
import com.greenbox.coyni.utils.outline_et.OutLineBoxPhoneNumberEditText;
import com.greenbox.coyni.viewmodel.TeamViewModel;

public class EditTeamMember extends AppCompatActivity {
    TextInputLayout editFNameTil,editLNameTil,editEmailTil,editPhoneTil;
    TextInputEditText editFNameET,editLNameET,editEmailET;
    OutLineBoxPhoneNumberEditText editPhoneET;
    LinearLayout editFNameLL,editLNameLL,editEmailLL,editPhoneLL;
    TextView editFNameTV,editLNameTV,editEmailTV,editPhoneTV;
    public static int focusedID = 0;
    public CardView sendCV;
    public boolean isFirstName = false, isLastName = false, isEmail = false, isPhoneNumber = false,isNextEnabled=false;
    String firstName="",lastName="",role="",status="",emailAddress="",phoneNumber="",imageName="";
    TeamViewModel teamViewModel;
    int roleId=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_team_member);
        Bundle bundle=getIntent().getExtras();
        firstName= bundle.getString("TeamMemberFirstName",firstName);
        lastName=bundle.getString("TeamMemberLastName",lastName);
        role=bundle.getString("Role",role);
        status=bundle.getString("Status",status);
        emailAddress=bundle.getString("EmailAddress",emailAddress);
        phoneNumber=bundle.getString("PhoneNumber",phoneNumber);
        imageName=bundle.getString("ImageName",imageName);
        roleId=bundle.getInt("RoleId",roleId);
        initFields();
        focusWatchers();
        textWatchers();

    }

    private void initFields() {
        editFNameTil = findViewById(R.id.edit_fName_til);
        editLNameTil = findViewById(R.id.edit_lName_til);
        editEmailTil = findViewById(R.id.edit_email_til);
        //editPhoneTil = findViewById(R.id.phoneNumberOET);

        editFNameET = findViewById(R.id.editFNameET);
        editLNameET = findViewById(R.id.editLNameET);
        editEmailET = findViewById(R.id.editEmailET);
        editPhoneET=findViewById(R.id.phoneNumberOET);

        editFNameLL = findViewById(R.id.editfirstNameErrorLL);
        editLNameLL = findViewById(R.id.editlastNameErrorLL);
        editEmailLL = findViewById(R.id.editemailErrorLL);
        editPhoneLL = findViewById(R.id.editphoneErrorLL);

        editFNameTV = findViewById(R.id.editfirstNameErrorTV);
        editLNameTV = findViewById(R.id.editlastNameErrorTV);
        editEmailTV = findViewById(R.id.editemailErrorTV);
        editPhoneTV = findViewById(R.id.editphoneErrorTV);

        editFNameET.setText(firstName);
        editLNameET.setText(lastName);
        editEmailET.setText(emailAddress);
        editPhoneET.setText(phoneNumber);

        sendCV = findViewById(R.id.sendCV);
        sendCV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                teamInfoAPICall(prepareRequest());
            }
        });


    }
    private void focusWatchers() {
        try {

            editFNameET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    if (!b) {
                        editFNameET.setHint("");
                        if (editFNameET.getText().toString().trim().length() > 1) {
                            editFNameLL.setVisibility(GONE);
                            editFNameTil.setBoxStrokeColorStateList(Utils.getNormalColorState(getApplicationContext()));
                            Utils.setUpperHintColor(editFNameTil, getColor(R.color.primary_black));

                        } else if (editFNameET.getText().toString().trim().length() == 1) {
                            editFNameTil.setBoxStrokeColorStateList(Utils.getErrorColorState(getApplicationContext()));
                            Utils.setUpperHintColor(editFNameTil, getColor(R.color.error_red));
                            editFNameLL.setVisibility(VISIBLE);
                            editFNameTV.setText("Minimum 2 Characters Required");
                        } else {
                            editFNameTil.setBoxStrokeColorStateList(Utils.getErrorColorState(getApplicationContext()));
                            Utils.setUpperHintColor(editFNameTil, getColor(R.color.light_gray));
                            editFNameLL.setVisibility(VISIBLE);
                            editFNameTV.setText("Field Required");
                        }
                    } else {
                        if (!Utils.isKeyboardVisible)
                            Utils.shwForcedKeypad(EditTeamMember.this);
                        focusedID = editFNameET.getId();
                        editFNameET.setHint("First Name");
                        editFNameTil.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
                        Utils.setUpperHintColor(editFNameTil, getColor(R.color.primary_green));
                    }
                }
            });

            editLNameET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    if (!b) {
                        editLNameET.setHint("");
                        if (editLNameET.getText().toString().trim().length() > 1) {
                            editLNameLL.setVisibility(GONE);
                            editLNameTil.setBoxStrokeColorStateList(Utils.getNormalColorState(getApplicationContext()));
                            Utils.setUpperHintColor(editLNameTil, getColor(R.color.primary_black));

                        } else if (editLNameET.getText().toString().trim().length() == 1) {
                            editLNameTil.setBoxStrokeColorStateList(Utils.getErrorColorState(getApplicationContext()));
                            Utils.setUpperHintColor(editLNameTil, getColor(R.color.error_red));
                            editLNameLL.setVisibility(VISIBLE);
                            editLNameTV.setText("Minimum 2 Characters Required");
                        } else {
                            editLNameTil.setBoxStrokeColorStateList(Utils.getErrorColorState(getApplicationContext()));
                            Utils.setUpperHintColor(editLNameTil, getColor(R.color.light_gray));
                            editLNameLL.setVisibility(VISIBLE);
                            editLNameTV.setText("Field Required");
                        }
                    } else {
                        if (!Utils.isKeyboardVisible)
                            Utils.shwForcedKeypad(EditTeamMember.this);
                        focusedID = editLNameET.getId();
                        editLNameET.setHint("Last Name");
                        editLNameTil.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
                        Utils.setUpperHintColor(editLNameTil, getColor(R.color.primary_green));
                    }
                }
            });

            editEmailET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    if (!b) {
                        editEmailET.setHint("");
                        if (editEmailET.getText().toString().trim().length() > 5 && !Utils.isValidEmail(editEmailET.getText().toString().trim())) {
                            editEmailTil.setBoxStrokeColorStateList(Utils.getErrorColorState(getApplicationContext()));
                            Utils.setUpperHintColor(editEmailTil, getColor(R.color.error_red));
                            editEmailLL.setVisibility(VISIBLE);
                            editEmailTV.setText("Please Enter a valid Email");
                        } else if (editEmailET.getText().toString().trim().length() > 5 && Utils.isValidEmail(editEmailET.getText().toString().trim())) {
                            editEmailTil.setBoxStrokeColorStateList(Utils.getNormalColorState(getApplicationContext()));
                            Utils.setUpperHintColor(editEmailTil, getColor(R.color.primary_black));
                            editEmailLL.setVisibility(GONE);
                        } else if (editEmailET.getText().toString().trim().length() > 0 && editEmailET.getText().toString().trim().length() <= 5) {
                            editEmailTil.setBoxStrokeColorStateList(Utils.getErrorColorState(getApplicationContext()));
                            Utils.setUpperHintColor(editEmailTil, getColor(R.color.error_red));
                            editEmailLL.setVisibility(VISIBLE);
                            editEmailTV.setText("Field Required");
                        } else {
                            editEmailTil.setBoxStrokeColorStateList(Utils.getErrorColorState(getApplicationContext()));
                            Utils.setUpperHintColor(editEmailTil, getColor(R.color.light_gray));
                            editEmailLL.setVisibility(VISIBLE);
                            editEmailTV.setText("Field Required");
                        }
                    } else {
                        if (!Utils.isKeyboardVisible)
                            Utils.shwForcedKeypad(EditTeamMember.this);
                        focusedID = editEmailET.getId();
                        editEmailET.setHint("Email");
                        editEmailTil.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
                        Utils.setUpperHintColor(editEmailTil, getColor(R.color.primary_green));
                    }
                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }
    }
    private void textWatchers() {
        editFNameET.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() > 1 && charSequence.toString().trim().length() < 31) {
                    isFirstName = true;
                    editFNameLL.setVisibility(GONE);
                    editFNameTil.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
                    Utils.setUpperHintColor(editFNameTil, getResources().getColor(R.color.primary_green));
                } else if (editFNameET.getText().toString().trim().length() == 0) {
                    editFNameLL.setVisibility(VISIBLE);
                    editFNameTV.setText("Field Required");
                } else {
                    isFirstName = false;
                }
                enableOrDisableNext();
            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    String str = editFNameET.getText().toString();
                    if (str.length() > 0 && str.substring(0, 1).equals(" ")) {
                        editFNameET.setText("");
                        editFNameET.setSelection(editFNameET.getText().length());
                    } else if (str.length() > 0 && str.contains(".")) {
                        editFNameET.setText(editFNameET.getText().toString().replaceAll("\\.", ""));
                        editFNameET.setSelection(editFNameET.getText().length());
                    } else if (str.length() > 0 && str.contains("http") || str.length() > 0 && str.contains("https")) {
                        editFNameET.setText("");
                        editFNameET.setSelection(editFNameET.getText().length());
                    }

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        editLNameET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (charSequence.toString().trim().length() > 1 && charSequence.toString().trim().length() < 31) {
                    isLastName = true;
                    editLNameLL.setVisibility(GONE);
                    editLNameTil.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
                    Utils.setUpperHintColor(editLNameTil, getResources().getColor(R.color.primary_green));
                } else if (editLNameET.getText().toString().trim().length() == 0) {
                    editLNameLL.setVisibility(VISIBLE);
                    editLNameTV.setText("Field Required");
                } else {
                    isLastName = false;
                }
                enableOrDisableNext();
            }

            @Override
            public void afterTextChanged(Editable editable) {
                try {
                    String str = editLNameET.getText().toString();
                    if (str.length() > 0 && str.substring(0).equals(" ")) {
                        editLNameET.setText(editLNameET.getText().toString().replaceAll(" ", ""));
                        editLNameET.setSelection(editLNameET.getText().length());
                    } else if (str.length() > 0 && str.substring(str.length() - 1).equals(".")) {
                        editLNameET.setText(editLNameET.getText().toString().replaceAll(".", ""));
                        editLNameET.setSelection(editLNameET.getText().length());
                    }

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        editEmailET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (charSequence.length() > 5 && Utils.isValidEmail(charSequence.toString().trim())) {
                    isEmail = false;
                    editEmailLL.setVisibility(GONE);
                    editEmailTil.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
//                        emailTIL.setHintTextColor(colorState);
                    Utils.setUpperHintColor(editEmailTil, getResources().getColor(R.color.primary_green));

                } else if (editEmailET.getText().toString().trim().length() == 0) {
                    editEmailLL.setVisibility(VISIBLE);
                    editEmailTV.setText("Field Required");
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
                    String str = editEmailET.getText().toString();
                    if (str.length() > 0 && str.substring(0).equals(" ") || (str.length() > 0 && str.contains(" "))) {
                        editEmailET.setText(editEmailET.getText().toString().replaceAll(" ", ""));
                        editEmailET.setSelection(editEmailET.getText().length());
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
                sendCV.setCardBackgroundColor(getResources().getColor(R.color.primary_color));

                Log.e("All boolean", isFirstName + " " + isLastName + " " + isEmail + " " );
            } else {

                Log.e("All boolean", isFirstName + " " + isLastName + " " + isEmail + " " + isPhoneNumber + " ");

                isNextEnabled = false;
                sendCV.setCardBackgroundColor(getResources().getColor(R.color.inactive_color));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    public void teamInfoAPICall(TeamRequest teamRequest) {
        teamViewModel.updateTeamInfo(teamRequest,roleId);
    }
    public TeamRequest prepareRequest() {
        TeamRequest teamRequest = new TeamRequest();
        try {
                PhoneNumberTeam phone = new PhoneNumberTeam();
                phone.setCountryCode(Utils.strCCode);
                phone.setPhoneNumber(editPhoneET.getText().toString());
                teamRequest.setFirstName(editFNameET.getText().toString());
                teamRequest.setFirstName(editLNameET.getText().toString());
                teamRequest.setFirstName(editEmailET.getText().toString());
                teamRequest.setFirstName(editPhoneET.getText().toString());

        } catch (Exception e) {
            e.printStackTrace();
        }

        return teamRequest;
    }

}