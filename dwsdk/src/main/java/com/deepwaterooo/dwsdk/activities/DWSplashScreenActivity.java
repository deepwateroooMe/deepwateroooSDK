package com.deepwaterooo.dwsdk.activities;

import android.content.Intent;
import android.os.Bundle;

import com.deepwaterooo.dwsdk.utils.SharedPrefUtil;

/**
 * Class used to launch the square panda splash/launch screen
 */
public class DWSplashScreenActivity extends BaseActivity {
    // Splash screen timer
    private static int SPLASH_TIME_OUT = 3000;
    SharedPrefUtil sharedPrefUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        Util.getDeviceDimensions(this);
        sharedPrefUtil = new SharedPrefUtil(getApplicationContext());

        new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (sharedPrefUtil.getBoolean(SharedPrefUtil.PREF_LOGIN_USER_STATUS)) {
                        Intent i = new Intent(DWSplashScreenActivity.this, DWManagePlayerActivity.class);
                        i.putExtra(Constants.EXTRA_SELECT_PLAYER, true);
                        i.putExtra(Constants.EXTRA_IS_FIRST_TIME, true);
                        startActivityForResult(i, Numerics.ZERO);
                    } else if (!sharedPrefUtil.getBoolean(SharedPrefUtil.PREF_HAVE_PLAYSET)) { // <<<<<<<<<< 
                        Intent i = new Intent(DWSplashScreenActivity.this, DWBeforeStartActivity.class);
                        startActivityForResult(i, Numerics.ZERO);
                    } else if (!sharedPrefUtil.getBoolean(SharedPrefUtil.PREF_DO_YOU_HAVE_ACC)) {
                        Intent i = new Intent(DWSplashScreenActivity.this, DWHaveAccountActivity.class);
                        startActivityForResult(i, Numerics.ZERO);
                    } else {
                        Intent i = new Intent(DWSplashScreenActivity.this, DWLoginActivity.class);
                        startActivityForResult(i, Numerics.ZERO);
                    }
                }
            }, DWLASH_TIME_OUT);
    }
}
