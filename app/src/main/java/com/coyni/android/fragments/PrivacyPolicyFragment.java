package com.coyni.android.fragments;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.Html;
import android.util.Log;
import android.util.Pair;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.coyni.android.viewmodel.DashboardViewModel;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.coyni.android.R;
import com.coyni.android.model.user.AgreementsById;
import com.coyni.android.model.user.AgreementsDataItems;
import com.coyni.android.model.user.SavePdfRequest;
import com.coyni.android.utils.MyApplication;
import com.coyni.android.utils.Utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.ResponseBody;

import static android.content.ContentValues.TAG;
import static com.coyni.android.view.MainActivity.bottomSheetBehavior;
import static com.coyni.android.view.MainActivity.viewBack;
import static com.coyni.android.view.MainActivity.viewBottomSheet;

public class PrivacyPolicyFragment extends Fragment {
    View view;
    static Context context;
    ImageView imgBack, ivSavePdf;
    TextView tvAgreementSignedBy, tvIpAddress, tvSignedDate;
    MyApplication objMyApplication;
    ProgressDialog dialog;
    DashboardViewModel dashboardViewModel;
    ArrayList<Integer> arrSignatureTypes = new ArrayList<>();
    ArrayList<Integer> arrRefTypes = new ArrayList<>();
    ArrayList<Integer> arrPrivacyPolicy = new ArrayList<>();
    ArrayList<String> arrPrivacyPolicySignedOn = new ArrayList<>();
    ArrayList<String> arrPrivacyPolicyIpAddress = new ArrayList<>();
    ArrayList<Integer> arrTOS = new ArrayList<>();
    DownloadZipFileTask downloadZipFileTask;
    String agreementContent;
    WebView webView;

    public PrivacyPolicyFragment() {
    }

    public static PrivacyPolicyFragment newInstance(Context cxt) {
        PrivacyPolicyFragment fragment = new PrivacyPolicyFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        context = cxt;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onResume() {
        super.onResume();
        ((AppCompatActivity) getActivity()).getSupportActionBar().show();
        if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            bottomSheetBehavior.setHideable(true);
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            if (viewBottomSheet.getVisibility() == View.VISIBLE) {
                viewBottomSheet.setVisibility(View.GONE);
            }
            viewBack.setVisibility(View.GONE);
        }
        getActivity().findViewById(R.id.layoutMenu).setVisibility(View.GONE);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_privacy_policy, container, false);
        try {
            objMyApplication = (MyApplication) context.getApplicationContext();
            dashboardViewModel = new ViewModelProvider(this).get(DashboardViewModel.class);
            if (Build.VERSION.SDK_INT >= 23) {
                //getActivity().getWindow().setStatusBarColor(getActivity().getColor(R.color.statusbar));
                getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                        WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            }
            getActivity().findViewById(R.id.layoutMenu).setVisibility(View.GONE);
            imgBack = (ImageView) view.findViewById(R.id.imgBack);
            ivSavePdf = (ImageView) view.findViewById(R.id.ivSavePdf);
            tvAgreementSignedBy = (TextView) view.findViewById(R.id.tvAgreementSignedBy);
            tvSignedDate = (TextView) view.findViewById(R.id.tvSignedDate);
            tvIpAddress = (TextView) view.findViewById(R.id.tvIpAddress);
//            tvAgreementsContent = (TextView) view.findViewById(R.id.tvAgreementsContent);
//            tvLastUpdatedDate = (TextView) view.findViewById(R.id.tvLastUpdatedDate);

            webView = (WebView) view.findViewById(R.id.idPDFView);
            WebSettings webSettings = webView.getSettings();
            webSettings.setBuiltInZoomControls(true);
            webSettings.setJavaScriptEnabled(true);
            webView.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);

            webView.getSettings().setBuiltInZoomControls(true);
            webView.getSettings().setUseWideViewPort(true);
            webView.getSettings().setLoadWithOverviewMode(true);

