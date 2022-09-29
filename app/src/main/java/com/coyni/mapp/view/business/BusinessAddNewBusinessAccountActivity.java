package com.coyni.mapp.view.business;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.cardview.widget.CardView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.coyni.mapp.R;
import com.coyni.mapp.adapters.AddNewBusinessAccountDBAAdapter;
import com.coyni.mapp.model.AccountsData;
import com.coyni.mapp.model.preferences.BaseProfile;
import com.coyni.mapp.model.preferences.ProfilesResponse;
import com.coyni.mapp.model.profile.AddBusinessUserResponse;
import com.coyni.mapp.utils.LogUtils;
import com.coyni.mapp.utils.MyApplication;
import com.coyni.mapp.utils.Utils;
import com.coyni.mapp.view.BaseActivity;
import com.coyni.mapp.viewmodel.DashboardViewModel;
import com.coyni.mapp.viewmodel.IdentityVerificationViewModel;
import com.coyni.mapp.viewmodel.LoginViewModel;

import java.util.ArrayList;
import java.util.List;

public class BusinessAddNewBusinessAccountActivity extends BaseActivity {

    private ImageView imageViewClose;
    private LinearLayout llNewComapny, llNewDba;
    private MyApplication objMyApplication;
    private List<String> listComapny = new ArrayList<>();
    private IdentityVerificationViewModel identityVerificationViewModel;
    private DashboardViewModel dashboardViewModel;
    private List<ProfilesResponse.Profiles> filterList = new ArrayList<>();
    private List<BaseProfile> businessAccountList;
    private List<ProfilesResponse.Profiles> personalAccountList = new ArrayList<>();
    private BaseProfile selectedProfile = null;
    private Long mLastClickTimeQA = 0L;
    private LoginViewModel loginViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        setContentView(R.layout.business_add_new_business_account);
        objMyApplication = (MyApplication) getApplicationContext();

        llNewComapny = findViewById(R.id.ll_new_company);
        llNewDba = findViewById(R.id.ll_new_dba);
        imageViewClose = findViewById(R.id.imv_close);

