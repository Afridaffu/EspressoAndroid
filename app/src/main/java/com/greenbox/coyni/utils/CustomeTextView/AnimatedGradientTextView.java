package com.greenbox.coyni.utils.CustomeTextView;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

/**
 * A TextView using an animated gradient as text color
 */
public class AnimatedGradientTextView extends TextView {

    GradientManager gradientManager;

    public AnimatedGradientTextView(Context context) {
        super(context);
        gradientManager = new GradientManager(this);
    }

    public AnimatedGradientTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        gradientManager = new GradientManager(this, attrs);
        CustomFontManager.applyFontFromAttrs(this, attrs);
    }

    public AnimatedGradientTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        gradientManager = new GradientManager(this, attrs);
        CustomFontManager.applyFontFromAttrs(this, attrs);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
//        gradientManager.stopGradient();
//        gradientManager.startGradient();
    }

    @Override
    protected void onVisibilityChanged(View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
//        if (visibility == VISIBLE) {
//            if (getScaleX() != 0 && getScaleY() != 0) {
//                gradientManager.startGradient();
//            }
//        } else {
//            gradientManager.stopGradient();
//        }

        if (visibility == GONE) {
            gradientManager.stopGradient();
            changedView.setVisibility(GONE);
        } else if (visibility == VISIBLE) {
            gradientManager.startGradient();
            changedView.setVisibility(VISIBLE);
        }
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
//        if (visibility == VISIBLE) {
//            if (getScaleX() != 0 && getScaleY() != 0) {
//                gradientManager.startGradient();
//            }
//        } else {
//            gradientManager.stopGradient();
//        }
    }

    @Override
    public void onScreenStateChanged(int screenState) {
        super.onScreenStateChanged(screenState);
//        if (screenState == SCREEN_STATE_OFF) {
//            gradientManager.stopGradient();
//        } else if (screenState == SCREEN_STATE_ON) {
//            gradientManager.startGradient();
//        }
    }

}