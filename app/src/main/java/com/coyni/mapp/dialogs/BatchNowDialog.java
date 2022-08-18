package com.coyni.mapp.dialogs;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.constraintlayout.motion.widget.MotionLayout;

import com.coyni.mapp.R;
import com.coyni.mapp.model.BusinessBatchPayout.BatchPayoutListItems;
import com.coyni.mapp.utils.CustomeTextView.AnimatedGradientTextView;
import com.coyni.mapp.utils.MyApplication;
import com.coyni.mapp.utils.Utils;
import com.coyni.mapp.viewmodel.BusinessDashboardViewModel;

public class BatchNowDialog extends BaseDialog {

    private TextView openAmountTV, sentToDescriptionTV, availBalTV;
    private MotionLayout slideToConfirm;
    private BusinessDashboardViewModel businessDashboardViewModel;
    private Context context;
    private boolean isBatch = false;
    private Activity activity;
    private BatchPayoutListItems batchNowRequest;
    private MyApplication objMyApplication;
    private String openAmount = "", sent = "", availbal = "";
    private Double availableAmount;
    private boolean isBatchProcessCalled = false;
    private Object BusinessDashboardFragment;


    public BatchNowDialog(Context context, BatchPayoutListItems batchNowRequest, Double availableAmount) {
        super(context);
        this.batchNowRequest = batchNowRequest;
        activity = (Activity) context;
        this.availableAmount = availableAmount;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.clear_batch_prompt);
        initFields();
    }
//    private void batchNowProcessAPI(BatchNowSlideRequest request) {
//        businessDashboardViewModel.batchNowSlideData(request);
//    }
//    public BatchNowSlideRequest batchnowTransaction() {
//        BatchNowSlideRequest request = new BatchNowSlideRequest();
//        try {
//            request.setBatchId();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        return request;
//    }


    private void initFields() {
        openAmountTV = findViewById(R.id.openAmountTV);
        sentToDescriptionTV = findViewById(R.id.sentToDescriptionTV);
        availBalTV = findViewById(R.id.availBalTV);
        MotionLayout slideToConfirm = findViewById(R.id.slideToConfirmm);
        CardView im_lock_ = findViewById(R.id.im_lock);
        AnimatedGradientTextView tv_lable = findViewById(R.id.tv_lable);


        if (batchNowRequest != null && batchNowRequest.getStatus() != null
                && batchNowRequest.getStatus().equalsIgnoreCase(Utils.OPEN)) {
            openAmountTV.setText(Utils.convertBigDecimalUSDC(batchNowRequest.getTotalAmount()));
            sentToDescriptionTV.setText(batchNowRequest.getSentTo());
            availBalTV.setText(Utils.convertBigDecimalUSDC(availableAmount.toString()));
        }

        slideToConfirm.setTransitionListener(new MotionLayout.TransitionListener() {
            @Override
            public void onTransitionStarted(MotionLayout motionLayout, int startId, int endId) {

            }

            @Override
            public void onTransitionChange(MotionLayout motionLayout, int startId, int endId, float progress) {
                if (progress > Utils.slidePercentage) {
                    im_lock_.setAlpha(1.0f);
                    motionLayout.setTransition(R.id.middle, R.id.end);
                    motionLayout.transitionToState(motionLayout.getEndState());
                    slideToConfirm.setInteractionEnabled(false);
                    if (!isBatchProcessCalled) {
                        isBatchProcessCalled = true;
                        dismiss();
                        tv_lable.setText("Verifying");
                        if (getOnDialogClickListener() != null) {
                            getOnDialogClickListener().onDialogClicked(Utils.Swiped, batchNowRequest.getBatchId());
                        }
                        //isBatchProcessCalled = true;
                    }

                }
            }

            @Override
            public void onTransitionCompleted(MotionLayout motionLayout, int currentId) {

            }

            @Override
            public void onTransitionTrigger(MotionLayout motionLayout, int triggerId, boolean positive, float progress) {

            }
        });

    }
}