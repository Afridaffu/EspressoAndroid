package com.coyni.mapp.view.business;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.coyni.mapp.R;
import com.coyni.mapp.custom_camera.CameraActivity;
import com.coyni.mapp.dialogs.AddCommentsDialog;
import com.coyni.mapp.dialogs.ApplicationApprovedDialog;
import com.coyni.mapp.dialogs.CustomConfirmationDialog;
import com.coyni.mapp.dialogs.OnDialogClickListener;
import com.coyni.mapp.dialogs.ShowFullPageImageDialog;
import com.coyni.mapp.interfaces.OnKeyboardVisibilityListener;
import com.coyni.mapp.model.DialogAttributes;
import com.coyni.mapp.model.underwriting.ActionRequiredResponse;
import com.coyni.mapp.model.underwriting.ActionRequiredSubmitResponse;
import com.coyni.mapp.model.underwriting.InformationChangeData;
import com.coyni.mapp.model.underwriting.ProposalsData;
import com.coyni.mapp.model.underwriting.ProposalsPropertiesData;
import com.coyni.mapp.utils.CustomTypefaceSpan;
import com.coyni.mapp.utils.DisplayImageUtility;
import com.coyni.mapp.utils.FileUtils;
import com.coyni.mapp.utils.LogUtils;
import com.coyni.mapp.utils.Utils;
import com.coyni.mapp.view.BaseActivity;
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

public class BusinessAdditionalActionRequiredActivity extends BaseActivity implements OnKeyboardVisibilityListener {
    public ScrollView scrollview;
    private LinearLayout additionReservedLL, llApprovedReserved, llHeading, llBottomView, additionalDocumentRequiredLL,
            websiteRevisionRequiredLL, informationRevisionLL;
    private String selectedDocType = "";
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 102;
    private static final int ACTIVITY_CHOOSE_FILE = 3;
    private static final int PICK_IMAGE_REQUEST = 4;
    private Long mLastClickTime = 0L;
    public static BusinessAdditionalActionRequiredActivity businessAdditionalActionRequired;
    public static File additional2fFle = null, businessLicenceFile = null;
    public boolean isSubmitEnabled = false;
    public CardView submitCV;
    private UnderwritingUserActionRequiredViewModel underwritingUserActionRequiredViewModel;
    private HashMap<Integer, String> fileUpload;
    private HashMap<Integer, File> filesToUpload;
    private ActionRequiredResponse actionRequired;
    private int documentID;
    private LinearLayout selectedLayout = null;
    private RelativeLayout additionalActionRL;
    private TextView selectedText = null;
    public static ArrayList<File> documentsFIle;
    private JSONObject informationJSON;
    public static File mediaFile;
    private boolean reservedRuleAccepted = false;
    private boolean reservedRule = false;
    private ImageView imvCLose;
    private HashMap<String, ProposalsPropertiesData> proposalsMap;
    private TextView adminMessageTV,resubmitTV,editTextTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_additional_action_required);
        businessAdditionalActionRequired = this;
        initFields();
        initObserver();
