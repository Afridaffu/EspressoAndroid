package com.greenbox.coyni.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.greenbox.coyni.R;
import com.greenbox.coyni.fragments.IdVeBottomSheetFragment;
import com.greenbox.coyni.fragments.SetLimitFragment;
import com.greenbox.coyni.model.APIError;
import com.greenbox.coyni.model.wallet.UserDetails;
import com.greenbox.coyni.model.wallet.WalletResponse;
import com.greenbox.coyni.utils.MyApplication;
import com.greenbox.coyni.utils.Utils;
import com.greenbox.coyni.viewmodel.DashboardViewModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Time;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

public class PayRequestScanActivity extends AppCompatActivity {
    TextView scanMe, scanCode, scanmeSetAmountTV, savetoAlbum, userNameTV;
    LinearLayout layoutHead;
    RelativeLayout flashLL;
    ScrollView scanMeSV;
    QRGEncoder qrgEncoder;
    Bitmap bitmap;
    Long mLastClickTime = 0L;
    ImageView idIVQrcode, imageShare, copyRecipientAddress;
    ImageView closeBtnScanCode, closeBtnScanMe;
    private CodeScanner mcodeScanner;
    private CodeScannerView mycodeScannerView;
    MyApplication objMyApplication;
    DashboardViewModel dashboardViewModel;
    TextView scancode, tvWalletAddress, tvName, tvNameHead;
    boolean isTorchOn = true;
    private ImageView toglebtn1;
    String strWallet = "", strScanWallet = "";
    ProgressDialog dialog;
    ObjectAnimator animator;
    View scannerLayout;
    View scannerBar;
    boolean isPermissionEnable = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_request_scan);
        try {

            closeBtnScanCode = findViewById(R.id.closeBtnSC);
            closeBtnScanMe = findViewById(R.id.imgCloseSM);
            scanCode = findViewById(R.id.scanCodeTV);
            scanMe = findViewById(R.id.scanMeTV);
            toglebtn1 = findViewById(R.id.toglebtn);
            tvWalletAddress = findViewById(R.id.tvWalletAddress);
            objMyApplication = (MyApplication) getApplicationContext();
            mycodeScannerView = findViewById(R.id.scanner_view);
            scannerLayout = findViewById(R.id.scannerLayout);
            scannerBar = findViewById(R.id.lineView);
            flashLL = findViewById(R.id.flashBtnLL);
            idIVQrcode = (ImageView) findViewById(R.id.idIVQrcode);
            tvName = findViewById(R.id.tvName);
            tvNameHead = findViewById(R.id.tvUserInfo);
            layoutHead = findViewById(R.id.layoutHead);
            scanMeSV = findViewById(R.id.scanmeScrlView);
            savetoAlbum = findViewById(R.id.saveToAlbumTV);
            scanmeSetAmountTV = findViewById(R.id.scanMesetAmountTV);
            imageShare = findViewById(R.id.imgShare);
            userNameTV = findViewById(R.id.tvUserInfo);
            copyRecipientAddress = findViewById(R.id.imgCopy);
            dashboardViewModel = new ViewModelProvider(this).get(DashboardViewModel.class);
            String strUserName = Utils.capitalize(objMyApplication.getMyProfile().getData().getFirstName().substring(0, 1).toUpperCase() + "" + objMyApplication.getMyProfile().getData().getLastName().substring(0, 1).toUpperCase());
            String strName = Utils.capitalize(objMyApplication.getMyProfile().getData().getFirstName() + " " + objMyApplication.getMyProfile().getData().getLastName());
            userNameTV.setText(strUserName.toUpperCase(Locale.US));
            if (strName != null && strName.length() > 21) {
                tvName.setText(strName.substring(0, 21) + "...");
            } else {
                tvName.setText(strName);
            }

            WalletResponse walletResponse = objMyApplication.getWalletResponse();
            if (walletResponse != null) {
                strWallet = walletResponse.getData().getWalletInfo().get(0).getWalletId();
                //   generateCode();
                generateQRCode(strWallet);
//                Log.e("responce",""+strWallet);
//               tvWalletAddress.setText(walletResponse.getData().getWalletInfo().get(0).getWalletId().substring(0, 16) + "...");

            }
            //           tvName.setText(Utils.capitalize(objMyApplication.getStrUser()));
            //           tvNameHead.setText(objMyApplication.getStrUserCode());
            tvWalletAddress.setText(walletResponse.getData().getWalletInfo().get(0).getWalletId().substring(0, 16) + "...");
            listeners();
            initObserveres();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    private void listeners() {
        try {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 123);
            } else {
                StartScaaner();
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
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });

            scanCode.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        mcodeScanner.startPreview();
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
//                        showToast();

                        Utils.showCustomToast(PayRequestScanActivity.this, "Your address has successfully copied to clipboard.", R.drawable.ic_custom_tick, "");
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
                    SetLimitFragment setLimitFragment = new SetLimitFragment();
                    setLimitFragment.show(getSupportFragmentManager(), setLimitFragment.getTag());
                }
            });

            closeBtnScanCode.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mcodeScanner.setFlashEnabled(false);
                    finish();
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
                    saveToGallery();
//                    Toast.makeText(PayRequestScanActivity.this, "saved to Gallery successfully", Toast.LENGTH_SHORT).show();
                    Utils.showCustomToast(PayRequestScanActivity.this, "Saved to gallery successfully", R.drawable.ic_custom_tick, "");

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

