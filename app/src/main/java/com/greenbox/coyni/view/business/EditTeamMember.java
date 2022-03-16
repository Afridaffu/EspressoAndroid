package com.greenbox.coyni.view.business;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.greenbox.coyni.R;
import com.greenbox.coyni.interfaces.OnKeyboardVisibilityListener;
import com.greenbox.coyni.model.team.PhoneNumberTeam;
import com.greenbox.coyni.model.team.TeamInfoAddModel;
import com.greenbox.coyni.model.team.TeamRequest;
import com.greenbox.coyni.utils.Utils;
import com.greenbox.coyni.utils.outline_et.OutLineBoxPhoneNumberEditText;
import com.greenbox.coyni.view.BaseActivity;
import com.greenbox.coyni.viewmodel.TeamViewModel;

public class EditTeamMember extends BaseActivity {
    private TextInputLayout editFNameTil, editLNameTil, editEmailTil, editPhoneTil;
    private TextInputEditText editFNameET, editLNameET, editEmailET;
    private OutLineBoxPhoneNumberEditText editPhoneET;
    private LinearLayout editFNameLL, editLNameLL, editEmailLL;
    public LinearLayout editPhonLL;
    private TextView editFNameTV, editLNameTV, editEmailTV;
    public TextView editPhoneTV;
    public static int focusedID = 0;
    public CardView sendCV;
    private Long mLastClickTime = 0L;
    public boolean isFirstName = false, isLastName = false, isEmail = false, isPhoneNumber = false, isNextEnabled = false;
    private String firstName = "", lastName = "", role = "", status = "", emailAddress = "", phoneNumber = "", imageName = "";
    private TeamViewModel teamViewModel;
    private int teamMemberId = 0;
    private ImageView backBtn;
    public static EditTeamMember editTeamMember;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_team_member);

        Bundle bundle = getIntent().getExtras();
        firstName = bundle.getString(Utils.teamFirstName, firstName);
        lastName = bundle.getString(Utils.teamLastName, lastName);
        role = bundle.getString(Utils.teamRoleName, role);
        status = bundle.getString(Utils.teamStatus, status);
        emailAddress = bundle.getString(Utils.teamEmailAddress, emailAddress);
        phoneNumber = bundle.getString(Utils.teamPhoneNumber, phoneNumber);
        imageName = bundle.getString(Utils.teamImageName, imageName);
        teamMemberId = bundle.getInt(Utils.teamMemberId, teamMemberId);
        initFields();
        initObservers();
        focusWatchers();
        textWatchers();

    }

    private void initFields() {
        editTeamMember = this;
        backBtn = findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        editFNameTil = findViewById(R.id.edit_fName_til);
        editLNameTil = findViewById(R.id.edit_lName_til);
        editEmailTil = findViewById(R.id.edit_email_til);
        //editPhoneTil = findViewById(R.id.phoneNumberOET);

        editFNameET = findViewById(R.id.editFNameET);
        editLNameET = findViewById(R.id.editLNameET);
        editEmailET = findViewById(R.id.editEmailET);
        editPhoneET = findViewById(R.id.phoneNumberOET);
        editPhoneET.setFrom("EDIT_TEAM_MEMBER");

        editFNameLL = findViewById(R.id.editfirstNameErrorLL);
        editLNameLL = findViewById(R.id.editlastNameErrorLL);
        editEmailLL = findViewById(R.id.editemailErrorLL);
        editPhonLL = findViewById(R.id.editphoneErrorLL);

        editFNameTV = findViewById(R.id.editfirstNameErrorTV);
        editLNameTV = findViewById(R.id.editlastNameErrorTV);
        editEmailTV = findViewById(R.id.editemailErrorTV);
        editPhoneTV = findViewById(R.id.editphoneErrorTV);

        editFNameTil.setBoxStrokeColorStateList(Utils.getNormalColorState(getApplicationContext()));
        editLNameTil.setBoxStrokeColorStateList(Utils.getNormalColorState(getApplicationContext()));
        editEmailTil.setBoxStrokeColorStateList(Utils.getNormalColorState(getApplicationContext()));

        editFNameET.setText(firstName);
        editLNameET.setText(lastName);
        editEmailET.setText(emailAddress);
        editPhoneET.setText(phoneNumber);
        teamViewModel = new ViewModelProvider(this).get(TeamViewModel.class);

        sendCV = findViewById(R.id.sendCV);
        sendCV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isNextEnabled==true) {
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                        return;
                    }
                    mLastClickTime = SystemClock.elapsedRealtime();
                    showProgressDialog();
                    teamInfoAPICall(prepareRequest());                }

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
                        if (editFNameET.getText().toString().length() > 0 && !editFNameET.getText().toString().substring(0, 1).equals(" ")) {
                            editFNameET.setText(editFNameET.getText().toString().substring(0, 1).toUpperCase() + editFNameET.getText().toString().substring(1));
                        }
                    } else {
                        editFNameLL.setVisibility(GONE);
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
                        if (editLNameET.getText().toString().length() > 0 && !editLNameET.getText().toString().substring(0, 1).equals(" ")) {
                            editLNameET.setText(editLNameET.getText().toString().substring(0, 1).toUpperCase() + editLNameET.getText().toString().substring(1));
                        }
                    } else {
                        focusedID = editLNameET.getId();
                        editLNameLL.setVisibility(GONE);
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
                    Utils.setUpperHintColor(editFNameTil, getResources().getColor(R.color.primary_black));
                } else {
                    isFirstName = false;
                }
                enableOrDisableNext();
            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    String str = editFNameET.getText().toString();
                    if (str.length() > 0 && str.toString().trim().length() == 0) {
                        editFNameET.setText("");
                        editFNameET.setSelection(editFNameET.getText().length());
                    } else if (str.length() > 0 && String.valueOf(str.charAt(0)).equals(" ")) {
                        editFNameET.setText(str.trim());
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
                    Utils.setUpperHintColor(editLNameTil, getResources().getColor(R.color.primary_black));
                } else {
                    isLastName = false;
                }
                enableOrDisableNext();
            }

            @Override
            public void afterTextChanged(Editable editable) {
                try {
                    String str = editLNameET.getText().toString();
                    if (str.length() > 0 && str.toString().trim().length() == 0) {
                        editLNameET.setText("");
                        editLNameET.setSelection(editLNameET.getText().length());
                    } else if (str.length() > 0 && String.valueOf(str.charAt(0)).equals(" ")) {
                        editLNameET.setText(str.trim());
                    } else if (str.length() > 0 && str.contains(".")) {
                        editLNameET.setText(editLNameET.getText().toString().replaceAll("\\.", ""));
                        editLNameET.setSelection(editLNameET.getText().length());
                    } else if (str.length() > 0 && str.contains("http") || str.length() > 0 && str.contains("https")) {
                        editLNameET.setText("");
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
                    Utils.setUpperHintColor(editEmailTil, getResources().getColor(R.color.primary_black));

                } else if (editEmailET.getText().toString().trim().length() == 0) {
//                    editEmailLL.setVisibility(VISIBLE);
//                    editEmailTV.setText("Field Required");
                    isEmail = false;
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

    public void enableOrDisableNext() {
        try {
            if (isFirstName && isLastName && isEmail && isPhoneNumber) {
                isNextEnabled = true;
                sendCV.setCardBackgroundColor(getResources().getColor(R.color.primary_color));

                Log.e("All boolean", isFirstName + " " + isLastName + " " + isEmail + " " + isPhoneNumber + " ");
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
        teamViewModel.updateTeamInfo(teamRequest, teamMemberId);
    }

    public TeamRequest prepareRequest() {
        TeamRequest teamRequest = new TeamRequest();
        try {
            String emailAddress = editEmailET.getText().toString();
            String phoneNumber = editPhoneET.getText().toString();
            PhoneNumberTeam phone = new PhoneNumberTeam();
            phone.setCountryCode(Utils.strCCode);
            phone.setPhoneNumber(phoneNumber);
            teamRequest.setEmailAddress(emailAddress);
            teamRequest.setRoleId(19);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return teamRequest;
    }
    private void setKeyboardVisibilityListener(final OnKeyboardVisibilityListener onKeyboardVisibilityListener) {
        final View parentView = ((ViewGroup) findViewById(android.R.id.content)).getChildAt(0);
        parentView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            private boolean alreadyOpen;
            private final int defaultKeyboardHeightDP = 100;
            private final int EstimatedKeyboardDP = defaultKeyboardHeightDP + (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ? 48 : 0);
            private final Rect rect = new Rect();

            @Override
            public void onGlobalLayout() {
                int estimatedKeyboardHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, EstimatedKeyboardDP, parentView.getResources().getDisplayMetrics());
                parentView.getWindowVisibleDisplayFrame(rect);
                int heightDiff = parentView.getRootView().getHeight() - (rect.bottom - rect.top);
                boolean isShown = heightDiff >= estimatedKeyboardHeight;

                if (isShown == alreadyOpen) {
                    Log.i("Keyboard state", "Ignoring global layout change...");
                    return;
                }
                alreadyOpen = isShown;
                onKeyboardVisibilityListener.onVisibilityChanged(isShown);
            }
        });
    }

    private void initObservers() {
        teamViewModel.getTeamUpdateMutableLiveData().observe(this, new Observer<TeamInfoAddModel>() {
            @Override
            public void onChanged(TeamInfoAddModel teamInfoAddModel) {
                dismissDialog();
                try {
                    if (teamInfoAddModel != null) {
                        if (teamInfoAddModel.getStatus().equalsIgnoreCase("SUCCESS")) {
                            Utils.showCustomToast(EditTeamMember.this, getResources().getString(R.string.invitation_sent), R.drawable.ic_custom_tick, "PHONE");

                        } else {
                            Utils.displayAlert(teamInfoAddModel.getError().getErrorDescription(), EditTeamMember.this, "", teamInfoAddModel.getError().getFieldErrors().get(0));
                        }
                    } else {
                        Toast.makeText(EditTeamMember.this, "Something Went Wrong", Toast.LENGTH_SHORT).show();

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }
    @Override
    protected void onResume() {
        super.onResume();
        editFNameET.requestFocus();
        Utils.shwForcedKeypad(EditTeamMember.this);
    }


}