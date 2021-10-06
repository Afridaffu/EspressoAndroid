package com.coyni.android.view;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.Gravity;
import android.view.View;
import android.webkit.WebSettings;
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

import javax.net.ssl.HttpsURLConnection;

public class SignUpTOSWebViewActivity extends AppCompatActivity {
    WebView webView;
    ImageView imgBack;
    ImageView ivSavePdf;
    public Context mContext;
    ProgressDialog dialog;
    MyApplication objMyApplication;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_sign_up_tosweb_view);

            imgBack = (ImageView) findViewById(R.id.imgBack);
            webView = (WebView) findViewById(R.id.idPDFView);
            ivSavePdf = (ImageView) findViewById(R.id.ivSavePdf);
            objMyApplication = (MyApplication) getApplicationContext();
            WebSettings webSettings = webView.getSettings();
            webSettings.setBuiltInZoomControls(true);
            webSettings.setJavaScriptEnabled(true);
            webView.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);

            webView.getSettings().setBuiltInZoomControls(true);
            webView.getSettings().setUseWideViewPort(true);
            webView.getSettings().setLoadWithOverviewMode(true);
            webView.getSettings().setJavaScriptEnabled(true);

            String pdfurl = "https://crypto-resources.s3.amazonaws.com/Gen+3+V1+TOS+v6.pdf";
//            String pdfurl = Uri.encode("https://crypto-resources.s3.amazonaws.com/Gen+3+V1+TOS+v6.pdf");
            webView.loadUrl("https://docs.google.com/gview?embedded=true&url=" + pdfurl);
//            webView.loadUrl("https://docs.google.com/viewer?url=" + pdfurl);
            webView.setWebViewClient(new Callback());

            imgBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(SignUpTOSWebViewActivity.this, CreateAccountActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);
                }
            });
            ivSavePdf.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    askForPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, 101);
                    dialog = new ProgressDialog(SignUpTOSWebViewActivity.this, R.style.MyAlertDialogStyle);
                    dialog.setIndeterminate(false);
                    dialog.setMessage("Please wait...");
                    dialog.getWindow().setGravity(Gravity.CENTER);
                    dialog.show();
                    new DownloadFile(getApplicationContext()).execute("https://crypto-resources.s3.amazonaws.com/Gen+3+V1+TOS+v6.pdf");
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
        if (ContextCompat.checkSelfPermission(SignUpTOSWebViewActivity.this, permission) != PackageManager.PERMISSION_GRANTED) {


            if (ActivityCompat.shouldShowRequestPermissionRationale(SignUpTOSWebViewActivity.this, permission)) {
                ActivityCompat.requestPermissions(SignUpTOSWebViewActivity.this, new String[]{permission}, requestCode);

            } else {
                ActivityCompat.requestPermissions(SignUpTOSWebViewActivity.this, new String[]{permission}, requestCode);
            }
        } else if (ContextCompat.checkSelfPermission(SignUpTOSWebViewActivity.this, permission) == PackageManager.PERMISSION_DENIED) {
            //Toast.makeText(getActivity(), "Permission was denied", Toast.LENGTH_SHORT).show();
            Utils.displayAlert("Permission was denied", SignUpTOSWebViewActivity.this);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (ActivityCompat.checkSelfPermission(SignUpTOSWebViewActivity.this, permissions[0]) == PackageManager.PERMISSION_GRANTED) {

            if (requestCode == 101)
                dialog.dismiss();
            Utils.displayAlert("Permission granted", SignUpTOSWebViewActivity.this);
        } else {
            dialog.dismiss();
            Utils.displayAlert("Permission denied", SignUpTOSWebViewActivity.this);
        }
    }

    @Override
    public void onBackPressed() {
        // code here to show dialog
        super.onBackPressed();  // optional depending on your needs
        Intent i = new Intent(SignUpTOSWebViewActivity.this, CreateAccountActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
    }

    public class DownloadFile extends AsyncTask<String, Void, Void> {


        public DownloadFile(Context context) {
            mContext = context;
        }

        @Override
        protected Void doInBackground(String... strings) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy");
            String currentDate = sdf.format(new Date());
            String fileUrl = "https://crypto-resources.s3.amazonaws.com/Gen+3+V1+TOS+v6.pdf";   // -> http://maven.apache.org/maven-1.x/maven.pdf
            String fileName = "GreenBox-Terms of Service-" + currentDate + ".pdf";  // -> maven.pdf

            File pdfFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName);

            try {
                pdfFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            downloadFile(fileUrl, pdfFile);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }

    //    public static class FileDownloader {
    public final int MEGABYTE = 1024 * 1024;

    public void downloadFile(String fileUrl, File directory) {
        try {

            URL url = new URL(fileUrl);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
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
//                    Utils.displayAlert("File Downloaded Succesfully", SignUpTOSWebViewActivity.this);
                    Utils.displayCloseAlert("File Downloaded Succesfully", SignUpTOSWebViewActivity.this);
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