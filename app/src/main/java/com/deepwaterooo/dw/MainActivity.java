package com.deepwaterooo.dw;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

// 这个类的大致意思是说: 模拟几个游戏中会调用到要SDK中方法的按钮,回调SDK中需要启动的活动
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}