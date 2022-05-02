package com.greenbox.coyni.view.business;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.greenbox.coyni.R;
import com.greenbox.coyni.adapters.AddNewBusinessAccountDBAAdapter;
import com.greenbox.coyni.adapters.BusinessProfileRecyclerAdapter;
import com.greenbox.coyni.model.identity_verification.IdentityAddressResponse;
import com.greenbox.coyni.model.identity_verification.IdentityImageResponse;
import com.greenbox.coyni.model.identity_verification.RemoveIdentityResponse;
import com.greenbox.coyni.model.preferences.ProfilesResponse;
import com.greenbox.coyni.model.profile.AddBusinessUserResponse;
import com.greenbox.coyni.model.profile.Profile;
import com.greenbox.coyni.model.profile.TrackerResponse;
import com.greenbox.coyni.utils.LogUtils;
import com.greenbox.coyni.utils.MyApplication;
import com.greenbox.coyni.utils.Utils;
import com.greenbox.coyni.view.BaseActivity;
import com.greenbox.coyni.view.BuyTokenActivity;
import com.greenbox.coyni.view.IdVeAdditionalActionActivity;
import com.greenbox.coyni.view.IdentityVerificationActivity;
import com.greenbox.coyni.view.IdentityVerificationBindingLayoutActivity;
import com.greenbox.coyni.view.PINActivity;
import com.greenbox.coyni.viewmodel.DashboardViewModel;
import com.greenbox.coyni.viewmodel.IdentityVerificationViewModel;
import com.microsoft.appcenter.ingestion.models.Log;

import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class BusinessAddNewBusinessAccountActivity extends BaseActivity {

    private ImageView imageViewClose;
    private LinearLayout llNewComapny, llNewDba;
    private MyApplication objMyApplication;
    private List<String> listComapny = new ArrayList<>();
    private IdentityVerificationViewModel identityVerificationViewModel;
    private DashboardViewModel dashboardViewModel;
    private List<ProfilesResponse.Profiles> filterList = new ArrayList<>();
    private List<ProfilesResponse.Profiles> businessAccountList = new ArrayList<>();
    private List<ProfilesResponse.Profiles> personalAccountList = new ArrayList<>();
    private int companyId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        setContentView(R.layout.business_add_new_business_account);

        llNewComapny = findViewById(R.id.ll_new_company);
        llNewDba = findViewById(R.id.ll_new_dba);
        imageViewClose = findViewById(R.id.imv_close);

        identityVerificationViewModel = new ViewModelProvider(this).get(IdentityVerificationViewModel.class);
        dashboardViewModel = new ViewModelProvider(this).get(DashboardViewModel.class);

        dashboardViewModel.getProfiles();

        llNewComapny.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                identityVerificationViewModel.getAddBusinessUser();


            }
        });

        llNewDba.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                listComapny.clear();
                displayAlert(BusinessAddNewBusinessAccountActivity.this);


            }
        });

        imageViewClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        initObservers();


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

//        for(ProfilesResponse.Profiles c: businessAccountList){
//            listComapny.add(c.getCompanyName());
//        }
        LogUtils.d(TAG,"businessAccountList"+businessAccountList.toString());
        AddNewBusinessAccountDBAAdapter addNewBusinessAccountDBAAdapter = new AddNewBusinessAccountDBAAdapter(businessAccountList, mContext, new AddNewBusinessAccountDBAAdapter.OnSelectListner(){
            @Override
            public void selectedItem(ProfilesResponse.Profiles item) {
                LogUtils.d("dbaselected", "dbaselectes" + item.toString());
                addDBACardView.setEnabled(true);
                addDBACardView.setCardBackgroundColor(getColor(R.color.primary_green));
                companyId = item.getId();
            }
        });
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(mContext);
        rvCompanyList.setLayoutManager(mLayoutManager);
        rvCompanyList.setItemAnimator(new DefaultItemAnimator());
        rvCompanyList.setAdapter(addNewBusinessAccountDBAAdapter);
        LogUtils.d(TAG, "eeee" + companyId);

        if (companyId != 0){
            addDBACardView.setCardBackgroundColor(getColor(R.color.primary_green));
            addDBACardView.setEnabled(true);
        } else {
            addDBACardView.setCardBackgroundColor(getColor(R.color.inactive_color));
            addDBACardView.setEnabled(false);
        }

        addDBACardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (companyId != 0) {
                    identityVerificationViewModel.getPostAddDBABusiness(companyId);
                    dialog.cancel();
                } else {

                }
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
    }

    public void initObservers() {
        try {
            dashboardViewModel.getProfileRespMutableLiveData().observe(this, new Observer<ProfilesResponse>() {
                @Override
                public void onChanged(ProfilesResponse profilesResponse) {
                    if (profilesResponse != null) {
                        filterList = profilesResponse.getData();
                        for (ProfilesResponse.Profiles c : filterList) {
                            LogUtils.d(TAG,"getProfileRespMutableLiveData"+c.getDbaOwner());
                            if (c.getDbaOwner()==null && c.getAccountType().equals(Utils.BUSINESS) ) {
                                businessAccountList.add(c);
                            } else {
         }
                        }
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            identityVerificationViewModel.getBusinessAddCustomer().observe(this, new Observer<AddBusinessUserResponse>() {
                @Override
                public void onChanged(AddBusinessUserResponse identityImageResponse) {
                    if (identityImageResponse.getStatus().equalsIgnoreCase("success")) {
                        Utils.setStrAuth(identityImageResponse.getData().getJwtToken());
                        startActivity(new Intent(BusinessAddNewBusinessAccountActivity.this, BusinessRegistrationTrackerActivity.class)
                                .putExtra("ADDBUSINESS", true)
                                .putExtra("ADDDBA", false));

                    } else {
                        Utils.displayAlert(identityImageResponse.getError().getErrorDescription(), BusinessAddNewBusinessAccountActivity.this, "", identityImageResponse.getError().getFieldErrors().get(0));
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            identityVerificationViewModel.getBusinessAddDBAResponse().observe(this, new Observer<AddBusinessUserResponse>() {
                @Override
                public void onChanged(AddBusinessUserResponse identityImageResponse) {
                    LogUtils.d("addDBAresponse", "addDBAresponse" + identityImageResponse);
                    if (identityImageResponse.getStatus().equalsIgnoreCase("success")) {
                        Utils.setStrAuth(identityImageResponse.getData().getJwtToken());
                        startActivity(new Intent(BusinessAddNewBusinessAccountActivity.this, BusinessRegistrationTrackerActivity.class)
                                .putExtra("ADDBUSINESS", true)
                                .putExtra("ADDDBA", true));
                        //displayAlert(BusinessAddNewBusinessAccountActivity.this);
                    } else {
                        Utils.displayAlert(identityImageResponse.getError().getErrorDescription(), BusinessAddNewBusinessAccountActivity.this, "", identityImageResponse.getError().getFieldErrors().get(0));
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    @Override
//    public void selectedItem(ProfilesResponse.Profiles item) {
//
//        LogUtils.d("dbaselected", "dbaselectes" + item.toString());
//        addDBACardView.setEnabled(true);
//        companyId = item.getId();
//    }
}