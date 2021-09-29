package com.coyni.android.fragments;

import android.app.KeyguardManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.Base64;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.coyni.android.model.APIError;
import com.coyni.android.model.GlideApp;
import com.coyni.android.model.notification.Notifications;
import com.coyni.android.model.receiverequests.ReceiveRequests;
import com.coyni.android.view.MainActivity;
import com.coyni.android.viewmodel.DashboardViewModel;
import com.coyni.android.viewmodel.NotificationsViewModel;
import com.coyni.android.viewmodel.PayViewModel;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.coyni.android.R;
import com.coyni.android.adapters.CustomerTimeZonesAdapter;
import com.coyni.android.model.coynipin.CoyniPinRequest;
import com.coyni.android.model.coynipin.CoyniPinResponse;
import com.coyni.android.model.preferences.UserPreference;
import com.coyni.android.model.preferences.Preferences;
import com.coyni.android.model.user.User;
import com.coyni.android.model.user.UserPreferenceModel;
import com.coyni.android.model.usertracker.UserTracker;
import com.coyni.android.utils.MyApplication;
import com.coyni.android.utils.Utils;
import com.coyni.android.view.NotificationsActivity;
import com.coyni.android.viewmodel.BuyViewModel;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.ArrayList;

import static com.coyni.android.view.MainActivity.bottomSheetBehavior;
import static com.coyni.android.view.MainActivity.viewBack;
import static com.coyni.android.view.MainActivity.viewBottomSheet;

public class ProfileFragment extends Fragment implements View.OnClickListener {
    View view;
    static Context context;
    ImageView imgRoundProfile, imgBack, imgNotifications, imgRequests, imgMyAddress;
    TextView tvLogout, tvUserName, tvUserId, tvUserMail, tvUserPhone;
    RelativeLayout rlSecurity, rlChangePassword, rlAddress, rlAgreements, rlPaymentMethods, rlAccLimits, rlPreferences, rlPreferencesTimezone, rlRequests, rlFaceLock;
    CardView cvConfirm_AddPayment;
    MyApplication objMyApplication;
    DashboardViewModel dashboardViewModel;
    NotificationsViewModel notificationsViewModel;
    PayViewModel payViewModel;
    ProgressDialog dialog;
    TextInputLayout textInputLayoutTimeZone;
    CustomerTimeZonesAdapter customerTimeZonesAdapter;
    BuyViewModel buyViewModel;
    RelativeLayout lyCoyniPin, rlNotifications;
    Switch switchLock, switchFaceLock;
    static TextInputEditText etPreferenceTimezone;
    static LinearLayout llUpdatePreferences;
    static RecyclerView rvTimeZones;
    static RelativeLayout rlCurrency;
    static ImageView imgCancelPreferences, imgPreferencesArrowUp, imgPreferencesArrowDown;
    CircularImageView round_image;
    Boolean isFaceLock = false;
    SQLiteDatabase mydatabase;
    Cursor dsFacePin;


    public ProfileFragment() {
    }

