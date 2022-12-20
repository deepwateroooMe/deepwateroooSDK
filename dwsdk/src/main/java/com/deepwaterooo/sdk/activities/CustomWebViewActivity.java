package com.deepwaterooo.sdk.activities;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.deepwaterooo.sdk.R;
import com.deepwaterooo.sdk.appconfig.Constants;

/**
 * This class is used to load web pages using urls.
 */
// 必要的时候,我可能需要用这个特性,比如要用户帮给游戏打分,不知道是否可以无缝衔接好
public class CustomWebViewActivity extends BaseActivity {
    private WebView webView;
    private Activity conActivity; // <<<<<<<<<<<<<<<<<<<< 因为我不需要实现什么接口,所以这里可能就不需要了

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        conActivity = this;
        setContentView(R.layout.activity_custom_webview);
        initUI();
    }

    private void initUI() {
        webView = (WebView) findViewById(R.id.webView);
        if (getIntent().hasExtra(Constants.EXTRA_URL)) {
            webView.setWebViewClient(new MyWebClient());
            webView.loadUrl(getIntent().getStringExtra(Constants.EXTRA_URL));
        }
    }

    private class MyWebClient extends WebViewClient {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
//            showProgressDialog(null); // 空字符串
//            showProgressDialog(null); // 空字符串
        }
        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            if (!conActivity.isDestroyed()) {
                dismissProgressDialog();
            }
        }
    }
//    public void availableServices() {}
//    public void gamePaused(boolean isScreenLocked) {}
//    protected void didNavigatesToMainMenu() {}

    @Override
    public void onBackPressed() {
        if (webView != null && webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }
}
