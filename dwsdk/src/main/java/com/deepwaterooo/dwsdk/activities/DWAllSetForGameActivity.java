package com.deepwaterooo.dwsdk.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.deepwaterooo.dwsdk.R;
import com.deepwaterooo.dwsdk.appconfig.Constants;
import com.deepwaterooo.dwsdk.appconfig.Numerics;
import com.deepwaterooo.dwsdk.utils.PlayerUtil;

/**
 * Activity used for set before enter to the game
 */
// 忘记细节了:这里好像是说,设置了两个不透明隐藏的按钮,用来模拟调用返回SDK或是进入游戏 ??? 再检查一下
public class DWAllSetForGameActivity extends BaseActivity implements View.OnClickListener {

    private Button btnTakeToPlayGround;
    private Button btnTakeToGame;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_set_game);
        intUI();
    }

    /**
     * initialising the views used in this activity
     */
    private void intUI() {

        btnTakeToPlayGround = (Button) findViewById(R.id.btnTakeToPlayGround);
        btnTakeToGame = (Button) findViewById(R.id.btnTakeToGame);

        btnTakeToPlayGround.setMinimumWidth((int) (Constants.getDeviceWidth() * 0.35));
        btnTakeToGame.setMinimumWidth((int) (Constants.getDeviceWidth() * 0.35));


        btnTakeToPlayGround.setOnClickListener(this);
        btnTakeToGame.setOnClickListener(this);
    }

    /**
     * Listener for activity UI fields click events
     *
     * @param v return clicked view
     */
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnTakeToPlayGround) {
            btnTakeToPlayGround.setClickable(false);
//            PlayerUtil.startParentalCheckActivity(this, Numerics.ZERO);

        } else if (v.getId() == R.id.btnTakeToGame) {
            Intent intent = new Intent(DWAllSetForGameActivity.this, DWGameImageActivity.class);
            intent.putExtra(Constants.EXTRA_SELECT_PLAYER, true);
            startActivityForResult(intent, Numerics.ZERO);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Constants.RESULT_PARENTAL_CHECK_SUCCESS) {
            PlayerUtil.startPlaygroundActivity(this);
        }
        btnTakeToPlayGround.setClickable(true);
    }

    @Override
    public void didFinishSdkUserConfiguration() {

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }
}
