package com.tencent.jinjingcao.coverchoice.coverchoice;

import android.view.View;

/**
 * Created by jinjingcao on 2017/10/27.
 */

public abstract class ViewBinding {

	public View mRootView;

	protected static <T> T $(View root, int id) {
		return (T) root.findViewById(id);
	}

	protected ViewBinding(View rootView) {
		this.mRootView = rootView;
	}
}
