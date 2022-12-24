package com.deepwaterooo.sdk.activities.authentication;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.deepwaterooo.sdk.R;
import com.deepwaterooo.sdk.activities.BaseActivity;
import com.deepwaterooo.sdk.activities.DWHaveAccountActivity;
import com.deepwaterooo.sdk.appconfig.Constants;
import com.deepwaterooo.sdk.appconfig.Numerics;
import com.deepwaterooo.sdk.utils.SharedPrefUtil;

/**
 * Activity used for User, before starting the game
 */
// 加这个类,只是用来帮助自己debug的,
public class DWBeforeStartActivity extends BaseActivity implements OnClickListener {

    private Button btnYes;
    private Button btnWhatPlayset;
    private SharedPrefUtil sharedPrefUtil;
    private boolean isClicked = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_before_start);
        intUI();
        sharedPrefUtil = new SharedPrefUtil(this);
        sharedPrefUtil.setBoolean(SharedPrefUtil.PREF_BATTER_SAVER, true);
    }


    /**
     * initialising the views used in this activity
     */
    private void intUI() {

        btnYes = (Button) findViewById(R.id.btnYes);
        btnWhatPlayset = (Button) findViewById(R.id.btnWhatPlayset);

        btnYes.setOnClickListener(this);
        btnWhatPlayset.setOnClickListener(this);
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
            sharedPrefUtil.setBoolean(SharedPrefUtil.PREF_HAVE_PLAYSET, true);
            Intent intent = new Intent(DWBeforeStartActivity.this, DWHaveAccountActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivityForResult(intent, Numerics.ZERO);
//            finish();
        } else if (v.getId() == R.id.btnWhatPlayset) {

            Intent intent = new Intent(DWBeforeStartActivity.this, DWParentalCheckActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivityForResult(intent, Numerics.ZERO);
        }
    }

    /**
     * device back navigation control
     */
    @Override
    public void onBackPressed() {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Constants.RESULT_PARENTAL_CHECK_SUCCESS) {
//            PlayerUtil.startPlaygroundActivity(this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        isClicked = false;
    }
}