package com.greenbox.coyni.view;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.zxing.Reader;
import com.greenbox.coyni.utils.keyboards.CustomKeyboard;
import com.bumptech.glide.Glide;
import com.google.android.material.textfield.TextInputLayout;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Result;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.net.Uri;
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
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;
import com.greenbox.coyni.R;
import com.greenbox.coyni.fragments.SetLimitFragment;
import com.greenbox.coyni.model.APIError;
import com.greenbox.coyni.model.wallet.UserDetails;
import com.greenbox.coyni.model.wallet.WalletResponse;
import com.greenbox.coyni.utils.MyApplication;
import com.greenbox.coyni.utils.Utils;
import com.greenbox.coyni.viewmodel.DashboardViewModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

public class ScanActivity extends AppCompatActivity implements TextWatcher {
    TextView scanMe, scanCode, scanmeSetAmountTV, savetoAlbum, userNameTV, scanMeRequestAmount;
    LinearLayout layoutHead;
    LinearLayout imageSaveAlbumLL, scanAmountLL, setAmountLL;
    ConstraintLayout flashLL;
    ScrollView scanMeSV;
    QRGEncoder qrgEncoder;
    Bitmap bitmap;
    View divider;
    Dialog setAmountDialog;
    Long mLastClickTime = 0L;
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 101;
    ImageView idIVQrcode, imageShare, copyRecipientAddress, albumIV;
    ImageView closeBtnScanCode, closeBtnScanMe, imgProfile;
    private CodeScanner mcodeScanner;
    private CodeScannerView mycodeScannerView;
    MyApplication objMyApplication;
    DashboardViewModel dashboardViewModel;
    TextView tvWalletAddress, tvName;
    boolean isTorchOn = true;
    private ImageView toglebtn1;
    String strWallet = "", strScanWallet = "", strQRAmount = "";
    ProgressDialog dialog;
    ConstraintLayout scannerLayout;
    View scannerBar;
    float fontSize;
    CustomKeyboard ctKey;
    public static ScanActivity scanActivity;
    EditText setAmount;

