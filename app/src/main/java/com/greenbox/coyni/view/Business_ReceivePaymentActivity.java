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

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
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
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import com.greenbox.coyni.R;
import com.greenbox.coyni.model.APIError;
import com.greenbox.coyni.model.businesswallet.BusinessWalletResponse;
import com.greenbox.coyni.model.businesswallet.WalletResponseData;
import com.greenbox.coyni.model.wallet.UserDetails;
import com.greenbox.coyni.model.wallet.WalletResponse;
import com.greenbox.coyni.utils.MyApplication;
import com.greenbox.coyni.utils.Utils;
import com.greenbox.coyni.utils.keyboards.CustomKeyboard;
import com.greenbox.coyni.viewmodel.BusinessDashboardViewModel;
import com.greenbox.coyni.viewmodel.DashboardViewModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;
import de.hdodenhof.circleimageview.CircleImageView;

public class Business_ReceivePaymentActivity extends AppCompatActivity implements TextWatcher {

    TextView scanmeSetAmountTV, savetoAlbum, userNameTV, scanMeRequestAmount;
    LinearLayout layoutHead, imageSaveAlbumLL, scanAmountLL, setAmountLL,closeBtn;
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
    public static Business_ReceivePaymentActivity business_receivePaymentActivity;

    MyApplication objMyApplication;
    BusinessDashboardViewModel dashboardViewModel;
    TextView tvWalletAddress, tvName;
    boolean isTorchOn = true, isQRScan = false;
    ImageView toglebtn1;
    String strWallet = "", strScanWallet = "", strQRAmount = "";
    ProgressDialog dialog;
    Dialog errorDialog;
    ConstraintLayout scannerLayout;
    View scannerBar;
    float fontSize;
    CustomKeyboard ctKey;
    public static Business_ReceivePaymentActivity Business_ReceivePaymentActivity;
    EditText setAmount;

    //Saved To Album Layout Comp..
    TextView tvSaveUserName, saveProfileTitle, saveSetAmount;
    ImageView savedImageView;
    CircleImageView saveProfileIV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        setContentView(R.layout.activity_business_receive_payment);

