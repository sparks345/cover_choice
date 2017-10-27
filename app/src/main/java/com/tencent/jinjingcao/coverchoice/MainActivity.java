package com.tencent.jinjingcao.coverchoice;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.tencent.jinjingcao.coverchoice.coverchoice.ChoiceBinding;

public class MainActivity extends AppCompatActivity {

    private ChoiceBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        mBinding = new ChoiceBinding(getWindow().getDecorView().getRootView());
    }
}
