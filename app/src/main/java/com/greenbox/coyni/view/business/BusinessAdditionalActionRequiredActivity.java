package com.greenbox.coyni.view.business;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.textfield.TextInputLayout;
import com.greenbox.coyni.R;
import com.greenbox.coyni.custom_camera.CameraActivity;
import com.greenbox.coyni.model.APIError;
import com.greenbox.coyni.model.bank.BankDeleteResponseData;
import com.greenbox.coyni.model.bank.SignOn;
import com.greenbox.coyni.model.bank.SyncAccount;
import com.greenbox.coyni.model.cards.CardDeleteResponse;
import com.greenbox.coyni.model.paymentmethods.PaymentMethodsResponse;
import com.greenbox.coyni.model.underwriting.ActionRequiredDataResponse;
import com.greenbox.coyni.model.underwriting.ActionRequiredResponse;
import com.greenbox.coyni.model.underwriting.AdditionalDocumentData;
import com.greenbox.coyni.utils.FileUtils;
import com.greenbox.coyni.utils.LogUtils;
import com.greenbox.coyni.utils.Utils;
import com.greenbox.coyni.view.BaseActivity;
import com.greenbox.coyni.view.WithdrawPaymentMethodsActivity;
import com.greenbox.coyni.viewmodel.CustomerProfileViewModel;
import com.greenbox.coyni.viewmodel.UnderwritingUserActionRequired;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class BusinessAdditionalActionRequiredActivity extends BaseActivity {
    private static Object ActivityCompat;
    LinearLayout additionalDocumentRequiredLL, websiteRevisionRequiredLL, informationRevisionLL, actionReqFileUploadedLL, sscFileUploadLL, actionReqFileUploadLL, businessLicenseUploadLL, lincenseFileUploadedLL, sscfileUploadedLL, acceptLL, declineLL, acceptDeclineLL, acceptdneLL, declindneLL;
    TextView fileUploadTV, actionReqFileTV, licenseTV, fileUploadedTV, remarksTV, acceptMsgTV, declineMsgTV, compnyNameTV, actionReqFileUpdatedOnTV, licenseUploadedTV;
    String selectedDocType = "", from = "";
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 102;
    private static final int ACTIVITY_CHOOSE_FILE = 3;
    private static final int PICK_IMAGE_REQUEST = 4;
    Long mLastClickTime = 0L;
    public int docTypeID = 0;
    public static BusinessAdditionalActionRequiredActivity businessAdditionalActionRequired;
    public static File adtionalSscFile = null, addtional2fFle = null, businessLincenseFile = null;
    public boolean issscFileUploadLL = false, isactionReqFileUploadLL = false, isbusinessLicenseUploadLL = false, ischeckbox2CB = false, ischeckbox3CB = false, ischeckboxCB = false, isSubmitEnabled = false;
    private EditText addNoteET;
    public CheckBox checkboxCB, checkbox2CB, checkbox3CB;
    public CardView submitCV;
    private UnderwritingUserActionRequired underwritingUserActionRequired;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_additional_action_required);

        businessAdditionalActionRequired = this;
        initFields();
        initObserver();

    }


    private void initFields() {

        sscFileUploadLL = findViewById(R.id.sscFileUploadLL);
//        actionReqFileUploadLL = findViewById(R.id.actionReqFileUploadLL);
//        businessLicenseUploadLL = findViewById(R.id.businessLicenseUploadLL);

        additionalDocumentRequiredLL = findViewById(R.id.ll_document_required);
        websiteRevisionRequiredLL = findViewById(R.id.website_revision_required);
        informationRevisionLL = findViewById(R.id.information_revision);

        fileUploadTV = findViewById(R.id.sscuploadFileTV);
//        actionReqFileTV = findViewById(R.id.actionReqFileTV);
//        licenseTV = findViewById(R.id.licenseTV);

        sscfileUploadedLL = findViewById(R.id.sscfileUploadedLL);
//        actionReqFileUploadedLL = findViewById(R.id.actionReqFileUploadedLL);
//        lincenseFileUploadedLL = findViewById(R.id.lincenseFileUploadedLL);

        fileUploadedTV = findViewById(R.id.sscfileUpdatedOnTV);
       // actionReqFileUpdatedOnTV = findViewById(R.id.actionReqFileUpdatedOnTV);

        checkboxCB = findViewById(R.id.checkboxCB);
        checkbox2CB = findViewById(R.id.checkbox2CB);
        checkbox3CB = findViewById(R.id.checkbox3CB);
        submitCV = findViewById(R.id.submitCV);

        declindneLL = findViewById(R.id.declineDneLL);
        declineLL = findViewById(R.id.declineLL);
        acceptLL = findViewById(R.id.acceptLL);
        remarksTV = findViewById(R.id.remarksTV);
        acceptDeclineLL = findViewById(R.id.acceptDeclineLL);
        acceptMsgTV = findViewById(R.id.acceptMsgTV);
        declineMsgTV = findViewById(R.id.declineMsgTV);
        acceptdneLL = findViewById(R.id.acceptDneLL);
        compnyNameTV = findViewById(R.id.comapny_nameTV);

        underwritingUserActionRequired = new ViewModelProvider(this).get(UnderwritingUserActionRequired.class);

        underwritingUserActionRequired.postactionRequired();

        declineLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                    return;

                }
                mLastClickTime = SystemClock.elapsedRealtime();
                displayComments();

            }

        });
        acceptLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                acceptDeclineLL.setVisibility(View.GONE);
                compnyNameTV.setVisibility(View.GONE);
                acceptdneLL.setVisibility(View.VISIBLE);
                acceptMsgTV.setVisibility(View.VISIBLE);

            }

        });
    }

    private void initObserver() {
        underwritingUserActionRequired.getUserAccountLimitsMutableLiveData().observe(this,
                new Observer<ActionRequiredResponse>() {
                    @Override
                    public void onChanged(ActionRequiredResponse actionRequiredDataResponse) {
                        try {
                            LogUtils.d(TAG, "ActionRequiredDataResponse" + actionRequiredDataResponse);

                            if (actionRequiredDataResponse != null && actionRequiredDataResponse.getData() != null) {
                                if (actionRequiredDataResponse.getData().getAdditionalDocument() != null &&
                                        actionRequiredDataResponse.getData().getAdditionalDocument().size() != 0) {
                                    additionalDocumentRequiredLL.setVisibility(View.VISIBLE);

                                    for(AdditionalDocumentData documents : actionRequiredDataResponse.getData().getAdditionalDocument()){

                                        TextView documentName = findViewById(R.id.tvdocumentName);
                                        LinearLayout dosumentFileUploadLL = findViewById(R.id.sscFileUploadLL);
                                        LinearLayout dosumentsLL = findViewById(R.id.documentLL);
                                        documentName.setText(documents.getDocumentName());

                                        dosumentsLL.addView(documentName);
                                        dosumentsLL.addView(dosumentFileUploadLL);

                                    }
                                } else if (actionRequiredDataResponse.getData().getWebsiteChange() != null
                                && actionRequiredDataResponse.getData().getWebsiteChange().size()!=0) {
                                    websiteRevisionRequiredLL.setVisibility(View.VISIBLE);
                                }else if (actionRequiredDataResponse.getData().getInformationChange() != null
                                        && actionRequiredDataResponse.getData().getInformationChange().size()!=0) {
                                    informationRevisionLL.setVisibility(View.VISIBLE);
                                }
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                });

    }

    private void displayComments() {
        try {
            Dialog cvvDialog = new Dialog(BusinessAdditionalActionRequiredActivity.this);
            cvvDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            cvvDialog.setContentView(R.layout.add_note_layout);
            cvvDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            DisplayMetrics mertics = getResources().getDisplayMetrics();
            int width = mertics.widthPixels;

            addNoteET = cvvDialog.findViewById(R.id.addNoteET);
            CardView doneBtn = cvvDialog.findViewById(R.id.doneBtn);
            TextInputLayout addNoteTIL = cvvDialog.findViewById(R.id.etlMessage);
            LinearLayout cancelBtn = cvvDialog.findViewById(R.id.cancelBtn);


            cancelBtn.setOnClickListener(new View.OnClickListener() {
                @Override


                public void onClick(View view) {
                    cvvDialog.dismiss();
                    Utils.hideKeypad(BusinessAdditionalActionRequiredActivity.this);

                }
            });
            doneBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        remarksTV.setText(addNoteET.getText().toString().trim());
                        cvvDialog.dismiss();
                        Utils.hideKeypad(BusinessAdditionalActionRequiredActivity.this);
                        remarksTV.setVisibility(View.VISIBLE);
                        acceptDeclineLL.setVisibility(View.GONE);
                        compnyNameTV.setVisibility(View.GONE);
                        declineMsgTV.setVisibility(View.VISIBLE);
                        declindneLL.setVisibility(View.VISIBLE);
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
                    } else {
                        addNoteTIL.setCounterEnabled(true);

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
            if (!remarksTV.getText().toString().trim().equals("")) {
                addNoteET.setText(remarksTV.getText().toString().trim());
                addNoteET.setSelection(addNoteET.getText().toString().trim().length());
            }
            Window window = cvvDialog.getWindow();
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
            WindowManager.LayoutParams wlp = window.getAttributes();
            wlp.gravity = Gravity.BOTTOM;
            wlp.flags &= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
            window.setAttributes(wlp);
            cvvDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
            cvvDialog.show();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static boolean checkAndRequestPermissions(final Activity context) {
        try {
            int WExtstorePermission = ContextCompat.checkSelfPermission(context,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
            int cameraPermission = ContextCompat.checkSelfPermission(context,
                    android.Manifest.permission.CAMERA);
            int internalStorage = ContextCompat.checkSelfPermission(context,
                    Manifest.permission.READ_EXTERNAL_STORAGE);
            List<String> listPermissionsNeeded = new ArrayList<>();
            if (cameraPermission != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(android.Manifest.permission.CAMERA);
            }
            if (WExtstorePermission != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded
                        .add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
//            if (internalStorage != PackageManager.PERMISSION_GRANTED) {
//                listPermissionsNeeded
//                        .add(android.Manifest.permission.READ_EXTERNAL_STORAGE);
//            }
            if (!listPermissionsNeeded.isEmpty()) {
                androidx.core.app.ActivityCompat.requestPermissions(context, listPermissionsNeeded
                                .toArray(new String[listPermissionsNeeded.size()]),
                        REQUEST_ID_MULTIPLE_PERMISSIONS);
                return false;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        try {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            switch (requestCode) {
                case REQUEST_ID_MULTIPLE_PERMISSIONS:
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        Utils.displayAlert("Requires Access to Camera.", BusinessAdditionalActionRequiredActivity.this, "", "");

                    } else if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        Utils.displayAlert("Requires Access to Your Storage.", BusinessAdditionalActionRequiredActivity.this, "", "");

                    } else if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        Utils.displayAlert("Requires Access to Your Storage.", BusinessAdditionalActionRequiredActivity.this, "", "");

                    } else {
                        chooseFilePopup(this, selectedDocType);
                        if (Utils.isKeyboardVisible)
                            Utils.hideKeypad(BusinessAdditionalActionRequiredActivity.this);
                    }
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void chooseFilePopup(final Context context, String type) {
        try {
            Dialog chooseFile = new Dialog(context);
            chooseFile.requestWindowFeature(Window.FEATURE_NO_TITLE);
            chooseFile.setContentView(R.layout.activity_choose_file_botm_sheet);
            chooseFile.setCancelable(true);
            Window window = chooseFile.getWindow();
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
            chooseFile.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            WindowManager.LayoutParams wlp = window.getAttributes();
            wlp.gravity = Gravity.BOTTOM;
            wlp.flags &= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
            window.setAttributes(wlp);
            chooseFile.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
            TextView libraryTV = chooseFile.findViewById(R.id.libraryTV);
            TextView takePhotoTV = chooseFile.findViewById(R.id.takePhotoTV);
            TextView browseFileTV = chooseFile.findViewById(R.id.browseFileTV);
            libraryTV.setOnClickListener(view -> {
                chooseFile.dismiss();
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, PICK_IMAGE_REQUEST);

            });
            takePhotoTV.setOnClickListener(view -> {
                chooseFile.dismiss();
                startActivity(new Intent(BusinessAdditionalActionRequiredActivity.this, CameraActivity.class).putExtra("FROM", type));
            });
            browseFileTV.setOnClickListener(view -> {
                chooseFile.dismiss();
                Intent pickIntent = new Intent();
                pickIntent.addCategory(Intent.CATEGORY_OPENABLE);
                pickIntent.setType("*/*");
                String[] extraMimeTypes = {"application/pdf", "image/*"};
                pickIntent.putExtra(Intent.EXTRA_MIME_TYPES, extraMimeTypes);
                pickIntent.setAction(Intent.ACTION_GET_CONTENT);
                Intent
                        chooserIntent = Intent.createChooser(pickIntent, "Select Picture");
                startActivityForResult(chooserIntent, ACTIVITY_CHOOSE_FILE);
            });
            chooseFile.show();

        } catch (Exception ex) {
            ex.printStackTrace();

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            super.onActivityResult(requestCode, resultCode, data);
            if (resultCode != RESULT_OK) return;
            String path = "";
            if (requestCode == ACTIVITY_CHOOSE_FILE) {
                uploadDocumentFromLibrary(data.getData(), ACTIVITY_CHOOSE_FILE);

            } else if (requestCode == PICK_IMAGE_REQUEST && data != null && data.getData() != null) {
                uploadDocumentFromLibrary(data.getData(), PICK_IMAGE_REQUEST);
            }

        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    public String getRealPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        if (cursor == null) return null;
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);

    }


    public void removeAndUploadAdditionalDoc(int docID) {
        //        docTypeID = docID;
        //identityVerificationViewModel.removeIdentityImage(docTypeID + "");

    }


    public void uploadDocumentFromLibrary(Uri uri, int reqType) {
        try {
            String FilePath = "";
            if (reqType == ACTIVITY_CHOOSE_FILE) {
                FilePath = FileUtils.getReadablePathFromUri(getApplicationContext(), uri);

            } else {
                FilePath = getRealPathFromURI(uri);

            }
            File mediaFile = new File(FilePath);
            if (selectedDocType.equals("AAR-SSC")) {
                adtionalSscFile = mediaFile;
                removeAndUploadAdditionalDoc(1);

            } else if (selectedDocType.equals("AAR-SecFile")) {
                addtional2fFle = mediaFile;
                removeAndUploadAdditionalDoc(2);

            } else if (selectedDocType.equals("AAR-FBL")) {
                businessLincenseFile = mediaFile;
                removeAndUploadAdditionalDoc(3);

            }

        } catch (Exception e) {
            e.printStackTrace();

        }

    }

    public void fileOnClick(View view) {
        selectedDocType = "AAR-SSC";
        if (checkAndRequestPermissions(BusinessAdditionalActionRequiredActivity.this)) {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                return;

            }
            mLastClickTime = SystemClock.elapsedRealtime();
            if (Utils.isKeyboardVisible)
                Utils.hideKeypad(this);
            chooseFilePopup(this, selectedDocType);

        }

    }

    public void actionReqOnClick(View view) {
        selectedDocType = "AAR-SecFile";
        if (checkAndRequestPermissions(BusinessAdditionalActionRequiredActivity.this)) {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                return;

            }
            mLastClickTime = SystemClock.elapsedRealtime();
            if (Utils.isKeyboardVisible)
                Utils.hideKeypad(this);
            chooseFilePopup(this, selectedDocType);

        }
    }

    public void licenseOnClick(View view) {
        selectedDocType = "AAR-FBL";
        if (checkAndRequestPermissions(BusinessAdditionalActionRequiredActivity.this)) {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                return;
            }
            mLastClickTime = SystemClock.elapsedRealtime();
            if (Utils.isKeyboardVisible)
                Utils.hideKeypad(this);
            chooseFilePopup(this, selectedDocType);

        }
    }


    public void enableOrDisableNext() {
        try {

            if (issscFileUploadLL && isactionReqFileUploadLL && isbusinessLicenseUploadLL && ischeckboxCB && ischeckbox2CB && ischeckbox3CB) {
                isSubmitEnabled = true;
                submitCV.setCardBackgroundColor(getResources().getColor(R.color.primary_color));

            } else {
                isSubmitEnabled = false;
                submitCV.setCardBackgroundColor(getResources().getColor(R.color.inactive_color));

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