        Business_ReceivePaymentActivity = this;
        initialization();
        listeners();
        initObservers();


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
//                if (editable.length() > 0 && !editable.toString().equals(".") && !editable.toString().equals(".00") && Double.parseDouble(editable.toString()) > 0) {
                if (editable.length() > 0 && !editable.toString().equals(".") && !editable.toString().equals(".00")) {
                    setAmount.setHint("");
                    if (editable.length() > 8) {
                        setAmount.setTextSize(TypedValue.COMPLEX_UNIT_SP, 33);
                    } else if (editable.length() > 5) {
                        setAmount.setTextSize(TypedValue.COMPLEX_UNIT_SP, 43);
                    } else {
                        setAmount.setTextSize(Utils.pixelsToSp(Business_ReceivePaymentActivity.this, fontSize));
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
            dashboardViewModel = new ViewModelProvider(this).get(BusinessDashboardViewModel.class);
            objMyApplication = (MyApplication) getApplicationContext();
            closeBtnScanCode = findViewById(R.id.closeBtnSC);
            closeBtnScanMe = findViewById(R.id.imgCloseSM);

            toglebtn1 = findViewById(R.id.toglebtn);
            tvWalletAddress = findViewById(R.id.b_tvWalletAddress);
//            scannerLayout = findViewById(R.id.scannerLayout);
//            scannerBar = findViewById(R.id.lineView);
//            flashLL = findViewById(R.id.flashBtnRL);
            closeBtn=findViewById(R.id.receivePaymentLL);
            idIVQrcode = (ImageView) findViewById(R.id.b_idIVQrcode);
            savedImageView = findViewById(R.id.savedImageIV);
            tvName = findViewById(R.id.tvName);
            business_receivePaymentActivity=this;
            scanMeRequestAmount = findViewById(R.id.scanMeRequestAmount);
            scanAmountLL = findViewById(R.id.scanAmountLL);
//            layoutHead = findViewById(R.id.layoutHead);
//            scanMeSV = findViewById(R.id.scanmeScrlView);
            savetoAlbum = findViewById(R.id.saveToAlbumTV);
            scanmeSetAmountTV = findViewById(R.id.scanMesetAmountTV);
            imageShare = findViewById(R.id.imgShare);
            userNameTV = findViewById(R.id.tvUserInfo);
            copyRecipientAddress = findViewById(R.id.imgCopy);
            imgProfile = findViewById(R.id.imgProfile);
            albumIV = findViewById(R.id.albumIV);
            imageSaveAlbumLL = findViewById(R.id.b_saveToAlbumLL);
            setAmountLL = findViewById(R.id.setAmountLL);
            divider = findViewById(R.id.divider1);

            //init Saved to Album Layout
            savedImageView = findViewById(R.id.qrImageIV);
            tvSaveUserName = findViewById(R.id.tvNameSave);
            saveProfileIV = findViewById(R.id.saveprofileIV);
            saveProfileTitle = findViewById(R.id.saveprofileTitle);
            saveSetAmount = findViewById(R.id.tvsaveSetAmount);


            findViewById(R.id.receivePaymentLL).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        finish();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

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
            WalletResponseData walletResponse = objMyApplication.getWalletResponseData();
            if (walletResponse != null) {
                strWallet = walletResponse.getWalletNames().get(0).getWalletId();
                generateQRCode(strWallet);
            }
            tvWalletAddress.setText(walletResponse.getWalletNames().get(0).getWalletId().substring(0, 16) + "...");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void listeners() {
        try {

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

                        Utils.showCustomToast(Business_ReceivePaymentActivity.this, "Your address has successfully copied to clipboard.", R.drawable.ic_custom_tick, "");

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
                        if (!scanmeSetAmountTV.getText().equals("Clear Amount")) {
                            if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                                return;
                            }
                            mLastClickTime = SystemClock.elapsedRealtime();
                            setAmountDialog = new Dialog(Business_ReceivePaymentActivity.this);
                            setAmountDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
                            setAmountDialog.setContentView(R.layout.fragment_set_limit);
                            setAmountDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                            ctKey = (CustomKeyboard) setAmountDialog.findViewById(R.id.customKeyBoard);
                            setAmount = setAmountDialog.findViewById(R.id.setAmountET);
                            InputConnection ic = setAmount.onCreateInputConnection(new EditorInfo());
                            ctKey.setInputConnection(ic);
                            ctKey.setKeyAction("OK");
                            ctKey.setScreenName("receivepayments");
                            fontSize = setAmount.getTextSize();
                            setAmount.requestFocus();
                            setAmount.setShowSoftInputOnFocus(false);
                            setAmount.addTextChangedListener(Business_ReceivePaymentActivity.this);
                            setAmount.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Utils.hideSoftKeypad(Business_ReceivePaymentActivity.this, v);
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




            savetoAlbum.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        if (ContextCompat.checkSelfPermission(Business_ReceivePaymentActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                            ActivityCompat.requestPermissions(Business_ReceivePaymentActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 123);
                        }
                        else {
                            saveToGallery();
                            Utils.showCustomToast(Business_ReceivePaymentActivity.this, "Saved to gallery successfully", R.drawable.ic_custom_tick, "");
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
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



    @Override
    protected void onResume() {
        try {
            super.onResume();
            dashboardViewModel.meMerchantWallet();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        try {
            super.onPause();
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

//    private void getUserDetails(String strWalletId) {
//        try {
//            if (Utils.checkInternet(Business_ReceivePaymentActivity.this)) {
//                dialog = Utils.showProgressDialog(Business_ReceivePaymentActivity.this);
//                dashboardViewModel.getUserDetail(strWalletId);
//            } else {
//                Utils.displayAlert(getString(R.string.internet), Business_ReceivePaymentActivity.this, "", "");
//            }
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//    }

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
            if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                return;
            }
            mLastClickTime = SystemClock.elapsedRealtime();
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
        Boolean value = true;
        try {
            String strPay = setAmount.getText().toString().trim().replace("\"", "");
            if ((Double.parseDouble(strPay.replace(",", "")) > Double.parseDouble(getString(R.string.payrequestMaxAmt)))) {
                value = false;
                Utils.displayAlert("You can request up to " + Utils.USNumberFormat(Double.parseDouble(getString(R.string.payrequestMaxAmt))) + " CYN", Business_ReceivePaymentActivity.this, "Oops!", "");
            } else if (Double.parseDouble(strPay.replace(",", "")) <= 0) {
                value = false;
                Utils.displayAlert("Amount should be grater than zero.", Business_ReceivePaymentActivity.this, "Oops!", "");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return value;
    }

    private String USFormat(EditText etAmount) {
        String strAmount = "", strReturn = "";
        try {
            strAmount = Utils.convertBigDecimalUSDC(etAmount.getText().toString().trim().replace(",", ""));
            etAmount.removeTextChangedListener(Business_ReceivePaymentActivity.this);
            etAmount.setText(Utils.USNumberFormat(Double.parseDouble(strAmount)));
            etAmount.addTextChangedListener(Business_ReceivePaymentActivity.this);
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
                setAmount.setTextSize(Utils.pixelsToSp(Business_ReceivePaymentActivity.this, fontSize));
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

    private void initObservers() {
        dashboardViewModel.getBusinessWalletResponseMutableLiveData().observe(this, new Observer<BusinessWalletResponse>() {
            @Override
            public void onChanged(BusinessWalletResponse businessWalletResponse) {
                if (businessWalletResponse !=null){
                    objMyApplication.setWalletResponseData(businessWalletResponse.getData());
                    strWallet=businessWalletResponse.getData().getWalletNames().get(0).getWalletId();
                    generateQRCode(strWallet);
                }
            }
        });

    }

//    private void displayAlert(String msg, String headerText) {
//        // custom dialog
//        errorDialog = new Dialog(Business_ReceivePaymentActivity.this);
//        errorDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
//        errorDialog.setContentView(R.layout.bottom_sheet_alert_dialog);
//        errorDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
//
//        DisplayMetrics mertics = getResources().getDisplayMetrics();
//        int width = mertics.widthPixels;
//
//        TextView header = errorDialog.findViewById(R.id.tvHead);
//        TextView message = errorDialog.findViewById(R.id.tvMessage);
//        CardView actionCV = errorDialog.findViewById(R.id.cvAction);
//        TextView actionText = errorDialog.findViewById(R.id.tvAction);
//
//        if (!headerText.equals("")) {
//            header.setVisibility(View.VISIBLE);
//            header.setText(headerText);
//        }
//
//        actionCV.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                try {
//                    errorDialog.dismiss();
//                    errorDialog = null;
//                } catch (Exception ex) {
//                    ex.printStackTrace();
//                }
//            }
//        });
//
//        message.setText(msg);
//        Window window = errorDialog.getWindow();
//        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
//
//        WindowManager.LayoutParams wlp = window.getAttributes();
//
//        wlp.gravity = Gravity.BOTTOM;
//        wlp.flags &= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
//        window.setAttributes(wlp);
//
//        errorDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
//
//        errorDialog.setCanceledOnTouchOutside(false);
//        errorDialog.show();
//    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode==123){
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                Utils.displayAlert("Requires Access to Your Storage.", Business_ReceivePaymentActivity.this, "", "");
            }
            else if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                saveToGallery();
                Utils.showCustomToast(Business_ReceivePaymentActivity.this, "Saved to gallery successfully", R.drawable.ic_custom_tick, "");
            }
        }
    }
}



