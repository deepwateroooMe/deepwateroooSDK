package com.deepwaterooo.sdk.activities;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;

import androidx.appcompat.app.AppCompatActivity;

import com.deepwaterooo.sdk.R;
import com.deepwaterooo.sdk.activities.authentication.DWSignUpActivity;
import com.deepwaterooo.sdk.appconfig.Constants;
import com.deepwaterooo.sdk.appconfig.Numerics;
import com.deepwaterooo.sdk.utils.LocaleHelper;
import com.deepwaterooo.sdk.utils.PlayerUtil;
import com.deepwaterooo.sdk.utils.SharedPrefUtil;
import com.deepwaterooo.sdk.utils.Util;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;

/**
 * Activity used for base class for all activities
 */
// ACTIVITY基类: 就是把一些必要的会话框及其相关的回调都包装在基类里了.把这个作为安卓SDK端所有活动的基类
// 那么就还需要一个DWBaseActivity的基类,作为DWUnityActivity的基类    
public class BaseActivity extends AppCompatActivity implements DialogInterface.OnDismissListener {
    private final String TAG = "BaseActivity";

    private ProgressDialog progressDialog;
    public static boolean IS_APP_RUNNING;
    private View mDecorView;
    private SharedPrefUtil sharedPrefUtil;

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
        Log.d(TAG, "onResume()"); 
        super.onResume();
        IS_APP_RUNNING = true; 
        hideSystemUI();
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate() ");
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        sharedPrefUtil = new SharedPrefUtil(this); 
        super.onCreate(savedInstanceState);
       LocaleHelper.setLocale(this, LocaleHelper.getLanguage(this));
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
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
//        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase)); // 以前老旧版本的
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
        Log.d(TAG, "onDismiss() ");
        hideSystemUI();
    }
    public void didFinishSdkUserConfiguration() { }
    protected void didfinishSDKscreenflow() { }
    protected void callTeacherAlert(final boolean isFromLogin) {
        Util.showAlertTeacherAccount(this, getString(R.string.are_you_teacher),
                                     getString(R.string.create_a_teacher), getString(R.string.not_a_teacher),
                                     getString(R.string.Back), new View.OnClickListener() {
                                             @Override
                                             public void onClick(View v) {
                                                 ((Dialog) v.getTag()).dismiss();
                                                 callTeacherContinue();
                                             }
                                         }, new View.OnClickListener() {
                                                 @Override
                                                 public void onClick(View v) {
                                                     ((Dialog) v.getTag()).dismiss();
                                                     Intent intent = new Intent(BaseActivity.this, DWSignUpActivity.class);
                                                     if (isFromLogin) {
                                                         intent.putExtra(Constants.EXTRA_IS_FROM_LOGIN, true);
                                                     } else {
                                                         sharedPrefUtil.setBoolean(SharedPrefUtil.PREF_DO_YOU_HAVE_ACC, true);
                                                     }
                                                     intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                                     startActivityForResult(intent, Numerics.ZERO);
                                                 }
                                             }, null);
    }

    protected void callTeacherContinue() {

        String msg = sharedPrefUtil.getString(SharedPrefUtil.PREF_UPDATE_MSG);
        msg = msg != null ? msg : getString(R.string.app_update_msg);

        Util.showAlertTeacherAccount(this, "",
                                     msg, getString(R.string.click_here_to_continue),
                                     getString(R.string.Back), null, new View.OnClickListener() {
                                             @Override
                                             public void onClick(View v) {
                                                 ((Dialog) v.getTag()).dismiss();
                                                 PlayerUtil.startParentalCheckActivity(BaseActivity.this, 0);
                                             }
                                         }, null);
    }

    protected void callTeacherContinue(View.OnClickListener listener) {

        String msg = sharedPrefUtil.getString(SharedPrefUtil.PREF_UPDATE_MSG);
        msg = msg != null ? msg : getString(R.string.app_update_msg);

        Util.showAlertTeacherAccount(this, "",
                                     msg, getString(R.string.click_here_to_continue),
                                     getString(R.string.Back), null, new View.OnClickListener() {
                                             @Override
                                             public void onClick(View v) {
                                                 ((Dialog) v.getTag()).dismiss();
                                                 PlayerUtil.startParentalCheckActivity(BaseActivity.this, 0);
                                             }
                                         }, listener);
    }
}