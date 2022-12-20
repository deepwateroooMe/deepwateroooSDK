package com.deepwaterooo.sdk.utils;

// 用户注册或是登录时,用于与服务端交互的回调监听
public interface ApiCallListener {

    public void onResponse(Object object);
    public void onFailure(Object error);
}
