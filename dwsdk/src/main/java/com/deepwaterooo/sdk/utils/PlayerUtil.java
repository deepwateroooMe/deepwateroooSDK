package com.deepwaterooo.sdk.utils;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.deepwaterooo.sdk.R;
import com.deepwaterooo.sdk.activities.DWAllSetForGameActivity;
import com.deepwaterooo.sdk.activities.DWBaseActivity;
import com.deepwaterooo.sdk.activities.DWSplashScreenActivity;
import com.deepwaterooo.sdk.activities.authentication.DWLoginActivity;
import com.deepwaterooo.sdk.activities.authentication.DWParentalCheckActivity;
import com.deepwaterooo.sdk.appconfig.Constants;
import com.deepwaterooo.sdk.appconfig.Numerics;
import com.deepwaterooo.sdk.beans.ParentInfoDO;
import com.deepwaterooo.sdk.beans.PlayerDO;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

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
// 改写成给游戏评分呀
    public static void startPlaygroundActivity(DWAllSetForGameActivity dwAllSetForGameActivity) {
    }

    /**
     * Used to logout the user
     *
     * @param activity activity
     */
    public static void logoutUser(DWBaseActivity activity) {
        // 把这里的桥折了,直接调用网络请求        
        // startParentalCheckActivity(activity, Constants.REQUEST_CODE_PARENTAL_CHECK);
        // 这里就是直接换成是网络请求登出用户呀
    }

    /**
     * call the splash activity
     *
     * @param activity activity
     */
// 这里好像是游戏端吊起Splash,然后吊起了安卓SDK的整个流程
    public static void startSplashScreenActivity(Activity activity) {
        Log.d(TAG, "startSplashScreenActivity() ");
        Intent intent = new Intent(activity, DWSplashScreenActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        activity.startActivityForResult(intent, Numerics.ZERO);
    }

    /**
     * This method is to get the selected player information
     *
     * @param activity - Context of the Activity
     * @return - Player information object
     */
    public static PlayerDO getSelectedPlayer(Activity activity) {
        SharedPrefUtil sharedPrefUtil = new SharedPrefUtil(activity);
        Gson gson = new Gson();
        String json = sharedPrefUtil.getString(SharedPrefUtil.PREF_SELECTED_PLAYER);
        PlayerDO obj = null;
        if (!TextUtils.isEmpty(json)) {
            obj = gson.fromJson(json, PlayerDO.class);
            if (obj.getProfileURL() == null) {
                obj.setProfileURL("");
            }
        }
        return obj;
    }

    /**
     * This method is to get the Logged-in parent/user information
     *
     * @param activity - Context of the Activity
     * @return - Parent/User information object
     */
    public static ParentInfoDO getParentInfo(Activity activity) {
        SharedPrefUtil sharedPrefUtil = new SharedPrefUtil(activity);
        ParentInfoDO obj = null;

        if (sharedPrefUtil.getBoolean(SharedPrefUtil.PREF_LOGIN_USER_STATUS)) {
            Gson gson = new Gson();
            String json = sharedPrefUtil.getString(SharedPrefUtil.PREF_LOGIN_USER_INFO);
            if (!TextUtils.isEmpty(json)) {
                obj = gson.fromJson(json, ParentInfoDO.class);
            }
        }
        return obj;
    }

    /**
     * This method to get the player profile image bitmap using profile URL
     *
     * @param bluetoothBaseActivity - Context of the activity
     * @param profileURL            - profile image url to display
     * @return Bitmap -  profile image bitmap
     */
    static Bitmap profileBitmap = null;
    public static void getProfileImageURLString(final DWBaseActivity bluetoothBaseActivity, final PlayerImageListener listener,
                                                String profileURL) {
        profileBitmap = null;
//        bluetoothBaseActivity.showProgressDialog(null);
        if (TextUtils.isEmpty(profileURL)) {
//            bluetoothBaseActivity.dismissProgressDialog();
            profileBitmap = BitmapFactory.decodeResource(bluetoothBaseActivity.getResources(), R.drawable.default_avatar);
            if (listener != null) {
                listener.onImageLoadSuccess(profileBitmap);
            }

            return;
        }
        Picasso.with(bluetoothBaseActivity).load(profileURL).into(new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
//                bluetoothBaseActivity.dismissProgressDialog();
                    profileBitmap = bitmap;
                    if (listener != null) {
                        listener.onImageLoadSuccess(profileBitmap);
                    }
                }

                @Override
                public void onBitmapFailed(Drawable errorDrawable) {
//                bluetoothBaseActivity.dismissProgressDialog();
                    profileBitmap = BitmapFactory.decodeResource(bluetoothBaseActivity.getResources(), R.drawable.default_avatar);
                    if (listener != null) {
                        listener.onImageLoadSuccess(profileBitmap);
                    }
                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {
//                context.dismissProgressDialog();
//                profileBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.default_avatar);
                }
            });

    }

    /**
     * This method is to save the selected player details.
     * 只在启蒙模式下,可能需要父母监护的情况下使用,默认为登录的唯一用户
     * @param activity - Context of the Activity
     * @param playerDO - Selected player information object
     */
    public static void setSelectedPlayer(Activity activity, PlayerDO playerDO) {
        SharedPrefUtil sharedPrefUtil = new SharedPrefUtil(activity);
        if (playerDO != null) {
            sharedPrefUtil = new SharedPrefUtil(activity);
            Gson gson = new Gson();
            String json = gson.toJson(playerDO);
            sharedPrefUtil.setString(SharedPrefUtil.PREF_SELECTED_PLAYER, json);
        } else {
            sharedPrefUtil.deleteFromDW(SharedPrefUtil.PREF_SELECTED_PLAYER);
        }
    }

    public static void logoutParent(DWBaseActivity activity) {
        SharedPrefUtil sharedPrefUtil = new SharedPrefUtil(activity);
        sharedPrefUtil.deleteFromDW(SharedPrefUtil.PREF_LOGIN_USER_STATUS);
        sharedPrefUtil.deleteFromDW(SharedPrefUtil.PREF_LOGIN_USER_ID);
        sharedPrefUtil.deleteFromDW(SharedPrefUtil.PREF_LOGIN_USER_TOKEN);
        PlayerUtil.setSelectedPlayer(activity, null);
//        if(BluetoothUtil.isPlaysetConnected()){
//            BluetoothUtil.disconnectPlayset(Numerics.ZERO);
//        }
        Intent intent = new Intent(activity, DWLoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        activity.startActivityForResult(intent, Numerics.ZERO);
        activity.setResult(Constants.RESULT_LOGOUT);
        DWLoginActivity.setListener(activity);
    }

    /**
     * call the parental check  activity
     *
     * @param activity    activity
     * @param requestCode request code
     */
    public static void startParentalCheckActivity(Activity activity, int requestCode) {
        Intent intent = new Intent(activity, DWParentalCheckActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        activity.startActivityForResult(intent, requestCode);
    }

    /**
     * This method loads the playground help url
     *
     * @param activity activity
     */
    public static void startHelpActivity(Activity activity) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(Constants.HELP_URL));
        //activity.startActivityForResult(browserIntent, Numerics.ZERO);
        activity.startActivity(browserIntent);
    }
}