//        enableOrDisableNext();
    }

    @Override
    public void onBackPressed() {
        if (!reservedRule) {
            super.onBackPressed();
        }
    }

    private void initFields() {

        additionReservedLL = findViewById(R.id.lladditionReserve);
        llApprovedReserved = findViewById(R.id.llapprovedreserved);
        scrollview = findViewById(R.id.scrollview);
        llHeading = findViewById(R.id.llHeading);
        llBottomView = findViewById(R.id.llBottomView);
        additionalDocumentRequiredLL = findViewById(R.id.ll_document_required);
        websiteRevisionRequiredLL = findViewById(R.id.website_revision_required);
        informationRevisionLL = findViewById(R.id.information_revision);
        imvCLose = findViewById(R.id.imvCLose);
        submitCV = findViewById(R.id.submitCV);
        adminMessageTV = findViewById(R.id.adminMessageTV);
        additionalActionRL = findViewById(R.id.additionalActionRL);
        resubmitTV = findViewById(R.id.resubmitTV);
        editTextTV = findViewById(R.id.editTextTV);

        underwritingUserActionRequiredViewModel = new ViewModelProvider(this).get(UnderwritingUserActionRequiredViewModel.class);
        showProgressDialog();
        underwritingUserActionRequiredViewModel.getAdditionalActionRequiredData();

        fileUpload = new HashMap<Integer, String>();
        filesToUpload = new HashMap<Integer, File>();
        documentsFIle = new ArrayList<>();

        setKeyboardVisibilityListener(BusinessAdditionalActionRequiredActivity.this);

        imvCLose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        submitCV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LogUtils.d(TAG, "submitCV" + fileUpload);
                if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                postSubmitAPiCall();
            }
        });
        resubmitTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                startActivity(new Intent(BusinessAdditionalActionRequiredActivity.this, AddManualBankAccount.class).putExtra("FROM", "Resubmit"));

            }
        });
        editTextTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                startActivity(new Intent(BusinessAdditionalActionRequiredActivity.this, AddManualBankAccount.class).putExtra("FROM", "Edit"));

            }
        });
    }

    private void postSubmitAPiCall() {
        showProgressDialog();

        informationJSON = new JSONObject();
        try {
            JSONArray documents = new JSONArray();
            JSONArray website = new JSONArray();
            if (actionRequired.getData().getAdditionalDocument() != null) {
                for (int i = 0; i <= actionRequired.getData().getAdditionalDocument().size() - 1; i++) {
                    documents.put(actionRequired.getData().getAdditionalDocument().get(i).getDocumentId());
                }
            }
            if (actionRequired.getData().getWebsiteChange() != null) {
                for (int i = 0; i <= actionRequired.getData().getWebsiteChange().size() - 1; i++) {
                    website.put(actionRequired.getData().getWebsiteChange().get(i).getId());
                }
            }
            if (actionRequired.getData().getReserveRule() != null) {
                informationJSON.put("reserveRuleAccepted", reservedRuleAccepted);
            }
            informationJSON.put("documentIdList", documents);
            informationJSON.put("websiteUpdates", website);

            JSONArray proposals = new JSONArray();

            if (actionRequired.getData().getInformationChange() != null) {
                for (int i = 0; i < actionRequired.getData().getInformationChange().size(); i++) {
                    InformationChangeData data = actionRequired.getData().getInformationChange().get(i);
                    List<ProposalsData> proposalsData = data.getProposals();
                    for (int j = 0; j < proposalsData.size(); j++) {
                        ProposalsData proposal = proposalsData.get(j);
                        String type = proposal.getType();
                        JSONObject proposalsObj = new JSONObject();
                        JSONArray proposalsArray = new JSONArray();
                        if (proposal != null && proposal.getProperties() != null && proposal.getProperties().size() > 0) {
                            for (int k = 0; k < proposal.getProperties().size(); k++) {
                                ProposalsPropertiesData property = proposal.getProperties().get(k);
                                JSONObject propertyObj = new JSONObject();
                                //String verificationKey = type + "" + capFirstLetter(property.getDisplayName());
                                String verificationKey = type + "" + property.getName();
                                propertyObj.put("isUserAccepted", proposalsMap.get(verificationKey).isUserAccepted());
                                propertyObj.put("name", property.getName());
                                propertyObj.put("userMessage", proposalsMap.get(verificationKey).getUserMessage());
                                proposalsArray.put(propertyObj);
                            }
                        }

                        proposalsObj.put("dbId", proposal.getDbId());
                        proposalsObj.put("type", proposal.getType());
                        proposalsObj.put("properties", proposalsArray);

                        proposals.put(proposalsObj);
                        informationJSON.put("proposals", proposals);
                    }

                }
            }


            // new code
            List<MultipartBody.Part> multiparts = new ArrayList<>();

            MultipartBody.Part[] docs = new MultipartBody.Part[filesToUpload.size()];

            Map<Integer, File> map = new TreeMap<Integer, File>(filesToUpload);

            for (Map.Entry<Integer, File> entry : map.entrySet()) {
                RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), entry.getValue());
                MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData("documents",
                        entry.getValue().getName(), requestBody);
                multiparts.add(fileToUpload);
                LogUtils.e("Key and Name", "" + entry.getKey() + " - " + entry.getValue().getName());
            }

            for (int i = 0; i < multiparts.size(); i++) {
                docs[i] = multiparts.get(i);
            }

            RequestBody underwritingActionRequired = RequestBody.create(MediaType.parse("application/json"),
                    String.valueOf(informationJSON));
            underwritingUserActionRequiredViewModel.submitActionRequired(docs, underwritingActionRequired);

            //new code
        } catch (Exception e) {
            e.printStackTrace();
            dismissDialog();
        }
    }

    private void initObserver() {
        underwritingUserActionRequiredViewModel.getActionRequiredSubmitResponseMutableLiveData().observe(this, new Observer<ActionRequiredSubmitResponse>() {
            @Override
            public void onChanged(ActionRequiredSubmitResponse actionRequiredSubmitResponse) {
                dismissDialog();
                if (actionRequiredSubmitResponse != null && actionRequiredSubmitResponse.getStatus().equalsIgnoreCase(Utils.SUCCESS)) {
                    setResult(RESULT_OK);
                    finish();
                } else {
                    String errorMessage = getString(R.string.something_went_wrong);
                    if (actionRequiredSubmitResponse != null && actionRequiredSubmitResponse.getError() != null
                            && actionRequiredSubmitResponse.getError().getErrorDescription() != null
                            && !actionRequiredSubmitResponse.getError().getErrorDescription().equals("")) {
                        errorMessage = actionRequiredSubmitResponse.getError().getErrorDescription();
                    }
                    Utils.displayAlert(errorMessage,
                            BusinessAdditionalActionRequiredActivity.this, "", "");

                }
            }
        });

        underwritingUserActionRequiredViewModel.getUserAccountLimitsMutableLiveData().observe(this,
                new Observer<ActionRequiredResponse>() {
                    @Override
                    public void onChanged(ActionRequiredResponse actionRequiredResponse) {
                        try {
                            dismissDialog();
                            additionalActionRL.setVisibility(View.VISIBLE);
                            // LogUtils.d(TAG, "ActionRequiredResponse" + actionRequiredResponse.getData().getWebsiteChange().size());
                            actionRequired = actionRequiredResponse;
                            if (actionRequiredResponse != null && actionRequiredResponse.getStatus().equalsIgnoreCase("SUCCESS")) {

                                if (actionRequiredResponse != null && actionRequiredResponse.getData() != null) {
                                    if (actionRequiredResponse.getData().getMessage() != null) {
                                        adminMessageTV.setText(actionRequiredResponse.getData().getMessage());
                                    }
                                    if (actionRequiredResponse.getData().getAdditionalDocument() != null &&
                                            actionRequiredResponse.getData().getAdditionalDocument().size() != 0) {
                                        additionalRequiredDocuments(actionRequiredResponse);
                                    }
                                    if (actionRequiredResponse.getData().getWebsiteChange() != null
                                            && actionRequiredResponse.getData().getWebsiteChange().size() != 0) {
                                        websiteChanges(actionRequiredResponse);
                                    }
                                    if (actionRequiredResponse.getData().getInformationChange() != null
                                            && actionRequiredResponse.getData().getInformationChange().size() != 0) {
                                        informationRevision(actionRequiredResponse);
                                    }
                                    if (actionRequiredResponse.getData().getReserveRule() != null) {
                                        showReserveRule(actionRequiredResponse);
                                    }
                                }

                            } else {
                                Utils.displayAlert(actionRequiredResponse.getError().getErrorDescription(),
                                        BusinessAdditionalActionRequiredActivity.this, "",
                                        actionRequiredResponse.getError().getFieldErrors().get(0));
                            }

                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                });

    }

    private void additionalRequiredDocuments(ActionRequiredResponse actionRequiredResponse) {

        additionalDocumentRequiredLL.setVisibility(View.VISIBLE);

        LinearLayout.LayoutParams layoutParamss = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        for (int i = 0; i < actionRequiredResponse.getData().getAdditionalDocument().size(); i++) {
            View inf = getLayoutInflater().inflate(R.layout.additional_document_item, null);
            LinearLayout documentRequiredLL = inf.findViewById(R.id.documentRequired);
            LinearLayout sscFileUploadLL = inf.findViewById(R.id.sscFileUploadLL);
            LinearLayout sscfileUploadedLL = inf.findViewById(R.id.sscfileUploadedLL);
            TextView sscuploadFileTV = inf.findViewById(R.id.sscuploadFileTV);
            TextView sscfileUpdatedOnTV = inf.findViewById(R.id.sscfileUpdatedOnTV);

            TextView documentName = inf.findViewById(R.id.tvdocumentName);
            documentRequiredLL.setVisibility(View.VISIBLE);
            documentName.setText(actionRequiredResponse.getData().getAdditionalDocument().get(i).getDocumentName());
            additionalDocumentRequiredLL.addView(inf, layoutParamss);
            sscfileUpdatedOnTV.setText("Uploaded on " + Utils.getCurrentDate());
            sscFileUploadLL.setTag(i);

            fileUpload.put(actionRequiredResponse.getData().getAdditionalDocument().get(i).getDocumentId(), null);
            filesToUpload.put(actionRequiredResponse.getData().getAdditionalDocument().get(i).getDocumentId(), null);

            sscFileUploadLL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = (int) view.getTag();
                    documentID = actionRequiredResponse.getData().getAdditionalDocument().get(pos).getDocumentId();
                    selectedLayout = sscfileUploadedLL;
                    selectedText = sscuploadFileTV;
                    if (checkAndRequestPermissions(BusinessAdditionalActionRequiredActivity.this)) {
                        if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                            return;
                        }
                        mLastClickTime = SystemClock.elapsedRealtime();
                        if (Utils.isKeyboardVisible)
                            Utils.hideKeypad(BusinessAdditionalActionRequiredActivity.this);
                        chooseFilePopup(BusinessAdditionalActionRequiredActivity.this, selectedDocType);

                    }
                    // chooseFilePopup(BusinessAdditionalActionRequiredActivity.this, selectedDocType);
                }
            });
            enableOrDisableNext();
        }
    }

    private void websiteChanges(ActionRequiredResponse actionRequiredResponse) {
        websiteRevisionRequiredLL.setVisibility(View.VISIBLE);

        LinearLayout.LayoutParams layoutParamss1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        ArrayList<DisplayImageUtility.ImageHolder> imagesList = new ArrayList<>();
        for (int i = 0; i < actionRequiredResponse.getData().getWebsiteChange().size(); i++) {
            View websiteView = getLayoutInflater().inflate(R.layout.additional_website_changes_item, null);
            TextView tvheading = websiteView.findViewById(R.id.tvheading);
            CheckBox checkboxCB = websiteView.findViewById(R.id.checkboxCB);
            ImageView imgWebsite1 = websiteView.findViewById(R.id.imgWebsite1);
            ImageView imgWebsite2 = websiteView.findViewById(R.id.imgWebsite2);
            ImageView imgWebsite3 = websiteView.findViewById(R.id.imgWebsite3);

            int headerLength = actionRequiredResponse.getData().getWebsiteChange().get(i).getHeader().length();
            String websiteChanges = actionRequiredResponse.getData().getWebsiteChange().get(i).getHeader()
                    + " - " + actionRequiredResponse.getData().getWebsiteChange().get(i).getComment();
            Typeface font = Typeface.createFromAsset(getAssets(), "font/opensans_bold.ttf");
            SpannableStringBuilder SS = new SpannableStringBuilder(websiteChanges);
            SS.setSpan(new CustomTypefaceSpan("", font), 0, headerLength, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
            tvheading.setText(SS);

            if (actionRequiredResponse.getData().getWebsiteChange().get(i).getDocumentUrl1() != null
                    && !actionRequiredResponse.getData().getWebsiteChange().get(i).getDocumentUrl1().equals("")) {
                imagesList.add(displayWebsiteImage(actionRequiredResponse.getData().getWebsiteChange().get(i).getDocumentUrl1(), imgWebsite1));
            }
            if (actionRequiredResponse.getData().getWebsiteChange().get(i).getDocumentUrl2() != null
                    && !actionRequiredResponse.getData().getWebsiteChange().get(i).getDocumentUrl2().equals("")) {
                imagesList.add(displayWebsiteImage(actionRequiredResponse.getData().getWebsiteChange().get(i).getDocumentUrl2(), imgWebsite2));
            }
            if (actionRequiredResponse.getData().getWebsiteChange().get(i).getDocumentUrl3() != null
                    && !actionRequiredResponse.getData().getWebsiteChange().get(i).getDocumentUrl3().equals("")) {
                imagesList.add(displayWebsiteImage(actionRequiredResponse.getData().getWebsiteChange().get(i).getDocumentUrl3(), imgWebsite3));
            }

            fileUpload.put(actionRequiredResponse.getData().getWebsiteChange().get(i).getId(), null);

            websiteRevisionRequiredLL.addView(websiteView, layoutParamss1);

            checkboxCB.setTag(i);

            checkboxCB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    int pos = (int) compoundButton.getTag();
                    checkboxCB.setSelected(true);
                    LogUtils.d(TAG, "checkboxCB" + checkboxCB.isChecked());
                    if (fileUpload.containsKey(actionRequiredResponse.getData().getWebsiteChange().get(pos).getId())) {
                        fileUpload.replace(actionRequiredResponse.getData().getWebsiteChange().get(pos).getId(), b ? "true" : null);
                    }
                    enableOrDisableNext();
                }
            });
        }
        if (imagesList.size() > 0) {
            DisplayImageUtility utility = DisplayImageUtility.getInstance(getApplicationContext());
            utility.addImages(imagesList);
        }
        enableOrDisableNext();
    }

    private void informationRevision(ActionRequiredResponse actionRequiredResponse) {

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        List<InformationChangeData> informationChangeData = actionRequiredResponse.getData().getInformationChange();
        if (informationChangeData != null && informationChangeData.size() > 0) {
            InformationChangeData changeData = informationChangeData.get(0);
            if (changeData.getProposals() != null && changeData.getProposals().size() > 0) {
                proposalsMap = new HashMap<>();
                for (int count = 0; count < changeData.getProposals().size(); count++) {
                    ProposalsData data = changeData.getProposals().get(count);
                    List<ProposalsPropertiesData> proposalsPropertiesData = data.getProperties();
                    if (proposalsPropertiesData != null && proposalsPropertiesData.size() > 0) {
                        informationRevisionLL.setVisibility(View.VISIBLE);
                        for (int i = 0; i < proposalsPropertiesData.size(); i++) {
                            View inf1 = getLayoutInflater().inflate(R.layout.additional_information_change, null);
                            LinearLayout websiteChangeLL = inf1.findViewById(R.id.informationChange);
                            TextView typeNameTV = inf1.findViewById(R.id.type_nameTV);
                            TextView fieldNameTV = inf1.findViewById(R.id.field_nameTV);
                            TextView companyNameOriginal = inf1.findViewById(R.id.comapnyNameOriginal);
                            TextView companyNameProposed = inf1.findViewById(R.id.comapnyNamePropesed);
                            TextView tvMessage = inf1.findViewById(R.id.tvMessage);
                            ImageView imvAcceptTick = inf1.findViewById(R.id.imvAccepttick);
                            TextView tvAcceptMsg = inf1.findViewById(R.id.acceptMsgTV);
                            LinearLayout llDecline = inf1.findViewById(R.id.declineLL);
                            LinearLayout llAccept = inf1.findViewById(R.id.acceptLL);
                            ProposalsPropertiesData propertiesData = proposalsPropertiesData.get(i);
                            if (data.getDisplayName() != null) {
                                typeNameTV.setText(data.getDisplayName());
                            }

                            String fieldName = propertiesData.getDisplayName() != null ? propertiesData.getDisplayName() : "";
                            fieldNameTV.setText(fieldName);
                            if (propertiesData.getName().equalsIgnoreCase("phoneNumber")) {
                                companyNameOriginal.setText(Utils.formatPhoneNumber(propertiesData.getOriginalValue()));
                                companyNameProposed.setText(Utils.formatPhoneNumber(propertiesData.getProposedValue()));
                            } else {
                                companyNameOriginal.setText(propertiesData.getOriginalValue());
                                companyNameProposed.setText(propertiesData.getProposedValue());
                            }

                            if (propertiesData.getAdminMessage() != null && !propertiesData.getAdminMessage().equalsIgnoreCase("")) {
                                String message = "";
                                if (!propertiesData.getAdminMessage().startsWith("\"")) {
                                    message += "\"";
                                }
                                message += propertiesData.getAdminMessage();
                                if (!propertiesData.getAdminMessage().endsWith("\"")) {
                                    message += "\"";
                                }
                                tvMessage.setText(message);
                            }

                            String verificationKey = data.getType() + "" + propertiesData.getName();
                            typeNameTV.setTag(verificationKey);
                            proposalsMap.put(verificationKey, propertiesData);
                            fileUpload.put(verificationKey.trim().hashCode(), null);

                            informationRevisionLL.addView(inf1, layoutParams);
                            llAccept.setTag(inf1);
                            llAccept.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    imvAcceptTick.setVisibility(View.VISIBLE);
                                    tvAcceptMsg.setVisibility(View.VISIBLE);
                                    llAccept.setVisibility(View.GONE);
                                    llDecline.setVisibility(View.GONE);
                                    tvAcceptMsg.setText(getResources().getString(R.string.Accepted) + " " + Utils.getCurrentDate());
                                    View v = (View) view.getTag();
                                    TextView tv = v.findViewById(R.id.type_nameTV);
//                                    TextView displayNameTV = v.findViewById(R.id.display_nameTV);
//                                    String verificationKey1 = displayNameTV.getText().toString() + "" + tv.getText().toString();
                                    String verificationKey1 = (String) tv.getTag();
                                    if (fileUpload.containsKey(verificationKey1.trim().hashCode())) {
                                        fileUpload.replace(verificationKey1.trim().hashCode(), "true");
                                    }
                                    if (proposalsMap.get(verificationKey1) != null) {
                                        proposalsMap.get(verificationKey1).setUserAccepted(true);
                                        proposalsMap.get(verificationKey1).setUserMessage("Accepted");
                                    }
                                    enableOrDisableNext();

                                }
                            });
                            llDecline.setTag(inf1);
                            llDecline.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                                        return;
                                    }
                                    mLastClickTime = SystemClock.elapsedRealtime();
                                    View v = (View) view.getTag();
                                    showCommentDialog(v);
                                    if (!Utils.isKeyboardVisible)
                                        Utils.shwForcedKeypad(BusinessAdditionalActionRequiredActivity.this);
                                }
                            });
                        }
                    }
                }
            }
        }
        enableOrDisableNext();
    }


    private DisplayImageUtility.ImageHolder displayWebsiteImage(String imageId, ImageView iv) {
        iv.setVisibility(View.VISIBLE);
        iv.setTag(imageId);
        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                ShowFullPageImageDialog showImgDialog = new ShowFullPageImageDialog(BusinessAdditionalActionRequiredActivity.this, (String) v.getTag());
                showImgDialog.show();
            }
        });
        DisplayImageUtility.ImageHolder holder = new DisplayImageUtility.ImageHolder();
        holder.key = imageId;
        holder.imageView = iv;
        holder.resId = 0;
        return holder;
    }

    private String capFirstLetter(String text) {
        if (text == null || text.equals("")) {
            return "";
        }
        if (text.length() == 1) {
            return text.toUpperCase();
        }
        return text.substring(0, 1).toUpperCase() + text.substring(1);
    }

    private void showCommentDialog(final View view) {
        TextView tv = view.findViewById(R.id.type_nameTV);
        //TextView displayNameTV = view.findViewById(R.id.display_nameTV);
        TextView tvRemarks = view.findViewById(R.id.remarksTV);
        ImageView imvAcceptTick = view.findViewById(R.id.imvAccepttick);
        LinearLayout llDecline = view.findViewById(R.id.declineLL);
        LinearLayout llAccept = view.findViewById(R.id.acceptLL);
        TextView tvDeclinedMsg = view.findViewById(R.id.declineMsgTV);
        AddCommentsDialog dialog = new AddCommentsDialog(BusinessAdditionalActionRequiredActivity.this, null);
        dialog.setOnDialogClickListener(new OnDialogClickListener() {
            @Override
            public void onDialogClicked(String action, Object value) {
                if (action.equalsIgnoreCase(Utils.COMMENT_ACTION) && tv.getText() != null) {
                    String comm = (String) value;
                    tvRemarks.setText("\"" + comm + "\"");
                    imvAcceptTick.setVisibility(View.VISIBLE);
                    imvAcceptTick.setImageDrawable(getResources().getDrawable(R.drawable.ic_decline));
                    tvRemarks.setVisibility(View.VISIBLE);
                    llAccept.setVisibility(View.GONE);
                    tvDeclinedMsg.setVisibility(View.VISIBLE);
                    tvDeclinedMsg.setText(getString(R.string.Decline) + " " + Utils.getCurrentDate() + " due to: ");
                    llDecline.setVisibility(View.GONE);
                    //String verificationKey = displayNameTV.getText().toString() + "" + tv.getText().toString();
                    String verificationKey = (String) tv.getTag();
                    if (proposalsMap.get(verificationKey) != null) {
                        proposalsMap.get(verificationKey).setUserAccepted(false);
                        proposalsMap.get(verificationKey).setUserMessage(comm);
                        if (fileUpload.containsKey(verificationKey.trim().hashCode())) {
                            fileUpload.replace(verificationKey.trim().hashCode(), "false");
                        }
                    }
//                    if (Utils.isKeyboardVisible) {
//                        Utils.hideKeypad(BusinessAdditionalActionRequiredActivity.this);
//                    }
                }
                enableOrDisableNext();

            }
        });
        dialog.show();
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                if (Utils.isKeyboardVisible) {
                    Utils.hideKeypad(BusinessAdditionalActionRequiredActivity.this);
                }
            }
        });
    }

    private void showReserveRule(ActionRequiredResponse actionRequiredResponse) {
        llHeading.setVisibility(View.GONE);
        llBottomView.setVisibility(View.GONE);
        scrollview.setVisibility(View.GONE);
        llApprovedReserved.setVisibility(View.VISIBLE);
        reservedRule = true;
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        View reserveRule = getLayoutInflater().inflate(R.layout.activity_business_application_approved, null);

        TextView tv_mv = reserveRule.findViewById(R.id.tvMonthlyVolume);
        TextView tv_ht = reserveRule.findViewById(R.id.tvhighticket);
        TextView note = reserveRule.findViewById(R.id.noteTV);
        TextView tv_reserveAmount = reserveRule.findViewById(R.id.tvreserveAMountTransaction);
        TextView tv_reservePeriod = reserveRule.findViewById(R.id.tvreservePeriod);
        CardView cardAccept = reserveRule.findViewById(R.id.cardAccept);
        TextView tvcardDeclined = reserveRule.findViewById(R.id.cardDeclined);

        if (actionRequiredResponse.getData().getReserveRule().getReserveReason() != null
                && !actionRequiredResponse.getData().getReserveRule().getReserveReason().equals("")) {
            note.setText(actionRequiredResponse.getData().getReserveRule().getReserveReason());
        } else {
            note.setText(R.string.thank_you_for_your_interest_in_coyni_after_ncarefully_reviewing_the_coyni_team_made_ndecision_to_approve_your_application_with);
        }
        if (actionRequiredResponse.getData().getReserveRule().getMonthlyProcessingVolume() != null) {

            if (actionRequiredResponse.getData().getReserveRule().getMonthlyProcessingVolume().contains("CYN")) {
                tv_mv.setText(Utils.convertTwoDecimal(actionRequiredResponse.getData().getReserveRule().getMonthlyProcessingVolume()));
            } else {
                tv_mv.setText(Utils.convertTwoDecimal(actionRequiredResponse.getData().getReserveRule().getMonthlyProcessingVolume().replace("", "CYN")));
            }
        }
        if (actionRequiredResponse.getData().getReserveRule().getHighTicket() != null) {
            if (actionRequiredResponse.getData().getReserveRule().getHighTicket().contains("CYN")) {
                tv_ht.setText(Utils.convertTwoDecimal(actionRequiredResponse.getData().getReserveRule().getHighTicket()));
            } else {
                tv_ht.setText(Utils.convertTwoDecimal(actionRequiredResponse.getData().getReserveRule().getHighTicket().replace("", "CYN")));
            }
        }
        String percent = Utils.convertBigDecimalUSD(String.valueOf(actionRequiredResponse.getData().getReserveRule().getReserveAmount().toString()));
        tv_reserveAmount.setText(Utils.convertTwoDecimal(percent.replace("0*$", "") + " %"));
        tv_reservePeriod.setText(actionRequiredResponse.getData().getReserveRule().getReservePeriod() + " " + "days");

        ImageView i_iconIV = reserveRule.findViewById(R.id.i_iconIV);

        i_iconIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                ApplicationApprovedDialog dialog = new ApplicationApprovedDialog(BusinessAdditionalActionRequiredActivity.this);
                dialog.show();
            }
        });
        llApprovedReserved.addView(reserveRule, layoutParams);

        cardAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                reservedRuleAccepted = true;
                postSubmitAPiCall();
            }
        });

        tvcardDeclined.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                reservedRuleAccepted = false;
                //postSubmitAPiCall();
                showDeclineDialog();
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
//                        Utils.displayAlert("Requires Access to Camera.", BusinessAdditionalActionRequiredActivity.this, "", "");
                        Utils.showDialogPermission(BusinessAdditionalActionRequiredActivity.this, getString(R.string.allow_access_header), getString(R.string.camera_permission_desc));

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
                startActivity(new Intent(BusinessAdditionalActionRequiredActivity.this, CameraActivity.class).putExtra("FROM", "BAARA"));
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

        if (filesToUpload.containsKey(documentID)) {
            filesToUpload.replace(documentID, mediaFile);
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

            if (filesToUpload.containsKey(documentID)) {
                filesToUpload.replace(documentID, mediaFile);
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

    private void showDeclineDialog() {
        DialogAttributes attributes = new DialogAttributes(getString(R.string.decline_reserve_rules),
                getString(R.string.decline_reserve_rules_message), getString(R.string.yes),
                getString(R.string.no_go_back));
        CustomConfirmationDialog dialog = new CustomConfirmationDialog(BusinessAdditionalActionRequiredActivity.this, attributes);
        dialog.setOnDialogClickListener(new OnDialogClickListener() {
            @Override
            public void onDialogClicked(String action, Object value) {
                if (action.equalsIgnoreCase(getString(R.string.yes))) {
                    postSubmitAPiCall();
                }
            }
        });

        dialog.show();

    }

    public void enableOrDisableNext() {
        try {
            LogUtils.d(TAG, "fileUpload" + fileUpload);
            if (fileUpload.containsValue(null)) {
                isSubmitEnabled = false;
                submitCV.setClickable(false);
                submitCV.setEnabled(false);
                submitCV.setCardBackgroundColor(getResources().getColor(R.color.inactive_color));
            } else {
                isSubmitEnabled = true;
                submitCV.setClickable(true);
                submitCV.setEnabled(true);
                submitCV.setCardBackgroundColor(getResources().getColor(R.color.primary_color));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
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

    @Override
    public void onVisibilityChanged(boolean visible) {
        Utils.isKeyboardVisible = visible;
    }

}
