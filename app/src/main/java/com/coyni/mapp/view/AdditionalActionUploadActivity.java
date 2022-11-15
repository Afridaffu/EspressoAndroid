package com.coyni.mapp.view;

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
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.coyni.mapp.R;
import com.coyni.mapp.custom_camera.CameraActivity;
import com.coyni.mapp.model.actionRqrd.ActionRqrdResponse;
import com.coyni.mapp.model.actionRqrd.SubmitActionRqrdResponse;
import com.coyni.mapp.utils.CustomTypefaceSpan;
import com.coyni.mapp.utils.FileUtils;
import com.coyni.mapp.utils.LogUtils;
import com.coyni.mapp.utils.Utils;
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
    private static final int ACTIVITY_CHOOSE_FILE = 3;
    private static final int PICK_IMAGE_REQUEST = 4;
    private Long mLastClickTime = 0L;
    private int documentID;
    private LinearLayout selectedLayout = null;
    private TextView selectedText = null, adminMessageTV;
    public static ArrayList<File> documentsFIle;
    public static File mediaFile;
    private LinearLayout additionalDocumentRequiredLL;
    private HashMap<Integer, File> filesToUpload;
    public static AdditionalActionUploadActivity additionalActionUploadActivity;
    public boolean isSubmitEnabled = false;
    public CardView submitCV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_additional_action_upload);

        initFields();
        initObserver();
    }

    private void initFields() {

        additionalActionUploadActivity = this;
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        ssnCloseLL = findViewById(R.id.ssnCloseLL);
        submitCV = findViewById(R.id.actRqrdSubmitCV);
        adminMessageTV = findViewById(R.id.adminMessageTV);
        ssnCloseLL.setOnClickListener(view -> finish());

        additionalDocumentRequiredLL = findViewById(R.id.ll_document_required);
        showProgressDialog();
        underwritingUserActionRequiredViewModel = new ViewModelProvider(this).get(UnderwritingUserActionRequiredViewModel.class);
        underwritingUserActionRequiredViewModel.getActionRequiredCustData();

//        fileUpload = new HashMap<Integer, String>();
        filesToUpload = new HashMap<Integer, File>();
        documentsFIle = new ArrayList<>();

        submitCV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LogUtils.d(TAG, "submitCV" + filesToUpload);
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
                startActivity(new Intent(AdditionalActionUploadActivity.this, CameraActivity.class).putExtra("FROM", "ActRqrdDocs"));
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

        if (Utils.isValidFileSize(mediaFile)) {

            LogUtils.d(TAG, "uploadDocumentFromLibrary" + mediaFile);
            LogUtils.d(TAG, "documentID" + documentID);

//        if (fileUpload.containsKey(documentID)) {
//            fileUpload.replace(documentID, mediaFile.getAbsolutePath());
//            documentsFIle.add(mediaFile);
//        }
            if (filesToUpload.containsKey(documentID)) {
                filesToUpload.replace(documentID, mediaFile);
                documentsFIle.add(mediaFile);
            }

            if (selectedLayout != null) {
                selectedLayout.setVisibility(View.VISIBLE);
                selectedText.setVisibility(View.GONE);
            }
            enableOrDisableNext();
            LogUtils.d(TAG, "fileUpload" + filesToUpload);
        } else {
            Utils.displayAlert(getString(R.string.allowed_file_size_error), this, "coyni", "");
        }


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
            if (Utils.isValidFileSize(mediaFile)) {

                LogUtils.d(TAG, "uploadDocumentFromLibrary" + mediaFile);
                LogUtils.d(TAG, "documentID" + documentID);

//            if (fileUpload.containsKey(documentID)) {
//                fileUpload.replace(documentID, mediaFile.getAbsolutePath());
//                documentsFIle.add(mediaFile);
//            }

                if (filesToUpload.containsKey(documentID)) {
                    filesToUpload.replace(documentID, mediaFile);
                    documentsFIle.add(mediaFile);
                }

                if (selectedLayout != null) {
                    selectedLayout.setVisibility(View.VISIBLE);
                    selectedText.setVisibility(View.GONE);

                }
                enableOrDisableNext();
                LogUtils.d(TAG, "fileUpload" + filesToUpload);
            } else {
                Utils.displayAlert(getString(R.string.allowed_file_size_error), this, "coyni", "");
            }


        } catch (Exception e) {
            e.printStackTrace();

        }

    }

    public void enableOrDisableNext() {
        try {
            LogUtils.d(TAG, "fileUpload" + filesToUpload);
            if (filesToUpload.containsValue(null)) {
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
//        for (int i = 0; i < additionalDocumentRequiredLL.getChildCount(); i++) {
//            View child = additionalDocumentRequiredLL.getChildAt(i);
//            additionalDocumentRequiredLL.removeAllViews();
//        }

        additionalDocumentRequiredLL.setVisibility(View.VISIBLE);
//        adminMsgTV.setText(getResources().getString(R.string.please_click_the_upload_button_below_to_proceed));
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
            sscuploadFileTV.setTag(i);

            filesToUpload.put(actionRqrdResponse.getData().getAdditionalDocument().get(i).getDocumentId(), null);

            setSpannableText(sscuploadFileTV, actionRqrdResponse.getData().getAdditionalDocument().get(i).getDocumentName(), R.color.black);

            //setSpannableText(uploadedTV, actionRqrdResponse.getData().getAdditionalDocument().get(i).getDocumentName(), R.color.primary_color);
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
                            documentID = actionRqrdResponse.getData().getAdditionalDocument().get(pos).getDocumentId();
                            selectedLayout = sscfileUploadedLL;
                            selectedText = sscuploadFileTV;
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
                    int pos = (int) view.getTag();
                    documentID = actionRqrdResponse.getData().getAdditionalDocument().get(pos).getDocumentId();
                    selectedLayout = sscfileUploadedLL;
                    selectedText = sscuploadFileTV;
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
                    int pos = (int) view.getTag();
                    documentID = actionRqrdResponse.getData().getAdditionalDocument().get(pos).getDocumentId();
                    selectedLayout = sscfileUploadedLL;
                    selectedText = sscuploadFileTV;
                    if (checkAndRequestPermissions(AdditionalActionUploadActivity.this)) {
                        if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                            return;
                        }
                        mLastClickTime = SystemClock.elapsedRealtime();
                        chooseFilePopup(AdditionalActionUploadActivity.this, selectedDocType);

                    }
                }
            });
            enableOrDisableNext();
        }
    }

    public void setSpannableText(TextView sscuploadFileTV, String documentName, int color) {

        SpannableString ss = new SpannableString("Upload " + documentName);

//        final StyleSpan bss = new StyleSpan(android.graphics.Typeface.BOLD);
//        ss.setSpan(new RelativeSizeSpan(1f), 7, ss.length(), 0);
//        ss.setSpan(bss, 7, ss.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//        ss.setSpan(new ForegroundColorSpan(getColor(color)), 7, ss.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        Typeface font = Typeface.createFromAsset(getAssets(), "font/opensans_bold.ttf");
        SpannableStringBuilder SS = new SpannableStringBuilder(ss);
        SS.setSpan(new CustomTypefaceSpan("", font), 7, ss.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
        SS.setSpan(new ForegroundColorSpan(getColor(color)), 0, ss.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        sscuploadFileTV.setText(SS);
        sscuploadFileTV.setMovementMethod(LinkMovementMethod.getInstance());
        sscuploadFileTV.setHighlightColor(Color.TRANSPARENT);
    }

    //    private void postSubmitAPiCall() {
//        try {
//
//            List<MultipartBody.Part> multiparts = new ArrayList<>();
//            JSONArray documents = new JSONArray();
//            JSONObject underwritingActionRequiredJSON = new JSONObject();
//
//            for (Map.Entry<Integer, File> entry : filesToUpload.entrySet()) {
//                RequestBody requestBody = RequestBody.create(MediaType.parse("*/*"), entry.getValue());
//                MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData(String.valueOf(entry.getKey()),
//                        entry.getValue().getName(), requestBody);
//                multiparts.add(fileToUpload);
//            }
//
//
//            underwritingActionRequiredJSON.put("documentIdList", documents);
////            underwritingActionRequiredJSON.put("userId", objMyApplication.getCurrentUserData().getLoginUserId());
//
//            MultipartBody.Builder buildernew = new MultipartBody.Builder()
//                    .setType(MultipartBody.FORM)
//                    .addFormDataPart("underwritingActionRequired", null,
//                            RequestBody.create(underwritingActionRequiredJSON.toString().getBytes(), MediaType.parse("application/json")));
//
//            for (Map.Entry<Integer, File> entry : filesToUpload.entrySet()) {
//                documents.put(entry.getKey());
//                buildernew.addFormDataPart("documentList ", entry.getValue().getName() + ".jpg",
//                        RequestBody.create(MediaType.parse("multipart/form-data"), entry.getValue()));
//            }
//
//            MultipartBody requestBody = buildernew.build();
//
//            underwritingUserActionRequiredViewModel.submitActionRequiredCustomer(requestBody);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
    private void postSubmitAPiCall() {
        try {
            showProgressDialog();
            List<MultipartBody.Part> multiparts = new ArrayList<>();
            JSONArray documents = new JSONArray();
            JSONObject underwritingActionRequiredJSON = new JSONObject();

            MultipartBody.Part[] docs = new MultipartBody.Part[filesToUpload.size()];

            Map<Integer, File> map = new TreeMap<Integer, File>(filesToUpload);

            for (Map.Entry<Integer, File> entry : map.entrySet()) {
                RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), entry.getValue());
                MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData("documentList",
                        entry.getValue().getName(), requestBody);
                multiparts.add(fileToUpload);
                LogUtils.e("Key and Name", "" + entry.getKey() + " - " + entry.getValue().getName());
                documents.put(entry.getKey());
            }

            for (int i = 0; i < multiparts.size(); i++) {
                docs[i] = multiparts.get(i);
            }

            underwritingActionRequiredJSON.put("documentIdList", documents);

            RequestBody underwritingActionRequired = RequestBody.create(MediaType.parse("application/json"),
                    String.valueOf(underwritingActionRequiredJSON));
            underwritingUserActionRequiredViewModel.submitActionRequiredCustomer(docs, underwritingActionRequired);

        } catch (Exception e) {
            e.printStackTrace();
            dismissDialog();
        }
    }
}