    //Saved To Album Layout Comp..
    TextView tvSaveUserName, saveProfileTitle, saveSetAmount;
    ImageView savedImageView, saveProfileIV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

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
                if (editable.length() > 0 && !editable.toString().equals(".") && !editable.toString().equals(".00") && Double.parseDouble(editable.toString()) > 0) {
                    setAmount.setHint("");
                    if (editable.length() > 8) {
                        setAmount.setTextSize(TypedValue.COMPLEX_UNIT_SP, 33);
                    } else if (editable.length() > 5) {
                        setAmount.setTextSize(TypedValue.COMPLEX_UNIT_SP, 43);
                    } else {
                        setAmount.setTextSize(Utils.pixelsToSp(ScanActivity.this, fontSize));
                    }
                    ctKey.enableButton();
                } else if (editable.toString().equals(".")) {
                    setAmount.setText("");
                    ctKey.disableButton();
                } else if (editable.length() == 0) {
                    setAmount.setHint("0.00");
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
            objMyApplication = (MyApplication) getApplicationContext();
            closeBtnScanCode = findViewById(R.id.closeBtnSC);
            closeBtnScanMe = findViewById(R.id.imgCloseSM);
            scanCode = findViewById(R.id.scanCodeTV);
            scanMe = findViewById(R.id.scanMeTV);
            toglebtn1 = findViewById(R.id.toglebtn);
            tvWalletAddress = findViewById(R.id.tvWalletAddress);
            mycodeScannerView = findViewById(R.id.scanner_view);
            scannerLayout = findViewById(R.id.scannerLayout);
            scannerBar = findViewById(R.id.lineView);
            flashLL = findViewById(R.id.flashBtnRL);
            idIVQrcode = (ImageView) findViewById(R.id.idIVQrcode);
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

            String strName = Utils.capitalize(objMyApplication.getMyProfile().getData().getFirstName() + " " + objMyApplication.getMyProfile().getData().getLastName());
            if (strName != null && strName.length() > 22) {
                tvName.setText(strName.substring(0, 22) + "...");
            } else {
                tvName.setText(strName);
            }
            bindImage();
            String savedStrName = Utils.capitalize(objMyApplication.getMyProfile().getData().getFirstName() + " " + objMyApplication.getMyProfile().getData().getLastName());

            if (savedStrName != null && savedStrName.length() > 22) {
                tvSaveUserName.setText(savedStrName.substring(0, 22) + "...");
            } else {
                tvSaveUserName.setText(savedStrName);
            }
            saveToAlbumbindImage();
            WalletResponse walletResponse = objMyApplication.getWalletResponse();
            if (walletResponse != null) {
                strWallet = walletResponse.getData().getWalletInfo().get(0).getWalletId();
                generateQRCode(strWallet);
            }
            tvWalletAddress.setText(walletResponse.getData().getWalletInfo().get(0).getWalletId().substring(0, 16) + "...");
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
                        mcodeScanner.setFlashEnabled(false);
                        mcodeScanner.stopPreview();
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
                            mcodeScanner.startPreview();
                        }
                        scanCode.setTextColor(getResources().getColor(R.color.white));
                        scanCode.setBackgroundResource(R.drawable.bg_core_colorfill);
                        scanMe.setBackgroundColor(getResources().getColor(R.color.white));
                        scanMe.setTextColor(getResources().getColor(R.color.primary_black));
                        scanMeSV.setVisibility(View.GONE);
                        layoutHead.setVisibility(View.GONE);
                        closeBtnScanMe.setVisibility(View.GONE);
                        //ScanCode Visible
                        mycodeScannerView.setVisibility(View.VISIBLE);
                        scannerLayout.setVisibility(View.VISIBLE);
                        flashLL.setVisibility(View.VISIBLE);
                        closeBtnScanCode.setVisibility(View.VISIBLE);
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
                        String text = objMyApplication.getWalletResponse().getData().getWalletInfo().get(0).getWalletId();
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
                        if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                            return;
                        }
                        mLastClickTime = SystemClock.elapsedRealtime();

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
                        setAmountDialog = new Dialog(ScanActivity.this);
                        setAmountDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
                        setAmountDialog.setContentView(R.layout.fragment_set_limit);
                        setAmountDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                        ctKey = (CustomKeyboard) setAmountDialog.findViewById(R.id.customKeyBoard);
                        setAmount = setAmountDialog.findViewById(R.id.setAmountET);
                        InputConnection ic = setAmount.onCreateInputConnection(new EditorInfo());
                        ctKey.setInputConnection(ic);
                        ctKey.setKeyAction("OK");
                        ctKey.setScreenName("setAmount");
                        fontSize = setAmount.getTextSize();
                        setAmount.addTextChangedListener(ScanActivity.this);

                        Window window = setAmountDialog.getWindow();
                        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

                        WindowManager.LayoutParams wlp = window.getAttributes();

                        wlp.gravity = Gravity.BOTTOM;
                        wlp.flags &= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
                        window.setAttributes(wlp);
                        setAmountDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
                        setAmountDialog.setCanceledOnTouchOutside(true);
                        setAmountDialog.show();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });

            closeBtnScanCode.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        mcodeScanner.setFlashEnabled(false);
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
                        mcodeScanner.startPreview();
                        scanCode.setTextColor(getResources().getColor(R.color.white));
                        scanCode.setBackgroundResource(R.drawable.bg_core_colorfill);
                        scanMe.setBackgroundResource(R.drawable.bg_white);
                        scanMe.setTextColor(getResources().getColor(R.color.primary_black));
                        scanMeSV.setVisibility(View.GONE);
                        layoutHead.setVisibility(View.GONE);
                        closeBtnScanMe.setVisibility(View.GONE);
                        //ScanCode Visible
                        mycodeScannerView.setVisibility(View.VISIBLE);
                        scannerLayout.setVisibility(View.VISIBLE);
                        flashLL.setVisibility(View.VISIBLE);
                        closeBtnScanCode.setVisibility(View.VISIBLE);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });

            savetoAlbum.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        saveToGallery();
                        Utils.showCustomToast(ScanActivity.this, "Saved to gallery successfully", R.drawable.ic_custom_tick, "");
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
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
                        if (checkAndRequestPermissions(ScanActivity.this)) {
                            if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                                return;
                            }
                            mLastClickTime = SystemClock.elapsedRealtime();

                            Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                            photoPickerIntent.setType("image/*");
                            startActivityForResult(photoPickerIntent, 101);

                        }
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
                        Intent i = new Intent(ScanActivity.this, PayRequestActivity.class);
                        i.putExtra("walletId", strScanWallet);
                        i.putExtra("amount", strQRAmount);
                        i.putExtra("screen", "scan");
                        startActivity(i);
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
                            Utils.displayAlert("Try scanning a coyni QR code.", ScanActivity.this, "Invalid QR code", apiError.getError().getErrorDescription());
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
                        Utils.displayAlert("Try scanning a coyni QR code.", ScanActivity.this, "Invalid QR code", "");
                    }
                }
            }
        });
    }

    private void saveToGallery() {
        try {
            if (scanAmountLL.getVisibility() == View.VISIBLE) {
                setAmountLL.setVisibility(View.VISIBLE);
                divider.setVisibility(View.GONE);
            } else {
                setAmountLL.setVisibility(View.GONE);
                divider.setVisibility(View.VISIBLE);
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
                                    }
//                                    getUserDetails(strScanWallet);
                                    if (!strScanWallet.equals(strWallet)) {
                                        getUserDetails(strScanWallet);
                                    } else {
                                        Utils.displayAlert("Tokens can not request to your own wallet", ScanActivity.this, "", "");
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
                        mcodeScanner.startPreview();
                        scannerLayout.setVisibility(View.VISIBLE);
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
            if (requestCode == 123) {
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
                        Toast.makeText(this, "Permistion Denied", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            } else if (requestCode == 101) {
                if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    Utils.displayAlert("Requires Access to Your Storage.", ScanActivity.this, "", "");
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
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_GRANTED) {
                mcodeScanner.startPreview();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        try {
            super.onPause();
            mcodeScanner.releaseResources();
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
                Glide.with(this)
                        .load(imageString)
                        .placeholder(R.drawable.ic_profile_male_user)
                        .into(imgProfile);
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
                Glide.with(this)
                        .load(imageString)
                        .placeholder(R.drawable.ic_profile_male_user)
                        .into(saveProfileIV);
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
        //Change Not Updated
        //Accept yours
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

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
                            getUserDetails(strScanWallet);
                        } else {
                            Utils.displayAlert("Tokens can not request to your own wallet", ScanActivity.this, "", "");
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }


                } catch (Exception e) {
                    Utils.displayAlert("Try scanning a coyni QR code.", ScanActivity.this, "Invalid QR code", "");
                    e.printStackTrace();
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(ScanActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
            }
        } else {
            //Toast.makeText(ScanActivity.this, "You haven't picked QR ", Toast.LENGTH_LONG).show();
        }
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
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private String USFormat(EditText etAmount) {
        String strAmount = "", strReturn = "";
        try {
            strAmount = Utils.convertBigDecimalUSDC(etAmount.getText().toString().trim().replace(",", ""));
            etAmount.removeTextChangedListener(ScanActivity.this);
            etAmount.setText(Utils.USNumberFormat(Double.parseDouble(strAmount)));
            etAmount.addTextChangedListener(ScanActivity.this);
            etAmount.setSelection(etAmount.getText().toString().length());
            strReturn = Utils.USNumberFormat(Double.parseDouble(strAmount));
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

}