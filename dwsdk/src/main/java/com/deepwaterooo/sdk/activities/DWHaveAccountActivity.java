package com.deepwaterooo.sdk.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.deepwaterooo.sdk.R;
import com.deepwaterooo.sdk.activities.authentication.DWLoginActivity;
import com.deepwaterooo.sdk.appconfig.Constants;
import com.deepwaterooo.sdk.appconfig.Numerics;
import com.deepwaterooo.sdk.utils.SharedPrefUtil;

/**
 * Activity used for User navigation for Login or Signup
 */
public class DWHaveAccountActivity extends BaseActivity implements View.OnClickListener {
    private final String TAG = "DWHaveAccountActivity";
    private Button btnYes;
    private Button btnNotYetUser;
    private SharedPrefUtil sharedPrefUtil;
    private boolean isClicked = false;

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause() ");
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy() ");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate() ");
        setContentView(R.layout.activity_have_account);

        // NetworkUtil.callGetAppUpdate(this); // 我不认为我的SDK需要检查应用的更新,跳过,把这里面的逻辑检查一遍
        intUI();
        sharedPrefUtil = new SharedPrefUtil(this);
    }

    /**
     * initialising the views used in this activity
     */
    private void intUI() {
        btnYes = (Button) findViewById(R.id.btnYes);
        btnNotYetUser = (Button) findViewById(R.id.btnNotYetUser);

        btnYes.setOnClickListener(this);
        btnNotYetUser.setOnClickListener(this);
        btnYes.setMinimumWidth((int) (Constants.getDeviceWidth() * 0.20));
        btnNotYetUser.setMinimumWidth((int) (Constants.getDeviceWidth() * 0.20));
    }

    /**
     * Listener for activity UI fields click events
     *
     * @param v return clicked view
     */
    @Override
    public void onClick(View v) {
        if (isClicked) {
            return;
        }
        isClicked = true;
        if (v.getId() == R.id.btnYes) {
            Log.d(TAG, "onClick() Yes");
            Intent intent = new Intent(DWHaveAccountActivity.this, DWLoginActivity.class);

            Log.d(TAG, "onClick() (sharedPrefUtil == null): " + (sharedPrefUtil == null));
            sharedPrefUtil.setBoolean(SharedPrefUtil.PREF_DO_YOU_HAVE_ACC, true); 
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivityForResult(intent, Numerics.ZERO);
// 感觉这里极其诡异,还没有想明白为什么?左边点了就空指针异常,像是ActivityManagerService之类的找不到什么东西,右边就狠正常            
        } else if (v.getId() == R.id.btnNotYetUser) {
            Log.d(TAG, "onClick() No");
            // Intent intent = new Intent(DWHaveAccountActivity.this, DWSignUpActivity.class);
            // sharedPrefUtil.setBoolean(SharedPrefUtil.PREF_DO_YOU_HAVE_ACC, true);
            // intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            // startActivityForResult(intent, Numerics.ZERO);
            // 它说这里,叫老师来,或是叫家长来
            callTeacherAlert(false); // 应该就是调用基类的方法了
        }
        isClicked = false;
    }

    /**
     * device back navigation control
     */
    @Override
    public void onBackPressed() {
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume() ");
        isClicked = false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void didFinishSdkUserConfiguration() {

    }
}