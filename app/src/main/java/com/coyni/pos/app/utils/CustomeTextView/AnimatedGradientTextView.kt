package com.coyni.pos.app.utils.CustomeTextView

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.TextView

/**
 * A TextView using an animated gradient as text color
 */
class AnimatedGradientTextView : TextView {
    var gradientManager: GradientManager

    constructor(context: Context?) : super(context) {
        gradientManager = GradientManager(this)
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        gradientManager = GradientManager(this, attrs!!)
        CustomFontManager.applyFontFromAttrs(this, attrs)
    }

    constructor(context: Context?, attrs: AttributeSet, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
        gradientManager = GradientManager(this, attrs!!)
        CustomFontManager.applyFontFromAttrs(this, attrs)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        gradientManager.stopGradient()
        gradientManager.startGradient()
    }

    override fun onVisibilityChanged(changedView: View, visibility: Int) {
        super.onVisibilityChanged(changedView, visibility)
        //        if (visibility == VISIBLE) {
//            if (getScaleX() != 0 && getScaleY() != 0) {
//                gradientManager.startGradient();
//            }
//        } else {
//            gradientManager.stopGradient();
//        }
        if (visibility == GONE) {
            gradientManager.stopGradient()
            changedView.visibility = GONE
        } else if (visibility == VISIBLE) {
            gradientManager.startGradient()
            changedView.visibility = VISIBLE
        }
    }

    override fun onWindowVisibilityChanged(visibility: Int) {
        super.onWindowVisibilityChanged(visibility)
        if (visibility == VISIBLE) {
            if (scaleX != 0f && scaleY != 0f) {
                gradientManager.startGradient()
            }
        } else {
            gradientManager.stopGradient()
        }
    }

    override fun onScreenStateChanged(screenState: Int) {
        super.onScreenStateChanged(screenState)
        if (screenState == SCREEN_STATE_OFF) {
            gradientManager.stopGradient()
        } else if (screenState == SCREEN_STATE_ON) {
            gradientManager.startGradient()
        }
    }
}