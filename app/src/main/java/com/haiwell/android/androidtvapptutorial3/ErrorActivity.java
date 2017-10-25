package com.haiwell.android.androidtvapptutorial3;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;

/**
 * Created by Administrator on 2017/10/24.
 */

public class ErrorActivity extends Activity{
    private static final String TAG = ErrorActivity.class.getSimpleName();

    private ErrorFragment mErrorFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        testError();
    }

    private void testError() {
        mErrorFragment = new ErrorFragment();
        getFragmentManager().beginTransaction().add(R.id.main_browse_fragment, mErrorFragment).commit();

    }
}
