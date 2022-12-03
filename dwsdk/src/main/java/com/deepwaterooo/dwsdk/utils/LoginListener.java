package com.deepwaterooo.dwsdk.utils;

public interface LoginListener {
    /**
     * This method is to get called when user login successful
     * 用户注册或是登录成功了之后,用于游戏端的监听实现,准备开始游戏 ?
     */
    public void didFinishSdkUserConfiguration();
}
