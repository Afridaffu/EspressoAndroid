package com.coyni.mapp.view.business;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.coyni.mapp.custom_camera.CameraUtility.BROWSE;
import static com.coyni.mapp.custom_camera.CameraUtility.CHOOSE_LIBRARY;
import static com.coyni.mapp.custom_camera.CameraUtility.TAKE_PHOTO;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.coyni.mapp.R;
import com.coyni.mapp.custom_camera.CameraHandlerActivity;
import com.coyni.mapp.custom_camera.CameraUtility;
import com.coyni.mapp.dialogs.AddCommentsDialog;
import com.coyni.mapp.dialogs.ApplicationApprovedDialog;
import com.coyni.mapp.dialogs.CustomConfirmationDialog;
import com.coyni.mapp.dialogs.FilePickerDialog;
import com.coyni.mapp.dialogs.OnDialogClickListener;
import com.coyni.mapp.dialogs.ShowFullPageImageDialog;
import com.coyni.mapp.interfaces.OnKeyboardVisibilityListener;
import com.coyni.mapp.model.DialogAttributes;
import com.coyni.mapp.model.DocLayout;
import com.coyni.mapp.model.actionRqrd.BankRequest;
import com.coyni.mapp.model.actionRqrd.InformationRequest;
import com.coyni.mapp.model.actionRqrd.PropertyRequest;
import com.coyni.mapp.model.actionRqrd.ProposalRequest;
import com.coyni.mapp.model.identity_verification.IdentityImageResponse;
import com.coyni.mapp.model.identity_verification.RemoveIdentityResponse;
import com.coyni.mapp.model.paymentmethods.PaymentMethodsResponse;
import com.coyni.mapp.model.underwriting.ActionRequiredResponse;
import com.coyni.mapp.model.underwriting.ActionRequiredSubmitResponse;
import com.coyni.mapp.model.underwriting.InformationChangeData;
import com.coyni.mapp.model.underwriting.ProposalsData;
import com.coyni.mapp.model.underwriting.ProposalsPropertiesData;
import com.coyni.mapp.utils.CustomTypefaceSpan;
import com.coyni.mapp.utils.DisplayImageUtility;
import com.coyni.mapp.utils.LogUtils;
import com.coyni.mapp.utils.MyApplication;
import com.coyni.mapp.utils.Utils;
import com.coyni.mapp.view.BaseActivity;
import com.coyni.mapp.viewmodel.BusinessDashboardViewModel;
import com.coyni.mapp.viewmodel.IdentityVerificationViewModel;
import com.coyni.mapp.viewmodel.UnderwritingUserActionRequiredViewModel;
import com.google.gson.Gson;

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
    private LinearLayout llApprovedReserved, llHeading, llBottomView, additionalDocumentRequiredLL,
            websiteRevisionRequiredLL, informationRevisionLL, bank_information, uploadLayout = null;
    private String selectedDocType = "";
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 102;
    private Long mLastClickTime = 0L;
    public boolean isSubmitEnabled = false;
    public CardView submitCV;
    private UnderwritingUserActionRequiredViewModel underwritingUserActionRequiredViewModel;
    private IdentityVerificationViewModel identityVerificationViewModel;
    private HashMap<Integer, String> fileUpload;
    private ActionRequiredResponse actionRequired;
    private int documentID;
    private RelativeLayout additionalActionRL;
    private TextView selectedText = null;
    private JSONObject informationJSON;
    //    private InformationRequest informationJSON;
    private File mediaFile;
    private boolean reservedRuleAccepted = false;
    private boolean reservedRule = false;
    private ImageView imvCLose;
    private HashMap<String, ProposalsPropertiesData> proposalsMap;
    private TextView adminMessageTV;
    private MyApplication objMyApplication;
    private ProposalsData bankProposal;
    private BusinessDashboardViewModel businessDashboardViewModel;
    private int bankID = 0, lastUploadedDoc = 0;
    private List<DocLayout> listOfDocLayouts = new ArrayList<>();
    LinearLayout.LayoutParams layoutParamss = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_additional_action_required);
        initFields();
        initObserver();
    }

    @Override
    public void onBackPressed() {
        if (!reservedRule) {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        objMyApplication.setBankAccount(null);
        super.onDestroy();
    }

    private void initFields() {
        objMyApplication = (MyApplication) getApplicationContext();
        llApprovedReserved = findViewById(R.id.llapprovedreserved);
        scrollview = findViewById(R.id.scrollview);
        llHeading = findViewById(R.id.llHeading);
        llBottomView = findViewById(R.id.llBottomView);
        additionalDocumentRequiredLL = findViewById(R.id.ll_document_required);
        websiteRevisionRequiredLL = findViewById(R.id.website_revision_required);
        bank_information = findViewById(R.id.bank_information);
        informationRevisionLL = findViewById(R.id.information_revision);
        imvCLose = findViewById(R.id.imvCLose);
        submitCV = findViewById(R.id.submitCV);
        adminMessageTV = findViewById(R.id.adminMessageTV);
        additionalActionRL = findViewById(R.id.additionalActionRL);
        businessDashboardViewModel = new ViewModelProvider(this).get(BusinessDashboardViewModel.class);
        underwritingUserActionRequiredViewModel = new ViewModelProvider(this).get(UnderwritingUserActionRequiredViewModel.class);
        identityVerificationViewModel = new ViewModelProvider(this).get(IdentityVerificationViewModel.class);
        showProgressDialog();
        underwritingUserActionRequiredViewModel.getAdditionalActionRequiredData();

        fileUpload = new HashMap<Integer, String>();

        setKeyboardVisibilityListener(BusinessAdditionalActionRequiredActivity.this);
        businessDashboardViewModel.meBusinessPaymentMethods();

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
    }

////     Keep this code for future use
//    private void postSubmitAPiCall() {
//        showProgressDialog();
//
////        informationJSON = new JSONObject();
//        informationJSON = new InformationRequest();
//
//        try {
////            JSONArray documents = new JSONArray();
////            JSONArray website = new JSONArray();
//            List<Integer> website = new ArrayList<>();
////            JSONObject bankRequest = new JSONObject();
//            BankRequest bankRequest = new BankRequest();
//
////            if (actionRequired.getData().getAdditionalDocument() != null) {
////                for (int i = 0; i <= actionRequired.getData().getAdditionalDocument().size() - 1; i++) {
////                    documents.put(actionRequired.getData().getAdditionalDocument().get(i).getId());
////                }
////            }
//            if (actionRequired.getData().getWebsiteChange() != null) {
//                for (int i = 0; i <= actionRequired.getData().getWebsiteChange().size() - 1; i++) {
////                    website.put(actionRequired.getData().getWebsiteChange().get(i).getId());
//                    website.add(actionRequired.getData().getWebsiteChange().get(i).getId());
//                }
//            }
//            if (actionRequired.getData().getReserveRule() != null) {
//                informationJSON.setReserveRuleAccepted(reservedRuleAccepted);
//            }
//
//            if (objMyApplication.getBankAccount() != null) {
//                bankRequest.setAccountName(objMyApplication.getBankAccount().getAccountName());
//                bankRequest.setAccountNumber(objMyApplication.getBankAccount().getAccountNumber());
//                if (bankID != 0) {
//                    bankRequest.setBankId(bankID);
//                } else {
//                    bankRequest.setBankId(bankProposal.getDbId());
//                }
//                bankRequest.setGiactReq(true);
//                bankRequest.setRoutingNumber(objMyApplication.getBankAccount().getRoutingNumber());
//            }
////            informationJSON.put("documentIdList", documents);
//            informationJSON.setWebsiteUpdates(website);
//            if (bankRequest.getAccountName() != null) {
//                informationJSON.setBankRequest(bankRequest);
//            } else {
//                informationJSON.setBankRequest(null);
//            }
//
////            JSONArray proposals = new JSONArray();
//            List<ProposalRequest> proposals = new ArrayList<>();
//
//            if (actionRequired.getData().getInformationChange() != null) {
//                for (int i = 0; i < actionRequired.getData().getInformationChange().size(); i++) {
//                    InformationChangeData data = actionRequired.getData().getInformationChange().get(i);
//                    List<ProposalsData> proposalsData = data.getProposals();
//                    for (int j = 0; j < proposalsData.size(); j++) {
//                        ProposalsData proposal = proposalsData.get(j);
//                        String type = proposal.getType();
////                        JSONObject proposalsObj = new JSONObject();
//                        ProposalRequest proposalsObj = new ProposalRequest();
////                        JSONArray proposalsArray = new JSONArray();
//                        List<PropertyRequest> proposalsArray = new ArrayList<>();
////                        JSONArray bankProposalsArray = new JSONArray();
//                        List<PropertyRequest> bankProposalsArray = new ArrayList<>();
//                        if (type.toLowerCase().equals("bank")) {
//                            if (proposal != null && proposal.getProperties() != null && proposal.getProperties().size() > 0) {
//                                for (int k = 0; k < proposal.getProperties().size(); k++) {
//                                    ProposalsPropertiesData property = proposal.getProperties().get(k);
////                                    JSONObject propertyObj = new JSONObject();
//                                    PropertyRequest propertyObj = new PropertyRequest();
//                                    if (bankProposal.getProperties().get(0).getAdminMessage() != null) {
//                                        propertyObj.setAdminMessage(bankProposal.getProperties().get(0).getAdminMessage());
//                                    } else {
//                                        propertyObj.setAdminMessage(null);
//                                    }
//                                    if (bankProposal.getProperties().get(0).getDisplayName() != null) {
//                                        propertyObj.setDisplayName(bankProposal.getProperties().get(0).getDisplayName());
//                                    } else {
//                                        propertyObj.setDisplayName(null);
//                                    }
//                                    propertyObj.setUserAccepted(true);
//                                    propertyObj.setName(bankProposal.getProperties().get(0).getName());
//                                    if (bankProposal.getProperties().get(0).getOriginalValue() != null) {
//                                        propertyObj.setOriginalValue(bankProposal.getProperties().get(0).getOriginalValue());
//                                    } else {
//                                        propertyObj.setOriginalValue(null);
//                                    }
//                                    if (bankProposal.getProperties().get(0).getProposedValue() != null) {
//                                        propertyObj.setProposedValue(bankProposal.getProperties().get(0).getProposedValue());
//                                    } else {
//                                        propertyObj.setProposedValue(null);
//                                    }
//                                    if (bankProposal.getProperties().get(0).getUserMessage() != null) {
//                                        propertyObj.setUserMessage(bankProposal.getProperties().get(0).getUserMessage());
//                                    } else {
//                                        propertyObj.setUserMessage(null);
//                                    }
//                                    bankProposalsArray.add(propertyObj);
//                                }
//                            }
//
//                            if (bankID != 0) {
//                                proposalsObj.setDbId(bankID);
//                            } else {
//                                proposalsObj.setDbId(proposal.getDbId());
//                            }
//                            if (bankProposal.getDisplayName() != null) {
//                                proposalsObj.setDisplayName(bankProposal.getDisplayName());
//                            } else {
//                                proposalsObj.setDisplayName(null);
//                            }
//                            if (bankProposal.getFirstName() != null) {
//                                proposalsObj.setFirstName(bankProposal.getFirstName());
//                            } else {
//                                proposalsObj.setFirstName(null);
//                            }
//                            if (bankProposal.getLastName() != null) {
//                                proposalsObj.setLastName(bankProposal.getLastName());
//                            } else {
//                                proposalsObj.setLastName(null);
//                            }
//                            proposalsObj.setType(proposal.getType());
//                            proposalsObj.setProperties(bankProposalsArray);
//                        } else {
//                            if (proposal != null && proposal.getProperties() != null && proposal.getProperties().size() > 0) {
//                                for (int k = 0; k < proposal.getProperties().size(); k++) {
//                                    ProposalsPropertiesData property = proposal.getProperties().get(k);
////                                    JSONObject propertyObj = new JSONObject();
//                                    PropertyRequest propertyObj = new PropertyRequest();
//                                    String verificationKey = type + "" + property.getName();
//                                    propertyObj.setUserAccepted(proposalsMap.get(verificationKey).isUserAccepted());
//                                    propertyObj.setName(property.getName());
//                                    propertyObj.setUserMessage(proposalsMap.get(verificationKey).getUserMessage());
//                                    proposalsArray.add(propertyObj);
//                                }
//                            }
//
//                            proposalsObj.setDbId(proposal.getDbId());
//                            proposalsObj.setType(proposal.getType());
//                            proposalsObj.setProperties(proposalsArray);
//                        }
//                        proposals.add(proposalsObj);
//                        informationJSON.setProposals(proposals);
//                    }
//
//                }
//            }
//
//
//            // new code
////            List<MultipartBody.Part> multiparts = new ArrayList<>();
////
////            MultipartBody.Part[] docs = new MultipartBody.Part[filesToUpload.size()];
////
////            Map<Integer, File> map = new TreeMap<Integer, File>(filesToUpload);
////
////            for (Map.Entry<Integer, File> entry : map.entrySet()) {
////                RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), entry.getValue());
////                MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData("documents",
////                        entry.getValue().getName(), requestBody);
////                multiparts.add(fileToUpload);
////                LogUtils.e("Key and Name", "" + entry.getKey() + " - " + entry.getValue().getName());
////            }
////
////            for (int i = 0; i < multiparts.size(); i++) {
////                docs[i] = multiparts.get(i);
////            }
////
////            RequestBody underwritingActionRequired = RequestBody.create(MediaType.parse("application/json"),
////                    String.valueOf(informationJSON));
////            RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), informationJSON.toString());
//
////            underwritingUserActionRequiredViewModel.submitActionRequired(docs, underwritingActionRequired);
//            Log.e("JSON",new Gson().toJson(informationJSON));
////            underwritingUserActionRequiredViewModel.submitMerchantActionRequired(informationJSON);
//
//            Log.e("JSON", informationJSON.toString());
//            //new code
//        } catch (Exception e) {
//            e.printStackTrace();
//            dismissDialog();
//        }
//    }

    private void postSubmitAPiCall() {
        showProgressDialog();

        informationJSON = new JSONObject();
        try {
            JSONArray website = new JSONArray();
            JSONObject bankRequest = new JSONObject();
            if (actionRequired.getData().getWebsiteChange() != null) {
                for (int i = 0; i <= actionRequired.getData().getWebsiteChange().size() - 1; i++) {
                    website.put(actionRequired.getData().getWebsiteChange().get(i).getId());
                }
            }
            if (actionRequired.getData().getReserveRule() != null) {
                informationJSON.put("reserveRuleAccepted", reservedRuleAccepted);
            }

            if (objMyApplication.getBankAccount() != null) {
                bankRequest.put("accountName", objMyApplication.getBankAccount().getAccountName());
                bankRequest.put("accountNumber", objMyApplication.getBankAccount().getAccountNumber());
                if (bankID != 0) {
                    bankRequest.put("bankId", bankID);
                } else {
                    bankRequest.put("bankId", bankProposal.getDbId());
                }
                bankRequest.put("giactReq", true);
                bankRequest.put("routingNumber", objMyApplication.getBankAccount().getRoutingNumber());
            }
            informationJSON.put("websiteUpdates", website);
            if (bankRequest.length() > 0) {
                informationJSON.put("bankRequest", bankRequest);
            } else {
                informationJSON.put("bankRequest", JSONObject.NULL);
            }

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
                        JSONArray bankProposalsArray = new JSONArray();
                        if (type.toLowerCase().equals("bank")) {
                            if (proposal != null && proposal.getProperties() != null && proposal.getProperties().size() > 0) {
                                for (int k = 0; k < proposal.getProperties().size(); k++) {
                                    ProposalsPropertiesData property = proposal.getProperties().get(k);
                                    JSONObject propertyObj = new JSONObject();
                                    if (bankProposal.getProperties().get(0).getAdminMessage() != null) {
                                        propertyObj.put("adminMessage", bankProposal.getProperties().get(0).getAdminMessage());
                                    } else {
                                        propertyObj.put("adminMessage", JSONObject.NULL);
                                    }
                                    if (bankProposal.getProperties().get(0).getDisplayName() != null) {
                                        propertyObj.put("displayName", bankProposal.getProperties().get(0).getDisplayName());
                                    } else {
                                        propertyObj.put("displayName", JSONObject.NULL);
                                    }
                                    propertyObj.put("isUserAccepted", true);
                                    propertyObj.put("name", bankProposal.getProperties().get(0).getName());
                                    if (bankProposal.getProperties().get(0).getOriginalValue() != null) {
                                        propertyObj.put("originalValue", bankProposal.getProperties().get(0).getOriginalValue());
                                    } else {
                                        propertyObj.put("originalValue", JSONObject.NULL);
                                    }
                                    if (bankProposal.getProperties().get(0).getProposedValue() != null) {
                                        propertyObj.put("proposedValue", bankProposal.getProperties().get(0).getProposedValue());
                                    } else {
                                        propertyObj.put("proposedValue", JSONObject.NULL);
                                    }
                                    if (bankProposal.getProperties().get(0).getUserMessage() != null) {
                                        propertyObj.put("userMessage", bankProposal.getProperties().get(0).getUserMessage());
                                    } else {
                                        propertyObj.put("userMessage", JSONObject.NULL);
                                    }
                                    bankProposalsArray.put(propertyObj);
                                }
                            }

                            if (bankID != 0) {
                                proposalsObj.put("dbId", bankID);
                            } else {
                                proposalsObj.put("dbId", proposal.getDbId());
                            }
                            if (bankProposal.getDisplayName() != null) {
                                proposalsObj.put("displayName", bankProposal.getDisplayName());
                            } else {
                                proposalsObj.put("displayName", JSONObject.NULL);
                            }
                            if (bankProposal.getFirstName() != null) {
                                proposalsObj.put("firstName", bankProposal.getFirstName());
                            } else {
                                proposalsObj.put("firstName", JSONObject.NULL);
                            }
                            if (bankProposal.getLastName() != null) {
                                proposalsObj.put("lastName", bankProposal.getLastName());
                            } else {
                                proposalsObj.put("lastName", JSONObject.NULL);
                            }
                            proposalsObj.put("type", proposal.getType());
                            proposalsObj.put("properties", bankProposalsArray);
                        } else {
                            if (proposal != null && proposal.getProperties() != null && proposal.getProperties().size() > 0) {
                                for (int k = 0; k < proposal.getProperties().size(); k++) {
                                    ProposalsPropertiesData property = proposal.getProperties().get(k);
                                    JSONObject propertyObj = new JSONObject();
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
                        }
                        proposals.put(proposalsObj);
                        informationJSON.put("proposals", proposals);
                    }

                }
            }

            RequestBody underwritingActionRequired = RequestBody.create(MediaType.parse("application/json"),
                    String.valueOf(informationJSON));
            Log.e("JSN", informationJSON.toString());
            underwritingUserActionRequiredViewModel.submitMerchantActionRequired(underwritingActionRequired);

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
                            additionalActionRL.setVisibility(VISIBLE);
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

        businessDashboardViewModel.getPaymentMethodsResponseMutableLiveData().observe(this, new Observer<PaymentMethodsResponse>() {
            @Override
            public void onChanged(PaymentMethodsResponse paymentMethodsResponse) {
                if (paymentMethodsResponse != null && paymentMethodsResponse.getStatus().equalsIgnoreCase(Utils.SUCCESS)) {
                    bankID = paymentMethodsResponse.getData().getData().get(0).getId();
                }
            }
        });

        underwritingUserActionRequiredViewModel.getActRqrdDocUploadMutableLiveData().observe(this, new Observer<IdentityImageResponse>() {
            @Override
            public void onChanged(IdentityImageResponse identityImageResponse) {
                try {
                    dismissDialog();
                    if (identityImageResponse != null && identityImageResponse.getStatus().equalsIgnoreCase("SUCCESS")) {
                        if (uploadLayout != null) {
                            uploadLayout.setVisibility(VISIBLE);
                            selectedText.setVisibility(GONE);
                        }
                        setUploadedTrue((int) uploadLayout.getTag());

                        enableOrDisableNext();

                    } else {
                        Utils.displayAlert(identityImageResponse.getError().getErrorDescription(),
                                BusinessAdditionalActionRequiredActivity.this, "",
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

    private void setUploadedTrue(int pos) {
        listOfDocLayouts.get(pos).setUploaded(true);
    }

    private void additionalRequiredDocuments(ActionRequiredResponse actionRequiredResponse) {
        try {
            additionalDocumentRequiredLL.removeAllViews();
            additionalDocumentRequiredLL.setVisibility(VISIBLE);
            for (int i = 0; i < actionRequiredResponse.getData().getAdditionalDocument().size(); i++) {
                addDynamicView(i);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addDynamicView(int mainDocPos) {
        try {
            ActionRequiredResponse actionRequiredResponse = actionRequired;
            View inf = getLayoutInflater().inflate(R.layout.additional_document_item, null);
            LinearLayout documentRequiredLL = inf.findViewById(R.id.documentRequired);
            //Setting object position
            documentRequiredLL.setTag(mainDocPos);
            LinearLayout containerLL = inf.findViewById(R.id.containerLL);
            LinearLayout additionalDocLL = inf.findViewById(R.id.additionalDocLL);
            TextView documentName = inf.findViewById(R.id.tvdocumentName);
            documentName.setText(actionRequiredResponse.getData().getAdditionalDocument().get(mainDocPos).getDocumentName());

            additionalDocumentRequiredLL.addView(inf, layoutParamss);
            DocLayout docLayout = new DocLayout();
            docLayout.setId(actionRequiredResponse.getData().getAdditionalDocument().get(mainDocPos).getId());
            docLayout.setLinearLayouts(documentRequiredLL);
            listOfDocLayouts.add(docLayout);

            if (actionRequiredResponse.getData().getAdditionalDocument().get(mainDocPos).getUploadDocs().size() > 0) {
                setUploadedTrue(mainDocPos);
                for (int i = 0; i < actionRequiredResponse.getData().getAdditionalDocument().get(mainDocPos).getUploadDocs().size(); i++) {
                    View docInf = getLayoutInflater().inflate(R.layout.doc_item, null);
                    LinearLayout sscFileUploadLL = docInf.findViewById(R.id.sscFileUploadLL);
                    LinearLayout sscfileUploadedLL = docInf.findViewById(R.id.sscfileUploadedLL);
                    //Setting document position
                    sscfileUploadedLL.setTag(mainDocPos);
                    TextView sscuploadFileTV = docInf.findViewById(R.id.sscuploadFileTV);
                    TextView sscfileUpdatedOnTV = docInf.findViewById(R.id.sscfileUpdatedOnTV);

                    if (actionRequiredResponse.getData().getAdditionalDocument().get(mainDocPos).getUploadDocs().size() > 0)
                        sscuploadFileTV.setTag(actionRequiredResponse.getData().getAdditionalDocument().get(mainDocPos).getUploadDocs().get(i).getDocId());
                    else
                        sscuploadFileTV.setTag(0);

                    sscfileUploadedLL.setVisibility(VISIBLE);
                    sscuploadFileTV.setVisibility(GONE);
                    sscfileUpdatedOnTV.setText("Uploaded on " + Utils.convertDocUploadedDate(actionRequiredResponse.getData().getAdditionalDocument().get(mainDocPos).getUploadDocs().get(i).getUploadDate()));

                    containerLL.addView(docInf, layoutParamss);

                    sscFileUploadLL.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            documentID = actionRequiredResponse.getData().getAdditionalDocument().get(mainDocPos).getId();
                            uploadLayout = sscfileUploadedLL;
                            selectedText = sscuploadFileTV;
                            lastUploadedDoc = (int) sscuploadFileTV.getTag();
                            if (checkAndRequestPermissions(BusinessAdditionalActionRequiredActivity.this)) {
                                if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                                    return;
                                }
                                mLastClickTime = SystemClock.elapsedRealtime();
                                if (Utils.isKeyboardVisible)
                                    Utils.hideKeypad(BusinessAdditionalActionRequiredActivity.this);
                                chooseFilePopup(BusinessAdditionalActionRequiredActivity.this, selectedDocType);

                            }

                        }
                    });

                }
                if (containerLL.getChildCount() > 2)
                    additionalDocLL.setVisibility(GONE);
            } else {
                View docInf = getLayoutInflater().inflate(R.layout.doc_item, null);
                LinearLayout sscFileUploadLL = docInf.findViewById(R.id.sscFileUploadLL);
                LinearLayout sscfileUploadedLL = docInf.findViewById(R.id.sscfileUploadedLL);
                //Setting document position
                sscfileUploadedLL.setTag(mainDocPos);
                TextView sscuploadFileTV = docInf.findViewById(R.id.sscuploadFileTV);
                TextView sscfileUpdatedOnTV = docInf.findViewById(R.id.sscfileUpdatedOnTV);
                sscuploadFileTV.setTag(0);

                sscfileUploadedLL.setVisibility(GONE);
                sscuploadFileTV.setVisibility(VISIBLE);
                sscfileUpdatedOnTV.setText("Uploaded on " + Utils.getCurrentDate());

                containerLL.addView(docInf, layoutParamss);

                sscFileUploadLL.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        documentID = actionRequiredResponse.getData().getAdditionalDocument().get(mainDocPos).getId();
                        uploadLayout = sscfileUploadedLL;
                        selectedText = sscuploadFileTV;
                        lastUploadedDoc = (int) sscuploadFileTV.getTag();
                        if (checkAndRequestPermissions(BusinessAdditionalActionRequiredActivity.this)) {
                            if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                                return;
                            }
                            mLastClickTime = SystemClock.elapsedRealtime();
                            if (Utils.isKeyboardVisible)
                                Utils.hideKeypad(BusinessAdditionalActionRequiredActivity.this);
                            chooseFilePopup(BusinessAdditionalActionRequiredActivity.this, selectedDocType);

                        }
                    }
                });
            }

            additionalDocLL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    try {
                        Log.e("count", containerLL.getChildCount() + "");

                        if (containerLL.getChildCount() >= 2)
                            additionalDocLL.setVisibility(GONE);

                        View docInf = getLayoutInflater().inflate(R.layout.doc_item, null);
                        LinearLayout sscFileUploadLL = docInf.findViewById(R.id.sscFileUploadLL);
                        LinearLayout sscfileUploadedLL = docInf.findViewById(R.id.sscfileUploadedLL);
                        //Setting document position
                        sscfileUploadedLL.setTag(mainDocPos);
                        TextView sscuploadFileTV = docInf.findViewById(R.id.sscuploadFileTV);
                        TextView sscfileUpdatedOnTV = docInf.findViewById(R.id.sscfileUpdatedOnTV);
                        sscuploadFileTV.setTag(0);

                        sscfileUploadedLL.setVisibility(GONE);
                        sscuploadFileTV.setVisibility(VISIBLE);
                        sscfileUpdatedOnTV.setText("Uploaded on " + Utils.getCurrentDate());

                        containerLL.addView(docInf, layoutParamss);

                        sscFileUploadLL.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                documentID = actionRequiredResponse.getData().getAdditionalDocument().get(mainDocPos).getId();
                                uploadLayout = sscfileUploadedLL;
                                selectedText = sscuploadFileTV;
                                lastUploadedDoc = (int) sscuploadFileTV.getTag();
                                if (checkAndRequestPermissions(BusinessAdditionalActionRequiredActivity.this)) {
                                    if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                                        return;
                                    }
                                    mLastClickTime = SystemClock.elapsedRealtime();
                                    if (Utils.isKeyboardVisible)
                                        Utils.hideKeypad(BusinessAdditionalActionRequiredActivity.this);
                                    chooseFilePopup(BusinessAdditionalActionRequiredActivity.this, selectedDocType);

                                }
                            }
                        });

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            enableOrDisableNext();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void websiteChanges(ActionRequiredResponse actionRequiredResponse) {
        websiteRevisionRequiredLL.setVisibility(VISIBLE);

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
                    if (data.getType().toLowerCase().equals("bank")) {
                        fileUpload.put(data.getDbId(), null);
                        bankProposal = data;
                        bankInformation(data);
                    } else {
                        List<ProposalsPropertiesData> proposalsPropertiesData = data.getProperties();
                        if (proposalsPropertiesData != null && proposalsPropertiesData.size() > 0) {
                            informationRevisionLL.setVisibility(VISIBLE);
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
                                        imvAcceptTick.setVisibility(VISIBLE);
                                        tvAcceptMsg.setVisibility(VISIBLE);
                                        llAccept.setVisibility(GONE);
                                        llDecline.setVisibility(GONE);
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
        }
        enableOrDisableNext();
    }

    private void bankInformation(ProposalsData data) {
        try {
            bank_information.setVisibility(VISIBLE);
            LinearLayout verificationFailLL, editLL;
            TextView errorDescrptnTV, resubmitTV, editTextTV, nameOnAccTV, routingNumTV, accNumberTV;
            errorDescrptnTV = findViewById(R.id.errorDescrptnTV);
            verificationFailLL = findViewById(R.id.verificationFailLL);
            editLL = findViewById(R.id.editLL);
            resubmitTV = findViewById(R.id.resubmitTV);
            editTextTV = findViewById(R.id.editTextTV);
            nameOnAccTV = findViewById(R.id.nameOnAccTV);
            routingNumTV = findViewById(R.id.routingNumTV);
            accNumberTV = findViewById(R.id.accNumberTV);
            if (objMyApplication.getBankAccount() == null) {
                verificationFailLL.setVisibility(VISIBLE);
                editLL.setVisibility(GONE);
                if (data != null && data.getProperties() != null && data.getProperties().size() > 0) {
                    errorDescrptnTV.setText(data.getProperties().get(0).getGiactResponse());
                }
            } else {
                fileUpload.put(bankProposal.getDbId(), "true");
                String convert = "";
                verificationFailLL.setVisibility(GONE);
                editLL.setVisibility(VISIBLE);
                nameOnAccTV.setText(objMyApplication.getBankAccount().getAccountName());
                routingNumTV.setText(objMyApplication.getBankAccount().getRoutingNumber());
                if (objMyApplication.getBankAccount().getAccountNumber() != null && objMyApplication.getBankAccount().getAccountNumber().length() > 4) {
                    //accNumberTV.setText("**** " + objMyApplication.getBankAccount().getAccountNumber().substring(objMyApplication.getBankAccount().getAccountNumber().length() - 4));
                    convert = objMyApplication.getBankAccount().getAccountNumber().replaceAll("", "");
                    String converted = convert.replaceAll("\\w(?=\\w{4})", "•");
                    accNumberTV.setText(converted);
                } else {
                    accNumberTV.setText(objMyApplication.getBankAccount().getAccountNumber());
                }
                enableOrDisableNext();
            }
            resubmitTV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                        return;
                    }
                    mLastClickTime = SystemClock.elapsedRealtime();
                    Intent i = new Intent(BusinessAdditionalActionRequiredActivity.this, AddManualBankAccount.class);
                    i.putExtra("From", "Resubmit");
                    startActivityForResult(i, 5);

                }
            });
            editTextTV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                        return;
                    }
                    mLastClickTime = SystemClock.elapsedRealtime();
                    Intent i = new Intent(BusinessAdditionalActionRequiredActivity.this, AddManualBankAccount.class);
                    i.putExtra("From", "Edit");
                    i.putExtra("bankObject", objMyApplication.getBankAccount());
                    startActivityForResult(i, 5);
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private DisplayImageUtility.ImageHolder displayWebsiteImage(String imageId, ImageView iv) {
        iv.setVisibility(VISIBLE);
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
                    imvAcceptTick.setVisibility(VISIBLE);
                    imvAcceptTick.setImageDrawable(getResources().getDrawable(R.drawable.ic_decline));
                    tvRemarks.setVisibility(VISIBLE);
                    llAccept.setVisibility(GONE);
                    tvDeclinedMsg.setVisibility(VISIBLE);
                    tvDeclinedMsg.setText(getString(R.string.Decline) + " " + Utils.getCurrentDate() + " due to: ");
                    llDecline.setVisibility(GONE);
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
        llHeading.setVisibility(GONE);
        llBottomView.setVisibility(GONE);
        scrollview.setVisibility(GONE);
        llApprovedReserved.setVisibility(VISIBLE);
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
        Intent camIntent = new Intent(BusinessAdditionalActionRequiredActivity.this, CameraHandlerActivity.class);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            super.onActivityResult(requestCode, resultCode, data);
            if (resultCode != RESULT_OK) return;
            LogUtils.d(TAG, "onActivityResult" + data.getData());
            if (requestCode == 5) {
                bankInformation(null);
            }

        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    private void uploadDocumentFromLibrary(String filePath) {
        try {

            mediaFile = new File(filePath);

            LogUtils.d(TAG, "uploadDocumentFromLibrary" + mediaFile);
            LogUtils.d(TAG, "documentID" + documentID);

            if (Utils.isValidFileSize(mediaFile)) {
                if (lastUploadedDoc != 0)
//                    identityVerificationViewModel.removeIdentityImage(lastUploadedDoc + "");
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

        boolean isDocs = true;
        for (int l = 0; l < listOfDocLayouts.size(); l++) {
            if (!listOfDocLayouts.get(l).isUploaded()) {
                isDocs = false;
                break;
            }
        }
        try {
            LogUtils.d(TAG, "fileUpload" + fileUpload);
            if (!isDocs || fileUpload.containsValue(null)) {
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

    private void uploadDoc(File file) {
        showProgressDialog();
        RequestBody requestBody = null;
        MultipartBody.Part idFile = null;

        requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        idFile = MultipartBody.Part.createFormData("document", file.getName(), requestBody);

        RequestBody idType = RequestBody.create(MediaType.parse("text/plain"), "100");
        RequestBody docID = RequestBody.create(MediaType.parse("text/plain"), documentID + "");
        underwritingUserActionRequiredViewModel.uploadActionRequiredDoc(idFile, idType, docID);
    }

}
