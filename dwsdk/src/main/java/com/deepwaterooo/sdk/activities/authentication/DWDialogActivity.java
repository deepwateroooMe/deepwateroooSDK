package com.deepwaterooo.sdk.activities.authentication;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PointF;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.deepwaterooo.sdk.R;
import com.deepwaterooo.sdk.activities.BaseActivity;
import com.deepwaterooo.sdk.appconfig.Constants;
import com.deepwaterooo.sdk.appconfig.JSONConstants;
import com.deepwaterooo.sdk.appconfig.Numerics;
import com.deepwaterooo.sdk.utils.SharedPrefUtil;
import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.shockwave.pdfium.PdfDocument;

/**
 * Activity used for User Privacy policy and Terms & Conditions
 */
// 感觉是处理各种各样的会话框: 同意游戏条款,设计个什么样的游戏条款呢?...
public class DWDialogActivity extends BaseActivity implements View.OnClickListener/*, OnPageChangeListener*/, OnLoadCompleteListener {
    private WebView wvPrivacyPolicy;
    private Button btnIAgree;
    private Button btnCancel;
    private LinearLayout parentView;
    private ProgressDialog progressDialog;
    private TextView tvTitle;
    private PDFView pdfView;
    private SharedPrefUtil sharedPrefUtil;
    private final String SAMPLE_FILE = "credits.pdf";
    private static final String TAG = DWDialogActivity.class.getSimpleName();
    Integer pageNumber = 0;
    private String version;
    private boolean isFromLogin = false;
    private boolean isFromSignUp = false;
    private String url;
    private boolean isPlaysetConnected;

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause() ");
//        if (!(isFromSignUp || isFromLogin) && isPlaysetConnected && BluetoothUtil.isPlaysetConnected() &&
//            !BluetoothUtil.isFirmwareUpdateInProgress() && !BluetoothUtil.isBootModeEnabled()) {
//            Util.IS_APP_RUNNING = false;
//            Util.playSound(DWDialogActivity.this, Constants.AUDIO_DISCONNECT);
//            BluetoothUtil.restartPlayset();
//        }
        Log.d(TAG, "onPause() (getIntent().hasExtra(Constants.EXTRA_IS_FROM_GAME)): " + (getIntent().hasExtra(Constants.EXTRA_IS_FROM_GAME)));
        if (getIntent().hasExtra(Constants.EXTRA_IS_FROM_GAME)){
            finish();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate() ");
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
        setContentView(R.layout.activity_dialog);
        sharedPrefUtil = new SharedPrefUtil(this);
        intUI();
    }
    /**
     * initialising the views used in this activity
     */
    private void intUI() {
        tvTitle = (TextView) findViewById(R.id.tvTitle);
        parentView = (LinearLayout) findViewById(R.id.parentView);
//        parentView.setMinimumWidth((int) (Constants.getDeviceWidth() * 0.5));
//        parentView.setMinimumHeight((int) (Constants.getDeviceHeight() * 0.75));
        wvPrivacyPolicy = (WebView) findViewById(R.id.wvPrivacyPolicy);
        btnIAgree = (Button) findViewById(R.id.btnIAgree);
        btnCancel = (Button) findViewById(R.id.btnCancel);
        pdfView = (PDFView) findViewById(R.id.pdfView);
        if (getIntent().hasExtra(Constants.EXTRA_IS_FROM_GAME)) {
            btnIAgree.setVisibility(View.GONE);
//            btnCancel.setText(getString(R.string.Close));
            btnCancel.setBackgroundResource(R.drawable.close_bg);
            isPlaysetConnected = true;
        } else if (getIntent().hasExtra(Constants.EXTRA_IS_FROM_LOGIN)) {
            isFromLogin = true;
        }
        if (getIntent().hasExtra(Constants.EXTRA_IS_FROM_SIGNUP)) {
            isFromSignUp = true;
        }
        wvPrivacyPolicy.getSettings().setJavaScriptEnabled(true);
        wvPrivacyPolicy.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        wvPrivacyPolicy.getSettings().setDefaultTextEncodingName("utf-8");
        wvPrivacyPolicy.setWebViewClient(new MyWebViewClient());
        btnIAgree.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this, R.style.DWProgressDialogTheme);
            progressDialog.setCancelable(false);
        }
        if (!getIntent().hasExtra(Constants.EXTRA_IS_CREDITS)) {
            try {
                if (getIntent().getBooleanExtra(JSONConstants.PRIVACY, false)) {
                    tvTitle.setText(getString(R.string.Privacy_Policy));
//                    btnIAgree.setText(getString(R.string.Submit));
//                    if (isFromSignUp) {
//                        callPrivacyPolicy();
//                    } else {
//                        callPrivacyPolicyWithToken();
//                    }
                } else {
                    tvTitle.setText(getString(R.string.Terms_and_Conditions));
//                    if (isFromSignUp) {
//                        callTermsAndConditions();
//                    } else {
//                        callTermsAndConditionsWithToken();
//                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            pdfView.setVisibility(View.GONE);
        } else {
            tvTitle.setText(getString(R.string.Credits));
            pdfView.fromAsset(SAMPLE_FILE)
                .enableSwipe(true)
                .enableDoubletap(true)
                .onLoad(this)
                .enableAnnotationRendering(true)
                .load();
        }
        parentView.post(new Runnable() {
                @Override
                public void run() {
                    ViewGroup.LayoutParams params = parentView.getLayoutParams();
                    params.height = (int) (Constants.getDeviceHeight() * 0.85);
                    params.width = (int) (Constants.getDeviceWidth() * 0.5);
                    parentView.setLayoutParams(params);
                    hideSystemUI();
                }
            });
    }
    @Override
    public void loadComplete(int nbPages) {
        PdfDocument.Meta meta = pdfView.getDocumentMeta();
        pdfView.zoomCenteredTo(3, new PointF());
        pdfView.scrollBy(10, 10);
    }
    /**
     * show the progress dialog with given message
     *
     * @param msg message we need to display
     */
    public void showProgressDialog(String msg) {
        if (progressDialog != null && !progressDialog.isShowing()) {
            if (msg != null)
                progressDialog.setMessage(msg);
            progressDialog.show();
        }
    }
    /**
     * dismiss  the progress dialog
     */
    public void dismissProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing() && !this.isFinishing()) {
            progressDialog.dismiss();
        }
        hideSystemUI();
    }

    @Override
    public void didFinishSdkUserConfiguration() {

    }

    /**
     * class used to control the web view
     */
    class MyWebViewClient extends WebViewClient {
        private ProgressDialog progressDialog;
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            if (progressDialog == null) {
                progressDialog = new ProgressDialog(DWDialogActivity.this, R.style.DWProgressDialogTheme);
                progressDialog.setCancelable(false);
                progressDialog.setOnDismissListener(DWDialogActivity.this);
            }
            if (!progressDialog.isShowing()) {
                if (!(DWDialogActivity.this).isFinishing()) {
                    progressDialog.show();
                }
            }
        }
        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            if (progressDialog != null && progressDialog.isShowing() && !DWDialogActivity.this.isFinishing()) {
                progressDialog.dismiss();
            }
            wvPrivacyPolicy.loadUrl(
                "javascript: var a = document.getElementsByTagName('a');" +
                "for(var i=0; i<a.length; i++){" +
//                            "document.getElementsByTagName('a')[i].style.setProperty(\"color\", \"black\");" +
                "document.getElementsByTagName('a')[i].removeAttribute(\"href\");" +
                "}"
                );
        }
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            //view.loadUrl(url);
            return true;
        }
    }
    /**
     * Listener for activity UI fields click events
     *
     * @param v return clicked view
     */
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnIAgree) {
//            Intent intent = new Intent();
//            intent.putExtra(JSONConstants.MESSAGE, JSONConstants.SUCCESS);
//            setResult(Numerics.ONE, intent);
//            finish();
            try {
                if (!TextUtils.isEmpty(url)) {
//                    if (getIntent().getBooleanExtra(JSONConstants.PRIVACY, false)) {
//                        callSubmitPrivacyPolicy();
//                    } else {
//                        callSubmitTermsAndConditions();
//                    }
                } else {
                    btnCancel.performClick();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (v.getId() == R.id.btnCancel) {
            isPlaysetConnected = false;
            Intent intent = new Intent();
            intent.putExtra(JSONConstants.MESSAGE, getString(R.string.Failed));
            setResult(Numerics.ONE, intent);
            finish();
        }
    }
    /**
     * device back navigation control with web view
     */
    @Override
    public void onBackPressed() {
        if (wvPrivacyPolicy != null && wvPrivacyPolicy.canGoBack()) {
            wvPrivacyPolicy.goBack();
        } else {
            // super.onBackPressed();
        }
    }
    View.OnClickListener okListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((Dialog) v.getTag()).dismiss();
                btnCancel.performClick();
            }
        };
//    /**
//     * this used to apply the custom font over the app from assets
//     * for this used calligraphy
//     *
//     * @param newBase context
//     */
//    @Override
//    protected void attachBaseContext(Context newBase) {
//        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
//    }
}