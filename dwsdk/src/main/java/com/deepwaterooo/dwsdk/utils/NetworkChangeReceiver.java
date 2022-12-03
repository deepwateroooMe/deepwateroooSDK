package com.deepwaterooo.dwsdk.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.deepwaterooo.dwsdk.networklayer.NetworkUtil;

// 这里比较犄角旮旯: [在用户想要登录或是登出时,]如果网络出异常(应该常备状态回调,登录登出时首先检查网络状态,而不是用时才去拿状态),是需要自动检测,提醒用户没有网络之类的
public class NetworkChangeReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifi = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobile = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        boolean isConnected = wifi != null && wifi.isConnectedOrConnecting() ||
            mobile != null && mobile.isConnectedOrConnecting();

        if (isConnected) {
//            NetworkUtil. // 保存一个什么游戏进度之类的,我不想保存,没有那么大的网.....
        }
    }
}