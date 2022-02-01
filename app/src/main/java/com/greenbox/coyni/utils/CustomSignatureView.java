package com.greenbox.coyni.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.io.FileOutputStream;

public class CustomSignatureView extends View {

    public final String TAG = getClass().getName();
    private Paint paint;
    private Path path;
    private float lastEventX, lastEventY;
    private final RectF dirtyRect = new RectF();
    private static final float PAINT_STROKE_WIDTH = 5f;
    private static final float HALF_STROKE_WIDTH = PAINT_STROKE_WIDTH / 2;
    private Bitmap mBitmap;

    public CustomSignatureView(Context context) {
        super(context);
        initializeView();

        // this.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }

    public CustomSignatureView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        initializeView();
    }

    private void initializeView() {
        paint = new Paint();
        path = new Path();

        paint.setAntiAlias(true);
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeWidth(PAINT_STROKE_WIDTH);
        this.setBackgroundColor(Color.WHITE);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawPath(path, paint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float eventX = event.getX();
        float eventY = event.getY();
        setEnabled(true);
        LogUtils.d(TAG, "Touch event at " + eventX + ":" + eventY);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                path.moveTo(eventX, eventY);
                lastEventX = eventX;
                lastEventY = eventY;
                return true;
            case MotionEvent.ACTION_MOVE:
            case MotionEvent.ACTION_UP:
                resetDirtyRect(eventX, eventY);
                int historySize = event.getHistorySize();
                for (int count = 0; count < historySize; count++) {
                    float hisX = event.getHistoricalX(count);
                    float hisY = event.getHistoricalY(count);
                    expandDirtyRect(hisX, hisY);
                    path.lineTo(hisX, hisY);
                }
                path.lineTo(eventX, eventY);
                break;
            default:
                LogUtils.d(TAG, "Ignore this event");
        }
        invalidate((int) (dirtyRect.left - HALF_STROKE_WIDTH),
                (int) (dirtyRect.top - HALF_STROKE_WIDTH),
                (int) (dirtyRect.right + HALF_STROKE_WIDTH),
                (int) (dirtyRect.bottom + HALF_STROKE_WIDTH));

        lastEventX = eventX;
        lastEventY = eventY;
        return true;
    }

    public void clear() {
        path.reset();
        invalidate();
    }

    private void expandDirtyRect(float historicalX, float historicalY) {
        if (historicalX < dirtyRect.left) {
            dirtyRect.left = historicalX;
        } else if (historicalX > dirtyRect.right) {
            dirtyRect.right = historicalX;
        }

        if (historicalY < dirtyRect.top) {
            dirtyRect.top = historicalY;
        } else if (historicalY > dirtyRect.bottom) {
            dirtyRect.bottom = historicalY;
        }
    }

    private void resetDirtyRect(float eventX, float eventY) {
        dirtyRect.left = Math.min(lastEventX, eventX);
        dirtyRect.right = Math.max(lastEventX, eventX);
        dirtyRect.top = Math.min(lastEventY, eventY);
        dirtyRect.bottom = Math.max(lastEventY, eventY);
    }

    public Bitmap save() {
//        LogUtils.v(TAG, "Width: " + v.getWidth());
//        LogUtils.v(TAG, "Height: " + v.getHeight());
        if(mBitmap != null) {
            mBitmap.recycle();
            mBitmap = null;
        }
        mBitmap = Bitmap.createBitmap(this.getWidth(), this.getHeight(), Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(mBitmap);
        draw(canvas);
        return mBitmap;
    }
}