    public static ProfileFragment newInstance(Context cxt) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        context = cxt;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onResume() {
        super.onResume();
        ((AppCompatActivity) getActivity()).getSupportActionBar().show();
        objMyApplication.setFromWhichFragment("fromProfile");
        if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            bottomSheetBehavior.setHideable(true);
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            if (viewBottomSheet.getVisibility() == View.VISIBLE) {
                viewBottomSheet.setVisibility(View.GONE);
            }
            viewBack.setVisibility(View.GONE);
        }
        getActivity().findViewById(R.id.layoutMenu).setVisibility(View.VISIBLE);
        notificationsViewModel.meNotifications();
        payViewModel.getReceiveRequests();
    }

    @Override
    public void onPause() {
        super.onPause();
        objMyApplication.setFromWhichFragment("");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_profile, container, false);
        try {
            objMyApplication = (MyApplication) context.getApplicationContext();
            dashboardViewModel = new ViewModelProvider(this).get(DashboardViewModel.class);
            buyViewModel = new ViewModelProvider(this).get(BuyViewModel.class);
            notificationsViewModel = new ViewModelProvider(this).get(NotificationsViewModel.class);
            payViewModel = new ViewModelProvider(this).get(PayViewModel.class);
            ((MainActivity) context).enableProfile();
            if (Build.VERSION.SDK_INT >= 23) {
                getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                        WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            }
            SetLock();
            tvUserName = view.findViewById(R.id.tvUserName);
            tvUserId = view.findViewById(R.id.tvUserId);
            tvUserMail = view.findViewById(R.id.tvUserMail);
            tvUserPhone = view.findViewById(R.id.tvUserPhone);
            tvLogout = view.findViewById(R.id.tvLogout);

            cvConfirm_AddPayment = (CardView) view.findViewById(R.id.cvConfirm_AddPayment);
            rlSecurity = (RelativeLayout) view.findViewById(R.id.rlSecurity);
            rlFaceLock = (RelativeLayout) view.findViewById(R.id.rlFaceLock);
            rlChangePassword = (RelativeLayout) view.findViewById(R.id.rlChangePassword);
            rlAddress = (RelativeLayout) view.findViewById(R.id.rlAddress);
            rlAccLimits = (RelativeLayout) view.findViewById(R.id.rlAccLimits);
            rlAgreements = (RelativeLayout) view.findViewById(R.id.rlAgreements);
            rlPaymentMethods = (RelativeLayout) view.findViewById(R.id.rlPaymentMethods);
            rlPreferences = (RelativeLayout) view.findViewById(R.id.rlPreferences);
            llUpdatePreferences = (LinearLayout) view.findViewById(R.id.llUpdatePreferences);
            rlPreferencesTimezone = (RelativeLayout) view.findViewById(R.id.rlPreferencesTimezone);
            rlRequests = (RelativeLayout) view.findViewById(R.id.rlRequests);
            rlNotifications = (RelativeLayout) view.findViewById(R.id.rlNotifications);
            getActivity().findViewById(R.id.layoutMenu).setVisibility(View.GONE);
            imgBack = (ImageView) view.findViewById(R.id.imgBack);
            imgNotifications = (ImageView) view.findViewById(R.id.imgNotifications);
            imgRequests = (ImageView) view.findViewById(R.id.imgRequests);
            imgMyAddress = (ImageView) view.findViewById(R.id.imgMyAddress);
            imgCancelPreferences = (ImageView) view.findViewById(R.id.imgCancelPreferences);
            imgPreferencesArrowDown = (ImageView) view.findViewById(R.id.imgPreferencesArrowDown);
            imgPreferencesArrowUp = (ImageView) view.findViewById(R.id.imgPreferencesArrowUp);
            round_image = (CircularImageView) view.findViewById(R.id.round_image);
            textInputLayoutTimeZone = (TextInputLayout) view.findViewById(R.id.textInputLayoutTimeZone);

            etPreferenceTimezone = (TextInputEditText) view.findViewById(R.id.etPreferenceTimezone);

            rvTimeZones = (RecyclerView) view.findViewById(R.id.rvTimeZones);
            rlCurrency = (RelativeLayout) view.findViewById(R.id.rlCurrency);
            switchLock = (Switch) view.findViewById(R.id.switchLock);
            switchFaceLock = (Switch) view.findViewById(R.id.switchFaceLock);
            lyCoyniPin = (RelativeLayout) view.findViewById(R.id.lyCoyniPin);

            viewBack = (View) view.findViewById(R.id.viewBack);
            bottomSheetBehavior = BottomSheetBehavior.from(viewBottomSheet);
            bottomSheetBehavior.setHideable(true);
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

            switchLock.setOnClickListener(this);
            lyCoyniPin.setOnClickListener(this);
            switchLock.setChecked(objMyApplication.getCoyniPin());

            switchFaceLock.setOnClickListener(this);
            switchFaceLock.setChecked(isFaceLock);
            enableMenuIcon();
            if (objMyApplication.getUserTracker() != null && objMyApplication.getUserTracker().getData().getAddressAvailable()) {
                imgMyAddress.setImageResource(R.drawable.ic_my_profile_without_red);
            } else {
                imgMyAddress.setImageResource(R.drawable.ic_my_profile);
            }
            initObservables();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        imgRoundProfile = view.findViewById(R.id.round_image);

        rlSecurity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    objMyApplication.setFromWhichFragment("");
                    getActivity().findViewById(R.id.layoutMenu).setVisibility(View.GONE);
                    bottomSheetBehavior.setHideable(true);
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                    viewBottomSheet.setVisibility(View.GONE);

                    viewBottomSheet = view.findViewById(R.id.bottom_sheet_security);
                    bottomSheetBehavior = BottomSheetBehavior.from(viewBottomSheet);
                    viewBottomSheet.setVisibility(View.VISIBLE);
                    viewBack.setVisibility(View.VISIBLE);
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        rlFaceLock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    objMyApplication.setFromWhichFragment("");
                    getActivity().findViewById(R.id.layoutMenu).setVisibility(View.GONE);
                    bottomSheetBehavior.setHideable(true);
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                    viewBottomSheet.setVisibility(View.GONE);

                    viewBottomSheet = view.findViewById(R.id.bottom_sheet_facepin);
                    bottomSheetBehavior = BottomSheetBehavior.from(viewBottomSheet);
                    viewBottomSheet.setVisibility(View.VISIBLE);
                    viewBack.setVisibility(View.VISIBLE);
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        rlChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ChangePasswordFragment changePwdFragment = ChangePasswordFragment.newInstance(getActivity());
                try {
                    openFragment(changePwdFragment, "changePassword");

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        cvConfirm_AddPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    getActivity().findViewById(R.id.layoutMenu).setVisibility(View.GONE);
                    bottomSheetBehavior.setHideable(true);
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                    viewBottomSheet.setVisibility(View.GONE);

                    AccountActivatedFragment accountActivatedFragment = AccountActivatedFragment.newInstance(getActivity());
                    try {
                        openFragment(accountActivatedFragment, "accActivated");
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        rlAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ProfileDetails profileDetailsFragment = ProfileDetails.newInstance(getActivity());
                try {
                    openFragment(profileDetailsFragment, "profileDetails");

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        rlAccLimits.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AccountLimitsFragment accLimitsFragment = AccountLimitsFragment.newInstance(getActivity());
                try {
                    openFragment(accLimitsFragment, "accountlimits");

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        llUpdatePreferences.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    UserPreferenceModel obj = new UserPreferenceModel();
                    int localCurrency = 0;
                    int timeZone = 0;
                    if (Utils.checkInternet(context)) {
                        dialog = new ProgressDialog(context, R.style.MyAlertDialogStyle);
                        dialog.setIndeterminate(false);
                        dialog.setMessage("Please wait...");
                        dialog.getWindow().setGravity(Gravity.CENTER);
                        dialog.show();

                        if (etPreferenceTimezone.getText().toString().trim().equals(getString(R.string.PST))) {
                            timeZone = 0;
                            objMyApplication.setStrPreference(context.getString(R.string.isPST));
                        } else if (etPreferenceTimezone.getText().toString().trim().equals(getString(R.string.MST))) {
                            timeZone = 1;
                            objMyApplication.setStrPreference(context.getString(R.string.isMST));
                        } else if (etPreferenceTimezone.getText().toString().trim().equals(getString(R.string.CST))) {
                            timeZone = 2;
                            objMyApplication.setStrPreference(context.getString(R.string.isCST));
                        } else if (etPreferenceTimezone.getText().toString().trim().equals(getString(R.string.EST))) {
                            timeZone = 3;
                            objMyApplication.setStrPreference(context.getString(R.string.isEST));
                        } else if (etPreferenceTimezone.getText().toString().trim().equals(getString(R.string.HST))) {
                            timeZone = 4;
                            objMyApplication.setStrPreference(context.getString(R.string.isHST));
                        }
                        obj.setTimezone(timeZone);
                        obj.setLocalCurrency(localCurrency);
                        dashboardViewModel.meUpdatePreferences(obj);
                        updatePreferences();

                    }

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        rlPreferences.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                objMyApplication.setFromWhichFragment("");
                if (Utils.checkInternet(context)) {
//                    dialog = new ProgressDialog(context, R.style.MyAlertDialogStyle);
//                    dialog.setIndeterminate(false);
//                    dialog.setMessage("Please wait...");
//                    dialog.getWindow().setGravity(Gravity.CENTER);
//                    dialog.show();
                    dashboardViewModel.mePreferences();
                    getPreferences();
                }

                getActivity().findViewById(R.id.layoutMenu).setVisibility(View.GONE);
                etPreferenceTimezone.requestFocus();
                bottomSheetBehavior.setHideable(true);
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                viewBottomSheet.setVisibility(View.GONE);

                viewBottomSheet = getView().findViewById(R.id.bottom_sheet_Preferences);
                bottomSheetBehavior = BottomSheetBehavior.from(viewBottomSheet);
                viewBottomSheet.setVisibility(View.VISIBLE);
                viewBack.setVisibility(View.VISIBLE);
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                bottomSheetBehavior.setDraggable(false);
            }
        });

        rlRequests.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().findViewById(R.id.layoutMenu).setVisibility(View.GONE);
                openFragment(RequestsFragment.newInstance(getActivity()), "request");
            }
        });

        rlNotifications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, NotificationsActivity.class);
                startActivity(i);
            }
        });

        viewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideMenu();
            }
        });

        tvUserName.setText(Utils.capitalize(objMyApplication.getStrUser().toString()));
        String styledText = "<font color='status'>C - </font>";
        tvUserId.setText(Html.fromHtml(styledText) + String.valueOf(objMyApplication.getIntUserId()), TextView.BufferType.SPANNABLE);
        bindImage(objMyApplication.getStrProfileImg());
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        imgCancelPreferences.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                objMyApplication.setFromWhichFragment("fromProfile");
                viewBottomSheet = getView().findViewById(R.id.bottom_sheet_Preferences);
                bottomSheetBehavior = BottomSheetBehavior.from(viewBottomSheet);
                viewBottomSheet.setVisibility(View.GONE);
                viewBack.setVisibility(View.GONE);
                bottomSheetBehavior.setHideable(true);
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            }
        });

        imgPreferencesArrowUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rvTimeZones.setVisibility(View.GONE);
                rlCurrency.setVisibility(View.VISIBLE);
                llUpdatePreferences.setVisibility(View.VISIBLE);
                imgCancelPreferences.setVisibility(View.VISIBLE);
                imgPreferencesArrowUp.setVisibility(View.GONE);
                imgPreferencesArrowDown.setVisibility(View.VISIBLE);
            }
        });

        imgPreferencesArrowDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bindTimeZones();
            }
        });

        rlAgreements.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserAgreementsFragment userAgreementFragment = UserAgreementsFragment.newInstance(getActivity());
                openFragment(userAgreementFragment, "userAgreeements");
            }
        });

        rlPaymentMethods.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    UserTracker userTracker = objMyApplication.getUserTracker();
                    if (userTracker != null && userTracker.getData().getAddressAvailable() && userTracker.getData().getProfileVerified()
                            && userTracker.getData().getPersonIdentified()) {
                        objMyApplication.setResolveUrl(false);
                        PaymentMethodsBuyFragment paymentMethodsBuyFragment = PaymentMethodsBuyFragment.newInstance(getActivity());
                        try {
                            openFragment(paymentMethodsBuyFragment, "PaymentMethodsBuy");
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    } else {
                        getActivity().findViewById(R.id.layoutMenu).setVisibility(View.GONE);
                        bottomSheetBehavior.setHideable(true);
                        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                        viewBottomSheet.setVisibility(View.GONE);

                        objMyApplication.setFromWhichFragment("");
                        viewBottomSheet = getView().findViewById(R.id.bottom_sheet_addPayment);
                        bottomSheetBehavior = BottomSheetBehavior.from(viewBottomSheet);
                        viewBottomSheet.setVisibility(View.VISIBLE);
                        viewBack.setVisibility(View.VISIBLE);
                        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        etPreferenceTimezone.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                try {
                    getActivity().findViewById(R.id.layoutMenu).setVisibility(View.GONE);
                    bindTimeZones();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                return false;
            }
        });

        tvLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                objMyApplication.setLogout(true);
                objMyApplication.logOut(getActivity());
