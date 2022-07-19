package com.greenbox.coyni.view.business;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.greenbox.coyni.R;
import com.greenbox.coyni.adapters.BeneficialOwnersAdapter;
import com.greenbox.coyni.interfaces.OnKeyboardVisibilityListener;
import com.greenbox.coyni.model.BeneficialOwners.BOIdResp;
import com.greenbox.coyni.model.BeneficialOwners.BOResp;
import com.greenbox.coyni.model.BeneficialOwners.BOValidateResp;
import com.greenbox.coyni.model.BeneficialOwners.DeleteBOResp;
import com.greenbox.coyni.utils.CustomTypefaceSpan;
import com.greenbox.coyni.utils.MyApplication;
import com.greenbox.coyni.utils.Utils;
import com.greenbox.coyni.view.BaseActivity;
import com.greenbox.coyni.viewmodel.BusinessIdentityVerificationViewModel;

public class AdditionalBeneficialOwnersActivity extends BaseActivity implements OnKeyboardVisibilityListener {

    ImageView backIV;
    RecyclerView beneficialOwnersRV;
    TextView percentageTV, notFoundTV, boDescTV;
    MyApplication objMyApplication;
    LinearLayout addNewBOLL;
    BusinessIdentityVerificationViewModel businessIdentityVerificationViewModel;
    CardView validateCV;
    boolean isValidateEnabled = false, isPostSuccess = false;
    Long mLastClickTime = 0L;
    boolean hasDrafts = false;
    public static boolean isActivityVisible = false;
    public static AdditionalBeneficialOwnersActivity additionalBeneficialOwnersActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            setContentView(R.layout.activity_additional_benifitial_owners);

