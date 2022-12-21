package com.deepwaterooo.sdk.activities;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;

import com.deepwaterooo.sdk.R;
import com.deepwaterooo.sdk.appconfig.Constants;
import com.deepwaterooo.sdk.appconfig.Numerics;
import com.deepwaterooo.sdk.beans.PlayerDO;
import com.deepwaterooo.sdk.utils.LoginListener;
import com.deepwaterooo.sdk.utils.PlayerUtil;
import com.deepwaterooo.sdk.utils.SharedPrefUtil;
import com.deepwaterooo.sdk.utils.Util;

// 这里主要是提供DWUnityActivity与安卓SDK交互上下文,吃完饭把它理清楚
public abstract class DWBaseActivity extends AppCompatActivity
    implements LoginListener,
    DialogInterface.OnDismissListener {

    private ProgressDialog progressDialog;
    private SharedPrefUtil sharedPrefUtil;
    private View mDecorView;
    private static PlaysetConnectionListener playsetConnectionListener;
    private boolean isScreenLocked = true;
    private BluetoothStateReceiver bluetoothStateReceiver;

    public static final int PERMISSION_CALLBACK_CONSTANT = 100;
    public static final int REQUEST_PERMISSION_SETTING = 101;

    private Handler onPauseHandler;
    Runnable _idleRunnable = new Runnable() {
            @Override
            public void run() {
                if (!Util.IS_APP_RUNNING && !BaseActivity.IS_APP_RUNNING &&
                    !BluetoothUtil.isFirmwareUpdateInProgress() && !BluetoothUtil.isBootModeEnabled()) {
                    PlayerUtil.setSelectedPlayer(BluetoothBaseActivity.this, null);
                    if (BluetoothUtil.isPlaysetConnected()) {
                        Util.playSound(BluetoothBaseActivity.this, Constants.AUDIO_DISCONNECT);
                        BluetoothUtil.disconnectPlayset(Numerics.ZERO);
                    }
                    try {
                        Class classs = Class.forName(getString(R.string.game_activity));
                        if (!(BluetoothBaseActivity.this).getClass().equals(classs)) {
                            if (playsetConnectionListener != null) {
                                playsetConnectionListener.gamePaused(isScreenLocked);
                            }
                            setResult(Constants.RESULT_FINISH_APP);
                            finish();
                        } else {
                            if (playsetConnectionListener != null) {
                                playsetConnectionListener.gamePaused(false);
                            }
                        }
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
        };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        mDecorView = getWindow().getDecorView();
        hideSystemUI();
        Util.IS_APP_RUNNING = true;
        bluetoothStateReceiver = new BluetoothStateReceiver();
        if (!BluetoothUtil.isInitialized()) {
            BluetoothUtil.initialize(getApplicationContext());
        }
        BluetoothUtil.setListener(this);
        sharedPrefUtil = new SharedPrefUtil(this);
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this, R.style.SPProgressDialogTheme);
            progressDialog.setOnDismissListener(this);
        }

        if (Constants.getDeviceWidth() == Numerics.ZERO) {
            Util.getDeviceDimensions(BluetoothBaseActivity.this);
        }

        new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    ViewGroup viewGroup = (ViewGroup) ((ViewGroup) BluetoothBaseActivity.this
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
                        BaseActivity.hideSoftKeyboard(BluetoothBaseActivity.this);
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
        Util.IS_APP_RUNNING = false;
        unregisterReceiver(bluetoothStateReceiver);
        if (onPauseHandler != null) {
            onPauseHandler.removeCallbacks(_idleRunnable);
            onPauseHandler = null;
        }
        //Sometimes removeCallbacks is not working properly so we are initializing with null and create new Handler.
        onPauseHandler = new Handler();
        if (onPauseHandler != null) {
            onPauseHandler.postDelayed(_idleRunnable, Numerics.ONE * Numerics.THOUSAND); //1 minute
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocaleHelper.setLocale(this, LocaleHelper.getLanguage(this));
        IntentFilter bluetooth = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(bluetoothStateReceiver, bluetooth);
        BluetoothUtil.setListener(this);
        Util.IS_APP_RUNNING = true;
        try {
            Class classs = Class.forName(getString(R.string.game_activity));
            if (((BluetoothBaseActivity) this).getClass().equals(classs)) {
                SPLoginActivity.setListener(this);
                SPManagePlayerActivity.setListener(this);
                setplaysetConnectionListener(this);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        hideSystemUI();
        isScreenLocked = true;
        if (getIntent().hasExtra(Constants.EXTRA_FROM_MENU)) {
            didNavigatesToMainMenu();
        }
    }

    /**
     * this method used to show alert for battery level
     *
     * @param level battery level
     */
    @Override
    public void bleBatteryLevel(String level) {
        if (playsetConnectionListener != null) {
            playsetConnectionListener.batteryLevel(level);
        }
        int batLevel = Util.parseInt(level);
        if (batLevel < Numerics.TEN) {
            Util.showAlert(this, getString(R.string.Oops), getString(R.string.LowBatteryMsg).replace("X", (batLevel == 0 ? "1" : level)),
                           getString(R.string.Ok), null);
        }
    }

    @Override
    public void bleAvailableServices() {
        if (playsetConnectionListener != null) {
            playsetConnectionListener.availableServices();
        }
    }

    @Override
    public void availableServices() {
    }

    @Override
    public void bleDisconnectedPlayset() {
        if (playsetConnectionListener != null) {
            playsetConnectionListener.disconnectedPlayset();
        }
        if (!BluetoothUtil.getFirmwareUpdateState() && !(this instanceof SPPlaysetScanActivity)) {

            if (Util.IS_APP_RUNNING || BaseActivity.IS_APP_RUNNING) {
                Util.playSound(this, Constants.AUDIO_DISCONNECT);
                try {
                    Class classs = Class.forName(getString(R.string.game_activity));
                    if (!((BluetoothBaseActivity) this).getClass().equals(classs)) {
                        setResult(Constants.RESULT_PLAYSET_DISCONNECTED);
                        finish();
                    } else {
                        PlayerUtil.startPlaysetScanActivity(this);
                    }
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    @Override
    public void blePlaysetModelNumber(String modelNumber) {
        if (playsetConnectionListener != null) {
            playsetConnectionListener.playsetModelNumber(modelNumber);
        }
        sharedPrefUtil.setString(SharedPrefUtil.PREF_PLAYSET_MODEL, modelNumber);
    }

    @Override
    public void blePlaysetManufacturerName(String manufacturerName) {
        if (playsetConnectionListener != null) {
            playsetConnectionListener.playsetManufacturerName(manufacturerName);
        }
        sharedPrefUtil.setString(SharedPrefUtil.PREF_PLAYSET_MANUFACTURER, manufacturerName);
    }

    @Override
    public void blePlaysetHardwareRevision(String hardwareRivision) {
        if (playsetConnectionListener != null) {
            playsetConnectionListener.playsetHardwareRevision(hardwareRivision);
        }
        sharedPrefUtil.setString(SharedPrefUtil.PREF_HARDWARE_RIVISION, hardwareRivision);
    }

    @Override
    public void blePlaysetFirmwareRevision(String rivision) {
        if (playsetConnectionListener != null) {
            playsetConnectionListener.playsetFirmwareRevision(rivision);
        }
        sharedPrefUtil.setString(SharedPrefUtil.PREF_FIRMWARE_RIVISION, rivision);
    }

    @Override
    public void bleLettersFromPlayset(String letters, byte[] hexData) {
        if (playsetConnectionListener != null) {
            playsetConnectionListener.lettersFromPlayset(letters, hexData);
            letters = letters.trim();
            if (letters.length() == Numerics.ZERO) {
                playsetConnectionListener.didClearTheCharctersOnBoard();
            }
        }
    }

    @Override
    public void bleLcdsStates(byte[] status) {
        if (playsetConnectionListener != null) {
            playsetConnectionListener.lcdsStates(status);
        }
    }

    @Override
    public void bleConnectedPlayset() {
        if (playsetConnectionListener != null) {
            playsetConnectionListener.connectedPlayset();
        }
    }

    @Override
    public void bleFirmwareUpdateStatus(int progress) {
        if (playsetConnectionListener != null) {
            playsetConnectionListener.firmwareUpdateStatus(progress);
        }
    }

    @Override
    public void blePlaysetName(String name) {
        if (playsetConnectionListener != null) {
            playsetConnectionListener.playsetName(name);
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
//Implement this for api 28 and below
//        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q){
//            super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
//        } else { // Or implement this for api 29 and above
        super.attachBaseContext(newBase);
//        }
        // super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
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
        if (onPauseHandler != null) {
            onPauseHandler.removeCallbacks(_idleRunnable);
            onPauseHandler = null;
        }
        if (resultCode == Constants.RESULT_FINISH_APP) {
            try {
                Class classs = Class.forName(getString(R.string.game_activity));
                if (!((BluetoothBaseActivity) this).getClass().equals(classs)) {
                    setResult(resultCode, data);
                    finish();
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

        } else if (resultCode == Constants.RESULT_LOGOUT ||
                   resultCode == Constants.RESULT_PLAYSET_DISCONNECTED) {
            try {
                Class classs = Class.forName(getString(R.string.game_activity));
                if (!((BluetoothBaseActivity) this).getClass().equals(classs)) {
                    setResult(resultCode, data);
                    finish();
                } else if (resultCode == Constants.RESULT_PLAYSET_DISCONNECTED && ((BluetoothBaseActivity) this).getClass().equals(classs)) {
                    PlayerUtil.startPlaysetScanActivity(this);
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        } else if (requestCode == Constants.REQUEST_CODE_PARENTAL_CHECK // 去找这两个码
                   && resultCode == Constants.RESULT_PARENTAL_CHECK_SUCCESS) {
            try {
                Class classs = Class.forName(getString(R.string.game_activity));
                if (((BluetoothBaseActivity) this).getClass().equals(classs)) {
                    onSuccessLogoutEvent(); // 上面两个条件都满足时，就会触发这个回调
                    PlayerUtil.logoutParent(this); // 登出父母
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        } else if (resultCode == Constants.RESULT_BACK_TO_GAME) {

            try {
                Class classs = Class.forName(getString(R.string.game_activity));
                if (!((BluetoothBaseActivity) this).getClass().equals(classs)) {
                    setResult(resultCode, data);
                    finish();
                } else if (data != null && data.hasExtra(Constants.EXTRA_FROM_MENU)) {
                    didNavigatesToMainMenu();
                } else if (data != null && data.hasExtra(Constants.EXTRA_DATA)) {
                    didfinishSDKscreenflow();
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

        } else if (requestCode == Constants.REQUEST_CODE_CHILD_ADD_TEACHER &&
                   resultCode == Constants.RESULT_PARENTAL_CHECK_SUCCESS) {
            PlayerUtil.loadTeacherPortalUrl(this, true);
        }/*else if (resultCode != Constants.RESULT_PARENTAL_CHECK_SUCCESS) {
           try {
           Class classs = Class.forName(getString(R.string.game_activity));
           if (((BluetoothBaseActivity) this).getClass().equals(classs)) {
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

    /**
     * This method is to get called when user login successful
     */
    public void didFinishSdkUserConfiguration() {
    }

    /**
     * This method is to get called when game activity launches from timer screen
     */
    public void didfinishSDKscreenflow() {
    }

    @Override
    public void onBackPressed() {
    }

    @Override
    public void didSelectedChild(PlayerDO playerDO) {
    }

    protected void onSuccessLogoutEvent() {
    }

    @Override
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

    public static void setplaysetConnectionListener(PlaysetConnectionListener playsetConnectionListener) {
        BluetoothBaseActivity.playsetConnectionListener = playsetConnectionListener;
    }

    @Override
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();
        isScreenLocked = false;
    }

    public void callTeacherContinue(View.OnClickListener listener) {

        String msg = sharedPrefUtil.getString(SharedPrefUtil.PREF_UPDATE_MSG);
        msg = msg != null ? msg : getString(R.string.app_update_msg);

        Util.showAlertTeacherAccount(this, "",
                                     msg, getString(R.string.click_here_to_continue),
                                     getString(R.string.Back), null, new View.OnClickListener() {
                                             @Override
                                             public void onClick(View v) {
                                                 ((Dialog) v.getTag()).dismiss();
                                                 PlayerUtil.startParentalCheckActivity(BluetoothBaseActivity.this, Constants.REQUEST_CODE_CHILD_ADD_TEACHER);
                                             }
                                         }, listener);
    }

    public void callZeroPlayerContinue(View.OnClickListener listener, final boolean isTeacher) {

        String msg = isTeacher ? getString(R.string.teacher_zero_player) : getString(R.string.parent_zero_player);


        Util.showAlertTeacherAccount(this, "",
                                     msg, isTeacher ? getString(R.string.click_here_to_continue) : getString(R.string.Yes),
                                     getString(R.string.Back), null, new View.OnClickListener() {
                                             @Override
                                             public void onClick(View v) {
                                                 ((Dialog) v.getTag()).dismiss();
                                                 if (isTeacher)
                                                     PlayerUtil.startParentalCheckActivity(BluetoothBaseActivity.this, Constants.REQUEST_CODE_CHILD_ADD_TEACHER);
                                                 else {
                                                     PlayerUtil.startManagePlayerActivity(BluetoothBaseActivity.this, Numerics.ZERO);
                                                 }
                                             }
                                         }, listener);
    }

    public class BluetoothStateReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(final Context context, Intent intent) {

            if (intent.getAction().equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,
                                                     BluetoothAdapter.ERROR);
                switch (state) {
                case BluetoothAdapter.STATE_OFF:
                    new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (!Util.isBtEnableActivityShowing()) {
                                    Intent intent1 = new Intent(context, SPEnableBluetoothActivity.class);
                                    intent1.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);

                                    if(BluetoothUtil.isPlaysetConnected()) {
                                        BluetoothUtil.disconnectPlayset(0l);
                                    }else {
                                        ((Activity) context).startActivityForResult(intent1, Numerics.ZERO);
                                    }
                                }
                            }
                        }, Numerics.FIVE * Numerics.HUNDRED);
                    break;
                case BluetoothAdapter.STATE_ON:
                    Util.keepAppAlive();
                    break;
                }
            }
        }
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
