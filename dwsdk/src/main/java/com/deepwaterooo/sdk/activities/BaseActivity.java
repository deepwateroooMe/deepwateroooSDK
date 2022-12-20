package com.deepwaterooo.sdk.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;

import com.deepwaterooo.sdk.R;
import com.deepwaterooo.sdk.appconfig.Constants;
import com.deepwaterooo.sdk.appconfig.Numerics;
import com.deepwaterooo.sdk.utils.LoginListener;
import com.deepwaterooo.sdk.utils.SharedPrefUtil;
import com.deepwaterooo.sdk.utils.Util;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Activity used for base class for all activities
 */
// ACTIVITY基类: 就是把一些必要的会话框及其相关的回调都包装在基类里了.把这个作为安卓SDK端所有活动的基类
public class BaseActivity extends AppCompatActivity implements DialogInterface.OnDismissListener, LoginListener {
    private static final String TAG = "BaseActivity";

    private ProgressDialog progressDialog;
    public static boolean IS_APP_RUNNING;
    private View mDecorView;
    private SharedPrefUtil sharedPrefUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        sharedPrefUtil = new SharedPrefUtil(this); 
        super.onCreate(savedInstanceState);
//        LocaleHelper.setLocale(this, LocaleHelper.getLanguage(this));
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        mDecorView = getWindow().getDecorView();
        IS_APP_RUNNING = true;
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this, R.style.DWProgressDialogTheme);
            // progressDialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
            progressDialog.setOnDismissListener(this);
        }
        if (Constants.getDeviceWidth() == Numerics.ZERO) {
            Util.getDeviceDimensions(BaseActivity.this);
        }
        new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    ViewGroup viewGroup = (ViewGroup) ((ViewGroup) BaseActivity.this.findViewById(android.R.id.content)).getChildAt(0);
                    setupUI(viewGroup);
                }
            }, 1000);
    }

    // This snippet hides the system bars.
    protected void hideSystemUI() {
        // Set the IMMERSIVE flag.
        // Set the content to appear under the system bars so that the content
        // doesn't resize when the system bars hide and show.
        mDecorView.setSystemUiVisibility(
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_FULLSCREEN
            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    /**
     * show the progress dialog with given message
     *
     * @param message      message to dispaly
     * @param isCancelable dismiss flag
     */
    public void showProgressDialog(CharSequence message, boolean isCancelable) {
        if (progressDialog != null && !progressDialog.isShowing()) {
            progressDialog.setCancelable(isCancelable);
            progressDialog.setMessage(message);
            try {
                if (!((Activity) this).isFinishing() && !((Activity) this).isDestroyed()) {
                    progressDialog.show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    /**
     * dismiss  the progress dialog
     */
    public void dismissProgressDialog() {
        try {
            if (progressDialog != null && progressDialog.isShowing() &&
                !((Activity) this).isFinishing() && !((Activity) this).isDestroyed()) {
                progressDialog.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause() start");
        super.onPause();
        IS_APP_RUNNING = false;
        Log.d(TAG, "onPause() IS_APP_RUNNING: " +  IS_APP_RUNNING); 
        Log.d(TAG, "onPause() end"); 
    }
    
    @Override
    protected void onResume() {
        Log.d(TAG, "onResume() start"); 
        Log.d(TAG, "onResume() bef super.onResume()"); 
        super.onResume();
        Log.d(TAG, "onResume() aft super.onResume()"); 
        IS_APP_RUNNING = true;
        hideSystemUI();
        Log.d(TAG, "onResume() end"); 
    }

    /**
     * Set up touch listener for non-text box views to hide keyboard.
     *
     * @param view view
     */
    private void setupUI(View view) {
        //Log.d(TAG, "setupUI() start"); 
        if ((view instanceof ViewGroup) && !(view instanceof AdapterView)) {
            view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        hideSoftKeyboard(BaseActivity.this);
                    }
                });
        } else {
            //Log.d(TAG, "setupUI() end (from else)"); 
            return;
        }
        //If a layout container, iterate over children and seed recursion.
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View innerView = ((ViewGroup) view).getChildAt(i);
                setupUI(innerView);
            }
        }
        //Log.d(TAG, "setupUI() end"); 
    }

    /**
     * Hide the soft key board if it is showing
     *
     * @param activity activity
     */
    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
            (InputMethodManager) activity.getSystemService(
                Activity.INPUT_METHOD_SERVICE);
        if (activity.getCurrentFocus() != null) {
            inputMethodManager.hideSoftInputFromWindow(
                activity.getCurrentFocus().getWindowToken(), 0);
        }
    }

    /**
     * this used to apply the custom font over the app from assets
     * for this used calligraphy
     *
     * @param newBase context
     */
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    /**
     * Call Back method  to get the Message form other Activity
     *
     * @param requestCode request code
     * @param resultCode  result code
     * @param data        intent data from form other Activity
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Constants.RESULT_FINISH_APP || resultCode == Constants.RESULT_LOGOUT) {
            setResult(resultCode, data);
            finish();
        } else if (resultCode == Constants.RESULT_BACK_TO_GAME) {
            setResult(resultCode, data);
            finish();
        }
    }
    @Override
    public void onBackPressed() {
    }
    @Override
    public void onDismiss(DialogInterface dialogInterface) {
        hideSystemUI();
    }
    @Override
    public void didFinishSdkUserConfiguration() {
    }
    protected void didfinishSDKscreenflow() {
    }
}