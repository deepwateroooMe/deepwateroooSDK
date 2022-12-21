package com.deepwaterooo.sdk;

import android.app.Application;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

public class DWApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
       //applying custom font over the application using Calligraphy
       CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
               .setDefaultFontPath("fonts/averta_light.ttf")
               .setFontAttrId(R.attr.fontPath)
               .build()
       );
    }
}