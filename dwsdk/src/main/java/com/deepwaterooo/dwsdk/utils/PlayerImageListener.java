package com.deepwaterooo.dwsdk.utils;

import android.graphics.Bitmap;

/**
 * Class used for profile image status
 */
public interface PlayerImageListener {

    public void onImageLoadSuccess(Bitmap bitmap);
    public void onImageLoadFailed(Bitmap errorBitmap);
}
