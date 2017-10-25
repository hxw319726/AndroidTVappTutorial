package com.haiwell.android.androidtvapptutorial3;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;

/**
 * Created by Administrator on 2017/10/24.
 */

public class ErrorFragment extends android.support.v17.leanback.app.ErrorFragment {
    private static final String TAG = ErrorFragment.class.getSimpleName();
    private static final boolean TRANSLUCENT = true;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);

        setTitle(getResources().getString(R.string.app_name));
        setErrorContent();
    }

    void setErrorContent() {
        setImageDrawable(getActivity().getDrawable(R.drawable.lb_ic_sad_cloud));
        setMessage(getResources().getString(R.string.error_fragment_message));
        setDefaultBackground(TRANSLUCENT);

        setButtonText(getResources().getString(R.string.dismiss_error));
        setButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager().beginTransaction().remove(ErrorFragment.this).commit();
            }
        });
    }
}
