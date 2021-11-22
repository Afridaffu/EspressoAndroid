package com.greenbox.coyni.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.budiyev.android.codescanner.ScanMode;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import android.Manifest;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.google.zxing.qrcode.QRCodeReader;
import com.google.zxing.qrcode.encoder.QRCode;
import com.greenbox.coyni.R;

import java.util.Collections;
import java.util.List;

public class PayRequestScanActivity extends AppCompatActivity {
        TextView scanMe,scanCode,scanmeSetAmountTV;
        LinearLayout flashLL,layoutHead;
        ScrollView scanMeSV;
        ImageView closeBtnScanCode,closeBtnScanMe;
    private CodeScanner mcodeScanner;
    private CodeScannerView mycodeScannerView;
    TextView scancode;
    boolean isTorchOn=true;
    private ImageView toglebtn1;

    ObjectAnimator animator;
    View scannerLayout;
    View scannerBar;
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
            mycodeScannerView=findViewById(R.id.scanner_view);
            scannerLayout = findViewById(R.id.scannerLayout);
            scannerBar = findViewById(R.id.lineView);
            flashLL=findViewById(R.id.flashBtnLL);
            layoutHead=findViewById(R.id.layoutHead);
            scanMeSV=findViewById(R.id.scanmeScrlView);
            scanmeSetAmountTV=findViewById(R.id.scanMesetAmountTV);
            listeners();


        }
        catch (Exception ex){
            ex.printStackTrace();
        }
    }

    private void listeners() {
        scanmeSetAmountTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(PayRequestScanActivity.this,SetAmountActivity.class));
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
                scanCode.setBackgroundResource(R.drawable.bg_white);
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
                Toast.makeText(this, "Permistion Granted!", Toast.LENGTH_SHORT).show();
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
}