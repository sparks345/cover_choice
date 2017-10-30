package com.tencent.jinjingcao.coverchoice.coverchoice.ui;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.tencent.jinjingcao.coverchoice.R;
import com.tencent.jinjingcao.coverchoice.coverchoice.ui.TouchGesture.IMoveEventListener;

/**
 * Created by jinjingcao on 2017/10/27.
 */

public class CoverListBar extends FrameLayout implements IMoveEventListener {

    private static final String TAG = "CoverListBar";

    public static final int FRAME_PRE_SECOND = 25;//fps
    public static final int KEY_FRAME_STEP = 30;//key frame step
    public static final int FRAME_STEP = 1000 * 30 / 25;//ms per key frame
    public static final int PRECISION_FRAME_STEP = FRAME_STEP * 2;
    public static final int MAX_FRAME_COUNT = 30000 / PRECISION_FRAME_STEP;//40;//max frame count -> 50
    private static final int MAX_THUMB_CNT = 10;

    private final Context mContext;

    private LinearLayout mCoverList;
    private ImageView mCoverChoice;
    private View mRootView;

    private HashMap<Long, Frame> mFrames = new HashMap<>(MAX_FRAME_COUNT);

    private Mode mMode = Mode.Squire;
    private long mDuration;
    private long mStep;

    private int mThumbWidth = 100;
    private int mThumbHeight = 100;

    private TouchGesture mTouchGesture;
    private float mStartX;
    private float mDiffX;

    public CoverListBar(@NonNull Context context) {
        super(context);

        mContext = context;
        initView();
    }

    public CoverListBar(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        mContext = context;
        initView();
    }

    private void initView() {
        mRootView = LayoutInflater.from(mContext).inflate(R.layout.cover_list_bar, this, false);
        addView(mRootView);

        mCoverList = (LinearLayout) findViewById(R.id.cover_list);
        mCoverChoice = (ImageView) findViewById(R.id.cover_choice);

        mTouchGesture = new TouchGesture();
        mTouchGesture.setListener(this);

        createListThumbs();
    }

    private void createListThumbs() {
        for (int i = 0; i < MAX_THUMB_CNT; i++) {
            ImageView iv = new ImageView(mContext);
            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) iv.getLayoutParams();
            lp.weight = 1;
            iv.setLayoutParams(lp);
            mCoverList.addView(iv);
        }
    }

    private void setMode(Mode mode) {
        this.mMode = mode;
    }

    private void setDuration(long duration) {
        this.mDuration = duration;
    }

    public void init(Mode mode, long duration) {
        setMode(mode);
        setDuration(duration);

        mStep = mDuration / FRAME_STEP;
    }

    /**
     * 通过时间获取到最邻近的关键帧
     *
     * @param ms 时间
     * @return 帧信息
     */
    public Frame getFrameAtMs(long ms) {
        long key = ms / mStep;
        int diff = ms % mStep <= mStep / 2 ? 0 : 1;
        key += diff;

        Frame ret = mFrames.get(key);

        if (ret == null) {
            Frame tmpFrame = createFrame(key);
            mFrames.put(key, tmpFrame);
            ret = tmpFrame;
        }

        return ret;
    }

    private Frame createFrame(long key) {
        return new Frame(key);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return mTouchGesture.onTouchEvent(event) || super.onTouchEvent(event);
    }

    @Override
    public void onMove(float nLastX, MotionEvent event) {
//        Log.i(TAG, "onMove >> mCoverChoice.getX():" + mCoverChoice.getX() + ", mCoverChoice.getLeft():" + mCoverChoice.getLeft());

        if (mStartX >= 0) {

            // 在滑动条有效区域内才移动
            float xPos = nLastX - mCoverChoice.getWidth() / 2 - mDiffX;
            if (xPos >= 0 && xPos <= getWidth() - mCoverChoice.getWidth()) {

                mCoverChoice.setX(xPos);//setTranslationX(nLastX);
            }
        }
    }

    @Override
    public void onStart(float mLastX, MotionEvent event) {
        // 按住的是滚动条才移动
        if (mLastX > mCoverChoice.getX() && mLastX < mCoverChoice.getX() + mCoverChoice.getWidth()) {
            mStartX = mLastX;

            float centerPosX = mCoverChoice.getX() + mCoverChoice.getWidth() / 2;
            mDiffX = mStartX - centerPosX;
//            Log.w(TAG, "onStart. diffX:" + mDiffX);
        }
    }

    @Override
    public void onEnd(float eLastX, MotionEvent event) {
        mStartX = -1.0f;
    }

    public enum Mode {
        Squire, FullScreen
    }

    public class Frame {

        private static final String TAG = "CoverListBar.Frame";
        private static final String ABS_PATH = "";
        private final long keyId;

        public String path;
        public byte[] thumbData;
        public Drawable thumb;

        public Frame(long keyId) {
            this.keyId = keyId;
            this.path = ABS_PATH.concat(File.separator).concat(String.valueOf(keyId));
        }

        public Drawable initThumb() {
            File file = new File(path);
            if (!file.exists()) {
                Log.e(TAG, "initThumb. file not exists:" + path);
                generateBitmap(keyId, path);

                if (!file.exists()) {
                    Log.e(TAG, "initThumb error.");
                    return null;
                }
            }

            if (thumb == null) {
                BitmapDrawable drawable = new BitmapDrawable(file.getAbsolutePath());
                Bitmap bitmap = drawable.getBitmap();
                Bitmap.createScaledBitmap(bitmap, mThumbWidth, mThumbHeight, false);

                // 缓存小图到内存
                thumb = new BitmapDrawable(bitmap);
                // 写大图文件
                writeThumb(bitmap);
            }

            return thumb;
        }

        /**
         * 生成特定帧的大图
         *
         * @param keyId key
         * @param path  path
         */
        private void generateBitmap(long keyId, String path) {

        }

        private void writeThumb(Bitmap bitmap) {
            FileOutputStream out = null;
            try {
                out = new FileOutputStream(new File(getThumbPath()));
                bitmap.compress(CompressFormat.JPEG, 90, out);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } finally {
                if (out != null) {
                    try {
                        out.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        public String getThumbPath() {
            return path + "_s";
        }

        public String getPath() {
            return path;
        }
    }
}