        identityVerificationViewModel = new ViewModelProvider(this).get(IdentityVerificationViewModel.class);
        dashboardViewModel = new ViewModelProvider(this).get(DashboardViewModel.class);
        loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);

        llNewComapny.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (SystemClock.elapsedRealtime() - mLastClickTimeQA < 2000) {
                    return;
                }
                mLastClickTimeQA = SystemClock.elapsedRealtime();
                //identityVerificationViewModel.getAddBusinessUser();
                startActivity(new Intent(BusinessAddNewBusinessAccountActivity.this, BusinessRegistrationTrackerActivity.class)
                        .putExtra(Utils.ADD_BUSINESS, true)
                        .putExtra(Utils.ADD_DBA, false));
            }
        });


        llNewDba.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (SystemClock.elapsedRealtime() - mLastClickTimeQA < 2000) {
                    return;
                }
                mLastClickTimeQA = SystemClock.elapsedRealtime();
                listComapny.clear();
                displayAlert(BusinessAddNewBusinessAccountActivity.this);
            }
        });

        imageViewClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (SystemClock.elapsedRealtime() - mLastClickTimeQA < 1000) {
                    return;
                }
                mLastClickTimeQA = SystemClock.elapsedRealtime();
                finish();
            }
        });

        initObservers();
    }

    @Override
    protected void onResume() {
        super.onResume();
        showProgressDialog();
//        selectedProfile=null;
        dashboardViewModel.getProfiles();

    }

    private void displayAlert(Context mContext) {
        // custom dialog
        final Dialog dialog = new Dialog(BusinessAddNewBusinessAccountActivity.this);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.add_new_business_account_alert_dialog);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        DisplayMetrics mertics = getResources().getDisplayMetrics();
        int width = mertics.widthPixels;

        RecyclerView rvCompanyList = dialog.findViewById(R.id.rv_company_list);
        CardView addDBACardView = dialog.findViewById(R.id.cvAction);
        selectedProfile = null;
        AddNewBusinessAccountDBAAdapter addNewBusinessAccountDBAAdapter = new AddNewBusinessAccountDBAAdapter(businessAccountList, mContext, new AddNewBusinessAccountDBAAdapter.OnSelectListner() {
            @Override
            public void selectedItem(BaseProfile item) {
                LogUtils.d(TAG, "ProfilesResponse.Profiles  " + item.toString());
                addDBACardView.setEnabled(true);
                addDBACardView.setCardBackgroundColor(getColor(R.color.primary_green));
                selectedProfile = item;
                if (selectedProfile != null) {
                    addDBACardView.setCardBackgroundColor(getColor(R.color.primary_green));
                    addDBACardView.setEnabled(true);
                }
            }
        });
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(mContext);
        rvCompanyList.setLayoutManager(mLayoutManager);
        rvCompanyList.setItemAnimator(new DefaultItemAnimator());
        rvCompanyList.setAdapter(addNewBusinessAccountDBAAdapter);

        if (selectedProfile != null) {
            addDBACardView.setCardBackgroundColor(getColor(R.color.primary_green));
            addDBACardView.setEnabled(true);
        } else {
            addDBACardView.setCardBackgroundColor(getColor(R.color.inactive_color));
            addDBACardView.setEnabled(false);
        }

        addDBACardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                if (companyId != 0) {
//                    identityVerificationViewModel.getPostAddDBABusiness(companyId);
//                    dialog.cancel();
//                }
//                Intent inAddDba = new Intent(BusinessAddNewBusinessAccountActivity.this, BusinessRegistrationTrackerActivity.class);
//                inAddDba.putExtra(Utils.ADD_BUSINESS, true);
//                inAddDba.putExtra(Utils.ADD_DBA, true);
//                inAddDba.putExtra(Utils.NEW_DBA, true);
//                inAddDba.putExtra(Utils.COMPANY_ID, selectedProfile.getId());
//                startActivity(inAddDba);
                showProgressDialog();
                loginViewModel.postChangeAccount(selectedProfile.getId());
                dialog.dismiss();
            }
        });

        Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

        WindowManager.LayoutParams wlp = window.getAttributes();

        wlp.gravity = Gravity.BOTTOM;
        wlp.flags &= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(wlp);

        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

        dialog.setCanceledOnTouchOutside(true);
        dialog.show();

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                if (selectedProfile != null) {
                    for (int i = 0; i < businessAccountList.size(); i++) {
                        businessAccountList.get(i).setSelected(false);
                    }
                }
            }
        });
    }

    private void prepareCompanyList() {
        businessAccountList = new ArrayList<>();
        AccountsData accountsData = new AccountsData(filterList);
        ArrayList<BaseProfile> groupData = accountsData.getGroupData();
        for (BaseProfile profile : groupData) {
            if (profile.getAccountType().equalsIgnoreCase(Utils.PERSONAL)
                    || profile.getAccountType().equalsIgnoreCase(Utils.SHARED)) {
                continue;
            }
            boolean isInActiveDBAFound = false;
            ArrayList<ProfilesResponse.Profiles> dBAList = (ArrayList<ProfilesResponse.Profiles>) accountsData.getData().get(profile.getId());

            if (dBAList == null || dBAList.size() == 0) {
                continue;
            }
            profile.setDbaCount(dBAList.size());
            if (dBAList.size() == 1 && !dBAList.get(0).getAccountStatus().equalsIgnoreCase(Utils.BUSINESS_ACCOUNT_STATUS.ACTIVE.getStatus())) {
                isInActiveDBAFound = true;
            } else {
                for (ProfilesResponse.Profiles dbaProfile : dBAList) {
                    if (dbaProfile.getAccountStatus().equalsIgnoreCase(Utils.BUSINESS_ACCOUNT_STATUS.UNDER_REVIEW.getStatus()) ||
                            dbaProfile.getAccountStatus().equalsIgnoreCase(Utils.BUSINESS_ACCOUNT_STATUS.UNVERIFIED.getStatus()) ||
                            dbaProfile.getAccountStatus().equalsIgnoreCase(Utils.BUSINESS_ACCOUNT_STATUS.ACTION_REQUIRED.getStatus())) {
                        isInActiveDBAFound = true;
                        break;
                    }
                }
            }
            if (isInActiveDBAFound) {
                profile.setAccountStatus(Utils.BUSINESS_ACCOUNT_STATUS.UNDER_REVIEW.getStatus());
            }
            businessAccountList.add(profile);
        }

    }

    public void initObservers() {
        try {
            dashboardViewModel.getProfileRespMutableLiveData().observe(this, new Observer<ProfilesResponse>() {
                @Override
                public void onChanged(ProfilesResponse profilesResponse) {
                    if (profilesResponse != null) {
                        filterList = profilesResponse.getData();
                        prepareCompanyList();
                    }
                    dismissDialog();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            identityVerificationViewModel.getBusinessAddDBAResponse().observe(this, new Observer<AddBusinessUserResponse>() {
                @Override
                public void onChanged(AddBusinessUserResponse identityImageResponse) {
                    LogUtils.d(TAG, "identityImageResponse " + identityImageResponse);
                    if (identityImageResponse.getStatus().equalsIgnoreCase("success")) {
                        Utils.setStrAuth(identityImageResponse.getData().getJwtToken());
                        startActivity(new Intent(BusinessAddNewBusinessAccountActivity.this, BusinessRegistrationTrackerActivity.class)
                                .putExtra(Utils.ADD_BUSINESS, true)
                                .putExtra(Utils.ADD_DBA, true));
                    } else {
                        Utils.displayAlert(identityImageResponse.getError().getErrorDescription(), BusinessAddNewBusinessAccountActivity.this, "", identityImageResponse.getError().getFieldErrors().get(0));
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            loginViewModel.postChangeAccountResponse().observe(this, new Observer<AddBusinessUserResponse>() {
                @Override
                public void onChanged(AddBusinessUserResponse btResp) {
                    dismissDialog();
                    if (btResp != null) {
                        if (btResp.getStatus().toLowerCase().toString().equals("success")) {
                            LogUtils.d("btResp", "btResp" + btResp);
                            Utils.setStrAuth(btResp.getData().getJwtToken());
                            objMyApplication.setOldLoginUserId(objMyApplication.getLoginUserId());
                            objMyApplication.setLoginUserId(selectedProfile.getId());
                            objMyApplication.setAccountType(btResp.getData().getAccountType());

                            Intent inAddDba = new Intent(BusinessAddNewBusinessAccountActivity.this, BusinessRegistrationTrackerActivity.class);
                            inAddDba.putExtra(Utils.ADD_BUSINESS, true);
                            inAddDba.putExtra(Utils.ADD_DBA, true);
                            inAddDba.putExtra(Utils.NEW_DBA, true);
                            inAddDba.putExtra(Utils.COMPANY_ID, selectedProfile.getId());
                            startActivity(inAddDba);

                        }
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}