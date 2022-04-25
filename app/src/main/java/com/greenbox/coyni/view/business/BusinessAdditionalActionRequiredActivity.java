package com.greenbox.coyni.view.business;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.google.android.material.textfield.TextInputLayout;
import com.greenbox.coyni.R;
import com.greenbox.coyni.custom_camera.CameraActivity;
import com.greenbox.coyni.model.underwriting.ActionRequiredResponse;
import com.greenbox.coyni.model.underwriting.InformationChangeData;
import com.greenbox.coyni.model.underwriting.ProposalsData;
import com.greenbox.coyni.model.underwriting.ProposalsPropertiesSubmitRequestData;
import com.greenbox.coyni.model.underwriting.ProposalsSubmitRequestData;
import com.greenbox.coyni.utils.CustomTypefaceSpan;
import com.greenbox.coyni.utils.FileUtils;
import com.greenbox.coyni.utils.LogUtils;
import com.greenbox.coyni.utils.Utils;
import com.greenbox.coyni.view.BaseActivity;
import com.greenbox.coyni.viewmodel.UnderwritingUserActionRequiredViewModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;

public class BusinessAdditionalActionRequiredActivity extends BaseActivity {
    public ScrollView scrollview;
    LinearLayout additionReservedLL, llapprovedreserved, llHeading, llBottomView, additionalDocumentRequiredLL,
            websiteRevisionRequiredLL, informationRevisionLL, actionReqFileUploadedLL, sscFileUploadLL,
            actionReqFileUploadLL, businessLicenseUploadLL, lincenseFileUploadedLL, acceptLL, declineLL,
            acceptDeclineLL, acceptdneLL, declindneLL;
    String selectedDocType = "";
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 102;
    private static final int ACTIVITY_CHOOSE_FILE = 3;
    private static final int PICK_IMAGE_REQUEST = 4;
    Long mLastClickTime = 0L;
    public static BusinessAdditionalActionRequiredActivity businessAdditionalActionRequired;
    public static File addtional2fFle = null, businessLincenseFile = null;
    public boolean isSubmitEnabled = false;
    private EditText addNoteET;
    public CardView submitCV;
    private UnderwritingUserActionRequiredViewModel underwritingUserActionRequiredViewModel;
    private HashMap<Integer, String> fileUpload;
    private ActionRequiredResponse actionRequired;
    private int documentID;
    private LinearLayout selectedLayout = null;
    private TextView selectedText = null;
    public static ArrayList<File> documentsFIle;
    private JSONObject informationJSON;
    private ImageView imvAcceptTick;
    private TextView tvdeclinedMsg;
    private TextView tvRemarks;
    private LinearLayout llDecline;
    private LinearLayout llAccept;
    private String currentDateTimeString;
    private String currentDateString;
    private boolean userAccepted = false;
    public static File mediaFile;
    private boolean reservedRuleAccepted = false;
    private ImageView imvCLose;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_additional_action_required);

        businessAdditionalActionRequired = this;
        initFields();
        initObserver();
        enableOrDisableNext();
    }

    private void initFields() {

        additionReservedLL = findViewById(R.id.lladditionReserve);
        llapprovedreserved = findViewById(R.id.llapprovedreserved);
        scrollview = findViewById(R.id.scrollview);
        llHeading = findViewById(R.id.llHeading);
        llBottomView = findViewById(R.id.llBottomView);
        additionalDocumentRequiredLL = findViewById(R.id.ll_document_required);
        websiteRevisionRequiredLL = findViewById(R.id.website_revision_required);
        informationRevisionLL = findViewById(R.id.information_revision);
        imvCLose = findViewById(R.id.imvCLose);
        submitCV = findViewById(R.id.submitCV);

        underwritingUserActionRequiredViewModel = new ViewModelProvider(this).get(UnderwritingUserActionRequiredViewModel.class);
        underwritingUserActionRequiredViewModel.getAdditionalActionRequiredData();

        fileUpload = new HashMap<Integer, String>();
        documentsFIle = new ArrayList<>();

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
                postSubmitAPiCall();
            }
        });
    }

    private void postSubmitAPiCall() {

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

            String userMessage = "";
            if (tvRemarks != null) {
                if (tvRemarks.getText() != null && tvRemarks.getText().toString().trim().equals("")) {
                    userMessage = "Accepted";
                } else {
                    userMessage = tvRemarks.getText().toString();
                }
            }

            informationJSON.put("documentIdList", documents);
            informationJSON.put("websiteUpdates", website);

            JSONArray proposals = new JSONArray();
            JSONObject proposalsobj = new JSONObject();

            JSONArray proposalsobjARRAY = new JSONArray();
            JSONObject PROPERTIESARRAY = new JSONObject();

            if (actionRequired.getData().getInformationChange() != null) {
                for (int i = 0; i <= actionRequired.getData().getInformationChange().size() - 1; i++) {

                    InformationChangeData data = actionRequired.getData().getInformationChange().get(i);
                    List<ProposalsData> proposalsData = data.getProposals();

                    for (int j = 0; j < proposalsData.size(); j++) {
                        ProposalsData proposal = proposalsData.get(j);
                        List<ProposalsPropertiesSubmitRequestData> list = new ArrayList<>();

                        ProposalsPropertiesSubmitRequestData requestData = new ProposalsPropertiesSubmitRequestData();
                        PROPERTIESARRAY.put("isUserAccepted", userAccepted);
                        PROPERTIESARRAY.put("name", proposal.getProperties().get(0).getName());
                        PROPERTIESARRAY.put("userMessage", userMessage);
                        proposalsobjARRAY.put(PROPERTIESARRAY);

                        ProposalsSubmitRequestData propsalsdata = new ProposalsSubmitRequestData();
                        proposalsobj.put("dbId", proposal.getDbId());
                        proposalsobj.put("type", proposal.getType());
                        proposalsobj.put("properties", proposalsobjARRAY);

                        proposals.put(proposalsobj);

                        informationJSON.put("proposals", proposals);
                    }

                }
            }

        } catch (JSONException je) {
            je.printStackTrace();
        }

        LogUtils.d(TAG, "jsonnn    " + informationJSON.toString());
        OkHttpClient client = new OkHttpClient().newBuilder().build();
        MultipartBody.Builder buildernew = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("information", null,
                        RequestBody.create(MediaType.parse("application/json"), informationJSON.toString().getBytes()));   //Here you can add the fix number of data.

        for (int i = 0; i < documentsFIle.size(); i++) {
            buildernew.addFormDataPart("documents", documentsFIle.get(i).getName() + ".jpg", RequestBody.create(MediaType.parse("application/octet-stream"), new File(String.valueOf(documentsFIle.get(i)))));
            LogUtils.d(TAG, "documentsssfffff" + new File(String.valueOf(documentsFIle.get(i))));
        }

        LogUtils.d(TAG, "documentsss56787656" + documentsFIle);

        MultipartBody requestBody = buildernew.build();
        showProgressDialog();
        underwritingUserActionRequiredViewModel.submitAdditionalActionRequired(requestBody);
    }

    private void initObserver() {
        underwritingUserActionRequiredViewModel.getActionRequiredSubmitResponseMutableLiveData().observe(this, new Observer<ActionRequiredResponse>() {
            @Override
            public void onChanged(ActionRequiredResponse actionRequiredResponse) {
                dismissDialog();
                if (actionRequiredResponse != null && actionRequiredResponse.getStatus().equalsIgnoreCase(Utils.SUCCESS)) {

                } else {
                    String errorMessage = getString(R.string.something_went_wrong);
                    if (actionRequiredResponse != null && actionRequiredResponse.getError() != null
                            && actionRequiredResponse.getError().getErrorDescription() != null
                            && !actionRequiredResponse.getError().getErrorDescription().equals("")) {
                        errorMessage = actionRequiredResponse.getError().getErrorDescription();
                        Utils.displayAlert(errorMessage,
                                BusinessAdditionalActionRequiredActivity.this, "", "");
                    }
                }
            }
        });

        underwritingUserActionRequiredViewModel.getUserAccountLimitsMutableLiveData().observe(this,
                new Observer<ActionRequiredResponse>() {
                    @Override
                    public void onChanged(ActionRequiredResponse actionRequiredResponse) {
                        try {
                            // LogUtils.d(TAG, "ActionRequiredResponse" + actionRequiredResponse.getData().getWebsiteChange().size());
                            actionRequired = actionRequiredResponse;
                            if (actionRequiredResponse != null && actionRequiredResponse.getStatus().equalsIgnoreCase("SUCCESS")) {

                                if (actionRequiredResponse != null && actionRequiredResponse.getData() != null) {
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

        for (int i = 0; i < actionRequiredResponse.getData().getWebsiteChange().size(); i++) {
            View websiteView = getLayoutInflater().inflate(R.layout.additional_website_changes_item, null);
            TextView tvheading = websiteView.findViewById(R.id.tvheading);
            CheckBox checkboxCB = websiteView.findViewById(R.id.checkboxCB);
            ImageView imgWebsite = websiteView.findViewById(R.id.imgWebsite);
            int headerLength = actionRequiredResponse.getData().getWebsiteChange().get(i).getHeader().length();
            String websiteChanges = actionRequiredResponse.getData().getWebsiteChange().get(i).getHeader()
                    + " - " + actionRequiredResponse.getData().getWebsiteChange().get(i).getComment();
            Typeface font = Typeface.createFromAsset(getAssets(), "font/opensans_bold.ttf");
            SpannableStringBuilder SS = new SpannableStringBuilder(websiteChanges);
            SS.setSpan(new CustomTypefaceSpan("", font), 0, headerLength, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
            tvheading.setText(SS);

            if (actionRequiredResponse.getData().getWebsiteChange().get(i).getDocumentUrl1() != null) {
                imgWebsite.setVisibility(View.VISIBLE);
                int width = imgWebsite.getWidth();
                Glide.with(this)
                        .load(actionRequiredResponse.getData().getWebsiteChange().get(i).getDocumentUrl1())
                        .fitCenter()
                        .override(imgWebsite.getWidth(), Target.SIZE_ORIGINAL)
                        .into(imgWebsite);
            } else {
                imgWebsite.setVisibility(View.GONE);
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
                        fileUpload.replace(actionRequiredResponse.getData().getWebsiteChange().get(pos).getId(), "true");
                    }
                    enableOrDisableNext();
                }
            });

        }

    }

    private void informationRevision(ActionRequiredResponse actionRequiredResponse) {

        informationRevisionLL.setVisibility(View.VISIBLE);
        LinearLayout.LayoutParams layoutParamss1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        for (int i = 0; i < actionRequiredResponse.getData().getInformationChange().size(); i++) {
            View inf1 = getLayoutInflater().inflate(R.layout.additional_information_change, null);
            LinearLayout websiteChangeLL = inf1.findViewById(R.id.informationChange);
            TextView comapny_nameTV = inf1.findViewById(R.id.comapny_nameTV);
//            TextView sucesscomapny_nameTV = inf1.findViewById(R.id.sucess_comapny_nameTV);
            TextView comapnynameOriginal = inf1.findViewById(R.id.comapnyNameOriginal);
            TextView comapnynameProposed = inf1.findViewById(R.id.comapnyNamePropesed);
            TextView tvMessage = inf1.findViewById(R.id.tvMessage);
            imvAcceptTick = inf1.findViewById(R.id.imvAccepttick);
            TextView tvacceptMsg = inf1.findViewById(R.id.acceptMsgTV);
            tvdeclinedMsg = inf1.findViewById(R.id.declineMsgTV);
            tvRemarks = inf1.findViewById(R.id.remarksTV);
            llDecline = inf1.findViewById(R.id.declineLL);
            llAccept = inf1.findViewById(R.id.acceptLL);

            websiteChangeLL.setVisibility(View.VISIBLE);

            if (actionRequiredResponse.getData().getInformationChange().get(i).getProposals().get(0) != null) {
                if (actionRequiredResponse.getData().getInformationChange().get(i).getProposals().get(0).getProperties().get(0) != null) {
                    comapny_nameTV.setText(actionRequiredResponse.getData().getInformationChange().get(i).getProposals().get(0).getProperties().get(0).getName());
//                    sucesscomapny_nameTV.setText(actionRequiredResponse.getData().getInformationChange().get(i).getProposals().get(0).getProperties().get(0).getName());
                    comapnynameOriginal.setText(actionRequiredResponse.getData().getInformationChange().get(i).getProposals().get(0).getProperties().get(0).getOriginalValue());
                    comapnynameProposed.setText(actionRequiredResponse.getData().getInformationChange().get(i).getProposals().get(0).getProperties().get(0).getProposedValue());
                    tvMessage.setText(actionRequiredResponse.getData().getInformationChange().get(i).getProposals().get(0).getProperties().get(0).getAdminMessage());
                }
            }

            fileUpload.put(i, null);

            int pos = i;

            informationRevisionLL.addView(inf1, layoutParamss1);

            currentDateTimeString = java.text.DateFormat.getDateTimeInstance().format(new Date());

            llAccept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    imvAcceptTick.setVisibility(View.VISIBLE);
                    tvacceptMsg.setVisibility(View.VISIBLE);
                    llAccept.setVisibility(View.GONE);
                    llDecline.setVisibility(View.GONE);
                    tvacceptMsg.setText(getResources().getString(R.string.Accepted) + " " + Utils.getCurrentDate());

                    if (fileUpload.containsKey(pos)) {
                        fileUpload.replace(pos, "true");
                    }
                    userAccepted = true;
                    enableOrDisableNext();

                }
            });

            llDecline.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    displayComments(pos);
                }
            });
        }
    }

    private void showReserveRule(ActionRequiredResponse actionRequiredResponse) {

        llHeading.setVisibility(View.GONE);
        llBottomView.setVisibility(View.GONE);
        scrollview.setVisibility(View.GONE);
        llapprovedreserved.setVisibility(View.VISIBLE);

        LinearLayout.LayoutParams layoutParamss1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        View reserverRule = getLayoutInflater().inflate(R.layout.activity_business_application_approved, null);

        TextView tv_mv = reserverRule.findViewById(R.id.tvMonthlyVolume);
        TextView tv_ht = reserverRule.findViewById(R.id.tvhighticket);
        TextView tv_reserveamount = reserverRule.findViewById(R.id.tvreserveAMountTransaction);
        TextView tv_reserveperiod = reserverRule.findViewById(R.id.tvreservePeriod);
        CardView cardAccept = reserverRule.findViewById(R.id.cardAccept);
        TextView tvcardDeclined = reserverRule.findViewById(R.id.cardDeclined);

        tv_mv.setText(actionRequiredResponse.getData().getReserveRule().getMonthlyProcessingVolume());
        tv_ht.setText(actionRequiredResponse.getData().getReserveRule().getHighTicket());
        tv_reserveamount.setText(actionRequiredResponse.getData().getReserveRule().getReserveAmount().toString().replace("0*$", "") + " %");
        tv_reserveperiod.setText(actionRequiredResponse.getData().getReserveRule().getReservePeriod() + " " + "days");

        llapprovedreserved.addView(reserverRule, layoutParamss1);

        cardAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                reservedRuleAccepted = true;
                postSubmitAPiCall();

            }
        });

        tvcardDeclined.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reservedRuleAccepted = false;
                postSubmitAPiCall();

            }
        });


    }

    private void displayComments(int position) {
        Utils.shwForcedKeypad(BusinessAdditionalActionRequiredActivity.this);
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
                    tvRemarks.setText(addNoteET.getText().toString().trim());
                    cvvDialog.dismiss();
                    Utils.hideKeypad(BusinessAdditionalActionRequiredActivity.this);
                    imvAcceptTick.setVisibility(View.VISIBLE);
                    imvAcceptTick.setImageDrawable(getResources().getDrawable(R.drawable.ic_decline));
                    tvRemarks.setVisibility(View.VISIBLE);
                    llAccept.setVisibility(View.GONE);
                    tvdeclinedMsg.setVisibility(View.VISIBLE);

                    tvdeclinedMsg.setText(getString(R.string.Decline) + " " + Utils.getCurrentDate() + " due to : ");
                    llDecline.setVisibility(View.GONE);
                    if (fileUpload.containsKey(position)) {
                        fileUpload.replace(position, "true");
                    }
                    userAccepted = false;
                    enableOrDisableNext();
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
                    doneBtn.setCardBackgroundColor(getResources().getColor(R.color.inactive_color));

                } else {
                    addNoteTIL.setCounterEnabled(true);
                    doneBtn.setCardBackgroundColor(getResources().getColor(R.color.primary_color));
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
        if (!tvRemarks.getText().toString().trim().equals("")) {
            addNoteET.setText(tvRemarks.getText().toString().trim());
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

        if (selectedLayout != null) {
            selectedLayout.setVisibility(View.VISIBLE);
            selectedText.setVisibility(View.GONE);
        }

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

            LogUtils.d(TAG, "fileUpload" + fileUpload);

        } catch (Exception e) {
            e.printStackTrace();

        }

    }


    public void enableOrDisableNext() {
        try {
            LogUtils.d(TAG, "fileUpload" + fileUpload);
            if (fileUpload.containsValue(null)) {
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

}
