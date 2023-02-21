package com.coyni.pos.app.dialog

import android.content.Context
import androidx.constraintlayout.motion.widget.MotionLayout
import com.coyni.pos.app.R
import com.coyni.pos.app.baseclass.BaseDialog
import com.coyni.pos.app.databinding.RefundPreviewBinding
import com.coyni.pos.app.utils.Utils


class RefundPreviewDialog(context: Context) : BaseDialog(context) {
    private var isCheckoutCalled = false
    private lateinit var dialogBinding: RefundPreviewBinding
    override fun getLayoutId() = R.layout.refund_preview

    override fun initViews() {
        dialogBinding = RefundPreviewBinding.bind(findViewById(R.id.root))
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
}