package com.coyni.pos.app.dialog

import android.content.Context
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.constraintlayout.motion.widget.MotionLayout
import com.coyni.pos.app.R
import com.coyni.pos.app.baseclass.BaseDialog
import com.coyni.pos.app.databinding.RefundPreviewBinding
import com.coyni.pos.app.utils.MyApplication
import com.coyni.pos.app.utils.Utils


class RefundPreviewDialog(
    context: Context,
    private var refundAmount: String?,
    private var reason: String?
) : BaseDialog(context) {
    private var isCheckoutCalled = false
    private lateinit var dialogBinding: RefundPreviewBinding
    lateinit var myApplication: MyApplication
    var processingFee: Double = 0.0
    var refundAmpount: Double = 0.0
    var total: Double = 0.0
    override fun getLayoutId() = R.layout.refund_preview

    override fun initViews() {
        dialogBinding = RefundPreviewBinding.bind(findViewById(R.id.root))
        myApplication = context.applicationContext as MyApplication
        dialogBinding.tvProcessingFee.text =
            Utils.convertTwoDecimal(myApplication.mCurrentUserData.refundResponseData?.processingFee.toString())
                .replace("$", "") + " CYN"
        if (myApplication.mCurrentUserData.transactionData!!.referenceId != null) {
            if (myApplication.mCurrentUserData.transactionData!!.referenceId?.length!! > 10) {
                dialogBinding.customerAddressTV.text =
                    myApplication.mCurrentUserData.transactionData!!.referenceId!!.substring(
                        0,
                        10
                    ) + "..."
            } else {
                dialogBinding.customerAddressTV.text =
                    myApplication.mCurrentUserData.transactionData!!.referenceId.toString()
            }
        }
        dialogBinding.copyCustomerAddressLL.setOnClickListener {
            Utils.copyText(
                myApplication.mCurrentUserData.transactionData!!.referenceId,
                context.applicationContext
            )
        }
//        dialogBinding.customerAddressTV.text =
//            myApplication.mCurrentUserData.refundResponseData!!.referenceId.toString()
        dialogBinding.amountPayTV.text = Utils.convertTwoDecimal(
            refundAmount.toString()
        ).replace("$", "")
        processingFee =
            Utils.doubleParsing(myApplication.mCurrentUserData.refundResponseData?.processingFee.toString())
        refundAmpount = Utils.doubleParsing(refundAmount.toString())
        total = processingFee + refundAmpount
        dialogBinding.tvTotal.text =
            Utils.convertTwoDecimal(total.toString()).replace("$", "") + " CYN"
        if (reason == "") {
            dialogBinding.lyMessage.visibility = GONE
        } else {
            dialogBinding.lyMessage.visibility = VISIBLE
            dialogBinding.messageNoteTV.text = "\"" + reason + "\""
        }
        dialogBinding.infoIV.setOnClickListener {
//            dialogBinding.viewFeesTextLL.visibility = VISIBLE
        }
        dialogBinding.customerNameTV.text =
            myApplication.mCurrentUserData.transactionData?.customerName.toString()
        dialogBinding.viewFeesTV.setOnClickListener {
//            feeDialog()
        }
        dialogBinding.slideToConfirmm.setTransitionListener(object :
            MotionLayout.TransitionListener {
            override fun onTransitionStarted(
                motionLayout: MotionLayout,
                startId: Int,
                endId: Int
            ) {
            }

            override fun onTransitionChange(
                motionLayout: MotionLayout,
                startId: Int,
                endId: Int,
                progress: Float
            ) {
                if (progress > Utils.slidePercentage) {
                    dialogBinding.imLock.alpha = 1.0f
                    motionLayout.setTransition(R.id.middle, R.id.end)
                    motionLayout.transitionToState(motionLayout.endState)
                    dialogBinding.slideToConfirmm.isInteractionEnabled = false
                    if (!isCheckoutCalled) {
                        isCheckoutCalled = true
                        dismiss()
                        dialogBinding.tvLable.text = "Verifying"
                        if (getOnDialogClickListener() != null) {
                            getOnDialogClickListener()!!.onDialogClicked(Utils.SWIPE, "")
                        }
                    }
                }
            }

            override fun onTransitionCompleted(motionLayout: MotionLayout, currentId: Int) {}
            override fun onTransitionTrigger(
                motionLayout: MotionLayout,
                triggerId: Int,
                positive: Boolean,
                progress: Float
            ) {
            }
        })
    }

    private fun feeDialog() {
        val feeDialog = ViewFeeDialog(context)
        feeDialog.show()
    }
}