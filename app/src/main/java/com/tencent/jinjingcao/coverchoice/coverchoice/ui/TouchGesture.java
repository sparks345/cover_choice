package com.tencent.jinjingcao.coverchoice.coverchoice.ui;

import android.database.CrossProcessCursor;
import android.util.Log;
import android.view.MotionEvent;

/**
 * Created by jinjingcao on 2017/10/27.
 */

public class TouchGesture {

    private static final String TAG = "TouchGesture";

    private float mLastX;
    private float mLastY;

    public static final int ACTION_RECOGNITION_OFFSET = 20;

    private IMoveEventListener mListener = new IMoveEventListener() {
        @Override
        public void onMove(float nLastX, MotionEvent event) {

        }
    };

    public boolean onTouchEvent(MotionEvent event) {
        Log.i(TAG, "onTouchEvent." + event.getAction());
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // start point.
                mLastX = event.getX();
                mLastY = event.getY();
                return true;

            case MotionEvent.ACTION_MOVE:
                float nLastX = event.getX();
                float nLastY = event.getY();
                if (nLastX > nLastY && (Math.abs(nLastX - mLastX) > ACTION_RECOGNITION_OFFSET)) {
                    // action.
                    this.getListener().onMove(nLastX, event);
//                    return true;
                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                // end point.
                break;
        }

//        return super.onTouchEvent(event);
        return false;
    }

    public void setListener(IMoveEventListener listener) {
        this.mListener = listener;
    }

    private IMoveEventListener getListener() {
        return mListener;
    }


    public interface IMoveEventListener {

        void onMove(float nLastX, MotionEvent event);
    }
}
