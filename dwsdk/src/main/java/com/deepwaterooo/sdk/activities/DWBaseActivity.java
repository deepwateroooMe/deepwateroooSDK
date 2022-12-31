package com.deepwaterooo.sdk.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;

import androidx.appcompat.app.AppCompatActivity;

import com.deepwaterooo.sdk.R;
import com.deepwaterooo.sdk.activities.authentication.DWLoginActivity;
import com.deepwaterooo.sdk.activities.players.DWManagePlayerActivity;
import com.deepwaterooo.sdk.appconfig.Constants;
import com.deepwaterooo.sdk.appconfig.Numerics;
import com.deepwaterooo.sdk.beans.PlayerDO;
import com.deepwaterooo.sdk.utils.LocaleHelper;
import com.deepwaterooo.sdk.utils.LoginListener;
import com.deepwaterooo.sdk.utils.PlayerUtil;
import com.deepwaterooo.sdk.utils.SharedPrefUtil;
import com.deepwaterooo.sdk.utils.Util;

// 这里主要是提供DWUnityActivity与安卓SDK交互上下文
public abstract class DWBaseActivity extends AppCompatActivity
    implements LoginListener,
    DialogInterface.OnDismissListener {
// Bluetooth: 因为公司是卖蓝牙产品的儿童玩具,所以涉入公司玩具产品蓝牙硬件与手机平板等安卓系统上的蓝牙连接,作为手机平板上游戏应用开玩之前的前提准备
// 所以关于蓝牙: 初始化,连接,连接状态监听等,产品与手机平板的连接连接状态等等,可以全部滤掉
    private final String TAG = "DWBaseActivity";

    private ProgressDialog progressDialog;
    private SharedPrefUtil sharedPrefUtil;
    private View mDecorView;
    private boolean isScreenLocked = true;

    public static final int PERMISSION_CALLBACK_CONSTANT = 100;
    public static final int REQUEST_PERMISSION_SETTING = 101;

    public static Context mAppContext;
    
    private Handler onPauseHandler;
    Runnable _idleRunnable = new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "run() _idleRunnable");
                 if (!Util.IS_APP_RUNNING && !BaseActivity.IS_APP_RUNNING) {
                    PlayerUtil.setSelectedPlayer(DWBaseActivity.this, null);
                    try {
// 这里就是从安卓SDK中启动游戏activity的地方: res/values/String.xml game_activity                        
                        Class classs = Class.forName(getString(R.string.game_activity)); // <<<<<<<<<<<<<<<<<<<< 
                        Log.d(TAG, "(!(DWBaseActivity.this).getClass().equals(classs)): " + (!(DWBaseActivity.this).getClass().equals(classs)));
                        if (!(DWBaseActivity.this).getClass().equals(classs)) { // 如果当前活动不是游戏里的活动,结束
                            Log.d(TAG, "run() Constants.RESULT_FINISH_APP");
                            setResult(Constants.RESULT_FINISH_APP);
                            finish();
                        }
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
        };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate() ");
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        mDecorView = getWindow().getDecorView();
        hideSystemUI();
        Util.IS_APP_RUNNING = true;
        // getApplicationContext(): DWUnityActivity中应该是可以拿到这个上下文的,把这个上下文作为安卓SDK的上下文 ?
        mAppContext = getApplicationContext();
//        bluetoothStateReceiver = new BluetoothStateReceiver();
        // if (!BluetoothUtil.isInitialized()) 
        //     BluetoothUtil.initialize(getApplicationContext()); // <<<<<<<<<<<<<<<<<<<< 这里会拿到应用层级的上下文,返回的是 ==> ContextWrapper extends Context
//        BluetoothUtil.setListener(this);
        sharedPrefUtil = new SharedPrefUtil(this);
        Log.d(TAG, "onCreate() (progressDialog == null): " + (progressDialog == null));
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this, R.style.DWProgressDialogTheme);
            progressDialog.setOnDismissListener(this);
        }
        Log.d(TAG, "(Constants.getDeviceWidth() == Numerics.ZERO): " + (Constants.getDeviceWidth() == Numerics.ZERO));
        if (Constants.getDeviceWidth() == Numerics.ZERO) 
            Util.getDeviceDimensions(DWBaseActivity.this);
        new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    ViewGroup viewGroup = (ViewGroup) ((ViewGroup) DWBaseActivity.this
                                                       .findViewById(android.R.id.content)).getChildAt(0);
                    setupUI(viewGroup);
                }
            }, 1000);
