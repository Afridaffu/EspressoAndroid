package com.coyni.mapp.dialogs;

import android.app.Dialog;
import android.app.DownloadManager;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.databinding.DataBindingUtil;

import com.coyni.mapp.R;
import com.coyni.mapp.databinding.ActivitySignAgreementsBinding;
import com.coyni.mapp.model.SignAgreementsResp;
import com.coyni.mapp.utils.JavaScriptInterface;
import com.coyni.mapp.utils.MyApplication;
import com.coyni.mapp.viewmodel.DashboardViewModel;
import com.coyni.mapp.viewmodel.LoginViewModel;

public class SignAgreementsDialog extends BaseDialog{
    private Context context;
    private ActivitySignAgreementsBinding binding;
    private DashboardViewModel dashboardViewModel;
    private LoginViewModel loginViewModel;
    private String myUrl = "", ACT_TYPE, DOC_NAME = "", DOC_URL = "", MATERIAL = "M", NON_MATERIAL = "N";
    private boolean isActionEnabled = false;
    private MyApplication objMyApplication;
    private int AGREE_TYPE, iterationCount = -1, currentIteration = 0;
    private Long mLastClickTime = 0L;
    private DownloadManager manager;
    private Dialog downloadDialog;
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 102;
    private JavaScriptInterface jsInterface;
    private SignAgreementsResp agrementsResponse;
    private boolean isSignatureCaptured = false;
    private String filePath = null;

    public SignAgreementsDialog(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.authorized_signers_learn_more_dialog);
        binding = DataBindingUtil
                .inflate(LayoutInflater.from(context), R.layout.activity_sign_agreements, null, false);
        setContentView(binding.getRoot());

    }
}
