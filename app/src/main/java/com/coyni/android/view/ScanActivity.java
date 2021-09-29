package com.coyni.android.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Display;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.coyni.android.model.wallet.WalletInfo;
import com.coyni.android.utils.MyApplication;
import com.coyni.android.utils.Utils;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.tabs.TabLayout;
import com.google.zxing.Result;
import com.coyni.android.R;

import org.json.JSONException;
import org.json.JSONObject;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

public class ScanActivity extends AppCompatActivity {
    RelativeLayout layoutBack, layoutScan, layoutQRDetails;
    private CodeScanner mCodeScanner;
    TabLayout tabLayout;
    AppBarLayout appbar;
    Toolbar toolbar;
    MyApplication objMyApplication;
    Bitmap bitmap;
    QRGEncoder qrgEncoder;
    String strWallet = "";
    ImageView idIVQrcode;
    TextView tvWalletAddress, tvName, tvNameHead;
    LinearLayout layoutPermission, layoutEnable;
    CodeScannerView scannerView;
    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 555;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_scan);
            initialization();
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
            if (ContextCompat.checkSelfPermission(ScanActivity.this, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                mCodeScanner.startPreview();
            }
            objMyApplication.getHandler().removeCallbacks(objMyApplication.getMyRunnable());
            objMyApplication.userInactive(ScanActivity.this, this, false);
            objMyApplication.getAppHandler().removeCallbacks(objMyApplication.getAppRunnable());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        objMyApplication.getHandler().removeCallbacks(objMyApplication.getMyRunnable());
        objMyApplication.userInactive(ScanActivity.this, this, true);
    }

    @Override
    public void onUserInteraction() {
        // TODO Auto-generated method stub
        super.onUserInteraction();
        objMyApplication.getHandler().removeCallbacks(objMyApplication.getMyRunnable());
        objMyApplication.userInactive(ScanActivity.this, this, false);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_PERMISSIONS_REQUEST_CAMERA) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                scannerView.setVisibility(View.VISIBLE);
                layoutPermission.setVisibility(View.GONE);
                layoutBack.setBackgroundColor(Color.parseColor("#181818"));
                statusBar("#181818");
                appbar.setBackgroundColor(Color.parseColor("#181818"));
                toolbar.setNavigationIcon(R.drawable.ic_back_arrow_white);
                mCodeScanner.startPreview();
            } else {
                Toast.makeText(this, "Camera permission denied", Toast.LENGTH_LONG).show();
                layoutQRDetails.setVisibility(View.GONE);
                layoutScan.setVisibility(View.VISIBLE);
                scannerView.setVisibility(View.GONE);
                layoutPermission.setVisibility(View.VISIBLE);
                layoutBack.setBackgroundColor(Color.parseColor("#FFFFFF"));
                statusBar("#FFFFFF");
                appbar.setBackgroundColor(Color.parseColor("#FFFFFF"));
                toolbar.setNavigationIcon(R.drawable.ic_back_arrow_black);
            }
        }
    }

    private void initialization() {
        try {
            toolbar = (Toolbar) findViewById(R.id.toolbar_sent);
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            statusBar("#181818");
            layoutBack = (RelativeLayout) findViewById(R.id.layoutBack);
            layoutScan = (RelativeLayout) findViewById(R.id.layoutScan);
            layoutQRDetails = (RelativeLayout) findViewById(R.id.layoutQRDetails);
            appbar = (AppBarLayout) findViewById(R.id.appbar);
            objMyApplication = (MyApplication) getApplicationContext();
            idIVQrcode = (ImageView) findViewById(R.id.idIVQrcode);
            scannerView = findViewById(R.id.scanner_view);
            mCodeScanner = new CodeScanner(this, scannerView);
            tabLayout = findViewById(R.id.tabLayout);
            layoutPermission = findViewById(R.id.layoutPermission);
            layoutEnable = findViewById(R.id.layoutEnable);
            tvWalletAddress = findViewById(R.id.tvWalletAddress);
            tvName = findViewById(R.id.tvName);
            tvNameHead = findViewById(R.id.tvNameHead);
            LinearLayout layoutShare, layoutCopy;
            layoutShare = findViewById(R.id.layoutShare);
            layoutCopy = findViewById(R.id.layoutCopy);
            mCodeScanner.setDecodeCallback(new DecodeCallback() {
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
                                    Intent i = new Intent(ScanActivity.this, PayRequestActivity.class);
                                    i.putExtra("walletId", strWallet);
                                    i.putExtra("screen", "scan");
                                    startActivity(i);
                                } else {
                                    Utils.displayAlert("Unable to scan the QR code.", ScanActivity.this);
                                }
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }
                    });
                }
            });

            scannerView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mCodeScanner.startPreview();
                }
            });

            tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    try {
                        if (tab.getPosition() == 0) {
                            layoutQRDetails.setVisibility(View.GONE);
                            layoutScan.setVisibility(View.VISIBLE);
                            if (ContextCompat.checkSelfPermission(ScanActivity.this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                                //ask for authorisation
                                ActivityCompat.requestPermissions(ScanActivity.this, new String[]{android.Manifest.permission.CAMERA}, MY_PERMISSIONS_REQUEST_CAMERA);
                            } else {
                                scannerView.setVisibility(View.VISIBLE);
                                layoutPermission.setVisibility(View.GONE);
                                layoutBack.setBackgroundColor(Color.parseColor("#181818"));
                                statusBar("#181818");
                                appbar.setBackgroundColor(Color.parseColor("#181818"));
                                toolbar.setNavigationIcon(R.drawable.ic_back_arrow_white);
                                mCodeScanner.startPreview();
                            }
                        } else {
                            layoutQRDetails.setVisibility(View.VISIBLE);
                            layoutScan.setVisibility(View.GONE);
                            layoutBack.setBackgroundColor(Color.parseColor("#FFFFFF"));
                            statusBar("#FFFFFF");
                            appbar.setBackgroundColor(Color.parseColor("#FFFFFF"));
                            toolbar.setNavigationIcon(R.drawable.ic_back_arrow_black);
                            mCodeScanner.stopPreview();
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {

                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {

                }
            });
            WalletInfo gbtWallet = objMyApplication.getGbtWallet();
            if (gbtWallet != null) {
                strWallet = gbtWallet.getWalletId();
                tvWalletAddress.setText(gbtWallet.getWalletId().substring(0, 16) + "...");
                //generateCode();
                generateQRCode(strWallet);
            }
            tvName.setText(Utils.capitalize(objMyApplication.getStrUser()));
            tvNameHead.setText(objMyApplication.getStrUserCode());
            layoutShare.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Intent myIntent = new Intent(Intent.ACTION_SEND);
                        myIntent.setType("text/plain");
                        String body = strWallet;
                        String sub = "Wallet Address";
                        myIntent.putExtra(Intent.EXTRA_SUBJECT, sub);
                        myIntent.putExtra(Intent.EXTRA_TEXT, body);
                        startActivity(Intent.createChooser(myIntent, "Share Using"));
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });
            layoutCopy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Utils.copyText(strWallet, ScanActivity.this);
                }
            });

            layoutEnable.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getPackageName(), null);
                        intent.setData(uri);
                        startActivity(intent);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });

            if (ContextCompat.checkSelfPermission(ScanActivity.this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                //ask for authorisation
                ActivityCompat.requestPermissions(ScanActivity.this, new String[]{android.Manifest.permission.CAMERA}, MY_PERMISSIONS_REQUEST_CAMERA);
            } else {
                mCodeScanner.startPreview();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void statusBar(String color) {
        try {
            if (Build.VERSION.SDK_INT >= 21) {
                Window window = getWindow();
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(Color.parseColor(color));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
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

    private boolean isJSONValid(String test) {
        try {
            new JSONObject(test);
        } catch (JSONException ex) {
            return false;
        }
        return true;
    }

}