//            imageShare.setOnClickListener(new View.OnClickListener() {
//
//                @Override
//                public void onClick(View b) {
//                    // TODO Auto-generated method stub
//                    //attempt to save the image
//
//                    b = findViewById(R.id.idIVQrcode);
//                    b.setDrawingCacheEnabled(true);
//                    Bitmap bitmap = b.getDrawingCache();
//                    //File file = new File("/DCIM/Camera/image.jpg");
//                    File root = Environment.getExternalStorageDirectory();
//                    File cachePath = new File(root.getAbsolutePath() + "/DCIM/Camera/image.jpg");
//                    try
//                    {
//                        cachePath.createNewFile();
//                        FileOutputStream ostream = new FileOutputStream(cachePath);
//                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, ostream);
//                        ostream.close();
//                    }
//                    catch (Exception e)
//                    {
//                        e.printStackTrace();
//                    }
//
//                }
//
//        });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initObserveres() {
        dashboardViewModel.getUserDetailsMutableLiveData().observe(this, new Observer<UserDetails>() {
            @Override
            public void onChanged(UserDetails userDetails) {
                dialog.dismiss();
                if (userDetails != null) {
                    Intent i = new Intent(PayRequestScanActivity.this, PayRequestTransactionActivity.class);
                    i.putExtra("walletId", strScanWallet);
                    i.putExtra("screen", "scan");
                    startActivity(i);
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
//                            Utils.displayAlert("Try scanning a coyni QR code.", PayRequestScanActivity.this, "Invalid QR code");
                            invalidQRCode("Try scanning a coyni QR code.", PayRequestScanActivity.this, "Invalid QR code");
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
//                        Utils.displayAlert("Try scanning a coyni QR code.", PayRequestScanActivity.this, "Invalid QR code");
                        invalidQRCode("Try scanning a coyni QR code.", PayRequestScanActivity.this, "Invalid QR code");
                    }
                }
            }
        });
    }

    private void saveToGallery() {
        try {
            idIVQrcode.setDrawingCacheEnabled(true);
            Bitmap b = idIVQrcode.getDrawingCache();

            MediaStore.Images.Media.insertImage(getContentResolver(), b, "Coyni-PayQr", "this is QR");
//        BitmapDrawable bitmapDrawable=(BitmapDrawable) idIVQrcode.getDrawable();
//        Bitmap bitmap=bitmapDrawable.getBitmap();
//        FileOutputStream outputStream=null;
//        File file= Environment.getExternalStorageDirectory();
//        File dir=new File(file.getAbsolutePath()+"/mypics");
//        dir.mkdirs();
//        String filename=String.format("%d.png",System.currentTimeMillis());
//        File outfile=new File(dir,filename);
//        try {
//            outputStream=new FileOutputStream(outfile);
//
//        }
//        catch (Exception fileNotFoundException){
//            fileNotFoundException.printStackTrace();
//        }
//        bitmap.compress(Bitmap.CompressFormat.PNG,100,outputStream);
//        try {
//            outputStream.flush();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        try {
//            outputStream.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

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

    private void StartScaaner() {
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
                                    if (isJSONValid(result.toString())) {
                                        JSONObject jsonObject = new JSONObject(result.toString());
                                        strScanWallet = jsonObject.get("address").toString();
                                    } else {
                                        strScanWallet = result.toString();
                                    }
                                    getUserDetails(strScanWallet);
                                } else {
                                    Utils.displayAlert("Unable to scan the QR code.", PayRequestScanActivity.this, "");
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
                    mcodeScanner.startPreview();
                    scannerLayout.setVisibility(View.VISIBLE);

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
        if (requestCode == 123) {
            try {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    StartScaaner();

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
        }
    }

    @Override
    protected void onResume() {
        try {
            super.onResume();
            mcodeScanner.startPreview();
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
            qrgEncoder = new QRGEncoder(wallet, null, QRGContents.Type.TEXT, dimen);
            // getting our qrcode in the form of bitmap.
            bitmap = qrgEncoder.encodeAsBitmap();
            // the bitmap is set inside our image
            // view using .setimagebitmap method.
            idIVQrcode.setImageBitmap(bitmap);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void showToast() {
        try {
            LayoutInflater inflater = getLayoutInflater();
            View layout = inflater.inflate(R.layout.custom_toast_recipientaddress, (ViewGroup) findViewById(R.id.toastRootLL));

            Toast toast = new Toast(getApplicationContext());
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.setDuration(Toast.LENGTH_SHORT);
            toast.setView(layout);
            toast.show();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void getUserDetails(String strWalletId) {
        try {
            if (Utils.checkInternet(PayRequestScanActivity.this)) {
                dialog = new ProgressDialog(PayRequestScanActivity.this, R.style.MyAlertDialogStyle);
                dialog.setIndeterminate(false);
                dialog.setMessage("Please wait...");
                dialog.getWindow().setGravity(Gravity.CENTER);
                dialog.show();
                dashboardViewModel.getUserDetail(strWalletId);
            } else {
                Utils.displayAlert(getString(R.string.internet), PayRequestScanActivity.this, "");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

   private void invalidQRCode(String msg, final Context context, String headerText) {
              // custom dialog
                        final Dialog dialog = new Dialog(context);
               dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
               dialog.setContentView(R.layout.bottom_sheet_alert_dialog);
               dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

                       DisplayMetrics mertics = context.getResources().getDisplayMetrics();
                int width = mertics.widthPixels;

                        TextView header = dialog.findViewById(R.id.tvHead);
                TextView message = dialog.findViewById(R.id.tvMessage);
                CardView actionCV = dialog.findViewById(R.id.cvAction);
                TextView actionText = dialog.findViewById(R.id.tvAction);

                       if (!headerText.equals("")) {
                       header.setVisibility(View.VISIBLE);
                        header.setText(headerText);
                    }

                        actionCV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                                        dialog.dismiss();
                                        mcodeScanner.startPreview();
                                        scannerLayout.setVisibility(View.VISIBLE);
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
                dialog.show();
            }

}