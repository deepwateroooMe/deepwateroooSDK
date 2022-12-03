package com.deepwaterooo.dwsdk.utils;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.SyncStateContract;

import com.deepwaterooo.dwsdk.R;

/**
 * Utility class for player activities
 */
public class PlayerUtil {
    private static final String TAG = "PlayerUtil";

    /**
     * start/call the game activity
     * 当SDK准备好,是否需要调用游戏活动 ?
     * @param activity    activity
     * @param bundle      intent data bundle
     * @param requestCode requset code
     */
    public static void startGameActivity(Activity activity, Bundle bundle, int requestCode) {
        try {
            Class classs = Class.forName(activity.getString(R.string.game_activity));
            Intent intent = new Intent(activity, classs);
            if (bundle != null) {
                intent.putExtra(Constants.EXTRA_DATA, bundle);
            }
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            activity.startActivityForResult(intent, requestCode);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
