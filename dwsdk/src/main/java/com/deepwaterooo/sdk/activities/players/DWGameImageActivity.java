package com.deepwaterooo.sdk.activities.players;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.deepwaterooo.sdk.R;
import com.deepwaterooo.sdk.activities.BaseActivity;
import com.deepwaterooo.sdk.appconfig.Constants;
import com.deepwaterooo.sdk.appconfig.Numerics;

public class DWGameImageActivity extends BaseActivity {

    private boolean isAcitivityOn = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_image);

        new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    if (isAcitivityOn) {
                        Intent intent = new Intent(DWGameImageActivity.this, DWManagePlayerActivity.class);
                        intent.putExtra(Constants.EXTRA_SELECT_PLAYER, true);
                        startActivityForResult(intent, Numerics.ZERO);
                    }
                }
            }, 3000);
    }

    @Override
    protected void onPause() {
        super.onPause();
        isAcitivityOn = false;
    }
}