            String pdfurl = Uri.encode("https://crypto-resources.s3.amazonaws.com/Greenbox+POS+GDPR+Privacy+Policy.pdf");
            webView.loadUrl("https://docs.google.com/gview?embedded=true&url=" + pdfurl);

            webView.setWebViewClient(new Callback());

            List<AgreementsDataItems> agreementsInfo = objMyApplication.getAgreements().getData().getItems();
            for (int i = 0; i < agreementsInfo.size(); i++) {
                arrSignatureTypes.add(agreementsInfo.get(i).getSignatureType());
                arrRefTypes.add(agreementsInfo.get(i).getRefId());

                if (arrSignatureTypes.get(i) == 1) {
                    arrPrivacyPolicy.add(agreementsInfo.get(i).getRefId());
                    arrPrivacyPolicySignedOn.add(agreementsInfo.get(i).getSignedOn());
                    arrPrivacyPolicyIpAddress.add(agreementsInfo.get(i).getIpAddress());

                } else {
                    arrTOS.add(agreementsInfo.get(i).getRefId());
                }
            }

//            int latestPrivacyPolicyRefId = arrPrivacyPolicy.get(arrPrivacyPolicySignedOn.size() - 1);
            int latestPrivacyPolicyRefId = arrPrivacyPolicy.get(0);
            if (Utils.checkInternet(context)) {
                dialog = new ProgressDialog(context, R.style.MyAlertDialogStyle);
                dialog.setIndeterminate(false);
                dialog.setMessage("Please wait...");
                dialog.getWindow().setGravity(Gravity.CENTER);
                dialog.show();
                dashboardViewModel.meAgreementsById(latestPrivacyPolicyRefId);

            } else {
                //Toast.makeText(context, getString(R.string.internet), Toast.LENGTH_LONG).show();
                Utils.displayAlert(getString(R.string.internet), getActivity());
            }
            initObservables();

