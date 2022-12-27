package com.deepwaterooo.sdk.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.deepwaterooo.sdk.R;
import com.deepwaterooo.sdk.activities.authentication.DWBeforeStartActivity;
import com.deepwaterooo.sdk.activities.authentication.DWLoginActivity;
import com.deepwaterooo.sdk.appconfig.Constants;
import com.deepwaterooo.sdk.appconfig.Numerics;
import com.deepwaterooo.sdk.utils.SharedPrefUtil;
import com.deepwaterooo.sdk.utils.Util;

/**
 * Class used to launch the square panda splash/launch screen
 */
public class DWSplashScreenActivity extends BaseActivity {
    private final String TAG = "DWSplashScreenActivity";

    // Splash screen timer
    private static int DWLASH_TIME_OUT = 3000; // 3000
    SharedPrefUtil sharedPrefUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate() ");
        setContentView(R.layout.activity_splash_screen);
        Util.getDeviceDimensions(this);
        sharedPrefUtil = new SharedPrefUtil(getApplicationContext());
// 这里想要把整个登录界面跳过去
        new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, "(sharedPrefUtil.getBoolean(SharedPrefUtil.PREF_LOGIN_USER_STATUS)): " + (sharedPrefUtil.getBoolean(SharedPrefUtil.PREF_LOGIN_USER_STATUS)));
                    Log.d(TAG, "(!sharedPrefUtil.getBoolean(SharedPrefUtil.PREF_HAVE_PLAYSET)): " + (!sharedPrefUtil.getBoolean(SharedPrefUtil.PREF_HAVE_PLAYSET)));
                    Log.d(TAG, "(!sharedPrefUtil.getBoolean(SharedPrefUtil.PREF_DO_YOU_HAVE_ACC)): " + (!sharedPrefUtil.getBoolean(SharedPrefUtil.PREF_DO_YOU_HAVE_ACC)));
                    
                    if (sharedPrefUtil.getBoolean(SharedPrefUtil.PREF_LOGIN_USER_STATUS)) {
                        Intent i = new Intent(DWSplashScreenActivity.this, DWManagePlayerActivity.class);
                        i.putExtra(Constants.EXTRA_SELECT_PLAYER, true);
                        i.putExtra(Constants.EXTRA_IS_FIRST_TIME, true);
                        startActivityForResult(i, Numerics.ZERO);
                    } else if (!sharedPrefUtil.getBoolean(SharedPrefUtil.PREF_HAVE_PLAYSET)) { // <<<<<<<<<< 只在必须要先登录的情况下才使用的呀
                        Intent i = new Intent(DWSplashScreenActivity.this, DWBeforeStartActivity.class);
                        startActivityForResult(i, Numerics.ZERO);
// //  我暂时把它改成是下面的                  
                    } else if (!sharedPrefUtil.getBoolean(SharedPrefUtil.PREF_DO_YOU_HAVE_ACC)) {
                        Intent i = new Intent(DWSplashScreenActivity.this, DWHaveAccountActivity.class);
                        startActivityForResult(i, Numerics.ZERO);
                    } else { // 这里每次从那个固定的界面来跳转
                        Intent i = new Intent(DWSplashScreenActivity.this, DWLoginActivity.class);
                        startActivityForResult(i, Numerics.ZERO);
                    }
                }
            }, DWLASH_TIME_OUT);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause() ");
    }
    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop() ");
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy() ");
    }
}