//        hideSystemUI();
    }

    /**
     * This snippet hides the system bars.
     */
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
     * Set up touch listener for non-text box views to hide keyboard.
     *
     * @param view view
     */

    private void setupUI(View view) {
        if ((view instanceof ViewGroup) && !(view instanceof AdapterView)) {

            if (view.getId() == R.id.llTakePicture || view.getId() == R.id.llFromLibrary || view.getId() == R.id.llChooseAvatar) {
                return;
            }
            view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        BaseActivity.hideSoftKeyboard(DWBaseActivity.this);
                    }
                });
        } else {
            return;
        }
        //If a layout container, iterate over children and seed recursion.
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View innerView = ((ViewGroup) view).getChildAt(i);
                setupUI(innerView);
            }
        }
    }

    /**
     * show the progress dialog with given message
     *
     * @param msg message we need to display
     */
    public void showProgressDialog(String msg) {
        if (progressDialog != null && !progressDialog.isShowing()) {
            if (msg != null)
                progressDialog.setMessage(msg);
            progressDialog.setCancelable(false);
            try {
                if (!((Activity) this).isFinishing() && !((Activity) this).isDestroyed() && !progressDialog.isShowing()) {
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
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        hideSystemUI();

    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause() ");
        Util.IS_APP_RUNNING = false;
//        unregisterReceiver(bluetoothStateReceiver);
        if (onPauseHandler != null) {
            onPauseHandler.removeCallbacks(_idleRunnable);
            onPauseHandler = null;
        }
        // Sometimes removeCallbacks is not working properly so we are initializing with null and create new Handler.
        onPauseHandler = new Handler();
        if (onPauseHandler != null) {
            onPauseHandler.postDelayed(_idleRunnable, Numerics.ONE * Numerics.THOUSAND); // 1000 
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume() ");
       LocaleHelper.setLocale(this, LocaleHelper.getLanguage(this));
       // IntentFilter bluetooth = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
       // registerReceiver(bluetoothStateReceiver, bluetooth);
       // BluetoothUtil.setListener(this);
        Util.IS_APP_RUNNING = true;
        try {
            Class classs = Class.forName(getString(R.string.game_activity));
            Log.d(TAG, "(((DWBaseActivity) this).getClass().equals(classs)): " + (((DWBaseActivity) this).getClass().equals(classs)));
// 任何时候,是这个基类的子类,在恢复的时候,都重新设置 登录 和 管理当前用户的回调            
            if (((DWBaseActivity) this).getClass().equals(classs)) {
                DWLoginActivity.setListener(this);
                DWManagePlayerActivity.setListener((LoginListener)this); // 对当前玩家的管理: 主要是想要监听这里的登录状态,谁是当前玩家等.观察者模式
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        hideSystemUI();
        isScreenLocked = true;
        if (getIntent().hasExtra(Constants.EXTRA_FROM_MENU)) 
            didNavigatesToMainMenu();
    }

    /**
     * this method used to show alert for battery level
     * 这里监听的是公司产品玩具的电池电量状态,而我需要监听的是手机的电量状态,我可以不必监听手机电量状态,但对玩家友好,必要情况下,游戏过程中的电量提醒是可以有的
     * @param level battery level
     */
// 方法是定义在某个接口中的,我需要把哪些方法提取到哪个接口会比较好呢?
    public void bleBatteryLevel(String level) { }
    public void availableServices() { }

    /**
     * this used to apply the custom font over the app from assets
     * for this used calligraphy
     *
     * @param newBase context
     */
    @Override
    protected void attachBaseContext(Context newBase) {
//Implement this for api 28 and below
//        if (VERSION.SDK_INT < VERSION_CODES.O) { // originally Q
//            super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
//        } else { // Or implement this for api 29 and above
            super.attachBaseContext(newBase);
//        }
    }

    /**
     * Call Back method  to get the Message form other Activity
     *
     * @param requestCode request code
     * @param resultCode  result code
     * @param data        intent data from form other Activity
     */
// 这里是对活动的分类导向:　可以跳至游戏端,可以安卓间活动的跳转等  
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult() ");
        super.onActivityResult(requestCode, resultCode, data);
        if (onPauseHandler != null) {
            onPauseHandler.removeCallbacks(_idleRunnable);
            onPauseHandler = null;
        }
        Log.d(TAG, "(resultCode == Constants.RESULT_FINISH_APP): " + (resultCode == Constants.RESULT_FINISH_APP));
        Log.d(TAG, "(resultCode == Constants.RESULT_BACK_TO_GAME): " + (resultCode == Constants.RESULT_BACK_TO_GAME));
        if (resultCode == Constants.RESULT_FINISH_APP) { // <<<<<<<<<<<<<<<<<<<< 
            try {
                Class classs = Class.forName(getString(R.string.game_activity));
                if (!((DWBaseActivity) this).getClass().equals(classs)) {
                    setResult(resultCode, data);
                    finish();
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

        } else if (resultCode == Constants.RESULT_LOGOUT) { // <<<<<<<<<<<<<<<<<<<< 
            try {
                Class classs = Class.forName(getString(R.string.game_activity));
                if (!((DWBaseActivity) this).getClass().equals(classs)) {
                    setResult(resultCode, data);
                    finish();
                }
//                else if (resultCode == Constants.RESULT_PLAYSET_DISCONNECTED && ((DWBaseActivity) this).getClass().equals(classs)) {
//                    PlayerUtil.startPlaysetScanActivity(this);
//                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        } else if (requestCode == Constants.REQUEST_CODE_PARENTAL_CHECK // <<<<<<<<<<<<<<<<<<<< 
                   && resultCode == Constants.RESULT_PARENTAL_CHECK_SUCCESS) {
            try {
                Class classs = Class.forName(getString(R.string.game_activity));
                if (((DWBaseActivity) this).getClass().equals(classs)) {
                    onSuccessLogoutEvent(); // 上面两个条件都满足时，就会触发这个回调
                    PlayerUtil.logoutParent(this); // 登出父母
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
// 把下面这里重点弄清楚:　安卓SDK与游戏端的切换            
        } else if (resultCode == Constants.RESULT_BACK_TO_GAME) { // <<<<<<<<<<<<<<<<<<<< 用这个作测试,要求SDK跳回至游戏端
            try {
                Class classs = Class.forName(getString(R.string.game_activity));
                Log.d(TAG, "(!((DWBaseActivity) this).getClass().equals(classs)): " + (!((DWBaseActivity) this).getClass().equals(classs)));
                Log.d(TAG, "(data != null && data.hasExtra(Constants.EXTRA_FROM_MENU)): " + (data != null && data.hasExtra(Constants.EXTRA_FROM_MENU)));
                Log.d(TAG, "(data != null && data.hasExtra(Constants.EXTRA_DATA)): " + (data != null && data.hasExtra(Constants.EXTRA_DATA)));
                if (!((DWBaseActivity) this).getClass().equals(classs)) {
                    setResult(resultCode, data);
                    finish();
// 因为我想要跳过DWLoginActivity的界面,我需要去找SDK中什么地方会导致这个结果,以便连接跳过                    
                } else if (data != null && data.hasExtra(Constants.EXTRA_FROM_MENU)) {
                    didNavigatesToMainMenu();
                } else if (data != null && data.hasExtra(Constants.EXTRA_DATA)) {
                    didfinishSDKscreenflow(); // <<<<<<<<<<<<<<<<<<<< 
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

        } else if (requestCode == Constants.REQUEST_CODE_CHILD_ADD_TEACHER && // <<<<<<<<<<<<<<<<<<<< 
                   resultCode == Constants.RESULT_PARENTAL_CHECK_SUCCESS) {
//            PlayerUtil.loadTeacherPortalUrl(this, true); // 这块儿暂时不管
        }/*else if (resultCode != Constants.RESULT_PARENTAL_CHECK_SUCCESS) {
           try {
           Class classs = Class.forName(getString(R.string.game_activity));
           if (((DWBaseActivity) this).getClass().equals(classs)) {
           didNavigatesToMainMenu();
           }
           } catch (ClassNotFoundException e) {
           e.printStackTrace();
           }
           }*/
    }

    /**
     * This method is to get called when activity control back to main menu from menu related activities
     */
    protected void didNavigatesToMainMenu() {
    }

// LoginListener 两个公用接口方法的定义,供子类DWUnityActivity覆写用来通知 游戏端 玩家选好登录好了    
    /**
     * This method is to get called when user login successful
     */
    public void didFinishSdkUserConfiguration() { // LoginListener
    }
    public void didSelectedChild(PlayerDO playerDO) {  // LoginListener
    }

    /**
     * This method is to get called when game activity launches from timer screen
     */
    // @Override
    public void didfinishSDKscreenflow() { 
    }

    @Override
    public void onBackPressed() {
    }

    protected void onSuccessLogoutEvent() {
    }

    public void didClearTheCharctersOnBoard() {

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

    @Override
    public void onDismiss(DialogInterface dialogInterface) {
        hideSystemUI();
    }

    @Override
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();
        isScreenLocked = false;
    }

    public void callTeacherContinue(View.OnClickListener listener) {

        String msg = sharedPrefUtil.getString(SharedPrefUtil.PREF_UPDATE_MSG);
        msg = msg != null ? msg : getString(R.string.app_update_msg);
//        Util.showAlertTeacherAccount(this, "",
//                                     msg, getString(R.string.click_here_to_continue),
//                                     getString(R.string.Back), null, new View.OnClickListener() {
//                                             @Override
//                                             public void onClick(View v) {
//                                                 ((Dialog) v.getTag()).dismiss();
//                                                 PlayerUtil.startParentalCheckActivity(DWBaseActivity.this, Constants.REQUEST_CODE_CHILD_ADD_TEACHER);
//                                             }
//                                         }, listener);
    }

    public void callZeroPlayerContinue(View.OnClickListener listener, final boolean isTeacher) {

        String msg = isTeacher ? getString(R.string.teacher_zero_player) : getString(R.string.parent_zero_player);
//        Util.showAlertTeacherAccount(this, "",
//                                     msg, isTeacher ? getString(R.string.click_here_to_continue) : getString(R.string.Yes),
//                                     getString(R.string.Back), null, new View.OnClickListener() {
//                                             @Override
//                                             public void onClick(View v) {
//                                                 ((Dialog) v.getTag()).dismiss();
//                                                 if (isTeacher)
//                                                     PlayerUtil.startParentalCheckActivity(DWBaseActivity.this, Constants.REQUEST_CODE_CHILD_ADD_TEACHER);
//                                                 else {
//                                                     PlayerUtil.startManagePlayerActivity(DWBaseActivity.this, Numerics.ZERO);
//                                                 }
//                                             }
//                                         }, listener);
    }

    public void showPermissionInfo(final Activity activity, final String[] permissions,
                                   final int[] grantResults, final boolean toSettings) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        StringBuilder sbPermissions = new StringBuilder();
        for (int i = 0; i < grantResults.length && i < permissions.length; i++) {
            if (grantResults[i] == -1) {
                if(permissions[i].contains("LOCATION") && !sbPermissions.toString().contains("LOCATION")){
                    sbPermissions.append("LOCATION,");
                }else if(permissions[i].contains("STORAGE")&&!sbPermissions.toString().contains("STORAGE")){
                    sbPermissions.append("STORAGE,");
                }else if(permissions[i].contains("CAMERA")&&!sbPermissions.toString().contains("CAMERA")){
                    sbPermissions.append("CAMERA,");
                }else{
                    sbPermissions.append("");
                }
            }
        }
        builder.setTitle(getString(R.string.NeedPermission));
        builder.setMessage(getString(R.string.PermissionStart)+" "+sbPermissions.toString().replaceAll(",$", "")+" "+getString(R.string.PermissionEnd));
        builder.setPositiveButton(toSettings ? getString(R.string.GoSettings):
                                  getString(R.string.Grant), new DialogInterface.OnClickListener() {
                                          @Override
                                          public void onClick(DialogInterface dialog, int which) {
                                              dialog.cancel();
                                              if (toSettings) { // 去settings 里面配置
                                                  Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                                  Uri uri = Uri.fromParts("package", getPackageName(), null);
                                                  intent.setData(uri);
                                                  Util.keepAppAlive();
                                                  activity.startActivityForResult(intent, REQUEST_PERMISSION_SETTING);
                                              } else { // 晚点儿处理,是这意思吧,暂时跳过
                                                  Util.keepAppAlive();
                                                  // ActivityCompat.requestPermissions(activity, permissions, PERMISSION_CALLBACK_CONSTANT);
                                              }
                                          }
                                      });
        builder.setNegativeButton(getString(R.string.Cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
        builder.show();
    }
}
