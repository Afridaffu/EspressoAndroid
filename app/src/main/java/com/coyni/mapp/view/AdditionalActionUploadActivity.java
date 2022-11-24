package com.coyni.mapp.view;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.coyni.mapp.custom_camera.CameraUtility.BROWSE;
import static com.coyni.mapp.custom_camera.CameraUtility.CHOOSE_LIBRARY;
import static com.coyni.mapp.custom_camera.CameraUtility.TAKE_PHOTO;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.coyni.mapp.R;
import com.coyni.mapp.custom_camera.CameraActivity;
import com.coyni.mapp.custom_camera.CameraHandlerActivity;
import com.coyni.mapp.custom_camera.CameraUtility;
import com.coyni.mapp.dialogs.FilePickerDialog;
import com.coyni.mapp.dialogs.OnDialogClickListener;
import com.coyni.mapp.model.DocLayout;
import com.coyni.mapp.model.actionRqrd.ActionRqrdResponse;
import com.coyni.mapp.model.actionRqrd.SubmitActionRqrdResponse;
import com.coyni.mapp.model.identity_verification.IdentityImageResponse;
import com.coyni.mapp.model.identity_verification.RemoveIdentityResponse;
import com.coyni.mapp.utils.CustomTypefaceSpan;
import com.coyni.mapp.utils.FileUtils;
import com.coyni.mapp.utils.LogUtils;
import com.coyni.mapp.utils.Utils;
import com.coyni.mapp.view.business.BusinessAdditionalActionRequiredActivity;
import com.coyni.mapp.viewmodel.IdentityVerificationViewModel;
import com.coyni.mapp.viewmodel.UnderwritingUserActionRequiredViewModel;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class AdditionalActionUploadActivity extends BaseActivity {
    LinearLayout ssnCloseLL;
    private UnderwritingUserActionRequiredViewModel underwritingUserActionRequiredViewModel;
    private String selectedDocType = "";
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 102;
    private Long mLastClickTime = 0L;
    private int documentID;
    private LinearLayout selectedLayout = null;
    private TextView selectedText = null, adminMessageTV;
    private File mediaFile;
    private LinearLayout additionalDocumentRequiredLL;
    public boolean isSubmitEnabled = false;
    public CardView submitCV;
    private IdentityVerificationViewModel identityVerificationViewModel;
    private int lastUploadedDoc = 0;
    private List<DocLayout> listOfDocLayouts = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_additional_action_upload);

        initFields();
        initObserver();
    }

    private void initFields() {

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        ssnCloseLL = findViewById(R.id.ssnCloseLL);
        submitCV = findViewById(R.id.actRqrdSubmitCV);
        adminMessageTV = findViewById(R.id.adminMessageTV);
        ssnCloseLL.setOnClickListener(view -> finish());

        additionalDocumentRequiredLL = findViewById(R.id.ll_document_required);
        showProgressDialog();
        underwritingUserActionRequiredViewModel = new ViewModelProvider(this).get(UnderwritingUserActionRequiredViewModel.class);
        identityVerificationViewModel = new ViewModelProvider(this).get(IdentityVerificationViewModel.class);
        underwritingUserActionRequiredViewModel.getActionRequiredCustData();
        submitCV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                if (isSubmitEnabled)
                    postSubmitAPiCall();
            }
        });
    }

    private void initObserver() {
        try {
            underwritingUserActionRequiredViewModel.getActionRqrdCustRespMutableLiveData().observe(this,
                    new Observer<ActionRqrdResponse>() {
                        @Override
                        public void onChanged(ActionRqrdResponse actionRqrdResponse) {
                            dismissDialog();
                            try {
                                if (actionRqrdResponse != null && actionRqrdResponse.getStatus().equalsIgnoreCase("SUCCESS")) {


                                    if (actionRqrdResponse != null && actionRqrdResponse.getData() != null) {
                                        if (actionRqrdResponse.getData().getAdditionalDocument() != null &&
                                                actionRqrdResponse.getData().getAdditionalDocument().size() != 0) {
                                            additionalRequiredDocuments(actionRqrdResponse);
                                        }
                                    }

                                } else {
                                    Utils.displayAlert(actionRqrdResponse.getError().getErrorDescription(),
                                            AdditionalActionUploadActivity.this, "",
                                            actionRqrdResponse.getError().getFieldErrors().get(0));
                                }

                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            underwritingUserActionRequiredViewModel.getActRqrdSubmitResponseMutableLiveData().observe(this,
                    new Observer<SubmitActionRqrdResponse>() {
                        @Override
                        public void onChanged(SubmitActionRqrdResponse actionRqrdResponse) {
                            dismissDialog();
                            try {
                                if (actionRqrdResponse != null && actionRqrdResponse.getStatus().equalsIgnoreCase("SUCCESS")) {
                                    finish();
                                } else {
                                    Utils.displayAlert(actionRqrdResponse.getError().getErrorDescription(),
                                            AdditionalActionUploadActivity.this, "",
                                            actionRqrdResponse.getError().getFieldErrors().get(0));
                                }

                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }

        underwritingUserActionRequiredViewModel.getActRqrdDocUploadMutableLiveData().observe(this, new Observer<IdentityImageResponse>() {
            @Override
            public void onChanged(IdentityImageResponse identityImageResponse) {
                try {
                    dismissDialog();
                    if (identityImageResponse != null && identityImageResponse.getStatus().equalsIgnoreCase("SUCCESS")) {
                        if (selectedLayout != null) {
                            selectedLayout.setVisibility(VISIBLE);
                            selectedText.setVisibility(GONE);
                            selectedText.setTag(Integer.parseInt(identityImageResponse.getData().getId()));
                        }
                        setUploadedTrue((int) selectedLayout.getTag());
                        enableOrDisableNext();
                    } else {
                        Utils.displayAlert(identityImageResponse.getError().getErrorDescription(),
                                AdditionalActionUploadActivity.this, "",
                                identityImageResponse.getError().getFieldErrors().get(0));
                    }

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        try {
            identityVerificationViewModel.getRemoveIdentityImageResponse().observe(this, new Observer<RemoveIdentityResponse>() {
                @Override
                public void onChanged(RemoveIdentityResponse imageResponse) {
                    if (imageResponse != null) {
                        uploadDoc(mediaFile);
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
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
//                        Utils.displayAlert("Requires Access to Camera.", AdditionalActionUploadActivity.this, "", "");
                        Utils.showDialogPermission(AdditionalActionUploadActivity.this, getString(R.string.allow_access_header), getString(R.string.camera_permission_desc));

                    } else if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        Utils.displayAlert("Requires Access to Your Storage.", AdditionalActionUploadActivity.this, "", "");

                    } else if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        Utils.displayAlert("Requires Access to Your Storage.", AdditionalActionUploadActivity.this, "", "");

                    } else {
                        chooseFilePopup(this, selectedDocType);
                        if (Utils.isKeyboardVisible)
                            Utils.hideKeypad(AdditionalActionUploadActivity.this);
                    }
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void chooseFilePopup(final Context context, String type) {
        FilePickerDialog pickerDialog = new FilePickerDialog(context, true);
        pickerDialog.setOnDialogClickListener(new OnDialogClickListener() {
            @Override
            public void onDialogClicked(String action, Object value) {
                switch (action) {
                    case CHOOSE_LIBRARY:
                        launchCameraActionActivity(CameraUtility.CAMERA_ACTION_SELECTOR.GALLERY, type);
                        break;
                    case TAKE_PHOTO:
                        launchCameraActionActivity(CameraUtility.CAMERA_ACTION_SELECTOR.CAMERA_RETAKE, type);
                        break;
                    case BROWSE:
                        launchCameraActionActivity(CameraUtility.CAMERA_ACTION_SELECTOR.BROWSE, type);
                        break;
                }
            }
        });
        pickerDialog.show();
    }

    private void launchCameraActionActivity(CameraUtility.CAMERA_ACTION_SELECTOR action, String type) {
        Intent camIntent = new Intent(AdditionalActionUploadActivity.this, CameraHandlerActivity.class);
        camIntent.putExtra(CameraUtility.CAMERA_ACTION, action);
        camIntent.putExtra(CameraUtility.SELECTING_ID, type);
        imageChooserActivityLauncher.launch(camIntent);
    }

    ActivityResultLauncher<Intent> imageChooserActivityLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    uploadDocumentFromLibrary(result.getData().getStringExtra(CameraUtility.TARGET_FILE));
                    LogUtils.e(TAG, result.getData().getStringExtra(CameraUtility.TARGET_FILE));
                } else {
                    LogUtils.e(TAG, "Error while selecting photo");
                }
            });

    private void uploadDocumentFromLibrary(String filePath) {
        try {

            mediaFile = new File(filePath);

            LogUtils.d(TAG, "uploadDocumentFromLibrary" + mediaFile);
            LogUtils.d(TAG, "documentID" + documentID);

            if (Utils.isValidFileSize(mediaFile)) {
                LogUtils.d(TAG, "selectedID" + selectedText.getTag());
                if (selectedText != null && (int) selectedText.getTag() != 0)
                    identityVerificationViewModel.removeImageMultiDocs(lastUploadedDoc + "");
                else
                    uploadDoc(mediaFile);
            } else {
                Utils.displayAlert(getString(R.string.allowed_file_size_error), this, "", "");
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void enableOrDisableNext() {

        boolean isDocs = true;
        for (int l = 0; l < listOfDocLayouts.size(); l++) {
            if (!listOfDocLayouts.get(l).isUploaded()) {
                isDocs = false;
                break;
            }
        }

        try {
            if (!isDocs) {
                isSubmitEnabled = false;
                submitCV.setCardBackgroundColor(getResources().getColor(R.color.inactive_color));
                submitCV.setClickable(false);
                submitCV.setEnabled(false);

            } else {
                isSubmitEnabled = true;
                submitCV.setCardBackgroundColor(getResources().getColor(R.color.primary_color));
                submitCV.setClickable(true);
                submitCV.setEnabled(true);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void additionalRequiredDocuments(ActionRqrdResponse actionRqrdResponse) {

        additionalDocumentRequiredLL.removeAllViews();
        additionalDocumentRequiredLL.setVisibility(View.VISIBLE);
        adminMessageTV.setText(actionRqrdResponse.getData().getMessage());
        LinearLayout.LayoutParams layoutParamss = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        for (int i = 0; i < actionRqrdResponse.getData().getAdditionalDocument().size(); i++) {
            View inf = getLayoutInflater().inflate(R.layout.additional_document_cust_item, null);
            LinearLayout documentRequiredLL = inf.findViewById(R.id.documentRequired);
            LinearLayout sscFileUploadLL = inf.findViewById(R.id.sscFileUploadLL);
            LinearLayout sscfileUploadedLL = inf.findViewById(R.id.sscfileUploadedLL);
            TextView sscuploadFileTV = inf.findViewById(R.id.sscuploadFileTV);
            TextView sscfileUpdatedOnTV = inf.findViewById(R.id.sscfileUpdatedOnTV);
            TextView uploadedTV = inf.findViewById(R.id.uploadedTV);

            TextView documentName = inf.findViewById(R.id.tvdocumentName);
            documentRequiredLL.setVisibility(View.VISIBLE);
            additionalDocumentRequiredLL.addView(inf, layoutParamss);
            sscfileUpdatedOnTV.setText("Uploaded on " + Utils.getCurrentDate());
            sscFileUploadLL.setTag(i);
            sscfileUploadedLL.setTag(i);

            DocLayout docLayout = new DocLayout();
            docLayout.setId(actionRqrdResponse.getData().getAdditionalDocument().get(i).getId());
            docLayout.setLinearLayouts(documentRequiredLL);
            listOfDocLayouts.add(docLayout);
            if (actionRqrdResponse.getData().getAdditionalDocument().get(i).getUploadDocs().size() > 0) {
                setUploadedTrue(i);
                sscfileUploadedLL.setVisibility(VISIBLE);
                sscuploadFileTV.setVisibility(GONE);
                sscfileUpdatedOnTV.setText("Uploaded on " + Utils.convertDocUploadedDate(actionRqrdResponse.getData().getAdditionalDocument().get(i).getUploadDocs().get(0).getUploadDate()));
                sscuploadFileTV.setTag(actionRqrdResponse.getData().getAdditionalDocument().get(i).getUploadDocs().get(0).getDocId());
            } else
                sscuploadFileTV.setTag(0);


            setSpannableText(sscuploadFileTV, actionRqrdResponse.getData().getAdditionalDocument().get(i).getDocumentName(), R.color.black);
            SpannableString ss = new SpannableString("Upload " + actionRqrdResponse.getData().getAdditionalDocument().get(i).getDocumentName());
            int color = R.color.primary_color;
            Typeface font = Typeface.createFromAsset(getAssets(), "font/opensans_bold.ttf");
            SpannableStringBuilder SS = new SpannableStringBuilder(ss);
            ClickableSpan cs = new ClickableSpan() {
                @Override
                public void onClick(@NonNull View view) {
                    try {
                        if (view != null) {
                            int pos = (int) sscFileUploadLL.getTag();
                            documentID = actionRqrdResponse.getData().getAdditionalDocument().get(pos).getId();
                            selectedLayout = sscfileUploadedLL;
                            selectedText = sscuploadFileTV;
                            lastUploadedDoc = (int) sscuploadFileTV.getTag();
                            if (checkAndRequestPermissions(AdditionalActionUploadActivity.this)) {
                                if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                                    return;
                                }
                                mLastClickTime = SystemClock.elapsedRealtime();
                                chooseFilePopup(AdditionalActionUploadActivity.this, selectedDocType);

                            }

                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }

                @Override
                public void updateDrawState(@NonNull TextPaint ds) {
                    ds.setUnderlineText(false);
                }
            };
            SS.setSpan(new CustomTypefaceSpan("", font), 7, ss.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
            SS.setSpan(new ForegroundColorSpan(getColor(color)), 0, ss.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            SS.setSpan(cs, 0, ss.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            uploadedTV.setText(SS);
            uploadedTV.setMovementMethod(LinkMovementMethod.getInstance());
            uploadedTV.setHighlightColor(Color.TRANSPARENT);

            sscFileUploadLL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = (int) sscFileUploadLL.getTag();
                    documentID = actionRqrdResponse.getData().getAdditionalDocument().get(pos).getId();
                    selectedLayout = sscfileUploadedLL;
                    selectedText = sscuploadFileTV;
                    lastUploadedDoc = (int) sscuploadFileTV.getTag();
                    if (checkAndRequestPermissions(AdditionalActionUploadActivity.this)) {
                        if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                            return;
                        }
                        mLastClickTime = SystemClock.elapsedRealtime();
                        chooseFilePopup(AdditionalActionUploadActivity.this, selectedDocType);

                    }
                }
            });

            sscuploadFileTV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = (int) sscFileUploadLL.getTag();
                    documentID = actionRqrdResponse.getData().getAdditionalDocument().get(pos).getId();
                    selectedLayout = sscfileUploadedLL;
                    selectedText = sscuploadFileTV;
                    lastUploadedDoc = (int) sscuploadFileTV.getTag();
                    if (checkAndRequestPermissions(AdditionalActionUploadActivity.this)) {
                        if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                            return;
                        }
                        mLastClickTime = SystemClock.elapsedRealtime();
                        chooseFilePopup(AdditionalActionUploadActivity.this, selectedDocType);

                    }
                }
            });
        }
        enableOrDisableNext();
    }

    public void setSpannableText(TextView sscuploadFileTV, String documentName, int color) {

        SpannableString ss = new SpannableString("Upload " + documentName);
        Typeface font = Typeface.createFromAsset(getAssets(), "font/opensans_bold.ttf");
        SpannableStringBuilder SS = new SpannableStringBuilder(ss);
        SS.setSpan(new CustomTypefaceSpan("", font), 7, ss.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
        SS.setSpan(new ForegroundColorSpan(getColor(color)), 0, ss.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        sscuploadFileTV.setText(SS);
        sscuploadFileTV.setMovementMethod(LinkMovementMethod.getInstance());
        sscuploadFileTV.setHighlightColor(Color.TRANSPARENT);
    }

    private void postSubmitAPiCall() {
        try {
            showProgressDialog();
            underwritingUserActionRequiredViewModel.submitActionRequiredCustomer();
        } catch (Exception e) {
            e.printStackTrace();
            dismissDialog();
        }
    }

    private void uploadDoc(File file) {
        Log.e("Document ID", documentID + "");
        showProgressDialog();
        RequestBody requestBody = null;
        MultipartBody.Part idFile = null;

        requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        idFile = MultipartBody.Part.createFormData("document", file.getName(), requestBody);

        RequestBody idType = RequestBody.create(MediaType.parse("text/plain"), "100");
        RequestBody docID = RequestBody.create(MediaType.parse("text/plain"), documentID + "");
        underwritingUserActionRequiredViewModel.uploadActionRequiredDoc(idFile, idType, docID);
    }

    private void setUploadedTrue(int pos) {
        listOfDocLayouts.get(pos).setUploaded(true);
    }
}