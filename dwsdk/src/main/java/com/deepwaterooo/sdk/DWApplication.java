package com.deepwaterooo.sdk;

import android.app.Application;
import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

import io.github.inflationx.calligraphy3.CalligraphyConfig;
import io.github.inflationx.calligraphy3.CalligraphyInterceptor;
import io.github.inflationx.viewpump.ViewPump;

public class DWApplication extends Application implements Thread.UncaughtExceptionHandler {
    private final String TAG = "DWApplication";

    @Override
    public void onCreate() {
        super.onCreate();
        //applying custom font over the application using Calligraphy
        ViewPump.init(ViewPump.builder()
                      .addInterceptor(new CalligraphyInterceptor(
                                          new CalligraphyConfig.Builder()
                                          .setDefaultFontPath("fonts/Roboto-RobotoRegular.ttf")
                                          .setFontAttrId(R.attr.fontPath)
                                          .build()))
                      .build());
//        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
//               .setDefaultFontPath("fonts/averta_light.ttf")
//               .setFontAttrId(R.attr.fontPath)
//               .build()
//       );
// 可以多点儿提示,就暂时还把它放这里
       Thread.setDefaultUncaughtExceptionHandler(this); // 这个捕获异常,好像帮助不大
    }
    @Override
    public void uncaughtException(final Thread thread, final Throwable throwable) {
        Writer writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        throwable.printStackTrace(printWriter);
        try {
            String result = writer.toString();
            Log.e(TAG,"\r\nCurrentThread:" + Thread.currentThread() + "\r\nException:\r\n" + result);
            printWriter.close();
            writer.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        //Toast 提示用户
        new Thread() {
            @Override
            public void run() {
                Looper.prepare();
                Toast.makeText(DWApplication.this, "Exceptions", Toast.LENGTH_LONG).show();
                Looper.loop();
            }
        }.start();

        //关闭应用程序
        SystemClock.sleep(1000);
        android.os.Process.killProcess(android.os.Process.myPid());
    }
}