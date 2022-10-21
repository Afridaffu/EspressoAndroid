package com.coyni.mapp.view;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.coyni.mapp.model.businesswallet.WalletInfo;
import com.coyni.mapp.view.business.SelectPaymentMethodActivity;
import com.google.zxing.Reader;
import com.coyni.mapp.dialogs.OnDialogClickListener;
import com.coyni.mapp.dialogs.PayToMerchantWithAmountDialog;
import com.coyni.mapp.interfaces.OnKeyboardVisibilityListener;
import com.coyni.mapp.model.DBAInfo.BusinessTypeResp;
import com.coyni.mapp.model.biometric.BiometricTokenRequest;
import com.coyni.mapp.model.businesswallet.WalletResponseData;
import com.coyni.mapp.model.check_out_transactions.CheckOutModel;
import com.coyni.mapp.model.featurecontrols.FeatureControlRespByUser;
import com.coyni.mapp.model.featurecontrols.FeatureData;
import com.coyni.mapp.model.paidorder.PaidOrderRequest;
import com.coyni.mapp.model.transactionlimit.LimitResponseData;
import com.coyni.mapp.model.transactionlimit.TransactionLimitRequest;
import com.coyni.mapp.model.transactionlimit.TransactionLimitResponse;
import com.coyni.mapp.model.transferfee.TransferFeeRequest;
import com.coyni.mapp.utils.CheckOutConstants;
import com.coyni.mapp.utils.DatabaseHandler;
import com.coyni.mapp.utils.DisplayImageUtility;
import com.coyni.mapp.utils.LogUtils;
import com.coyni.mapp.utils.keyboards.CustomKeyboard;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Result;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.google.zxing.common.HybridBinarizer;
import com.coyni.mapp.R;
import com.coyni.mapp.model.APIError;
import com.coyni.mapp.model.wallet.UserDetails;
import com.coyni.mapp.utils.MyApplication;
import com.coyni.mapp.utils.Utils;
import com.coyni.mapp.view.business.PayToMerchantActivity;
import com.coyni.mapp.viewmodel.BusinessIdentityVerificationViewModel;
import com.coyni.mapp.viewmodel.BuyTokenViewModel;
import com.coyni.mapp.viewmodel.CoyniViewModel;
import com.coyni.mapp.viewmodel.DashboardViewModel;
import com.coyni.mapp.viewmodel.PayViewModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;
import de.hdodenhof.circleimageview.CircleImageView;

public class ScanActivity extends BaseActivity implements TextWatcher, OnKeyboardVisibilityListener {
    TextView scanMe, scanCode, scanmeSetAmountTV, savetoAlbum, userNameTV, scanMeRequestAmount;
    LinearLayout layoutHead, imageSaveAlbumLL, scanAmountLL, setAmountLL, scanMeScanCodeLL;
    ConstraintLayout flashLL;
    ScrollView scanMeSV;
    QRGEncoder qrgEncoder;
    Bitmap bitmap;
    View divider;
    Dialog setAmountDialog;
    Long mLastClickTime = 0L, mLastClickTimeQa = 0L;
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 101;
    ImageView idIVQrcode, imageShare, copyRecipientAddress, albumIV;
    ImageView closeBtnScanCode, closeBtnScanMe, imgProfile;
    private CodeScanner mcodeScanner;
    private CodeScannerView mycodeScannerView;
    MyApplication objMyApplication;
    private DatabaseHandler dbHandler;
    DashboardViewModel dashboardViewModel;
    TextView tvWalletAddress, tvName;
    boolean isTorchOn = true, isQRScan = false, isOnResumeCamera = false, isSaleOrder = true;
    boolean isSlideActionClicked = false;
    ImageView toglebtn1;
    String strWallet = "", strScanWallet = "", strQRAmount = "", strLimit = "";
    Dialog dialog;
    Dialog errorDialog;
    ConstraintLayout scannerLayout;
    View scannerBar;
    float fontSize;
    CustomKeyboard ctKey;
    public static ScanActivity scanActivity;
    EditText setAmount;
    private UserDetails details;
    //Saved To Album Layout Comp..
    TextView tvSaveUserName, saveProfileTitle, saveSetAmount;
    ImageView savedImageView;
    CircleImageView saveProfileIV;
    private LinearLayout mDenyAccessScreen;
    private CardView goToSettings;

    private static int CODE_AUTHENTICATION_VERIFICATION = 251;
    boolean isAuthenticationCalled = false;
    Boolean isFaceLock = false, isTouchId = false, isAlbumClicked = false;

    Double cynValue = 0.0, avaBal = 0.0;
    Double maxValue = 0.0, pfee = 0.0, feeInAmount = 0.0, feeInPercentage = 0.0;
    BuyTokenViewModel buyTokenViewModel;
    PayViewModel payViewModel;
    CoyniViewModel coyniViewModel;
    TransactionLimitResponse objResponse;
    BusinessIdentityVerificationViewModel businessIdentityVerificationViewModel;
    private String businessTypeValue = "";
    private boolean slideActionEnabled = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

