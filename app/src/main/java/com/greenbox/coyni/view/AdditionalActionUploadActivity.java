package com.greenbox.coyni.view;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.greenbox.coyni.R;
import com.greenbox.coyni.custom_camera.CameraActivity;
import com.greenbox.coyni.model.actionRqrd.ActionRqrdResponse;
import com.greenbox.coyni.model.identity_verification.AddressObj;
import com.greenbox.coyni.model.identity_verification.GetIdentityResponse;
import com.greenbox.coyni.model.identity_verification.IdentityAddressRequest;
import com.greenbox.coyni.model.identity_verification.IdentityAddressResponse;
import com.greenbox.coyni.model.identity_verification.PhotoIDEntityObject;
import com.greenbox.coyni.model.underwriting.ActionRequiredResponse;
import com.greenbox.coyni.model.underwriting.ActionRequiredSubmitResponse;
import com.greenbox.coyni.model.underwriting.ProposalsPropertiesData;
import com.greenbox.coyni.utils.FileUtils;
import com.greenbox.coyni.utils.LogUtils;
import com.greenbox.coyni.utils.Utils;
import com.greenbox.coyni.view.business.BusinessAdditionalActionRequiredActivity;
import com.greenbox.coyni.viewmodel.IdentityVerificationViewModel;
import com.greenbox.coyni.viewmodel.UnderwritingUserActionRequiredViewModel;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AdditionalActionUploadActivity extends BaseActivity {
    LinearLayout ssnCloseLL;
    private UnderwritingUserActionRequiredViewModel underwritingUserActionRequiredViewModel;

    private String selectedDocType = "";
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 102;
    private static final int ACTIVITY_CHOOSE_FILE = 3;
    private static final int PICK_IMAGE_REQUEST = 4;
    private Long mLastClickTime = 0L;
    private HashMap<Integer, String> fileUpload;
    private ActionRqrdResponse actionRequired;
    private int documentID;
    private LinearLayout selectedLayout = null;
    private TextView selectedText = null;
    public static ArrayList<File> documentsFIle;
    private JSONObject informationJSON;
    public static File mediaFile;
    private boolean reservedRuleAccepted = false;
    private boolean reservedRule = false;
    private ImageView imvCLose;
    private HashMap<String, ProposalsPropertiesData> proposalsMap;
    private LinearLayout additionReservedLL, llApprovedReserved, llHeading, llBottomView, additionalDocumentRequiredLL,
            websiteRevisionRequiredLL, informationRevisionLL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_additional_action_upload);


        initFields();
        initObserver();
    }

    private void initFields() {

        ssnCloseLL = findViewById(R.id.ssnCloseLL);
        ssnCloseLL.setOnClickListener(view -> finish());

        additionalDocumentRequiredLL = findViewById(R.id.ll_document_required);

        underwritingUserActionRequiredViewModel = new ViewModelProvider(this).get(UnderwritingUserActionRequiredViewModel.class);
        underwritingUserActionRequiredViewModel.getActionRequiredCustData();

        fileUpload = new HashMap<Integer, String>();
        documentsFIle = new ArrayList<>();

    }

    private void initObserver() {
        underwritingUserActionRequiredViewModel.getActionRqrdCustRespMutableLiveData().observe(this,
                new Observer<ActionRqrdResponse>() {
                    @Override
                    public void onChanged(ActionRqrdResponse actionRqrdResponse) {
                        try {
                            actionRequired = actionRqrdResponse;
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
                        Utils.displayAlert("Requires Access to Camera.", AdditionalActionUploadActivity.this, "", "");

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
                startActivity(new Intent(AdditionalActionUploadActivity.this, CameraActivity.class).putExtra("FROM", "BAARA"));
            });
            browseFileTV.setOnClickListener(view -> {
                chooseFile.dismiss();
                Intent pickIntent = new Intent();
                pickIntent.addCategory(Intent.CATEGORY_OPENABLE);
                pickIntent.setType("*/*");
                String[] extraMimeTypes = {"application/pdf", "image/*"};
                pickIntent.putExtra(Intent.EXTRA_MIME_TYPES, extraMimeTypes);
                pickIntent.setAction(Intent.ACTION_GET_CONTENT);
                Intent chooserIntent = Intent.createChooser(pickIntent, "Select Picture");
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
            LogUtils.d(TAG, "onActivityResult" + data.getData());
            if (requestCode == ACTIVITY_CHOOSE_FILE) {
                LogUtils.d(TAG, "ACTIVITYs_CHOOSE_FILE" + data.getData());
                uploadDocumentFromLibrary(data.getData(), ACTIVITY_CHOOSE_FILE);
            } else if (requestCode == PICK_IMAGE_REQUEST && data != null && data.getData() != null) {
                LogUtils.d(TAG, "PICK_IMAGE_REQUEST" + data.getData());
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

    public void saveFileFromCamera(File cameraFIle) {

        LogUtils.d(TAG, "camera" + cameraFIle);

        String FilePath = String.valueOf(cameraFIle);

        mediaFile = new File(FilePath);

        LogUtils.d(TAG, "uploadDocumentFromLibrary" + mediaFile);
        LogUtils.d(TAG, "documentID" + documentID);

        if (fileUpload.containsKey(documentID)) {
            fileUpload.replace(documentID, mediaFile.getAbsolutePath());
            documentsFIle.add(mediaFile);
        }

        if (selectedLayout != null) {
            selectedLayout.setVisibility(View.VISIBLE);
            selectedText.setVisibility(View.GONE);
        }
        enableOrDisableNext();
        LogUtils.d(TAG, "fileUpload" + fileUpload);
    }

    public void uploadDocumentFromLibrary(Uri uri, int reqType) {
        try {
            String FilePath = "";
            if (reqType == ACTIVITY_CHOOSE_FILE) {
                FilePath = FileUtils.getReadablePathFromUri(getApplicationContext(), uri);
            } else {
                FilePath = getRealPathFromURI(uri);
            }
            mediaFile = new File(FilePath);

            LogUtils.d(TAG, "uploadDocumentFromLibrary" + mediaFile);
            LogUtils.d(TAG, "documentID" + documentID);

            if (fileUpload.containsKey(documentID)) {
                fileUpload.replace(documentID, mediaFile.getAbsolutePath());
                documentsFIle.add(mediaFile);
            }

            if (selectedLayout != null) {
                selectedLayout.setVisibility(View.VISIBLE);
                selectedText.setVisibility(View.GONE);

            }
            enableOrDisableNext();
            LogUtils.d(TAG, "fileUpload" + fileUpload);

        } catch (Exception e) {
            e.printStackTrace();

        }

    }

    public void enableOrDisableNext() {
        try {
            LogUtils.d(TAG, "fileUpload" + fileUpload);
//            if (fileUpload.containsValue(null)) {
//                isSubmitEnabled = false;
//                submitCV.setCardBackgroundColor(getResources().getColor(R.color.inactive_color));
//                submitCV.setClickable(false);
//                submitCV.setEnabled(false);
//
//            } else {
//                isSubmitEnabled = true;
//                submitCV.setCardBackgroundColor(getResources().getColor(R.color.primary_color));
//                submitCV.setClickable(true);
//                submitCV.setEnabled(true);
//            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void additionalRequiredDocuments(ActionRqrdResponse actionRqrdResponse) {

        additionalDocumentRequiredLL.setVisibility(View.VISIBLE);

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

            fileUpload.put(actionRqrdResponse.getData().getAdditionalDocument().get(i).getDocumentId(), null);

            setSpannableText(sscuploadFileTV, actionRqrdResponse.getData().getAdditionalDocument().get(i).getDocumentName(), R.color.black);

            setSpannableText(uploadedTV, actionRqrdResponse.getData().getAdditionalDocument().get(i).getDocumentName(), R.color.primary_color);

            sscFileUploadLL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = (int) view.getTag();
                    documentID = actionRqrdResponse.getData().getAdditionalDocument().get(pos).getDocumentId();
                    selectedLayout = sscfileUploadedLL;
                    selectedText = sscuploadFileTV;
                    if (checkAndRequestPermissions(AdditionalActionUploadActivity.this)) {
                        if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                            return;
                        }
                        mLastClickTime = SystemClock.elapsedRealtime();
                        if (Utils.isKeyboardVisible)
                            Utils.hideKeypad(AdditionalActionUploadActivity.this);
                        chooseFilePopup(AdditionalActionUploadActivity.this, selectedDocType);

                    }
                }
            });
            enableOrDisableNext();
        }
    }

    public void setSpannableText(TextView sscuploadFileTV, String documentName, int color) {

        SpannableString ss = new SpannableString("Upload " + documentName);

        final StyleSpan bss = new StyleSpan(android.graphics.Typeface.BOLD);
        ss.setSpan(new RelativeSizeSpan(1f), 7, ss.length(), 0);
        ss.setSpan(bss, 7, ss.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss.setSpan(new ForegroundColorSpan(getColor(color)), 7, ss.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        sscuploadFileTV.setText(ss);
        sscuploadFileTV.setMovementMethod(LinkMovementMethod.getInstance());
        sscuploadFileTV.setHighlightColor(Color.TRANSPARENT);
    }
}