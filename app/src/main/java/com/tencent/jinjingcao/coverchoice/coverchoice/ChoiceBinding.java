package com.tencent.jinjingcao.coverchoice.coverchoice;

import android.support.constraint.ConstraintLayout;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tencent.jinjingcao.coverchoice.R;
import com.tencent.jinjingcao.coverchoice.coverchoice.ui.CoverListBar;

/**
 * Created by jinjingcao on 2017/10/27.
 */

public class ChoiceBinding extends ViewBinding {

    private TextView mTextView;
    private ConstraintLayout mConstraintLayout;
    private ImageView mPreviewView;
    private CoverListBar mCoverListBar;
//    private LinearLayout mCoverList;
//    private ImageView mCoverChoice;

    public ChoiceBinding(View rootView) {
        super(rootView);

        this.mRootView = rootView;

        mTextView = $(mRootView, R.id.textView);
        mConstraintLayout = $(mRootView, R.id.constraintLayout);
        mPreviewView = $(mRootView, R.id.preview_view);
        mCoverListBar = $(mRootView, R.id.cover_list_bar);
//        mCoverList = $(mRootView, R.id.cover_list);
//        mCoverChoice = $(mRootView, R.id.cover_choice);
    }


}