            tvAgreementSignedBy.setText(Utils.capitalize(objMyApplication.getStrUser()));
//            tvSignedDate.setText(Utils.transactionDate(arrPrivacyPolicySignedOn.get(arrPrivacyPolicySignedOn.size() - 1)).toUpperCase());
            tvSignedDate.setText(objMyApplication.transactionDate(arrPrivacyPolicySignedOn.get(0)).toUpperCase());
            tvIpAddress.setText(arrPrivacyPolicyIpAddress.get(0));

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserAgreementsFragment userAgreementsFragment = UserAgreementsFragment.newInstance(getActivity());
                try {
                    openFragment(userAgreementsFragment, "userAgreeements");
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
        ivSavePdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utils.checkInternet(context)) {
                    askForPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, 101);
                    dialog = new ProgressDialog(getActivity(), R.style.MyAlertDialogStyle);
                    dialog.setIndeterminate(false);
                    dialog.setMessage("Please wait...");
                    dialog.getWindow().setGravity(Gravity.CENTER);
                    dialog.show();
                    new DownloadFile().execute("https://crypto-resources.s3.amazonaws.com/Greenbox+POS+GDPR+Privacy+Policy.pdf");
                } else {
                    Utils.displayAlert(getString(R.string.internet), getActivity());
                }
            }
        });

        return view;
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
        if (ContextCompat.checkSelfPermission(getActivity(), permission) != PackageManager.PERMISSION_GRANTED) {


            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), permission)) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{permission}, requestCode);

            } else {
                ActivityCompat.requestPermissions(getActivity(), new String[]{permission}, requestCode);
            }
        } else if (ContextCompat.checkSelfPermission(getActivity(), permission) == PackageManager.PERMISSION_DENIED) {
            Utils.displayAlert("Permission was denied", getActivity());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (ActivityCompat.checkSelfPermission(getActivity(), permissions[0]) == PackageManager.PERMISSION_GRANTED) {

            if (requestCode == 101)
                Utils.displayAlert("Permission granted", getActivity());
        } else {
            Utils.displayAlert("Permission denied", getActivity());
        }
    }

    private void openFragment(Fragment fragment, String tag) {
        try {
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.container, fragment, tag).addToBackStack(tag);
            transaction.commit();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void initObservables() {
        dashboardViewModel.getUserAgreementsByIdMutableLiveData().observe(getActivity(), new Observer<AgreementsById>() {
            @Override
            public void onChanged(AgreementsById user) {
                dialog.dismiss();
                if (user != null && user.getError() == null) {
                    agreementContent = user.getData().getContent();
                } else {
                    Utils.displayAlert(dashboardViewModel.getErrorMessage(), getActivity());
                }
            }
        });
    }

    private void initObservablesSavePdf() {
        dashboardViewModel.getUserAgreementsSavePdfMutableLiveData().observe(getActivity(), new Observer<ResponseBody>() {
            @Override
            public void onChanged(ResponseBody user) {
                dialog.dismiss();
                if (user != null) {
                    downloadZipFileTask = new DownloadZipFileTask();
                    downloadZipFileTask.execute(user);
                } else {
                    Utils.displayAlert(dashboardViewModel.getErrorMessage(), getActivity());
                }
            }
        });
    }

    private class DownloadZipFileTask extends AsyncTask<ResponseBody, Pair<Integer, Long>, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(ResponseBody... urls) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy");
            String currentDate = sdf.format(new Date());
            //Copy you logic to calculate progress and call
            saveToDisk(urls[0], "GreenBox-Privacy Policy-" + currentDate + ".pdf");
            return null;
        }

        protected void onProgressUpdate(Pair<Integer, Long>... progress) {

            Log.d("API123", progress[0].second + " ");

            if (progress[0].first == 100)
//                Utils.displayAlert("Download successful!", getActivity());
                Utils.displayCloseAlert("Download successful!", getActivity());

            if (progress[0].second > 0) {
                int currentProgress = (int) ((double) progress[0].first / (double) progress[0].second * 100);
            }

            if (progress[0].first == -1) {
//                Utils.displayAlert("Download failed", getActivity());
                Utils.displayCloseAlert("Download failed", getActivity());
            }

        }

        public void doProgress(Pair<Integer, Long> progressDetails) {
            publishProgress(progressDetails);
        }

        @Override
        protected void onPostExecute(String result) {

        }
    }

    private void saveToDisk(ResponseBody body, String filename) {
        try {

            File destinationFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), filename);

            InputStream inputStream = null;
            OutputStream outputStream = null;

            try {

                inputStream = body.byteStream();
                outputStream = new FileOutputStream(destinationFile);
                byte data[] = new byte[4096];
                int count;
                int progress = 0;
                long fileSize = body.contentLength();
                Log.d("TAG", "File Size=" + fileSize);
                while ((count = inputStream.read(data)) != -1) {
                    outputStream.write(data, 0, count);
                    progress += count;
                    Pair<Integer, Long> pairs = new Pair<>(progress, fileSize);
                    downloadZipFileTask.doProgress(pairs);
                    Log.d("TAG", "Progress: " + progress + "/" + fileSize + " >>>> " + (float) progress / fileSize);
                }

                outputStream.flush();

                Log.d("TAG", destinationFile.getParent());
                Pair<Integer, Long> pairs = new Pair<>(100, 100L);
                downloadZipFileTask.doProgress(pairs);
                return;
            } catch (IOException e) {
                e.printStackTrace();
                Pair<Integer, Long> pairs = new Pair<>(-1, Long.valueOf(-1));
                downloadZipFileTask.doProgress(pairs);
                Log.d("TAG", "Failed to save the file!");
                return;
            } finally {
                if (inputStream != null) inputStream.close();
                if (outputStream != null) outputStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG, "Failed to save the file!");
            return;
        }
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

            getActivity().runOnUiThread(new Runnable() {

                public void run() {
                    dialog.dismiss();
//                    Utils.displayAlert("File Downloaded Succesfully", getActivity());
                    Utils.displayCloseAlert("File Downloaded Succesfully", getActivity());

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