            initFields();
            initObservers();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void initFields() {

        try {
            isActivityVisible = true;
            additionalBeneficialOwnersActivity = this;
            objMyApplication = (MyApplication) getApplicationContext();
            businessIdentityVerificationViewModel = new ViewModelProvider(this).get(BusinessIdentityVerificationViewModel.class);
            setKeyboardVisibilityListener(AdditionalBeneficialOwnersActivity.this);

            backIV = findViewById(R.id.backIV);
            beneficialOwnersRV = findViewById(R.id.beneficialOwnersRV);
            percentageTV = findViewById(R.id.percentageTV);
            notFoundTV = findViewById(R.id.notFoundTV);
            addNewBOLL = findViewById(R.id.addNewBOLL);
            validateCV = findViewById(R.id.validateCV);
            boDescTV = findViewById(R.id.boDescTV);
            setSpannableText();

//            loadBeneficialOwners();

            backIV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    if (getIntent().getStringExtra("FROM").equals("REVIEW")) {
                    try {
                        if (ReviewApplicationActivity.reviewApplicationActivity != null)
                            ReviewApplicationActivity.reviewApplicationActivity.finish();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    finish();
//                    } else
//                        finish();


                }
            });

            addNewBOLL.setOnClickListener(view -> {
                try {
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                        return;
                    }
                    mLastClickTime = SystemClock.elapsedRealtime();
                    if (hasDrafts) {
                        Utils.displayAlert("Please complete draft beneficial owner information.", this, "", "");
                    } else {
                        try {
                            if (objMyApplication.getBeneficialOwnersResponse().getData().size() < 20) {
                                businessIdentityVerificationViewModel.postBeneficialOwnersID();
                            } else {
                                Utils.showCustomToast(this, "You are exceeded your benificial accounts max limit.", 0, "");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            businessIdentityVerificationViewModel.postBeneficialOwnersID();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            validateCV.setOnClickListener(view -> {
                if (isValidateEnabled) {
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                        return;
                    }
                    mLastClickTime = SystemClock.elapsedRealtime();
                    businessIdentityVerificationViewModel.validateBeneficialOwners();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initObservers() {
        try {
            businessIdentityVerificationViewModel.getDeleteBOesponse().observe(this, new Observer<DeleteBOResp>() {
                @Override
                public void onChanged(DeleteBOResp deleteBOResp) {

                    if (deleteBOResp != null) {
                        if (deleteBOResp.getStatus().toLowerCase().toString().equals("success")) {
                            hasDrafts = false;
                            businessIdentityVerificationViewModel.getBeneficialOwners();
                        } else {
                            Utils.displayAlert(deleteBOResp.getError().getErrorDescription(),
                                    AdditionalBeneficialOwnersActivity.this, "", deleteBOResp.getError().getFieldErrors().get(0));
                        }
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            businessIdentityVerificationViewModel.getBeneficialOwnersResponse().observe(this, new Observer<BOResp>() {
                @Override
                public void onChanged(BOResp boResp) {

                    if (boResp != null) {
                        if (boResp.getStatus().toLowerCase().toString().equals("success")) {
                            objMyApplication.setBeneficialOwnersResponse(boResp);
                            loadBeneficialOwners(boResp);
                        } else {
                            notFoundTV.setVisibility(View.GONE);
                            percentageTV.setVisibility(View.VISIBLE);
                            addNewBOLL.setVisibility(View.VISIBLE);
                            beneficialOwnersRV.setVisibility(View.GONE);
                            isValidateEnabled = false;
                            validateCV.setCardBackgroundColor(getResources().getColor(R.color.inactive_color));
                        }
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            businessIdentityVerificationViewModel.getBeneficialOwnersIDResponse().observe(this, new Observer<BOIdResp>() {
                @Override
                public void onChanged(BOIdResp boIdResp) {

                    if (boIdResp != null) {
                        if (boIdResp.getStatus().toLowerCase().toString().equals("success")) {
                            startActivity(new Intent(AdditionalBeneficialOwnersActivity.this, AddBeneficialOwnerActivity.class)
                                    .putExtra("FROM", "ADD_BO")
                                    .putExtra("ID", boIdResp.getData().getId()));
//                            finish();
                        } else {

                        }
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            businessIdentityVerificationViewModel.getValidateBOResponse().observe(this, new Observer<BOValidateResp>() {
                @Override
                public void onChanged(BOValidateResp boValidateResp) {

                    if (boValidateResp != null) {
                        if (boValidateResp.getStatus().toLowerCase().toString().equals("success")) {
                            finish();
                        } else {
                            Utils.displayAlert(boValidateResp.getError().getErrorDescription(),
                                    AdditionalBeneficialOwnersActivity.this, "", boValidateResp.getError().getFieldErrors().get(0));
                        }
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void chooseEditORDelete(int boID) {
        try {
            Dialog dialog = new Dialog(this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.edit_delete_dialog);
            dialog.setCancelable(true);
            Window window = dialog.getWindow();
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            WindowManager.LayoutParams wlp = window.getAttributes();

            wlp.gravity = Gravity.BOTTOM;
            wlp.flags &= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
            window.setAttributes(wlp);
            dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

            LinearLayout viewEditLL = dialog.findViewById(R.id.viewEditLL);
            LinearLayout deleteLL = dialog.findViewById(R.id.deleteLL);

            viewEditLL.setOnClickListener(view -> {
                dialog.dismiss();
                startActivity(new Intent(this, AddBeneficialOwnerActivity.class)
                        .putExtra("FROM", "EDIT_BO")
                        .putExtra("ID", boID));
//                finish();
            });

            deleteLL.setOnClickListener(view -> {
                dialog.dismiss();
                businessIdentityVerificationViewModel.deleteBeneficialOwner(boID);
            });

            dialog.show();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void loadBeneficialOwners(BOResp boResp) {

//        BOResp finalBOResp = new BOResp();
//        List<BOResp.BeneficialOwner> finalBOList = new ArrayList();

        if (boResp.getData().size() > 0) {
            notFoundTV.setVisibility(View.GONE);
            beneficialOwnersRV.setVisibility(View.VISIBLE);

            int totalPercentage = 0;

//            List<BOResp.BeneficialOwner> noNameList = new ArrayList<>();

            for (int i = 0; i < boResp.getData().size(); i++) {

                totalPercentage = totalPercentage + boResp.getData().get(i).getOwnershipParcentage();
                BOResp.BeneficialOwner bo = boResp.getData().get(i);
                try {
                    boResp.getData().get(i).setDraft(bo.getFirstName().equals("") || bo.getLastName().equals("") || bo.getDob().equals("")
                            || bo.getOwnershipParcentage() <= 0 || bo.getAddressLine1().equals("")
                            || bo.getCity().equals("") || bo.getState().equals("") || bo.getZipCode().equals("")
                            || bo.getSsn().equals("") || bo.getRequiredDocuments().size() <= 0);
                } catch (Exception e) {
                    e.printStackTrace();
                    boResp.getData().get(i).setDraft(true);
                }

                if (boResp.getData().get(i).isDraft()) {
                    hasDrafts = true;
//                    try {
//                        if (bo.getFirstName().equals("") || bo.getFirstName() == null || bo.getLastName().equals("") || bo.getLastName() == null
//                                || bo.getOwnershipParcentage() <= 0) {
//                            noNameList.add(boResp.getData().get(i));
//                        }
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                        noNameList.add(boResp.getData().get(i));
//                    }
                } else {
//                    finalBOList.add(boResp.getData().get(i));
//                    totalPercentage = totalPercentage + boResp.getData().get(i).getOwnershipParcentage();
                }
            }

//            finalBOResp.setData(finalBOList);

            BeneficialOwnersAdapter beneficialOwnersAdapter = new BeneficialOwnersAdapter(this, boResp);
            LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
            beneficialOwnersRV.setNestedScrollingEnabled(false);
            beneficialOwnersRV.setLayoutManager(mLayoutManager);
            beneficialOwnersRV.setItemAnimator(new DefaultItemAnimator());
            beneficialOwnersRV.setAdapter(beneficialOwnersAdapter);

//            if (noNameList.size() > 0) {
//                for (int i = 0; i < noNameList.size(); i++) {
//                    businessIdentityVerificationViewModel.deleteBeneficialOwner(noNameList.get(i).getId());
//                }
//            }
            if (totalPercentage >= Utils.boTargetPercentage && !hasDrafts) {
                percentageTV.setVisibility(View.GONE);
                isValidateEnabled = true;
                validateCV.setCardBackgroundColor(getResources().getColor(R.color.primary_color));
            } else {
                percentageTV.setVisibility(View.VISIBLE);
                isValidateEnabled = false;
                validateCV.setCardBackgroundColor(getResources().getColor(R.color.inactive_color));
            }

            if (totalPercentage >= 100) {
                addNewBOLL.setVisibility(View.GONE);
                percentageTV.setVisibility(View.GONE);
            } else {
                addNewBOLL.setVisibility(View.VISIBLE);
//                percentageTV.setVisibility(View.VISIBLE);
            }
        } else {
            addNewBOLL.setVisibility(View.VISIBLE);
            percentageTV.setVisibility(View.VISIBLE);

            isValidateEnabled = false;
            validateCV.setCardBackgroundColor(getResources().getColor(R.color.inactive_color));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            if (Utils.isKeyboardVisible)
                Utils.hideKeypad(this);
            businessIdentityVerificationViewModel.getBeneficialOwners();
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
        if (visible) {
            Utils.isKeyboardVisible = true;
        } else {
            Utils.isKeyboardVisible = false;
        }
    }

    public void setSpannableText() {
        SpannableString ss = new SpannableString(boDescTV.getText().toString());

        Typeface font = Typeface.createFromAsset(getAssets(), "font/opensans_bold.ttf");
        SpannableStringBuilder SS = new SpannableStringBuilder(ss);
        SS.setSpan(new CustomTypefaceSpan("", font), ss.length() - 16, ss.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);

        SS.setSpan(new ForegroundColorSpan(getColor(R.color.primary_green)), ss.length() - 16, ss.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        SS.setSpan(new ForegroundColorSpan(getColor(R.color.primary_green)), ss.length() - 16, ss.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        boDescTV.setText(SS);
        boDescTV.setMovementMethod(LinkMovementMethod.getInstance());
        boDescTV.setHighlightColor(Color.TRANSPARENT);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isActivityVisible = false;
    }
}