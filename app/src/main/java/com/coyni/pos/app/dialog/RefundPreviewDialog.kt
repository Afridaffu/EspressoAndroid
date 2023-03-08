package com.coyni.pos.app.dialog

import android.content.Context
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
        myApplication = context as MyApplication
        dialogBinding = RefundPreviewBinding.bind(findViewById(R.id.root))

        dialogBinding.tvProcessingFee.text =
            Utils.convertTwoDecimal(myApplication.mCurrentUserData.refundResponseData?.processingFee.toString())
        dialogBinding.customerAddressTV.text =
            myApplication.mCurrentUserData.refundResponseData!!.referenceId
        dialogBinding.amountPayTV.text = Utils.convertTwoDecimal(
            refundAmount.toString()
        )
        processingFee =
            Utils.doubleParsing(myApplication.mCurrentUserData.refundResponseData?.processingFee.toString())
        refundAmpount = Utils.doubleParsing(refundAmount.toString())
        total = processingFee + refundAmpount
        dialogBinding.tvTotal.text = Utils.convertTwoDecimal(total.toString())
        dialogBinding.messageNoteTV.text = reason
        dialogBinding.infoIV.setOnClickListener {
            dialogBinding.viewFeesTextLL.visibility = VISIBLE
        }
        dialogBinding.viewFeesTV.setOnClickListener {
            feeDialog()
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