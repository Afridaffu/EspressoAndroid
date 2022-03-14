package com.greenbox.coyni.view.business;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.greenbox.coyni.R;
import com.greenbox.coyni.model.team.PhoneNumberTeam;
import com.greenbox.coyni.model.team.TeamInfoAddModel;
import com.greenbox.coyni.model.team.TeamRequest;
import com.greenbox.coyni.utils.Utils;
import com.greenbox.coyni.utils.outline_et.OutLineBoxPhoneNumberEditText;
import com.greenbox.coyni.view.BaseActivity;
import com.greenbox.coyni.viewmodel.TeamViewModel;

public class AddNewTeamMemberActivity extends BaseActivity {
    private TextInputLayout editFNameTil, editLNameTil, editEmailTil, editPhoneTil;
    private TextInputEditText editFNameET, editLNameET, editEmailET;
    private OutLineBoxPhoneNumberEditText phoneNumberET;
    private LinearLayout editFNameLL, editLNameLL, editEmailLL, editPhoneLL;
    private TextView editFNameTV, editLNameTV, editEmailTV, editPhoneTV;
    private static int focusedID = 0;
    private CardView sendCV;
    private boolean isFirstName = false, isLastName = false, isEmail = false, isPhoneNumber = false, isNextEnabled = false;
    private String firstName = "", lastName = "", role = "", status = "", emailAddress = "", phoneNumber = "", imageName = "";
    private TeamViewModel teamViewModel;
    private LinearLayout backBtnLL;
    private int roleId = 19;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_team_member);
        initFields();
        initObservers();
        focusWatchers();
        textWatchers();
    }

    private void initObservers() {
        teamViewModel.getTeamAddMutableLiveData().observe(this, new Observer<TeamInfoAddModel>() {
            @Override
            public void onChanged(TeamInfoAddModel teamInfoAddModel) {
                dismissDialog();
                try {
                    if (teamInfoAddModel != null) {
                        if (teamInfoAddModel.getStatus().equalsIgnoreCase("SUCCESS")) {
                            Utils.showCustomToast(AddNewTeamMemberActivity.this, getResources().getString(R.string.invitation_sent), R.drawable.ic_custom_tick, "PHONE");
                            new Handler().postDelayed(() -> {
                                try {
                                    finish();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            }, 2000);
                        } else {
                            Utils.displayAlert(teamInfoAddModel.getError().getErrorDescription(), AddNewTeamMemberActivity.this, "", teamInfoAddModel.getError().getFieldErrors().get(0));
                        }
                    } else {
                        Toast.makeText(AddNewTeamMemberActivity.this, "Something Went Wrong", Toast.LENGTH_SHORT).show();

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    private void initFields() {

        backBtnLL = findViewById(R.id.backBtnLL);
        backBtnLL.setOnClickListener(v -> onBackPressed());
        editFNameTil = findViewById(R.id.fNameTIL);
        editLNameTil = findViewById(R.id.lNameTIL);
        editEmailTil = findViewById(R.id.emailIdTIL);

        editFNameET = findViewById(R.id.editFNameET);
        editLNameET = findViewById(R.id.lNameET);
        editEmailET = findViewById(R.id.emailIdET);
        phoneNumberET = findViewById(R.id.phoneNoET);

        editFNameLL = findViewById(R.id.fNameLL);
        editLNameLL = findViewById(R.id.lNameLL);
        editEmailLL = findViewById(R.id.emailIdLL);
        editPhoneLL = findViewById(R.id.phoneNoLL);

        editFNameTV = findViewById(R.id.fNameTV);
        editLNameTV = findViewById(R.id.lNameTV);
        editEmailTV = findViewById(R.id.emailIdTV);
        editPhoneTV = findViewById(R.id.phoneNoTV);


        sendCV = findViewById(R.id.cvSend);
        teamViewModel = new ViewModelProvider(this).get(TeamViewModel.class);
        sendCV.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View v) {
                showProgressDialog();
                //  phoneNumber = phoneNumberET.getText().toString().substring(1, 4) + phoneNumberET.getText().toString().substring(6, 9) + phoneNumberET.getText().toString().substring(10, phoneNumberET.getText().length());
                teamInfoAddAPICall(prepareRequest());
            }
        });


    }

    public TeamRequest prepareRequest() {
        TeamRequest teamRequest = new TeamRequest();
        try {
            firstName = editFNameET.getText().toString().trim();
            phoneNumber = phoneNumberET.getText().toString().trim();
            lastName = editLNameET.getText().toString().trim();
            emailAddress = editEmailET.getText().toString().trim();
            phoneNumberET.getText().toString().trim();
            PhoneNumberTeam phone = new PhoneNumberTeam();
            phone.setCountryCode(Utils.strCCode);
            phone.setPhoneNumber(phoneNumber);
            teamRequest.setPhoneNumber(phone);
            teamRequest.setFirstName(firstName);
            teamRequest.setLastName(lastName);
            teamRequest.setEmailAddress(emailAddress);
            teamRequest.setRoleId(roleId);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return teamRequest;
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
                            Utils.shwForcedKeypad(AddNewTeamMemberActivity.this);
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
                            Utils.shwForcedKeypad(AddNewTeamMemberActivity.this);
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
                            editEmailTV.setText("Please enter a valid Email");
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
                            Utils.shwForcedKeypad(AddNewTeamMemberActivity.this);
                        focusedID = editEmailET.getId();
                        editEmailET.setHint("Email");
                        editEmailTil.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
                        Utils.setUpperHintColor(editEmailTil, getColor(R.color.primary_green));
                    }
                }
            });

        } catch (Exception e) {
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

    public void teamInfoAddAPICall(TeamRequest teamRequest) {
        teamViewModel.addTeam(teamRequest);
    }

    public void addTeam() {
        teamInfoAddAPICall(prepareRequest());
    }

    @Override
    protected void onResume() {
        super.onResume();
        addTeam();
    }

    private void enableOrDisableNext() {
        try {
            if (isFirstName && isLastName && isEmail) {
                isNextEnabled = true;
                sendCV.setCardBackgroundColor(getResources().getColor(R.color.primary_color));

                Log.e("All boolean", isFirstName + " " + isLastName + " " + isEmail + " ");
            } else {

                Log.e("All boolean", isFirstName + " " + isLastName + " " + isEmail + " " + isPhoneNumber + " ");

                isNextEnabled = false;
                sendCV.setCardBackgroundColor(getResources().getColor(R.color.inactive_color));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}