//                objMyApplication.logOutApp(getActivity());
            }
        });

        switchLock.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                try {
                    if (objMyApplication.getCoyniPin()) {
                        updatePin(isChecked);
                    } else {
                        if (Utils.checkAuthentication(getActivity())) {
                            updatePin(isChecked);
                        } else {
                            Utils.displayAlert("You need to enable the security settings in phone for making transactions with security.", getActivity());
                            switchLock.setChecked(false);
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        switchFaceLock.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (Utils.checkAuthentication(getActivity())) {
                    if (isChecked) {
                        saveCredentials("true");
                    } else {
                        saveCredentials("false");
                    }
                    Utils.displayCloseAlert("Successfully Updated.", getActivity());
                    if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                        bottomSheetBehavior.setHideable(true);
                        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                        if (viewBottomSheet.getVisibility() == View.VISIBLE) {
                            viewBottomSheet.setVisibility(View.GONE);
                        }
                        viewBack.setVisibility(View.GONE);
                    }
                } else {
                    Utils.displayAlert("You need to enable the security settings in phone.", getActivity());
                    switchFaceLock.setChecked(false);
                }
            }
        });

        return view;
    }

    private void initObservables() {
        dashboardViewModel.getCoyniPinResponseMutableLiveData().observe(getViewLifecycleOwner(), new Observer<CoyniPinResponse>() {
            @Override
            public void onChanged(CoyniPinResponse coyniPinResponse) {
                dialog.dismiss();
                if (getViewLifecycleOwner().getLifecycle().getCurrentState() == Lifecycle.State.RESUMED) {
                    if (coyniPinResponse != null && coyniPinResponse.getStatus().toUpperCase().equals("SUCCESS")) {
//                        Utils.displayAlert("Successfully Updated.", getActivity());
                        Utils.displayCloseAlert("Successfully Updated.", getActivity());
                        objMyApplication.setCoyniPin(switchLock.isChecked());
                        if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                            bottomSheetBehavior.setHideable(true);
                            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                            if (viewBottomSheet.getVisibility() == View.VISIBLE) {
                                viewBottomSheet.setVisibility(View.GONE);
                            }
                            viewBack.setVisibility(View.GONE);
                        }
                    } else {
                        Utils.displayAlert(coyniPinResponse.getData().getMessage(), getActivity());
                    }
                }
            }
        });

        dashboardViewModel.getUserMutableLiveData().observe(getActivity(), new Observer<User>() {
            @Override
            public void onChanged(User user) {
                if (dialog != null) {
                    dialog.dismiss();
                }
                if (user != null) {
                    objMyApplication.setStrUser(Utils.capitalize(user.getData().getFirstName() + " " + user.getData().getLastName()));
                    objMyApplication.setStrUserCode(user.getData().getFirstName().substring(0, 1).toUpperCase() + user.getData().getLastName().substring(0, 1).toUpperCase());

                    objMyApplication.setStrEmail(user.getData().getEmail());
                    objMyApplication.setStrPhoneNum(user.getData().getPhoneNumber());
                    objMyApplication.setStrAddressLine1(user.getData().getAddressLine1());
                    objMyApplication.setStrAddressLine2(user.getData().getAddressLine2());
                    objMyApplication.setStrState(user.getData().getState());
                    objMyApplication.setStrCity(user.getData().getCity());
                    objMyApplication.setStrCountry(user.getData().getCountry());
                    objMyApplication.setStrZipCode(user.getData().getZipCode());
                    objMyApplication.setIntUserId(user.getData().getId());
                    objMyApplication.setStrProfileImg(user.getData().getImage());
                    bindImage(user.getData().getImage());
                }
            }
        });

        dashboardViewModel.getApiErrorMutableLiveData().observe(getViewLifecycleOwner(), new Observer<APIError>() {
            @Override
            public void onChanged(APIError apiError) {
                if (dialog != null) {
                    dialog.dismiss();
                }
                if (apiError != null) {
                    if (!apiError.getError().getErrorDescription().equals("")) {
                        if (apiError.getError().getErrorDescription().toLowerCase().contains("expire") || apiError.getError().getErrorDescription().toLowerCase().contains("invalid token")) {
                            objMyApplication.displayAlert(getActivity(), context.getString(R.string.session));
                        } else {
                            Utils.displayAlert(apiError.getError().getErrorDescription(), getActivity());
                        }
                    } else {
                        Utils.displayAlert(apiError.getError().getFieldErrors().get(0), getActivity());
                    }
                }
            }
        });

        notificationsViewModel.getNotificationsMutableLiveData().observe(getViewLifecycleOwner(), new Observer<Notifications>() {
            @Override
            public void onChanged(Notifications notifications) {
                if (notifications != null && notifications.getStatus().toUpperCase().equals("SUCCESS")) {
                    objMyApplication.setNotificationsDataItems(notifications.getData().getItems());
                    if (notifications.getData().getItems() != null && notifications.getData().getItems().size() > 0) {
                        for (int i = 0; i < notifications.getData().getItems().size(); i++) {
                            if (!notifications.getData().getItems().get(i).getRead()) {
                                objMyApplication.setNotiAvailable(true);
                                break;
                            } else {
                                objMyApplication.setNotiAvailable(false);
                            }
                        }
                    } else {
                        objMyApplication.setNotiAvailable(false);
                    }
                    enableMenuIcon();
                }
            }
        });

        payViewModel.getReceiveRequestsMutableLiveData().observe(getViewLifecycleOwner(), new Observer<ReceiveRequests>() {
            @Override
            public void onChanged(ReceiveRequests receiveRequests) {
                if (dialog != null) {
                    dialog.dismiss();
                }
                if (receiveRequests != null) {
                    if (receiveRequests.getStatus().toLowerCase().equals("success")) {
                        if (receiveRequests.getData().getItems() != null && receiveRequests.getData().getItems().size() > 0) {
                            String strStaus = "";
                            for (int i = 0; i < receiveRequests.getData().getItems().size(); i++) {
                                strStaus = Utils.capitalize(receiveRequests.getData().getItems().get(i).getStatus().replace(" ", ""));
                                if (strStaus.equals(Utils.requested) || strStaus.equals(Utils.remind)) {
                                    objMyApplication.setReqAvailable(true);
                                    break;
                                } else {
                                    objMyApplication.setReqAvailable(false);
                                }
                            }
                        } else {
                            objMyApplication.setReqAvailable(false);
                        }
                        enableMenuIcon();
                    }
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.switchLock:
                //selectScreenLock();
                break;
            case R.id.lyCoyniPin:
                selectCoyniPin();
                break;
        }
    }

    public void bindTimeZones() {
        try {
            ArrayList<String> arrZonesList = new ArrayList<>();
            arrZonesList.add(getString(R.string.PST));
            arrZonesList.add(getString(R.string.MST));
            arrZonesList.add(getString(R.string.CST));
            arrZonesList.add(getString(R.string.EST));
            arrZonesList.add(getString(R.string.HST));

            customerTimeZonesAdapter = new CustomerTimeZonesAdapter(arrZonesList, getActivity());
            LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
            rvTimeZones.setLayoutManager(mLayoutManager);
            rvTimeZones.setItemAnimator(new DefaultItemAnimator());
            rvTimeZones.setAdapter(customerTimeZonesAdapter);
            rvTimeZones.setVisibility(View.VISIBLE);
            rlCurrency.setVisibility(View.GONE);
            llUpdatePreferences.setVisibility(View.GONE);
            imgCancelPreferences.setVisibility(View.GONE);
            imgPreferencesArrowUp.setVisibility(View.VISIBLE);
            imgPreferencesArrowDown.setVisibility(View.GONE);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void getPreferences() {
        dashboardViewModel.getPreferencesResponseMutableLiveData().observe(getActivity(), new Observer<Preferences>() {
            @Override
            public void onChanged(Preferences user) {
                //dialog.dismiss();
                if (user.getData().getTimeZone() == 0) {
                    etPreferenceTimezone.setText(getString(R.string.PST));
                } else if (user.getData().getTimeZone() == 1) {
                    etPreferenceTimezone.setText(getString(R.string.MST));
                } else if (user.getData().getTimeZone() == 2) {
                    etPreferenceTimezone.setText(getString(R.string.CST));
                } else if (user.getData().getTimeZone() == 3) {
                    etPreferenceTimezone.setText(getString(R.string.EST));
                } else if (user.getData().getTimeZone() == 4) {
                    etPreferenceTimezone.setText(getString(R.string.HST));
                }
            }
        });
    }

    private void updatePreferences() {
        dashboardViewModel.getUserPreferenceMutableLiveData().observe(getActivity(), new Observer<UserPreference>() {
            @Override
            public void onChanged(UserPreference user) {

                if (user != null) {
                    dialog.dismiss();
//                    Toast.makeText(context, "Preferences Updated", Toast.LENGTH_LONG).show();
//                    Utils.displayAlert("Preferences successfully updated!", getActivity());
                    Utils.displayCloseAlert("Preferences successfully updated!", getActivity());
                    ProfileFragment profileFragment = ProfileFragment.newInstance(getActivity());
                    try {
                        openFragment(profileFragment, "profile");
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });
    }

    private void updateAddressAfterAdding() {
        dashboardViewModel.getUserMutableLiveData().observe(getActivity(), new Observer<User>() {
            @Override
            public void onChanged(User user) {
                if (user != null) {
                    objMyApplication.setStrAddressLine1(user.getData().getAddressLine1());
                    objMyApplication.setStrAddressLine2(user.getData().getAddressLine2());
                    objMyApplication.setStrState(user.getData().getState());
                    objMyApplication.setStrCity(user.getData().getCity());
//                    objMyApplication.setStrCountry(user.getData().getCountry());
                    objMyApplication.setStrZipCode(user.getData().getZipCode());
                    objMyApplication.setIntUserId(user.getData().getId());
                }
            }
        });
    }

    private void openFragment(Fragment fragment, String tag) {
        try {
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.container, fragment, tag).addToBackStack(tag);
//            transaction.replace(R.id.container, fragment, tag);
            transaction.commit();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void populatePreferenceTimeZone(String strCountry) {
        try {
            etPreferenceTimezone.setText(strCountry);
            rvTimeZones.setVisibility(View.GONE);
            rlCurrency.setVisibility(View.VISIBLE);
            llUpdatePreferences.setVisibility(View.VISIBLE);
            imgCancelPreferences.setVisibility(View.VISIBLE);
            imgPreferencesArrowUp.setVisibility(View.GONE);
            imgPreferencesArrowDown.setVisibility(View.VISIBLE);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void selectCoyniPin() {
        try {
//            ImageView imgLock = view.findViewById(R.id.imgLock);
//            ImageView imgCPin = view.findViewById(R.id.imgCPin);
//            imgLock.setImageResource(R.drawable.ic_radio_unselect);
//            imgCPin.setImageResource(R.drawable.ic_radio_select);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void hideMenu() {
        try {
            if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                bottomSheetBehavior.setHideable(true);
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                if (viewBottomSheet.getVisibility() == View.VISIBLE) {
                    viewBottomSheet.setVisibility(View.GONE);
                    objMyApplication.setFromWhichFragment("fromProfile");
                }
                viewBack.setVisibility(View.GONE);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void bindImage(String imageString) {
        try {
            if (imageString != null && !imageString.equals("")) {
                if (imageString != null && !imageString.trim().equals("")) {
                    GlideApp.with(context)
                            .load(imageString)
                            .placeholder(R.drawable.ic_profile_male_user)
                            .into(round_image);
                } else {
                    round_image.setImageResource(R.drawable.ic_profile_male_user);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void enableMenuIcon() {
        try {
            if (objMyApplication.getNotiAvailable()) {
                imgNotifications.setImageResource(R.drawable.ic_notification_red);
            } else {
                imgNotifications.setImageResource(R.drawable.ic_notification_without_red);
            }
            if (objMyApplication.getReqAvailable()) {
                imgRequests.setImageResource(R.drawable.ic_requests_red);
            } else {
                imgRequests.setImageResource(R.drawable.ic_requests);
            }
            ((MainActivity) context).enableMenuIcon();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void updatePin(Boolean isChecked) {
        try {
            CoyniPinRequest request = new CoyniPinRequest();
            if (isChecked) {
                request.setPin("true");
            } else {
                request.setPin("false");
            }
            if (Utils.checkInternet(context)) {
                dialog = new ProgressDialog(context, R.style.MyAlertDialogStyle);
                dialog.setIndeterminate(false);
                dialog.setMessage("Please wait...");
                dialog.getWindow().setGravity(Gravity.CENTER);
                dialog.show();
                dashboardViewModel.coyniPin(request);
            } else {
                Utils.displayAlert(getString(R.string.internet), getActivity());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void SetLock() {
        try {
            mydatabase = context.openOrCreateDatabase("Coyni", context.MODE_PRIVATE, null);
            dsFacePin = mydatabase.rawQuery("Select * from tblFacePinLock", null);
            dsFacePin.moveToFirst();
            if (dsFacePin.getCount() > 0) {
                String value = dsFacePin.getString(1);
                if (value.equals("true")) {
                    isFaceLock = true;
                } else {
                    isFaceLock = false;
                }
            }
        } catch (Exception ex) {
            if (ex.getMessage().toString().contains("no such table")) {
                mydatabase.execSQL("DROP TABLE IF EXISTS tblFacePinLock;");
                mydatabase.execSQL("CREATE TABLE IF NOT EXISTS tblFacePinLock(id INTEGER PRIMARY KEY AUTOINCREMENT DEFAULT 1, isLock TEXT);");
            }
        }
    }

    private void saveCredentials(String value) {
        try {
            mydatabase.execSQL("Delete from tblFacePinLock");
            mydatabase.execSQL("INSERT INTO tblFacePinLock(id,isLock) VALUES(null,'" + value + "')");
//            if (value.equals("true")) {
//                mydatabase.execSQL("Delete from tblUserDetails");
//                mydatabase.execSQL("INSERT INTO tblUserDetails(id,username,password) VALUES(null,'" + objMyApplication.getStrLEmail() + "','" + objMyApplication.getStrLPwd() + "')");
//            } else {
//                mydatabase.execSQL("Delete from tblUserDetails");
//            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
