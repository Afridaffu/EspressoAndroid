package com.coyni.android.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.coyni.android.fragments.Asset_Fragment;
import com.coyni.android.fragments.IssuingCardFragment;
import com.coyni.android.fragments.PaymentMethodsBuyFragment;
import com.coyni.android.fragments.ProfileFragment;
import com.coyni.android.fragments.SignetAccountFragment;
import com.coyni.android.model.usertracker.UserTracker;
import com.coyni.android.utils.Utils;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.coyni.android.R;
import com.coyni.android.fragments.AccountActivatedFragment;
import com.coyni.android.fragments.TokenFragment;
import com.coyni.android.utils.MyApplication;

import org.jetbrains.annotations.NotNull;

public class MainActivity extends AppCompatActivity {
    MyApplication objMyApplication;
    Boolean isToken = false, isAsset = false, isPayment = false, isMenu = false, isProfile = false;
    LinearLayout layoutToken, layoutAsset, layoutSubMenu, layoutProfile, layoutMenu, layoutPayment;
    public static BottomSheetBehavior bottomSheetBehavior;
    public static View viewBottomSheet, viewBack;
    TextView tvTitle;
    RelativeLayout layoutTWihdraw, layoutTBuy, layoutCard, layoutBank;
    RelativeLayout layoutInstant, layoutWBank, layoutGift, layoutWSignet, layoutTPay, layoutTScan;
    SharedPreferences settings;
    String MyPREFERENCES = "MyPrefs";
    UserTracker userTracker;
    CardView tokenIndicator, menuIndicator, payIndicator, assetIndicator, cvConfirm_AddPayment;
    BroadcastReceiver broadcastReceiver = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);
            initialization();
            receiver();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            if (bottomSheetBehavior != null && bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                bottomSheetBehavior.setHideable(true);
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                if (viewBottomSheet.getVisibility() == View.VISIBLE) {
                    viewBottomSheet.setVisibility(View.GONE);
                }
                viewBack.setVisibility(View.GONE);
            }

            objMyApplication.getHandler().removeCallbacks(objMyApplication.getMyRunnable());
            objMyApplication.userInactive(MainActivity.this, this, false);
            objMyApplication.getAppHandler().removeCallbacks(objMyApplication.getAppRunnable());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        objMyApplication.getHandler().removeCallbacks(objMyApplication.getMyRunnable());
        objMyApplication.userInactive(MainActivity.this, this, true);
    }

    @Override
    public void onUserInteraction() {
        // TODO Auto-generated method stub
        super.onUserInteraction();
        try {
            objMyApplication.getHandler().removeCallbacks(objMyApplication.getMyRunnable());
            objMyApplication.userInactive(MainActivity.this, this, false);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }

    @Override
    public void onBackPressed() {
        try {
            if (objMyApplication.getFromWhichFragment().equals("fromProfileDebitCreditCards")) {
                ProfileFragment profileFragment = ProfileFragment.newInstance(MainActivity.this);
                openFragment(profileFragment, "profileFragment");
            } else if (objMyApplication.getFromWhichFragment().equals("fromProfile")) {
                loadToken();
            } else if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                bottomSheetBehavior.setHideable(true);
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                if (viewBottomSheet.getVisibility() == View.VISIBLE) {
                    viewBottomSheet.setVisibility(View.GONE);
                    objMyApplication.setFromWhichFragment("fromProfile");
                }
                viewBack.setVisibility(View.GONE);
            } else {
                int fragments = getSupportFragmentManager().getBackStackEntryCount();
                if (fragments == 0) {
                    finish();
                } else if (fragments > 0) {
                    getSupportFragmentManager().popBackStack();
                    Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.container);
                    String strTag = currentFragment.getTag();
                    if (fragments == 1 || (strTag != null & (strTag.equals("asset") || strTag.equals("issuing")))) {
                        loadToken();
                    }
                } else {
                    super.onBackPressed();
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    private void initialization() {
        try {
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_sent);
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            objMyApplication = (MyApplication) getApplicationContext();
            layoutToken = (LinearLayout) findViewById(R.id.layoutToken);
            layoutAsset = (LinearLayout) findViewById(R.id.layoutAsset);
            layoutSubMenu = (LinearLayout) findViewById(R.id.layoutSubMenu);
            layoutPayment = (LinearLayout) findViewById(R.id.layoutPayment);
            layoutProfile = (LinearLayout) findViewById(R.id.layoutProfile);
            layoutMenu = (LinearLayout) findViewById(R.id.layoutMenu);
            layoutTWihdraw = (RelativeLayout) findViewById(R.id.layoutTWihdraw);
            layoutTBuy = (RelativeLayout) findViewById(R.id.layoutTBuy);
            layoutCard = (RelativeLayout) findViewById(R.id.layoutCard);
            layoutBank = (RelativeLayout) findViewById(R.id.layoutBank);
            layoutInstant = (RelativeLayout) findViewById(R.id.layoutInstant);
            layoutWBank = (RelativeLayout) findViewById(R.id.layoutWBank);
            layoutWSignet = (RelativeLayout) findViewById(R.id.layoutWSignet);
            layoutGift = (RelativeLayout) findViewById(R.id.layoutGift);
            layoutTPay = (RelativeLayout) findViewById(R.id.layoutTPay);
            layoutTScan = (RelativeLayout) findViewById(R.id.layoutTScan);
            tvTitle = (TextView) toolbar.findViewById(R.id.tvTitle);
            tokenIndicator = (CardView) findViewById(R.id.tokenIndicator);
            menuIndicator = (CardView) findViewById(R.id.menuIndicator);
            payIndicator = (CardView) findViewById(R.id.payIndicator);
            assetIndicator = (CardView) findViewById(R.id.assetIndicator);
            cvConfirm_AddPayment = (CardView) findViewById(R.id.cvConfirm_AddPayment);
            viewBottomSheet = findViewById(R.id.bottom_sheet_token);
            viewBack = (View) findViewById(R.id.viewBackMain);
            bottomSheetBehavior = BottomSheetBehavior.from(viewBottomSheet);
            bottomSheetBehavior.setHideable(true);
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            settings = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
            if (!settings.getBoolean("isLogin", false)) {
                SharedPreferences.Editor editor = settings.edit();
                editor.putBoolean("isLogin", true);
                editor.commit();
            }
            if (getIntent().getStringExtra("withsig") != null) {
                if (getIntent().getStringExtra("withsig").equals("signet")) {
                    objMyApplication.setFromWhichFragment("");
                    if (getIntent().getStringExtra("status") != null && getIntent().getStringExtra("status").equals("edit")) {
                        openFragment(SignetAccountFragment.newInstance(MainActivity.this, "Edit", "withdraw"), "signet");
                    } else {
                        openFragment(SignetAccountFragment.newInstance(MainActivity.this, "Add", "withdraw"), "signet");
                    }
                } else if (getIntent().getStringExtra("withsig").equals("payment")) {
                    AccountActivatedFragment accountActivatedFragment = AccountActivatedFragment.newInstance(MainActivity.this);
                    openFragment(accountActivatedFragment, "accActivated");
                }
            } else if (getIntent().getStringExtra("fromBuyTokenProfile") != null) {
                if (getIntent().getStringExtra("fromBuyTokenProfile").equals("fromBuyTokenProfile")) {
                    PaymentMethodsBuyFragment paymentMethodsBuyFragment = PaymentMethodsBuyFragment.newInstance(MainActivity.this);
                    openFragment(paymentMethodsBuyFragment, "PaymentMethodsBuy");
                }

            } else {
                loadToken();
            }

            layoutToken.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        if (!isToken) {
                            objMyApplication.setToken(true);
                            loadToken();
//                            tokenIndicator.setVisibility(View.VISIBLE);
//                            menuIndicator.setVisibility(View.GONE);
//                            payIndicator.setVisibility(View.GONE);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });

            layoutAsset.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!isAsset) {
                        objMyApplication.setToken(true);
                        loadAsset();
                        //Utils.displayAlert("Coming soon", MainActivity.this);
                    }
                }
            });

            layoutSubMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
//                        if (isToken) {
//                            viewBottomSheet = findViewById(R.id.bottom_sheet_token);
//                        } else if (isAsset) {
//                            viewBottomSheet = findViewById(R.id.bottom_sheet_asset);
//                        }
                        objMyApplication.setFromWhichFragment("");
                        hideMenu();
                        viewBottomSheet = findViewById(R.id.bottom_sheet_token);
                        viewBack = (View) findViewById(R.id.viewBackMain);
                        bottomSheetBehavior = BottomSheetBehavior.from(viewBottomSheet);
                        viewBottomSheet.setVisibility(View.VISIBLE);
                        viewBack.setVisibility(View.VISIBLE);
                        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });

            layoutPayment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        objMyApplication.setToken(true);
                        loadPayments();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });

            layoutTPay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    userTracker = objMyApplication.getUserTracker();
                    if (userTracker != null && userTracker.getData().getAddressAvailable() && userTracker.getData().getProfileVerified()
                            && userTracker.getData().getPersonIdentified()) {
                        Intent i = new Intent(MainActivity.this, AddRecipientActivity.class);
                        startActivity(i);
                    } else {
                        hideMenu();
                        AccountActivatedFragment accountActivatedFragment = AccountActivatedFragment.newInstance(MainActivity.this);
                        openFragment(accountActivatedFragment, "accActivated");
                    }
                }

            });

            layoutTScan.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        userTracker = objMyApplication.getUserTracker();
                        if (userTracker != null && userTracker.getData().getAddressAvailable() && userTracker.getData().getProfileVerified()
                                && userTracker.getData().getPersonIdentified()) {
                            Intent i = new Intent(MainActivity.this, ScanActivity.class);
                            startActivity(i);
                        } else {
                            hideMenu();
                            AccountActivatedFragment accountActivatedFragment = AccountActivatedFragment.newInstance(MainActivity.this);
                            openFragment(accountActivatedFragment, "accActivated");
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });

            layoutTWihdraw.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        userTracker = objMyApplication.getUserTracker();
                        if (userTracker != null && userTracker.getData().getAddressAvailable() && userTracker.getData().getProfileVerified()
                                && userTracker.getData().getPersonIdentified() && userTracker.getData().getPaymentModeAdded()) {
                            bottomSheetBehavior.setHideable(true);
                            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                            viewBottomSheet.setVisibility(View.GONE);

                            viewBottomSheet = findViewById(R.id.bottom_sheet_withdraw);
                            bottomSheetBehavior = BottomSheetBehavior.from(viewBottomSheet);
                            viewBottomSheet.setVisibility(View.VISIBLE);
                            viewBack.setVisibility(View.VISIBLE);
                            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                            bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
                                @Override
                                public void onStateChanged(@NonNull @NotNull View bottomSheet, int newState) {
                                    try {
                                        if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_DRAGGING) {
                                            viewBack.setVisibility(View.GONE);
                                        }
                                    } catch (Exception ex) {
                                        ex.printStackTrace();
                                    }
                                }

                                @Override
                                public void onSlide(@NonNull @NotNull View bottomSheet, float slideOffset) {

                                }
                            });
                        } else {
                            hideMenu();
                            AccountActivatedFragment accountActivatedFragment = AccountActivatedFragment.newInstance(MainActivity.this);
                            openFragment(accountActivatedFragment, "accActivated");
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });

            layoutTBuy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    userTracker = objMyApplication.getUserTracker();
                    if (userTracker != null && userTracker.getData().getAddressAvailable() && userTracker.getData().getProfileVerified()
                            && userTracker.getData().getPersonIdentified()) {
                        loadBuyTokens();
                    } else {
                        hideMenu();
                        AccountActivatedFragment accountActivatedFragment = AccountActivatedFragment.newInstance(MainActivity.this);
                        openFragment(accountActivatedFragment, "accActivated");
                    }
                }
            });

            layoutCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    hideMenu();
                    Intent i = new Intent(MainActivity.this, BuyTokenActivity.class);
                    i.putExtra("type", "card");
                    startActivity(i);
                }
            });

            layoutBank.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    hideMenu();
                    Intent i = new Intent(MainActivity.this, BuyTokenActivity.class);
                    i.putExtra("type", "bank");
                    startActivity(i);
                }
            });

            layoutInstant.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    hideMenu();
                    Intent i = new Intent(MainActivity.this, BuyTokenActivity.class);
                    i.putExtra("type", "withdraw");
                    i.putExtra("subtype", "instantpay");
                    startActivity(i);
                }
            });

            layoutWBank.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    hideMenu();