            setContentView(R.layout.activity_pay_request_scan);
            scanActivity = this;
            initialization();
            listeners();
            initObserveres();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
        if (editable == setAmount.getEditableText()) {
            try {
//                if (editable.length() > 0 && !editable.toString().equals(".") && !editable.toString().equals(".00") && Utils.doubleParsing(editable.toString()) > 0) {
                if (editable.length() > 0 && !editable.toString().equals(".") && !editable.toString().equals(".00")) {
                    setAmount.setHint("");
                    if (editable.length() > 8) {
                        setAmount.setTextSize(TypedValue.COMPLEX_UNIT_SP, 33);
                    } else if (editable.length() > 5) {
                        setAmount.setTextSize(TypedValue.COMPLEX_UNIT_SP, 43);
                    } else {
                        setAmount.setTextSize(Utils.pixelsToSp(ScanActivity.this, fontSize));
                    }
                    if (Utils.doubleParsing(editable.toString()) > 0.005)
                        ctKey.enableButton();
                    else
                        ctKey.disableButton();
                    setAmount.setSelection(setAmount.getText().length());
                    setAmount.setTextDirection(View.TEXT_DIRECTION_LTR);
                } else if (editable.toString().equals(".")) {
                    setAmount.setText("");
                    ctKey.disableButton();
                } else if (editable.length() == 0) {
                    setAmount.setHint("0.00");
                    setAmount.setTextDirection(View.TEXT_DIRECTION_RTL);
                    ctKey.disableButton();
                    ctKey.clearData();
                    setDefaultLength();
                } else {
                    setAmount.setText("");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private void initialization() {
        try {
            dashboardViewModel = new ViewModelProvider(this).get(DashboardViewModel.class);
            businessIdentityVerificationViewModel = new ViewModelProvider(this).get(BusinessIdentityVerificationViewModel.class);
            dbHandler = DatabaseHandler.getInstance(ScanActivity.this);
            objMyApplication = (MyApplication) getApplicationContext();
            closeBtnScanCode = findViewById(R.id.closeBtnSC);
            closeBtnScanMe = findViewById(R.id.imgCloseSM);
            scanCode = findViewById(R.id.scanCodeTV);
            mDenyAccessScreen = findViewById(R.id.deny_camera_access);
            scanMe = findViewById(R.id.scanMeTV);
            setKeyboardVisibilityListener(ScanActivity.this);
            toglebtn1 = findViewById(R.id.toglebtn);
            tvWalletAddress = findViewById(R.id.tvWalletAddress);
            mycodeScannerView = findViewById(R.id.scanner_view);
            scannerLayout = findViewById(R.id.scannerLayout);
            goToSettings = findViewById(R.id.clickToGoSettings);
            scannerBar = findViewById(R.id.lineView);
            flashLL = findViewById(R.id.flashBtnRL);
            idIVQrcode = findViewById(R.id.idIVQrcode);
            savedImageView = findViewById(R.id.savedImageIV);
            tvName = findViewById(R.id.tvName);
            scanMeRequestAmount = findViewById(R.id.scanMeRequestAmount);
            scanAmountLL = findViewById(R.id.scanAmountLL);
            layoutHead = findViewById(R.id.layoutHead);
            scanMeSV = findViewById(R.id.scanmeScrlView);
            savetoAlbum = findViewById(R.id.saveToAlbumTV);
            scanmeSetAmountTV = findViewById(R.id.scanMesetAmountTV);
            imageShare = findViewById(R.id.imgShare);
            userNameTV = findViewById(R.id.tvUserInfo);
            copyRecipientAddress = findViewById(R.id.imgCopy);
            imgProfile = findViewById(R.id.imgProfile);
            albumIV = findViewById(R.id.albumIV);
            imageSaveAlbumLL = findViewById(R.id.saveToAlbumLL);
            setAmountLL = findViewById(R.id.setAmountLL);
            divider = findViewById(R.id.divider1);

            //init Saved to Album Layout
            savedImageView = findViewById(R.id.qrImageIV);
            tvSaveUserName = findViewById(R.id.tvNameSave);
            saveProfileIV = findViewById(R.id.saveprofileIV);
            saveProfileTitle = findViewById(R.id.saveprofileTitle);
            saveSetAmount = findViewById(R.id.tvsaveSetAmount);
            scanMeScanCodeLL = findViewById(R.id.scanMeScanCodeLL);

            // Merchant Scan QR
            buyTokenViewModel = new ViewModelProvider(this).get(BuyTokenViewModel.class);
            payViewModel = new ViewModelProvider(this).get(PayViewModel.class);
            coyniViewModel = new ViewModelProvider(this).get(CoyniViewModel.class);

            if (Utils.isKeyboardVisible)
                Utils.hideKeypad(ScanActivity.this);

            avaBal = objMyApplication.getGBTBalance();
            businessIdentityVerificationViewModel.getBusinessType();
            calculateFee("10");

            if (Utils.checkInternet(ScanActivity.this)) {
                TransactionLimitRequest obj = new TransactionLimitRequest();
                obj.setTransactionType(Utils.saleOrder);
                obj.setTransactionSubType(Utils.saleOrderToken);
                dialog = Utils.showProgressDialog(ScanActivity.this);
                if (objMyApplication.getAccountType() == Utils.PERSONAL_ACCOUNT) {
                    buyTokenViewModel.transactionLimits(obj, Utils.userTypeCust);
                } else {
                    buyTokenViewModel.transactionLimits(obj, Utils.userTypeBusiness);
                }
            }
//            setFaceLock();
//            setTouchId();

            objMyApplication.initializeDBHandler(ScanActivity.this);
            isFaceLock = objMyApplication.setFaceLock();
            isTouchId = objMyApplication.setTouchId();
            if (isFaceLock || isTouchId) {
                objMyApplication.setLocalBiometric(true);
            } else {
                objMyApplication.setLocalBiometric(false);
            }
//            setFaceLock();

            if (objMyApplication.getAccountType() == Utils.PERSONAL_ACCOUNT) {
                scanMeScanCodeLL.setVisibility(View.VISIBLE);
            } else if (objMyApplication.getAccountType() == Utils.BUSINESS_ACCOUNT || objMyApplication.getAccountType() == Utils.SHARED_ACCOUNT) {
                scanMeScanCodeLL.setVisibility(View.GONE);
            }

            if (objMyApplication.getMyProfile() != null && objMyApplication.getMyProfile().getData() != null) {
                if (objMyApplication.getMyProfile().getData().getFirstName() != null && objMyApplication.getMyProfile().getData().getLastName() != null) {
                    String strName = Utils.capitalize(objMyApplication.getMyProfile().getData().getFirstName() + " " + objMyApplication.getMyProfile().getData().getLastName());
                    //                if (strName != null && strName.length() > 22) {
                    //                    tvName.setText(strName.substring(0, 22) + "...");
                    //                } else {
                    //                    tvName.setText(strName);
                    //                }

                    if (strName != null) {
                        tvName.setText(strName);
                    }
                }
            }
            bindImage();
            String savedStrName = null;
            try {
                savedStrName = Utils.capitalize(objMyApplication.getMyProfile().getData().getFirstName() + " " + objMyApplication.getMyProfile().getData().getLastName());
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (savedStrName != null && savedStrName.length() > 22) {
                tvSaveUserName.setText(savedStrName.substring(0, 22) + "...");
            } else {
                tvSaveUserName.setText(savedStrName);
            }
            saveToAlbumbindImage();
            WalletInfo walletResponse = objMyApplication.getCurrentUserData().getTokenWalletResponse();
            if (walletResponse != null && walletResponse.getWalletId() != null) {
                strWallet = walletResponse.getWalletId();
                generateQRCode(strWallet);
                tvWalletAddress.setText(strWallet.substring(0, 16) + "...");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void listeners() {
        try {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 123);
            } else {
                StartScanner();
            }


            scanMe.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        scanMe.setTextColor(getResources().getColor(R.color.white));
                        scanMe.setBackgroundResource(R.drawable.bg_core_colorfill);
                        scanCode.setBackgroundColor(getResources().getColor(R.color.white));
                        scanCode.setTextColor(getResources().getColor(R.color.primary_black));

                        layoutHead.setVisibility(View.VISIBLE);
                        scanMeSV.setVisibility(View.VISIBLE);
                        closeBtnScanMe.setVisibility(View.VISIBLE);
                        //Scan Code Visibility Gone
                        mycodeScannerView.setVisibility(View.GONE);
                        scannerLayout.setVisibility(View.GONE);
                        flashLL.setVisibility(View.GONE);
                        closeBtnScanCode.setVisibility(View.GONE);
                        if (ContextCompat.checkSelfPermission(ScanActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                            mcodeScanner.setFlashEnabled(false);
                            mcodeScanner.stopPreview();
                        }
                        if (scanAmountLL.getVisibility() == View.VISIBLE) {
                            scanMeRequestAmount.setText("");
                            scanAmountLL.setVisibility(View.GONE);
                            scanmeSetAmountTV.setText("Set Amount");
                            generateQRCode(strWallet);
                            ctKey.clearData();
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });

            scanCode.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        if (ContextCompat.checkSelfPermission(ScanActivity.this,
                                Manifest.permission.CAMERA)
                                == PackageManager.PERMISSION_GRANTED) {
                            mycodeScannerView.setVisibility(View.VISIBLE);
                            scannerLayout.setVisibility(View.VISIBLE);
                            flashLL.setVisibility(View.VISIBLE);
                            closeBtnScanCode.setVisibility(View.VISIBLE);
                            closeBtnScanMe.setVisibility(View.GONE);
                            mDenyAccessScreen.setVisibility(View.GONE);
                            layoutHead.setVisibility(View.GONE);
                            mcodeScanner.startPreview();
                        } else {
                            mycodeScannerView.setVisibility(View.GONE);
                            mDenyAccessScreen.setVisibility(View.VISIBLE);
                            flashLL.setVisibility(View.GONE);
                            scannerLayout.setVisibility(View.GONE);
                            layoutHead.setVisibility(View.VISIBLE);
                            closeBtnScanMe.setVisibility(View.VISIBLE);
                            closeBtnScanCode.setVisibility(View.GONE);
                        }
                        scanCode.setTextColor(getResources().getColor(R.color.white));
                        scanCode.setBackgroundResource(R.drawable.bg_core_colorfill);
                        scanMe.setBackgroundColor(getResources().getColor(R.color.white));
                        scanMe.setTextColor(getResources().getColor(R.color.primary_black));
                        scanMeSV.setVisibility(View.GONE);
//                        layoutHead.setVisibility(View.GONE);
//                        closeBtnScanMe.setVisibility(View.GONE);
                        //ScanCode Visible
//                        mycodeScannerView.setVisibility(View.VISIBLE);
//                        scannerLayout.setVisibility(View.VISIBLE);
//                        flashLL.setVisibility(View.VISIBLE);
//                        closeBtnScanCode.setVisibility(View.VISIBLE);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });

            copyRecipientAddress.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        ClipboardManager myClipboard;
                        myClipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);

                        ClipData myClip;
                        String text = objMyApplication.getCurrentUserData().getTokenWalletResponse().getWalletId();
                        myClip = ClipData.newPlainText("text", text);
                        myClipboard.setPrimaryClip(myClip);

                        Utils.showCustomToast(ScanActivity.this, "Your address has successfully copied to clipboard.", R.drawable.ic_custom_tick, "");

                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });

            imageShare.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        if (SystemClock.elapsedRealtime() - mLastClickTimeQa < 2000) {
                            return;
                        }
                        mLastClickTimeQa = SystemClock.elapsedRealtime();

                        Intent sendIntent = new Intent();
                        sendIntent.setAction(Intent.ACTION_SEND);
                        sendIntent.putExtra(Intent.EXTRA_TEXT, strWallet);
                        sendIntent.setType("text/plain");

                        Intent shareIntent = Intent.createChooser(sendIntent, null);
                        startActivity(shareIntent);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });

            scanmeSetAmountTV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        if (!scanmeSetAmountTV.getText().equals("Clear Amount")) {
                            if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                                return;
                            }
                            mLastClickTime = SystemClock.elapsedRealtime();
                            setAmountDialog = new Dialog(ScanActivity.this);
                            setAmountDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
                            setAmountDialog.setContentView(R.layout.fragment_set_limit);
                            setAmountDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                            ctKey = (CustomKeyboard) setAmountDialog.findViewById(R.id.customKeyBoard);
                            setAmount = setAmountDialog.findViewById(R.id.setAmountET);

                            setAmount.setAccessibilityDelegate(new View.AccessibilityDelegate() {
                                @Override
                                public void sendAccessibilityEvent(View host, int eventType) {
                                    super.sendAccessibilityEvent(host, eventType);
                                    if (eventType == AccessibilityEvent.TYPE_VIEW_TEXT_SELECTION_CHANGED) {
                                        setAmount.setSelection(setAmount.getText().toString().length());
                                    }
                                }
                            });

                            InputConnection ic = setAmount.onCreateInputConnection(new EditorInfo());
                            ctKey.setInputConnection(ic);
                            ctKey.setKeyAction("OK", ScanActivity.this);
                            ctKey.setScreenName("setAmount");
                            fontSize = setAmount.getTextSize();
                            setAmount.requestFocus();
                            setAmount.setShowSoftInputOnFocus(false);
                            setAmount.addTextChangedListener(ScanActivity.this);
                            setAmount.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Utils.hideSoftKeypad(ScanActivity.this, v);
                                }
                            });
                            Window window = setAmountDialog.getWindow();
                            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

                            WindowManager.LayoutParams wlp = window.getAttributes();

                            wlp.gravity = Gravity.BOTTOM;
                            wlp.flags &= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
                            window.setAttributes(wlp);
                            setAmountDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
                            setAmountDialog.setCanceledOnTouchOutside(true);
                            setAmountDialog.show();
                        } else {
                            scanAmountLL.setVisibility(View.GONE);
                            scanmeSetAmountTV.setText("Set Amount");
                            generateQRCode(strWallet);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });

            closeBtnScanCode.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        if (mcodeScanner != null) {
                            mcodeScanner.setFlashEnabled(false);
                        }
                        finish();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });

            closeBtnScanMe.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        if (mcodeScanner != null) {
                            mcodeScanner.setFlashEnabled(false);
                        }
                        finish();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });

            savetoAlbum.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    try {
                        if (ContextCompat.checkSelfPermission(ScanActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                            ActivityCompat.requestPermissions(ScanActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 321);
                        } else {
                            saveToGallery();
                            Utils.showCustomToast(ScanActivity.this, "Saved to gallery successfully", R.drawable.ic_custom_tick, "");
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }


//                    try {
//                        saveToGallery();
//                        Utils.showCustomToast(ScanActivity.this, "Saved to gallery successfully", R.drawable.ic_custom_tick, "");
//                    } catch (Exception ex) {
//                        ex.printStackTrace();
//                    }
                }
            });

            toglebtn1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        if (isTorchOn) {
                            mcodeScanner.setFlashEnabled(true);
                            torchTogle(isTorchOn);
                        } else {
                            mcodeScanner.setFlashEnabled(false);
                            torchTogle(isTorchOn);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });

            albumIV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                            return;
                        }
                        mLastClickTime = SystemClock.elapsedRealtime();

                        scannerLayout.setVisibility(View.GONE);
                        isAlbumClicked = true;
                        if (checkAndRequestPermissions(ScanActivity.this)) {
                            Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                            photoPickerIntent.setType("image/*");
                            startActivityForResult(photoPickerIntent, 101);

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            goToSettings.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
//                        startActivity(new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
//                                Uri.fromParts("package", getPackageName(), null)));
                        Utils.showDialogPermission(ScanActivity.this, "Allow Access to your Camera", "You are not allowing to access the Camera. If you want to Scan, please go to Settings and enable the Camera permission.");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initObserveres() {
        dashboardViewModel.getUserDetailsMutableLiveData().observe(this, new Observer<UserDetails>() {
            @Override
            public void onChanged(UserDetails userDetails) {
                if (dialog != null) {
                    dialog.dismiss();
                }
                try {
                    if (userDetails.getStatus().equalsIgnoreCase("SUCCESS")) {
                        if (objMyApplication.getAccountType() == Utils.PERSONAL_ACCOUNT && userDetails.getData().getAccountType() == Utils.PERSONAL_ACCOUNT) {
                            if (strQRAmount.equals("")) {
                                try {
                                    Intent i = new Intent(ScanActivity.this, PayRequestActivity.class);
                                    i.putExtra("walletId", strScanWallet);
                                    i.putExtra("amount", strQRAmount);
                                    i.putExtra("screen", "scan");
                                    startActivity(i);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else {
                                try {
                                    if (objMyApplication.getFeatureControlGlobal().getPay() != null && objMyApplication.getFeatureControlGlobal().getPay()
                                            && objMyApplication.getFeatureControlByUser().getPay() != null && objMyApplication.getFeatureControlByUser().getPay()) {
                                        Intent i = new Intent(ScanActivity.this, PayToPersonalActivity.class);
                                        i.putExtra("walletId", strScanWallet);
                                        i.putExtra("amount", strQRAmount);
                                        i.putExtra("screen", "scan");
                                        startActivity(i);
                                    } else {
                                        if (mcodeScanner != null) {
                                            mcodeScanner.startPreview();
                                        }
                                        Utils.displayAlert(getString(R.string.errormsg), ScanActivity.this, "", "");
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
//                        else if (objMyApplication.getAccountType() == Utils.PERSONAL_ACCOUNT &&
//                                (userDetails.getData().getAccountType() == Utils.BUSINESS_ACCOUNT || userDetails.getData().getAccountType() == Utils.SHARED_ACCOUNT)) {
                        else if ((userDetails.getData().getAccountType() == Utils.BUSINESS_ACCOUNT || userDetails.getData().getAccountType() == Utils.SHARED_ACCOUNT)) {
                            if (strQRAmount.equals("")) {
                                try {
                                    Intent i = new Intent(ScanActivity.this, PayToMerchantActivity.class);
                                    i.putExtra("walletId", strScanWallet);
                                    i.putExtra("amount", strQRAmount);
                                    i.putExtra("screen", "scan");
                                    startActivity(i);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else {
                                details = userDetails;
                                String amount = strQRAmount;
                                dialog = Utils.showProgressDialog(ScanActivity.this);
                                cynValue = Utils.doubleParsing(strQRAmount.toString().trim().replace(",", ""));
                                calculateFee(Utils.USNumberFormat(cynValue));
                                businessIdentityVerificationViewModel.getBusinessType();
                                showPayToMerchantWithAmountDialog(amount, userDetails, avaBal, businessTypeValue);
                                dashboardViewModel.getFeatureControlByUser(userDetails.getData().getUserId());
                            }
                        } else if ((objMyApplication.getAccountType() == Utils.BUSINESS_ACCOUNT || objMyApplication.getAccountType() == Utils.SHARED_ACCOUNT) && userDetails.getData().getAccountType() == Utils.PERSONAL_ACCOUNT) {
                            //ERROR MESSAGE DIsPLAY
                            try {
                                displayAlert("Sorry, we detected this is a personal account address, please scan a business QR code or switch to your personal account to complete the transaction. ", "Invalid QR code");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
//                        else if ((objMyApplication.getAccountType() == Utils.BUSINESS_ACCOUNT && (userDetails.getData().getAccountType() == Utils.BUSINESS_ACCOUNT || userDetails.getData().getAccountType() == Utils.SHARED_ACCOUNT)) ||
//                                (objMyApplication.getAccountType() == Utils.SHARED_ACCOUNT && (userDetails.getData().getAccountType() == Utils.BUSINESS_ACCOUNT || userDetails.getData().getAccountType() == Utils.SHARED_ACCOUNT))) {
//                            //ERROR MESSAGE DIsPLAY
//                            try {
//                                displayAlert("Sorry, we detected this is a Merchant account address, switch to your personal account to complete the transaction. ", "Invalid QR code");
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }
//                        }
                    } else {
                        displayAlert("Try scanning a coyni QR code.", "Invalid QR code");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        dashboardViewModel.getApiErrorMutableLiveData().observe(this, new Observer<APIError>() {
            @Override
            public void onChanged(APIError apiError) {
                dialog.dismiss();
                if (apiError != null) {
                    String strMsg = "";
                    if (!apiError.getError().getErrorDescription().equals("")) {
                        strMsg = apiError.getError().getErrorDescription();
                    } else {
                        strMsg = apiError.getError().getFieldErrors().get(0);
                    }
                    if (strMsg.toLowerCase().contains("token expired") || apiError.getError().getErrorDescription().toLowerCase().contains("invalid token")) {

                    } else {
                        if (mycodeScannerView.getVisibility() == View.VISIBLE) {
//                            Utils.displayAlert("Try scanning a coyni QR code.", ScanActivity.this, "Invalid QR code", apiError.getError().getErrorDescription());
                            if (errorDialog == null && scanMeSV.getVisibility() == View.GONE) {

                                displayAlert("Try scanning a coyni QR code.", "Invalid QR code");
                            }
                        }
                    }
                }
            }
        });

        dashboardViewModel.getErrorMutableLiveData().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                dialog.dismiss();
                if (s != null && !s.equals("")) {
                    if (mycodeScannerView.getVisibility() == View.VISIBLE) {
//                        Utils.displayAlert("Try scanning a coyni QR code.", ScanActivity.this, "Invalid QR code", "");
                        if (errorDialog == null && scanMeSV.getVisibility() == View.GONE) {
                            displayAlert("Try scanning a coyni QR code.", "Invalid QR code");
                        } else {
                            displayAlert(s, "Invalid QR code");
                        }
                    }
                }
            }
        });

        buyTokenViewModel.getTransferFeeResponseMutableLiveData().observe(this, transferFeeResponse -> {
            if (dialog != null) {
                dialog.dismiss();
            }
            if (transferFeeResponse != null) {
                objMyApplication.setTransferFeeResponse(transferFeeResponse);
                feeInAmount = transferFeeResponse.getData().getFeeInAmount();
                feeInPercentage = transferFeeResponse.getData().getFeeInPercentage();
                pfee = transferFeeResponse.getData().getFee();

            }
        });

        payViewModel.getPaidOrderRespMutableLiveData().observe(this, paidOrderResp -> {
            try {
                if (paidOrderResp != null) {
                    if (dialog != null) {
                        dialog.dismiss();
                    }
//                    Utils.setStrToken("");
                    objMyApplication.clearStrToken();
                    objMyApplication.setPaidOrderResp(paidOrderResp);
                    if (paidOrderResp.getStatus().equalsIgnoreCase("success")) {
                        startActivity(new Intent(ScanActivity.this, GiftCardBindingLayoutActivity.class)
                                .putExtra("status", "Success")
                                .putExtra("subtype", "paid"));

                    } else {
                        startActivity(new Intent(ScanActivity.this, GiftCardBindingLayoutActivity.class)
                                .putExtra("status", "Failed")
                                .putExtra("subtype", "paid"));
                    }
                } else {
                    Utils.displayAlert("something went wrong", ScanActivity.this, "", "");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        coyniViewModel.getBiometricTokenResponseMutableLiveData().observe(this, biometricTokenResponse -> {
            if (biometricTokenResponse != null) {
                if (biometricTokenResponse.getStatus().equalsIgnoreCase("success")) {
                    if (biometricTokenResponse.getData().getRequestToken() != null && !biometricTokenResponse.getData().getRequestToken().equals("")) {
                        objMyApplication.setStrToken(biometricTokenResponse.getData().getRequestToken());
                    }
                    payTransaction();
                }
            }
        });

        buyTokenViewModel.getTransactionLimitResponseMutableLiveData().observe(this, transactionLimitResponse -> {
            if (dialog != null) {
                dialog.dismiss();
            }
            if (transactionLimitResponse != null) {
                objResponse = transactionLimitResponse;
                if (transactionLimitResponse.getData() != null) {
                    setDailyWeekLimit(objResponse.getData());
                }
            }
        });

        businessIdentityVerificationViewModel.getBusinessTypesResponse().observe(this, new Observer<BusinessTypeResp>() {
            @Override
            public void onChanged(BusinessTypeResp businessTypeResp) {
                if (businessTypeResp != null && businessTypeResp.getStatus().equalsIgnoreCase("SUCCESS")) {
                    objMyApplication.setBusinessTypeResp(businessTypeResp);
                }
            }
        });

        dashboardViewModel.getFeatureControlRespByUserMutableLiveData().observe(this, new Observer<FeatureControlRespByUser>() {
            @Override
            public void onChanged(FeatureControlRespByUser featureControlRespByUser) {
                try {
                    FeatureData obj = new FeatureData();
                    if (featureControlRespByUser != null && featureControlRespByUser.getData() != null) {
                        obj = featureControlRespByUser.getData().getData();
                        if (obj != null && obj.getPermissionResponseList() != null && obj.getPermissionResponseList().size() > 0) {
                            for (int i = 0; i < obj.getPermissionResponseList().size(); i++) {
                                if (obj.getPermissionResponseList().get(i).getFeatureName().toLowerCase().equals(Utils.saleOrderEnable)) {
                                    isSaleOrder = obj.getPermissionResponseList().get(i).getPermission();
                                }
                            }
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
    }


    private void saveToGallery() {
        try {
            if (scanAmountLL.getVisibility() == View.VISIBLE) {
                setAmountLL.setVisibility(View.VISIBLE);
                //divider.setVisibility(View.GONE);
            } else {
                setAmountLL.setVisibility(View.GONE);
                //divider.setVisibility(View.VISIBLE);
            }
            imageSaveAlbumLL.setDrawingCacheEnabled(true);
            imageSaveAlbumLL.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
            imageSaveAlbumLL.layout(0, 0, imageSaveAlbumLL.getMeasuredWidth(), imageSaveAlbumLL.getMeasuredHeight());

            imageSaveAlbumLL.buildDrawingCache(true);
            Bitmap b = Bitmap.createBitmap(imageSaveAlbumLL.getDrawingCache());
            imageSaveAlbumLL.setDrawingCacheEnabled(false);
            MediaStore.Images.Media.insertImage(getContentResolver(), b, "Coyni-PayQr", "this is QR");// clear drawing cache
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void torchTogle(boolean command) {
        try {
            if (command) {
                mcodeScanner.setFlashEnabled(true);
                isTorchOn = false;
            } else {
                mcodeScanner.setFlashEnabled(false);
                isTorchOn = true;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void StartScanner() {
        try {
            mcodeScanner = new CodeScanner(this, mycodeScannerView);
            mcodeScanner.startPreview();
            BarcodeFormat barcodeFormat = BarcodeFormat.QR_CODE;
            List<BarcodeFormat> barcodeFormatList = Collections.singletonList(barcodeFormat);
            mcodeScanner.setFormats(barcodeFormatList);
            mcodeScanner.setDecodeCallback(new DecodeCallback() {
                @Override
                public void onDecoded(@NonNull final Result result) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                if (result != null && !result.toString().trim().equals("")) {
                                    strScanWallet = "";
                                    strQRAmount = "";
                                    if (isJSONValid(result.toString())) {
                                        JSONObject jsonObject = new JSONObject(result.toString());
                                        strScanWallet = jsonObject.get("referenceID").toString();
                                        strQRAmount = jsonObject.get("cynAmount").toString();
                                    } else {
                                        strScanWallet = result.toString();
                                        LogUtils.v(TAG, result.toString());
                                    }
                                    String requestToken = "";
                                    Uri uri = null;
                                    if (strScanWallet != null) {
                                        uri = Uri.parse(strScanWallet);
                                    }
                                    if (uri != null && uri.isAbsolute()) {
                                        try {
                                            Set<String> queryParams = uri.getQueryParameterNames();
                                            CheckOutModel checkOutModel = new CheckOutModel();
                                            for (String s : queryParams) {
                                                if (s.equalsIgnoreCase(CheckOutConstants.REQUEST_TOKEN)) {
                                                    requestToken = uri.getQueryParameter(s);
                                                }
                                                checkOutModel.setCheckOutFlag(true);
                                                checkOutModel.setEncryptedToken(requestToken);
                                                objMyApplication.setCheckOutModel(checkOutModel);
                                            }
                                            if (requestToken != null && requestToken.length() > 0) {
                                                if (objMyApplication.getMyProfile() != null && objMyApplication.getMyProfile().getData() != null
                                                        && objMyApplication.getMyProfile().getData().getAccountStatus() != null) {
                                                    if (objMyApplication.getMyProfile().getData().getAccountStatus().equalsIgnoreCase(Utils.BUSINESS_ACCOUNT_STATUS.ACTIVE.getStatus())) {
                                                        launchCheckoutFlow(checkOutModel);
                                                    } else {
                                                        objMyApplication.setCheckOutModel(new CheckOutModel());
                                                        Utils.displayAlertNew(getString(R.string.please_use_active_account), ScanActivity.this, "coyni");
                                                    }
                                                } else if (objMyApplication.getLoginResponse().getData().getAccountStatus().equalsIgnoreCase(Utils.BUSINESS_ACCOUNT_STATUS.ACTIVE.getStatus())) {
                                                    launchCheckoutFlow(checkOutModel);
                                                } else {
                                                    objMyApplication.setCheckOutModel(new CheckOutModel());
                                                    Utils.displayAlertNew(getString(R.string.please_use_active_account), ScanActivity.this, "coyni");
                                                }
                                            } else {
                                                displayAlert("Try scanning a coyni QR code.", "Invalid QR code");
                                                objMyApplication.setCheckOutModel(null);
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                            displayAlert("Try scanning a coyni QR code.", "Invalid QR code");

                                        }

//
                                    } else if (!strScanWallet.equals(strWallet)) {
                                        if (!android.util.Patterns.WEB_URL.matcher(strScanWallet).matches()) {
                                            if (!isQRScan) {
                                                isQRScan = true;
                                                getUserDetails(strScanWallet);
                                            }
                                        } else {
                                            displayAlert("Try scanning a coyni QR code.", "Invalid QR code");
                                        }
                                    } else {
//                                        Utils.displayAlert("Tokens can not request to your own wallet", ScanActivity.this, "", "");
                                        displayAlert(getString(R.string.tokens_msg), "");
                                    }
                                } else {
                                    Utils.displayAlert("Unable to scan the QR code.", ScanActivity.this, "", "");
                                }
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                            scannerLayout.setVisibility(View.GONE);
                        }
                    });
                }
            });

            mycodeScannerView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        if (ContextCompat.checkSelfPermission(ScanActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                            mcodeScanner.startPreview();
                            scannerLayout.setVisibility(View.VISIBLE);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });

            scannerBar.startAnimation(AnimationUtils.loadAnimation(this, R.anim.animate_scanner_line));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        try {

            if (requestCode == 321) {
                if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    Utils.displayAlert("Requires Access to Your Storage.", ScanActivity.this, "", "");
                } else if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    saveToGallery();
                    Utils.showCustomToast(ScanActivity.this, "Saved to gallery successfully", R.drawable.ic_custom_tick, "");
                }
            } else if (requestCode == 123) {
                try {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        StartScanner();

                        toglebtn1.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (isTorchOn) {
                                    mcodeScanner.setFlashEnabled(true);
                                    torchTogle(isTorchOn);
                                } else {
                                    mcodeScanner.setFlashEnabled(false);
                                    torchTogle(isTorchOn);
                                }
                            }
                        });

                    } else {
                        mycodeScannerView.setVisibility(View.GONE);
                        mDenyAccessScreen.setVisibility(View.VISIBLE);
                        flashLL.setVisibility(View.GONE);
                        scannerLayout.setVisibility(View.GONE);
                        layoutHead.setVisibility(View.VISIBLE);
                        closeBtnScanMe.setVisibility(View.VISIBLE);
                        closeBtnScanCode.setVisibility(View.GONE);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            } else if (requestCode == 101) {
                if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    displayAlert("Requires Access to Your Storage.", "coyni");
                } else {
                    Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                    photoPickerIntent.setType("image/*");
                    startActivityForResult(photoPickerIntent, 101);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        try {
            super.onResume();
            isQRScan = false;

            if (Utils.isSettingsBtnClicked && ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                StartScanner();
                mycodeScannerView.setVisibility(View.VISIBLE);
                mDenyAccessScreen.setVisibility(View.GONE);
                layoutHead.setVisibility(View.GONE);
                closeBtnScanMe.setVisibility(View.GONE);
                closeBtnScanCode.setVisibility(View.VISIBLE);
                flashLL.setVisibility(View.VISIBLE);
                Utils.isSettingsBtnClicked = false;
            }
            if (!isAlbumClicked && !isOnResumeCamera) {
                if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.CAMERA)
                        == PackageManager.PERMISSION_GRANTED) {
                    mcodeScanner.startPreview();
                    scannerLayout.setVisibility(View.VISIBLE);
                    if (errorDialog != null) {
                        errorDialog.dismiss();
                    }
                }
            } else {
                isAlbumClicked = false;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        try {
            super.onPause();
            if (mcodeScanner != null) {
                if (mcodeScanner.isFlashEnabled())
                    mcodeScanner.setFlashEnabled(false);
                mcodeScanner.releaseResources();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isJSONValid(String test) {
        try {
            new JSONObject(test);
        } catch (JSONException ex) {
            return false;
        }
        return true;
    }

    private void generateQRCode(String wallet) {
        try {
            WindowManager manager = (WindowManager) getSystemService(WINDOW_SERVICE);

            // initializing a variable for default display.
            Display display = manager.getDefaultDisplay();

            // creating a variable for point which
            // is to be displayed in QR Code.
            Point point = new Point();
            display.getSize(point);

            // getting width and
            // height of a point
            int width = point.x;
            int height = point.y;

            // generating dimension from width and height.
            int dimen = width < height ? width : height;
            dimen = dimen * 3 / 4;

            // setting this dimensions inside our qr code
            // encoder to generate our qr code.
            qrgEncoder = new QRGEncoder(wallet, null, QRGContents.Type.TEXT, 600);
            bitmap = Bitmap.createBitmap(qrgEncoder.encodeAsBitmap(), 50, 50, 500, 500);
//            bitmap  = Utils.trimLeave5Percent(bitmap, R.color.white);

            // getting our qrcode in the form of bitmap.
//            bitmap = qrgEncoder.encodeAsBitmap();
            // the bitmap is set inside our image
            // view using .setimagebitmap method.
            try {
                idIVQrcode.setImageBitmap(bitmap);
                savedImageView.setImageBitmap(bitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void getUserDetails(String strWalletId) {
        try {
            if (Utils.checkInternet(ScanActivity.this)) {
                dialog = Utils.showProgressDialog(ScanActivity.this);
                dashboardViewModel.getUserDetail(strWalletId);
            } else {
                Utils.displayAlert(getString(R.string.internet), ScanActivity.this, "", "");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void bindImage() {
        try {
            imgProfile.setVisibility(View.GONE);
            userNameTV.setVisibility(View.VISIBLE);
            String imageString = objMyApplication.getMyProfile().getData().getImage();
            String imageTextNew = "";
            imageTextNew = imageTextNew + objMyApplication.getMyProfile().getData().getFirstName().substring(0, 1).toUpperCase() +
                    objMyApplication.getMyProfile().getData().getLastName().substring(0, 1).toUpperCase();
            userNameTV.setText(imageTextNew);

            if (imageString != null && !imageString.trim().equals("")) {
                imgProfile.setVisibility(View.VISIBLE);
                userNameTV.setVisibility(View.GONE);

                DisplayImageUtility utility = DisplayImageUtility.getInstance(getApplicationContext());
                utility.addImage(imageString, imgProfile, R.drawable.ic_profile_male_user);

            } else {
                imgProfile.setVisibility(View.GONE);
                userNameTV.setVisibility(View.VISIBLE);
                String imageText = "";
                imageText = imageText + objMyApplication.getMyProfile().getData().getFirstName().substring(0, 1).toUpperCase() +
                        objMyApplication.getMyProfile().getData().getLastName().substring(0, 1).toUpperCase();
                userNameTV.setText(imageText);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        //Change Not Updated
        //Accept yours
    }

    public void saveToAlbumbindImage() {
        try {
            saveProfileIV.setVisibility(View.GONE);
            saveProfileTitle.setVisibility(View.VISIBLE);
            String imageString = objMyApplication.getMyProfile().getData().getImage();
            String imageTextNew = "";
            imageTextNew = imageTextNew + objMyApplication.getMyProfile().getData().getFirstName().substring(0, 1).toUpperCase() +
                    objMyApplication.getMyProfile().getData().getLastName().substring(0, 1).toUpperCase();
            saveProfileTitle.setText(imageTextNew);

            if (imageString != null && !imageString.trim().equals("")) {
                saveProfileIV.setVisibility(View.VISIBLE);
                saveProfileTitle.setVisibility(View.GONE);

                DisplayImageUtility utility = DisplayImageUtility.getInstance(getApplicationContext());
                utility.addImage(imageString, saveProfileIV, R.drawable.ic_profile_male_user);

            } else {
                saveProfileIV.setVisibility(View.GONE);
                saveProfileTitle.setVisibility(View.VISIBLE);
                String imageText = "";
                imageText = imageText + objMyApplication.getMyProfile().getData().getFirstName().substring(0, 1).toUpperCase() +
                        objMyApplication.getMyProfile().getData().getLastName().substring(0, 1).toUpperCase();
                saveProfileTitle.setText(imageText);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        try {
            if (ContextCompat.checkSelfPermission(ScanActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                if (mcodeScanner != null && mcodeScanner.isPreviewActive()) {
                    mcodeScanner.stopPreview();
                }
                if (scannerLayout.getVisibility() == View.VISIBLE) {
                    scannerLayout.setVisibility(View.GONE);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

//        if (resultCode == RESULT_OK) {
        if (requestCode == 101 && resultCode == RESULT_OK) {
            try {
                final Uri imageUri = data.getData();
                final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                try {
                    Bitmap bMap = selectedImage;
                    int[] intArray = new int[bMap.getWidth() * bMap.getHeight()];
                    bMap.getPixels(intArray, 0, bMap.getWidth(), 0, 0, bMap.getWidth(), bMap.getHeight());
                    LuminanceSource source = new RGBLuminanceSource(bMap.getWidth(), bMap.getHeight(), intArray);
                    BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

                    //                    Reader reader = new QRCodeReader();
                    Reader reader = new MultiFormatReader();

                    Result result = reader.decode(bitmap);

                    //strScanWallet = result.getText();
                    strScanWallet = "";
                    strQRAmount = "";
                    if (isJSONValid(result.toString())) {
                        JSONObject jsonObject = new JSONObject(result.toString());
                        strScanWallet = jsonObject.get("referenceID").toString();
                        strQRAmount = jsonObject.get("cynAmount").toString();
                    } else {
                        strScanWallet = result.toString();
                    }
                    Log.e("Image Text :- ", strScanWallet);
                    //                    Toast.makeText(getApplicationContext(),strScanWallet,Toast.LENGTH_LONG).show();

                    try {
                        if (!strScanWallet.equals(strWallet)) {
                            isQRScan = true;
                            getUserDetails(strScanWallet);
                        } else {
                            //                            Utils.displayAlert("Tokens can not request to your own wallet", ScanActivity.this, "", "");
                            if (errorDialog == null) {
//                                    mcodeScanner.stopPreview();
//                                    scannerLayout.setVisibility(View.GONE);
                                displayAlert(getString(R.string.tokens_msg), "");
                            }
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }


                } catch (Exception e) {
                    if (errorDialog == null && scanMeSV.getVisibility() == View.GONE) {
//                            mcodeScanner.stopPreview();
//                            scannerLayout.setVisibility(View.GONE);
                        displayAlert("Try scanning a coyni QR code.", "Invalid QR code");
                        objMyApplication.setCheckOutModel(null);
                    }
                    e.printStackTrace();
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(ScanActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
            }
        } else if (requestCode == 251 && resultCode == RESULT_OK) {
            try {
                //payTransaction();
                dialog = Utils.showProgressDialog(ScanActivity.this);
                BiometricTokenRequest request = new BiometricTokenRequest();
                request.setDeviceId(Utils.getDeviceID());
//                    request.setMobileToken(strToken);
                request.setMobileToken(objMyApplication.getStrMobileToken());
                request.setActionType(Utils.paidActionType);
                coyniViewModel.biometricToken(request);
                if (mcodeScanner != null && mcodeScanner.isPreviewActive()) {
                    mcodeScanner.stopPreview();
                }
                if (scannerLayout.getVisibility() == View.VISIBLE) {
                    scannerLayout.setVisibility(View.GONE);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else if (requestCode == 251 && resultCode == RESULT_CANCELED) {
            try {
//                    payTransaction();
                startActivity(new Intent(ScanActivity.this, PINActivity.class)
                        .putExtra("TYPE", "ENTER")
                        .putExtra("screen", "Paid")
                        .putExtra(Utils.wallet, strScanWallet)
                        .putExtra(Utils.amount, strQRAmount.replace(",", "").trim()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (requestCode == 101 && resultCode == RESULT_CANCELED) {
            if (mcodeScanner != null) {
                mcodeScanner.startPreview();
            }
            scannerLayout.setVisibility(View.VISIBLE);
        }
//            else if (requestCode == 251) {
//                try {
//                    //payTransaction();
//                    dialog = Utils.showProgressDialog(ScanActivity.this);
//                    BiometricTokenRequest request = new BiometricTokenRequest();
//                    request.setDeviceId(Utils.getDeviceID());
////                    request.setMobileToken(strToken);
//                    request.setMobileToken(objMyApplication.getStrMobileToken());
//                    request.setActionType(Utils.sendActionType);
//                    coyniViewModel.biometricToken(request);
//                } catch (Exception ex) {
//                    ex.printStackTrace();
//                }
//            } else if (requestCode == 0) {
//                try {
//                    payTransaction();
//                    startActivity(new Intent(ScanActivity.this, PINActivity.class)
//                            .putExtra("TYPE", "ENTER")
//                            .putExtra("screen", "Paid")
//                            .putExtra(Utils.wallet,strScanWallet)
//                            .putExtra(Utils.amount,strQRAmount.replace(",","").trim()));
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//        else {
//            //Toast.makeText(ScanActivity.this, "You haven't picked QR ", Toast.LENGTH_LONG).show();
//            if (ContextCompat.checkSelfPermission(ScanActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
//                mcodeScanner.startPreview();
//                scannerLayout.setVisibility(View.VISIBLE);
//            }
//
//            }
    }

    public static boolean checkAndRequestPermissions(final Activity context) {
        try {
            int WExtstorePermission = ContextCompat.checkSelfPermission(context,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE);

            List<String> listPermissionsNeeded = new ArrayList<>();

            if (WExtstorePermission != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded
                        .add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
            if (!listPermissionsNeeded.isEmpty()) {
                ActivityCompat.requestPermissions(context, listPermissionsNeeded
                                .toArray(new String[listPermissionsNeeded.size()]),
                        REQUEST_ID_MULTIPLE_PERMISSIONS);
                return false;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return true;
    }

    public void setAmountClick() {
        try {
            if (SystemClock.elapsedRealtime() - mLastClickTimeQa < 2000) {
                return;
            }
            mLastClickTimeQa = SystemClock.elapsedRealtime();
            if (validation()) {
                if (setAmountDialog != null) {
                    setAmountDialog.dismiss();
                }
                scanmeSetAmountTV.setText("Clear Amount");
                scanMeRequestAmount.setText(USFormat(setAmount));
                saveSetAmount.setText(USFormat(setAmount));
                scanAmountLL.setVisibility(View.VISIBLE);
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("cynAmount", scanMeRequestAmount.getText().toString());
                jsonObject.put("referenceID", strWallet);
                generateQRCode(jsonObject.toString());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private Boolean validation() {
        Boolean value = false;
        try {
            String strPay = setAmount.getText().toString().trim().replace("\"", "");
            if ((Utils.doubleParsing(strPay.replace(",", "")) > Utils.doubleParsing(getString(R.string.payrequestMaxAmt)))) {
                value = false;
                Utils.displayAlert("You can request up to " + Utils.USNumberFormat(Utils.doubleParsing(getString(R.string.payrequestMaxAmt))) + " CYN", ScanActivity.this, "Oops!", "");
            } else if (Utils.doubleParsing(strPay.replace(",", "")) <= 0.00) {
                value = false;
                Utils.displayAlert("Amount should be greater than zero.", ScanActivity.this, "Oops!", "");
            } else {
                value = true;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return value;
    }

    private String USFormat(EditText etAmount) {
        String strAmount = "", strReturn = "";
        try {
            strAmount = Utils.convertBigDecimalUSD(etAmount.getText().toString().trim().replace(",", ""));
            InputFilter[] FilterArray = new InputFilter[1];
            FilterArray[0] = new InputFilter.LengthFilter(Integer.parseInt(getString(R.string.maxlendecimal)));
            setAmount.setFilters(FilterArray);
            setAmount.removeTextChangedListener(ScanActivity.this);
            setAmount.setText(Utils.USNumberFormat(Utils.doubleParsing(strAmount)));
            setAmount.addTextChangedListener(ScanActivity.this);
            setAmount.setSelection(etAmount.getText().toString().length());
            strReturn = Utils.USNumberFormat(Utils.doubleParsing(strAmount));
            changeTextSize(strReturn);
            setDefaultLength();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return strReturn;
    }

    private void changeTextSize(String editable) {
        try {
            InputFilter[] FilterArray = new InputFilter[1];
            if (editable.length() > 8) {
                FilterArray[0] = new InputFilter.LengthFilter(Integer.parseInt(getString(R.string.maxlendecimal)));
                setAmount.setTextSize(TypedValue.COMPLEX_UNIT_SP, 33);
            } else if (editable.length() > 5) {
                FilterArray[0] = new InputFilter.LengthFilter(Integer.parseInt(getString(R.string.maxlendecimal)));
                setAmount.setTextSize(TypedValue.COMPLEX_UNIT_SP, 43);
            } else {
                FilterArray[0] = new InputFilter.LengthFilter(Integer.parseInt(getString(R.string.maxlength)));
                setAmount.setTextSize(Utils.pixelsToSp(ScanActivity.this, fontSize));
            }
            setAmount.setFilters(FilterArray);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void setDefaultLength() {
        try {
            InputFilter[] FilterArray = new InputFilter[1];
            FilterArray[0] = new InputFilter.LengthFilter(Integer.parseInt(getString(R.string.maxlength)));
            setAmount.setFilters(FilterArray);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void displayAlert(String msg, String headerText) {
        // custom dialog
        try {
//            if (errorDialog != null) {
//                errorDialog.dismiss();
//            }
            errorDialog = new Dialog(ScanActivity.this);
            errorDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            errorDialog.setContentView(R.layout.bottom_sheet_alert_dialog);
            errorDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

            DisplayMetrics mertics = getResources().getDisplayMetrics();
            int width = mertics.widthPixels;

            TextView header = errorDialog.findViewById(R.id.tvHead);
            TextView message = errorDialog.findViewById(R.id.tvMessage);
            CardView actionCV = errorDialog.findViewById(R.id.cvAction);
            TextView actionText = errorDialog.findViewById(R.id.tvAction);

            if (!headerText.equals("")) {
                try {
                    header.setVisibility(View.VISIBLE);
                    header.setText(headerText);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

//            if (ContextCompat.checkSelfPermission(ScanActivity.this,
//                    Manifest.permission.CAMERA)
//                    == PackageManager.PERMISSION_GRANTED) {
//                mcodeScanner.stopPreview();
//                scannerLayout.setVisibility(View.GONE);
//            }

            actionCV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        errorDialog.dismiss();
                        errorDialog = null;
                        isQRScan = false;
                        if (ContextCompat.checkSelfPermission(ScanActivity.this,
                                Manifest.permission.CAMERA)
                                == PackageManager.PERMISSION_GRANTED) {
                            mcodeScanner.startPreview();
                            scannerLayout.setVisibility(View.VISIBLE);
                            slideActionEnabled = false;
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });

            message.setText(msg);
            Window window = errorDialog.getWindow();
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

            WindowManager.LayoutParams wlp = window.getAttributes();

            wlp.gravity = Gravity.BOTTOM;
            wlp.flags &= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
            window.setAttributes(wlp);

            errorDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

            errorDialog.setCanceledOnTouchOutside(false);
            errorDialog.show();

            errorDialog.setOnDismissListener(dialogInterface -> {
                isQRScan = false;
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    mcodeScanner.startPreview();
                }
                scannerLayout.setVisibility(View.VISIBLE);
                errorDialog = null;
                slideActionEnabled = false;
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void displayAlertNew(String msg, String headerText) {
        // custom dialog
        final Dialog dialog = new Dialog(ScanActivity.this);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottom_sheet_alert_dialog);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        DisplayMetrics mertics = getResources().getDisplayMetrics();
        int width = mertics.widthPixels;

        TextView header = dialog.findViewById(R.id.tvHead);
        TextView message = dialog.findViewById(R.id.tvMessage);
        CardView actionCV = dialog.findViewById(R.id.cvAction);
        TextView actionText = dialog.findViewById(R.id.tvAction);
        actionText.setText("Buy Token");

        if (!headerText.equals("")) {
            header.setVisibility(View.VISIBLE);
            header.setText(headerText);
        }

        actionCV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                objMyApplication.setStrScreen("payRequest");
                Intent i;
                if (objMyApplication.getAccountType() == Utils.PERSONAL_ACCOUNT) {
                    i = new Intent(ScanActivity.this, BuyTokenPaymentMethodsActivity.class);
                    i.putExtra("screen", "payRequest");
                } else {
                    i = new Intent(ScanActivity.this, SelectPaymentMethodActivity.class);
                    i.putExtra("screen", "payRequest");
                    i.putExtra("menuitem", "buy");
                }
                startActivity(i);
                //finish();
            }
        });

        message.setText(msg);
        Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

        WindowManager.LayoutParams wlp = window.getAttributes();

        wlp.gravity = Gravity.BOTTOM;
        wlp.flags &= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(wlp);

        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

        dialog.setCanceledOnTouchOutside(true);

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                if (mcodeScanner != null) {
                    mcodeScanner.startPreview();
                    scannerLayout.setVisibility(View.VISIBLE);
                }
            }
        });
        dialog.show();
    }

    private void showPayToMerchantWithAmountDialog(String amount, UserDetails userDetails, Double balance, String btypeValue) {
        isQRScan = false;
        isOnResumeCamera = true;
        isSlideActionClicked = false;
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            mcodeScanner.stopPreview();
        }
        PayToMerchantWithAmountDialog payToMerchantWithAmountDialog = new PayToMerchantWithAmountDialog(ScanActivity.this, amount, userDetails, false, balance, objMyApplication);
        payToMerchantWithAmountDialog.setOnDialogClickListener(new OnDialogClickListener() {
            @Override
            public void onDialogClicked(String action, Object value) {
                LogUtils.v("Scan", "onDialog Clicked " + action);
                if (action.equalsIgnoreCase("payTransaction")) {
                    if (!isAuthenticationCalled) {
                        isAuthenticationCalled = true;
                        if (isSaleOrder) {
                            if (payValidation()) {
                                isSlideActionClicked = true;
                                if ((isFaceLock || isTouchId) && Utils.checkAuthentication(ScanActivity.this)) {
                                    if (Utils.getIsBiometric() && ((isTouchId && Utils.isFingerPrint(ScanActivity.this)) || (isFaceLock))) {
                                        Utils.checkAuthentication(ScanActivity.this, CODE_AUTHENTICATION_VERIFICATION);
                                    } else {
                                        startActivity(new Intent(ScanActivity.this, PINActivity.class)
                                                .putExtra("TYPE", "ENTER")
                                                .putExtra("screen", "Paid")
                                                .putExtra(Utils.wallet, strScanWallet)
                                                .putExtra(Utils.amount, strQRAmount.replace(",", "").trim()));

                                    }
                                } else {
                                    startActivity(new Intent(ScanActivity.this, PINActivity.class)
                                            .putExtra("TYPE", "ENTER")
                                            .putExtra("screen", "Paid")
                                            .putExtra(Utils.wallet, strScanWallet)
                                            .putExtra(Utils.amount, strQRAmount.replace(",", "").trim()));
                                }
                                slideActionEnabled = false;

                            }
                        } else {
                            Utils.displayAlert(getString(R.string.errormsg), ScanActivity.this, "", "");
                        }
                    }
                    LogUtils.v("Scan", "onDialog Clicked " + action);
                }

            }
        });

        payToMerchantWithAmountDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                isAuthenticationCalled = false;
                isOnResumeCamera = false;
                if (!isSlideActionClicked && !slideActionEnabled && ContextCompat.checkSelfPermission(ScanActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    mcodeScanner.startPreview();
                    scannerLayout.setVisibility(View.VISIBLE);
                } else {
                    mcodeScanner.stopPreview();
                    scannerLayout.setVisibility(View.GONE);
                }

            }
        });

        payToMerchantWithAmountDialog.show();

    }


//    private void setFaceLock() {
//        try {
//            isFaceLock = false;
//            String value = dbHandler.getFacePinLock();
//            if (value != null && value.equals("true")) {
//                isFaceLock = true;
//                objMyApplication.setLocalBiometric(true);
//            } else {
//                isFaceLock = false;
//                objMyApplication.setLocalBiometric(false);
//            }
//
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//    }
//
//    private void setTouchId() {
//        try {
//            isTouchId = false;
//            String value = dbHandler.getThumbPinLock();
//            if (value != null && value.equals("true")) {
//                isTouchId = true;
//                objMyApplication.setLocalBiometric(true);
//            } else {
//                isTouchId = false;
////                objMyApplication.setLocalBiometric(false);
//                if (!isFaceLock) {
//                    objMyApplication.setLocalBiometric(false);
//                }
//            }
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//    }

    private void calculateFee(String strAmount) {
        try {
            TransferFeeRequest request = new TransferFeeRequest();
            request.setTokens(strAmount.trim().replace(",", ""));
            request.setTxnType(String.valueOf(Utils.saleOrder));
            request.setTxnSubType(String.valueOf(Utils.saleOrderToken));
            if (Utils.PERSONAL_ACCOUNT == objMyApplication.getAccountType()) {
                if (Utils.checkInternet(ScanActivity.this)) {
                    buyTokenViewModel.transferFee(request);
                }
            } else if (Utils.BUSINESS_ACCOUNT == objMyApplication.getAccountType() || Utils.SHARED_ACCOUNT == objMyApplication.getAccountType()) {
                if (Utils.checkInternet(ScanActivity.this)) {
                    buyTokenViewModel.transferFee(request);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void payTransaction() {
        try {
//            TransferPayRequest request = new TransferPayRequest();
//            request.setTokens(strQRAmount.trim().replace(",", ""));
//            request.setRemarks("");
//            request.setRecipientWalletId(strScanWallet);
//            objMyApplication.setTransferPayRequest(request);
//            objMyApplication.setWithdrawAmount(cynValue);
//            if (Utils.checkInternet(ScanActivity.this)) {
//                payViewModel.sendTokens(request);
//            }
            PaidOrderRequest request = new PaidOrderRequest();
            request.setTokensAmount(Utils.doubleParsing(strQRAmount.trim().replace(",", "").trim()));
            request.setRecipientWalletId(strScanWallet);
//            request.setRequestToken(Utils.getStrToken());
            request.setRequestToken(objMyApplication.getStrToken());
            objMyApplication.setPaidOrderRequest(request);
            objMyApplication.setWithdrawAmount(cynValue);
            if (Utils.checkInternet(ScanActivity.this)) {
                payViewModel.paidOrder(request);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private Boolean payValidation() {
        boolean value = false;
        try {
            String strPay = strQRAmount.toString().trim().replace("\"", "");
            if (cynValue > avaBal) {
                slideActionEnabled = true;
                displayAlertNew("Seems like no token available in your account. Please follow one of the prompts below to buy token.", "Oops!");
                value = false;
            } else if (cynValue < Utils.doubleParsing(objResponse.getData().getMinimumLimit())) {
                slideActionEnabled = true;
                displayAlert("Minimum Amount is " + Utils.USNumberFormat(Utils.doubleParsing(objResponse.getData().getMinimumLimit())) + " CYN", "Oops!");
                value = false;

            } else if (cynValue > Utils.doubleParsing(objResponse.getData().getTransactionLimit()) && !objResponse.getData().getLimitType().equalsIgnoreCase("NO LIMIT")) {
                slideActionEnabled = true;
                displayAlert("Amount entered exceeds transaction limit.", "Oops!");
                value = false;
            } else {
                slideActionEnabled = false;
                value = true;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return value;
    }

    private void setDailyWeekLimit(LimitResponseData objLimit) {
        try {
            if (objLimit.getTransactionLimit() != null && !objLimit.getTransactionLimit().equalsIgnoreCase("NA") && !objLimit.getTransactionLimit().equalsIgnoreCase("unlimited")) {
                maxValue = Utils.doubleParsing(objLimit.getTransactionLimit());
            }
//                if (objLimit.getDailyAccountLimit() != null && !objLimit.getDailyAccountLimit().equalsIgnoreCase("NA") && !objLimit.getDailyAccountLimit().equalsIgnoreCase("unlimited")) {
//                    daily = Utils.doubleParsing(objLimit.getDailyAccountLimit());
//                }
            if (maxValue > 0) {
                if (objLimit.getLimitType().equalsIgnoreCase("daily")) {
                    strLimit = "daily";
                } else if (objLimit.getLimitType().equalsIgnoreCase("weekly")) {
                    strLimit = "week";
                } else if (objLimit.getLimitType().equalsIgnoreCase("unlimited")) {
                    strLimit = "unlimited";
                } else {
                    strLimit = "daily";
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
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

    private void launchCheckoutFlow(CheckOutModel checkOutModel) {
        try {
            dismissDialog();
            startActivity(new Intent(ScanActivity.this, CheckOutPaymentActivity.class));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}