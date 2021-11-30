package com.greenbox.coyni.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import android.Manifest;
import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.greenbox.coyni.model.wallet.WalletResponse;
import com.greenbox.coyni.utils.MyApplication;
import com.greenbox.coyni.utils.Utils;

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
        TextView scanMe,scanCode,scanmeSetAmountTV,savetoAlbum,userNameTV;
        LinearLayout layoutHead;
        RelativeLayout flashLL;
        ScrollView scanMeSV;
        QRGEncoder qrgEncoder;
        Bitmap bitmap;
        ImageView idIVQrcode,imageShare,copyRecipientAddress;
        ImageView closeBtnScanCode,closeBtnScanMe;
    private CodeScanner mcodeScanner;
    private CodeScannerView mycodeScannerView;
    MyApplication objMyApplication;
    TextView scancode,tvWalletAddress,tvName,tvNameHead;
    boolean isTorchOn=true;
    private ImageView toglebtn1;
    String strWallet = "";
    ProgressDialog dialog;
    ObjectAnimator animator;
    View scannerLayout;
    View scannerBar;
    boolean isPermissionEnable=true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_request_scan);
        try {
            closeBtnScanCode=findViewById(R.id.closeBtnSC);
            closeBtnScanMe=findViewById(R.id.imgCloseSM);
            scanCode=findViewById(R.id.scanCodeTV);
            scanMe=findViewById(R.id.scanMeTV);
            toglebtn1=findViewById(R.id.toglebtn);
            tvWalletAddress=findViewById(R.id.tvWalletAddress);
            objMyApplication = (MyApplication) getApplicationContext();
            mycodeScannerView=findViewById(R.id.scanner_view);
            scannerLayout = findViewById(R.id.scannerLayout);
            scannerBar = findViewById(R.id.lineView);
            flashLL=findViewById(R.id.flashBtnLL);
            idIVQrcode = (ImageView) findViewById(R.id.idIVQrcode);
            tvName = findViewById(R.id.tvName);
            tvNameHead = findViewById(R.id.tvUserInfo);
            layoutHead=findViewById(R.id.layoutHead);
            scanMeSV=findViewById(R.id.scanmeScrlView);
            savetoAlbum=findViewById(R.id.saveToAlbumTV);
            scanmeSetAmountTV=findViewById(R.id.scanMesetAmountTV);
            imageShare=findViewById(R.id.imgShare);
            userNameTV=findViewById(R.id.tvUserInfo);
              copyRecipientAddress=findViewById(R.id.imgCopy);
              copyRecipientAddress.setOnClickListener(new View.OnClickListener() {
                  @Override
                  public void onClick(View view) {
                      ClipboardManager myClipboard;
                      myClipboard = (ClipboardManager)getSystemService(CLIPBOARD_SERVICE);

                      ClipData myClip;
                      String text = objMyApplication.getWalletResponse().getData().getWalletInfo().get(0).getWalletId();
                      myClip = ClipData.newPlainText("text", text);
                      myClipboard.setPrimaryClip(myClip);
                      showToast();
                  }
              });
           listeners();
            String strUserName=Utils.capitalize(objMyApplication.getMyProfile().getData().getFirstName().substring(0,1).toUpperCase()+""+objMyApplication.getMyProfile().getData().getLastName().substring(0,1).toUpperCase());
            String strName = Utils.capitalize(objMyApplication.getMyProfile().getData().getFirstName() + " " + objMyApplication.getMyProfile().getData().getLastName());
           userNameTV.setText(strUserName.toUpperCase(Locale.US));
            if (strName != null && strName.length() > 21) {
                tvName.setText(strName.substring(0, 21) + "...");
            }
            else {
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

        }
        catch (Exception ex){
            ex.printStackTrace();
        }
    }

    private void listeners() {
        try {
            imageShare.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Drawable mDrawable = idIVQrcode.getDrawable();
                    Bitmap mBitmap = ((BitmapDrawable) mDrawable).getBitmap();

                    String path = MediaStore.Images.Media.insertImage(getContentResolver(), mBitmap, "Image Description", null);
                    Uri uri = Uri.parse(path);

                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("image/jpeg");
                    intent.putExtra(Intent.EXTRA_STREAM, uri);
                    intent.putExtra(Intent.EXTRA_TEXT,strWallet);
                    startActivity(Intent.createChooser(intent, "Share QrCode Image"));
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


               }
           });
            scanMe.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
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
                }
            });
            scanCode.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
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


                }
            });

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_DENIED){
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 123);
            }
            else if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_DENIED){
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 123);

            }
            else{

                  savetoAlbum.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            saveToGallery();
                            Toast.makeText(PayRequestScanActivity.this,"saved to Gallery successfully",Toast.LENGTH_SHORT).show();
                        }

                    });
            }

            if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)== PackageManager.PERMISSION_DENIED){
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CAMERA},123);

            } else {
                StartScaaner();
            }
            toglebtn1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(isTorchOn){
                        mcodeScanner.setFlashEnabled(true);
                        torchTogle(isTorchOn);
                    }else {
                        mcodeScanner.setFlashEnabled(false);
                        torchTogle(isTorchOn);
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
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveToGallery() {
        idIVQrcode.setDrawingCacheEnabled(true);
        Bitmap b = idIVQrcode.getDrawingCache();
        
        MediaStore.Images.Media.insertImage(getContentResolver(), b,"Coyni-PayQr", "this is QR");
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


    }


    private void torchTogle(boolean command) {

        if(command){
            mcodeScanner.setFlashEnabled(true);
            isTorchOn=false;
        }else {
            mcodeScanner.setFlashEnabled(false);
            isTorchOn=true;
        }

    }


    private void StartScaaner() {
        mcodeScanner=new CodeScanner(this,mycodeScannerView);
        mcodeScanner.startPreview();
        BarcodeFormat barcodeFormat=BarcodeFormat.QR_CODE;
        List<BarcodeFormat> barcodeFormatList= Collections.singletonList(barcodeFormat);
        mcodeScanner.setFormats(barcodeFormatList);
        mcodeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull final Result result) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (result != null && !result.toString().trim().equals("")) {
                                String strWallet = "";
                                if (isJSONValid(result.toString())) {
                                    JSONObject jsonObject = new JSONObject(result.toString());
                                    strWallet = jsonObject.get("address").toString();
                                } else {
                                    strWallet = result.toString();
                                }
                                Intent i = new Intent(PayRequestScanActivity.this, PayRequestTransactionActivity.class);
                                i.putExtra("walletId", strWallet);
                                i.putExtra("screen", "scan");
                                startActivity(i);
                            } else {
                                Utils.displayAlert("Unable to scan the QR code.", PayRequestScanActivity.this,"");
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                        Toast.makeText(PayRequestScanActivity.this, result.getText(), Toast.LENGTH_SHORT).show();
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

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==123){
            if(grantResults[0]==PackageManager.PERMISSION_GRANTED){
               StartScaaner();

                toglebtn1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(isTorchOn){
                            mcodeScanner.setFlashEnabled(true);
                            torchTogle(isTorchOn);
                        }else {
                            mcodeScanner.setFlashEnabled(false);
                            torchTogle(isTorchOn);
                        }

                    }
                });
            }
            else {
                Toast.makeText(this, "Permistion Denied", Toast.LENGTH_SHORT).show();
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
    private void showToast(){
        LayoutInflater inflater=getLayoutInflater();
        View layout=inflater.inflate(R.layout.custom_toast_recipientaddress,(ViewGroup) findViewById(R.id.toastRootLL));

        Toast toast=new Toast(getApplicationContext());
        toast.setGravity(Gravity.CENTER,0,0);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.show();


    }

}