//                    Intent i = new Intent(MainActivity.this, WithdrawTokenActivity.class);
//                    i.putExtra("type", "withdraw");
//                    i.putExtra("subtype", "bank");
//                    startActivity(i);
                }
            });

            layoutWSignet.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    hideMenu();
                    Intent i = new Intent(MainActivity.this, WithdrawTokenActivity.class);
                    i.putExtra("type", "withdraw");
                    i.putExtra("subtype", "signet");
                    startActivity(i);
                }
            });

            layoutGift.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    hideMenu();
                    Intent i = new Intent(MainActivity.this, GiftCardActivity.class);
                    startActivity(i);
                }
            });

            layoutProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        if (!isMenu) {
                            viewBack.setVisibility(View.GONE);
                            loadProfile();
//                            tokenIndicator.setVisibility(View.GONE);
//                            menuIndicator.setVisibility(View.VISIBLE);
//                            payIndicator.setVisibility(View.GONE);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });

            cvConfirm_AddPayment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        hideMenu();
                        AccountActivatedFragment accountActivatedFragment = AccountActivatedFragment.newInstance(MainActivity.this);
                        openFragment(accountActivatedFragment, "accActivated");
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });

            bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
                @Override
                public void onStateChanged(@NonNull @NotNull View bottomSheet, int newState) {
                    try {
                        if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_DRAGGING) {
                            viewBack.setVisibility(View.GONE);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }

                @Override
                public void onSlide(@NonNull @NotNull View bottomSheet, float slideOffset) {

                }
            });

            viewBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    hideMenu();
                }
            });

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void setToolbar(Toolbar toolbar, Boolean value) {
        try {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(value);
            getSupportActionBar().setDisplayShowHomeEnabled(value);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void openFragment(Fragment fragment, String tag) {
        try {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//            transaction.replace(R.id.container, fragment, tag).addToBackStack(tag);
            transaction.replace(R.id.container, fragment, tag);
            if (!tag.equals("token") && !tag.equals("signet") && !tag.equals("PaymentMethodsBuy")) {
                transaction.addToBackStack(null);
            }
            transaction.commit();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void loadToken() {
        try {
            hideMenu();
            TokenFragment tokenFragment = TokenFragment.newInstance(MainActivity.this);
            openFragment(tokenFragment, "token");
            enableToken();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void loadAsset() {
        try {
//            openFragment(AssetFragment.newInstance(MainActivity.this), "asset");
            hideMenu();
            openFragment(Asset_Fragment.newInstance(MainActivity.this), "asset");
            enableAsset();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void loadPayments() {
        try {
            hideMenu();
            openFragment(IssuingCardFragment.newInstance(MainActivity.this), "issuing");
            enablePayment();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void loadProfile() {
        ProfileFragment profileFragment = ProfileFragment.newInstance(MainActivity.this);
        try {
            hideMenu();
            openFragment(profileFragment, "profile");
            enableProfile();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void selectedMenu() {
        ImageView imgToken, imgAsset, imgPayments, imgMenu;
        TextView tvToken, tvAsset, tvPayments, tvMenu;
        try {
            imgToken = (ImageView) findViewById(R.id.imgToken);
            imgAsset = (ImageView) findViewById(R.id.imgAsset);
            imgPayments = (ImageView) findViewById(R.id.imgPayments);
            imgMenu = (ImageView) findViewById(R.id.imgMenu);
            tvToken = (TextView) findViewById(R.id.tvToken);
            tvAsset = (TextView) findViewById(R.id.tvAsset);
            tvPayments = (TextView) findViewById(R.id.tvPayments);
            tvMenu = (TextView) findViewById(R.id.tvMenu);
            tokenIndicator.setVisibility(View.GONE);
            menuIndicator.setVisibility(View.GONE);
            assetIndicator.setVisibility(View.GONE);
            payIndicator.setVisibility(View.GONE);
            if (isToken) {
                imgToken.setImageResource(R.drawable.ic_token);
                tvToken.setTextColor(getResources().getColor(R.color.status));
                tokenIndicator.setVisibility(View.VISIBLE);
            } else {
                imgToken.setImageResource(R.drawable.ic_token_unselect);
                tvToken.setTextColor(getResources().getColor(R.color.unselectmenu));
            }

            if (isAsset) {
                imgAsset.setImageResource(R.drawable.ic_assets);
                tvAsset.setTextColor(getResources().getColor(R.color.status));
                assetIndicator.setVisibility(View.VISIBLE);
            } else {
                imgAsset.setImageResource(R.drawable.ic_assets_unselect);
                tvAsset.setTextColor(getResources().getColor(R.color.unselectmenu));
            }

            if (isPayment) {
                imgPayments.setImageResource(R.drawable.ic_payments);
                tvPayments.setTextColor(getResources().getColor(R.color.status));
                payIndicator.setVisibility(View.VISIBLE);
            } else {
                imgPayments.setImageResource(R.drawable.ic_payments_unselect);
                tvPayments.setTextColor(getResources().getColor(R.color.unselectmenu));
            }

            if (isMenu) {
                if (objMyApplication.getReqAvailable() || objMyApplication.getNotiAvailable() || !objMyApplication.getUserTracker().getData().getAddressAvailable()) {
                    imgMenu.setImageResource(R.drawable.ic_menu_select);
                } else {
                    imgMenu.setImageResource(R.drawable.ic_menu_select_withoured);
                }
                tvMenu.setTextColor(getResources().getColor(R.color.status));
                menuIndicator.setVisibility(View.VISIBLE);
            } else {
                if (objMyApplication.getReqAvailable() || objMyApplication.getNotiAvailable() || !objMyApplication.getUserTracker().getData().getAddressAvailable()) {
                    imgMenu.setImageResource(R.drawable.ic_menu_unselect);
                } else {
                    imgMenu.setImageResource(R.drawable.ic_menu_unselect_withoutred);
                }
                tvMenu.setTextColor(getResources().getColor(R.color.unselectmenu));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void walletDetails(String strTitle, Fragment fragment, String tag) {
        try {
            tvTitle.setText(strTitle);
            openFragment(fragment, tag);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void loadBuyTokens() {
        try {
            hideMenu();
            userTracker = objMyApplication.getUserTracker();
            if (userTracker != null && userTracker.getData().getAddressAvailable() && userTracker.getData().getProfileVerified()
                    && userTracker.getData().getPersonIdentified() && userTracker.getData().getPaymentModeAdded()) {
                bottomSheetBehavior.setHideable(true);
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                viewBottomSheet.setVisibility(View.GONE);

                viewBottomSheet = findViewById(R.id.bottom_sheet_buytoken);
                bottomSheetBehavior = BottomSheetBehavior.from(viewBottomSheet);
                bottomSheetBehavior.setPeekHeight(0);
                viewBottomSheet.setVisibility(View.VISIBLE);
                viewBack.setVisibility(View.VISIBLE);
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
                    @Override
                    public void onStateChanged(@NonNull @NotNull View bottomSheet, int newState) {
                        try {
                            if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_DRAGGING) {
                                viewBack.setVisibility(View.GONE);
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }

                    @Override
                    public void onSlide(@NonNull @NotNull View bottomSheet, float slideOffset) {

                    }
                });
            } else {
                hideMenu();
                AccountActivatedFragment accountActivatedFragment = AccountActivatedFragment.newInstance(MainActivity.this);
                openFragment(accountActivatedFragment, "accActivated");
            }
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

    public void enableToken() {
        try {
            isToken = true;
            isAsset = false;
            isPayment = false;
            isMenu = false;
            selectedMenu();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void enableAsset() {
        try {
            isToken = false;
            isAsset = true;
            isPayment = false;
            isMenu = false;
            selectedMenu();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void enablePayment() {
        try {
            isToken = false;
            isAsset = false;
            isPayment = true;
            isMenu = false;
            selectedMenu();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void enableProfile() {
        try {
            isToken = false;
            isAsset = false;
            isPayment = false;
            isMenu = true;
            selectedMenu();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void enableMenuIcon() {
        try {
            ImageView imgMenu = (ImageView) findViewById(R.id.imgMenu);
            if (isMenu) {
                if (objMyApplication.getReqAvailable() || objMyApplication.getNotiAvailable() || !objMyApplication.getUserTracker().getData().getAddressAvailable()) {
                    imgMenu.setImageResource(R.drawable.ic_menu_select);
                } else {
                    imgMenu.setImageResource(R.drawable.ic_menu_select_withoured);
                }
            } else {
                if (objMyApplication.getReqAvailable() || objMyApplication.getNotiAvailable() || !objMyApplication.getUserTracker().getData().getAddressAvailable()) {
                    imgMenu.setImageResource(R.drawable.ic_menu_unselect);
                } else {
                    imgMenu.setImageResource(R.drawable.ic_menu_unselect_withoutred);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void receiver() {
        try {
            IntentFilter intentFilterACSD = new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);

            broadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    try {
                        if (intent.getAction().equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
                            objMyApplication.getAppHandler().removeCallbacks(objMyApplication.getAppRunnable());
                            objMyApplication.appMinimized(MainActivity.this);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            };
            this.registerReceiver(broadcastReceiver, intentFilterACSD);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}