package com.ve.irrigation.utils;
import android.view.GestureDetector;
import android.view.MotionEvent;

import com.ve.irrigation.irrigation.listners.SwipeListener;

/**
 * Created by dalvendrakumar on 15/2/19.
 */

public class IrrigationGestureDetector extends GestureDetector.SimpleOnGestureListener{
    private static final String TAG= IrrigationGestureDetector.class.getSimpleName();

    private SwipeListener listener;

    public IrrigationGestureDetector(SwipeListener listener) {
        this.listener = listener;
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        try {
            int swipeId=getSlope(e1.getX(), e1.getY(), e2.getX(), e2.getY());
            listener.onSwipe(swipeId);
        } catch (Exception e) {
            Utils.printLog(TAG, "onFling "+e.getMessage());
        }
        return super.onFling(e1, e2, velocityX, velocityY);
    }

    private int getSlope(float x1, float y1, float x2, float y2) {
        Double angle = Math.toDegrees(Math.atan2(y1 - y2, x2 - x1));
        if (angle > 45 && angle <= 135)// top
            return SwipeListener.SWIPE_UP;
        if (angle >= 135 && angle < 180 || angle < -135 && angle > -180)// left
            return SwipeListener.SWIPE_LEFT;
        if (angle < -45 && angle >= -135)// down
            return SwipeListener.SWIPE_DOWN;
        if (angle > -45 && angle <= 45)// right
            return SwipeListener.SWIPE_RIGHT;
        return 0;

    }
}
