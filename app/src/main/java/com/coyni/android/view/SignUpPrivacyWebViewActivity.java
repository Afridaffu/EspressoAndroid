package com.coyni.android.view;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.Gravity;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.coyni.android.R;
import com.coyni.android.utils.MyApplication;
import com.coyni.android.utils.Utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SignUpPrivacyWebViewActivity extends AppCompatActivity {
    WebView webView;
    ImageView imgBack;
    ImageView ivSavePdf;
    ProgressDialog dialog;
    MyApplication objMyApplication;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_sign_up_privacy_web_view);

            dialog = new ProgressDialog(SignUpPrivacyWebViewActivity.this, R.style.MyAlertDialogStyle);
            dialog.setIndeterminate(false);
            dialog.setMessage("Please wait...");
            dialog.getWindow().setGravity(Gravity.CENTER);
            objMyApplication = (MyApplication) getApplicationContext();
            webView = (WebView) findViewById(R.id.idPDFView);
            webView.getSettings().setJavaScriptEnabled(true);
            webView.getSettings().setLoadWithOverviewMode(true);
            webView.getSettings().setBuiltInZoomControls(true);
            webView.getSettings().setUseWideViewPort(true);

//            String pdfurl = Uri.encode("https://crypto-resources.s3.amazonaws.com/Greenbox+POS+GDPR+Privacy+Policy.pdf");
//            webView.loadUrl("https://docs.google.com/gview?embedded=true&url=" + pdfurl);

            String pdfurl = "https://crypto-resources.s3.amazonaws.com/Greenbox+POS+GDPR+Privacy+Policy.pdf";
            webView.loadUrl("https://drive.google.com/viewerng/viewer?embedded=true&url=" + pdfurl);


            webView.setWebViewClient(new Callback());

            imgBack = (ImageView) findViewById(R.id.imgBack);
            ivSavePdf = (ImageView) findViewById(R.id.ivSavePdf);

            imgBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(SignUpPrivacyWebViewActivity.this, CreateAccountActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);
                }
            });
            ivSavePdf.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    askForPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, 101);
                    dialog = new ProgressDialog(SignUpPrivacyWebViewActivity.this, R.style.MyAlertDialogStyle);
                    dialog.setIndeterminate(false);
                    dialog.setMessage("Please wait...");
                    dialog.getWindow().setGravity(Gravity.CENTER);
                    dialog.show();
                    new DownloadFile().execute("https://crypto-resources.s3.amazonaws.com/Greenbox+POS+GDPR+Privacy+Policy.pdf");
                }
            });

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private class Callback extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(
                WebView view, String url) {
            if (url.startsWith("mailto:")) {
                Intent intent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse(url));
                startActivity(intent);
            } else if (url.startsWith("http:") || url.startsWith("https:")) {
                view.loadUrl(url);
            }
            return true;
        }
    }

    private void askForPermission(String permission, Integer requestCode) {
        if (ContextCompat.checkSelfPermission(SignUpPrivacyWebViewActivity.this, permission) != PackageManager.PERMISSION_GRANTED) {


            if (ActivityCompat.shouldShowRequestPermissionRationale(SignUpPrivacyWebViewActivity.this, permission)) {
                ActivityCompat.requestPermissions(SignUpPrivacyWebViewActivity.this, new String[]{permission}, requestCode);

            } else {
                ActivityCompat.requestPermissions(SignUpPrivacyWebViewActivity.this, new String[]{permission}, requestCode);
            }
        } else if (ContextCompat.checkSelfPermission(SignUpPrivacyWebViewActivity.this, permission) == PackageManager.PERMISSION_DENIED) {
            Utils.displayAlert("Permission was denied", SignUpPrivacyWebViewActivity.this);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (ActivityCompat.checkSelfPermission(SignUpPrivacyWebViewActivity.this, permissions[0]) == PackageManager.PERMISSION_GRANTED) {

            if (requestCode == 101)
                dialog.dismiss();
            Utils.displayAlert("Permission granted", SignUpPrivacyWebViewActivity.this);
        } else {
            dialog.dismiss();
            Utils.displayAlert("Permission denied", SignUpPrivacyWebViewActivity.this);
        }
    }

    @Override
    public void onBackPressed() {
        // code here to show dialog
        super.onBackPressed();  // optional depending on your needs
        Intent i = new Intent(SignUpPrivacyWebViewActivity.this, CreateAccountActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
    }

    private class DownloadFile extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... strings) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy");
            String currentDate = sdf.format(new Date());
            String fileUrl = "https://crypto-resources.s3.amazonaws.com/Greenbox+POS+GDPR+Privacy+Policy.pdf";   // -> http://maven.apache.org/maven-1.x/maven.pdf
            String fileName = "GreenBox-Privacy Policy-" + currentDate + ".pdf";  // -> maven.pdf

            File pdfFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName);

            try {
                pdfFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            downloadFile(fileUrl, pdfFile);
            return null;
        }
    }

    private static final int MEGABYTE = 1024 * 1024;

    public void downloadFile(String fileUrl, File directory) {
        try {
            URL url = new URL(fileUrl);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            //urlConnection.setRequestMethod("GET");
            //urlConnection.setDoOutput(true);
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            FileOutputStream fileOutputStream = new FileOutputStream(directory);
            int totalSize = urlConnection.getContentLength();

            byte[] buffer = new byte[MEGABYTE];
            int bufferLength = 0;
            while ((bufferLength = inputStream.read(buffer)) > 0) {
                fileOutputStream.write(buffer, 0, bufferLength);
            }
            fileOutputStream.close();

            runOnUiThread(new Runnable() {
                public void run() {
                    dialog.dismiss();
//                    Utils.displayAlert("File Downloaded Succesfully", SignUpPrivacyWebViewActivity.this);
                    Utils.displayCloseAlert("File Downloaded Succesfully", SignUpPrivacyWebViewActivity.this);
                }
            });
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
