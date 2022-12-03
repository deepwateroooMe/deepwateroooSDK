package com.deepwaterooo.dwsdk.activities.authentication;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PointF;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.deepwaterooo.dwsdk.R;
import com.deepwaterooo.dwsdk.activities.BaseActivity;
import com.deepwaterooo.dwsdk.appconfig.Constants;
import com.deepwaterooo.dwsdk.appconfig.JSONConstants;
import com.deepwaterooo.dwsdk.appconfig.Numerics;
import com.deepwaterooo.dwsdk.networklayer.ApiClient;
import com.deepwaterooo.dwsdk.networklayer.NetworkUtil;
import com.deepwaterooo.dwsdk.utils.ApiCallListener;
import com.deepwaterooo.dwsdk.utils.PlayerUtil;
import com.deepwaterooo.dwsdk.utils.SharedPrefUtil;
import com.deepwaterooo.dwsdk.utils.Util;
import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.shockwave.pdfium.PdfDocument;

import org.json.JSONObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
                    if (isFromSignUp) {
                        callPrivacyPolicy();
                    } else {
                        callPrivacyPolicyWithToken();
                    }
                } else {
                    tvTitle.setText(getString(R.string.Terms_and_Conditions));
                    if (isFromSignUp) {
                        callTermsAndConditions();
                    } else {
                        callTermsAndConditionsWithToken();
                    }
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
                    if (getIntent().getBooleanExtra(JSONConstants.PRIVACY, false)) {
                        callSubmitPrivacyPolicy();
                    } else {
                        callSubmitTermsAndConditions();
                    }
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
     * sending Privacy Policy request to server and get the response
     *
     * @throws Exception thrown for network/response  issues
     */
    private void callPrivacyPolicy() throws Exception {
        if (NetworkUtil.checkInternetConnection(this)) {
            showProgressDialog(getString(R.string.Please_Wait));
            ApiClient.getApiInterface(this).privacyPolicyAPI(/*JSONConstants.AUTHORIZATION_BEARER +
                                                               sharedPrefUtil.getString(SharedPrefUtil.PREF_LOGIN_USER_TOKEN),*/
                getString(R.string.PARENT)).enqueue(new Callback<PrivacyPolicyDO>() {
                        @Override
                        public void onResponse(Call<PrivacyPolicyDO> call, Response<PrivacyPolicyDO> response) {
                            dismissProgressDialog();
                            try {
                                if (response.body() != null) {
                                    PrivacyPolicyDO privacyPolicyDO = (PrivacyPolicyDO) response.body();
                                    version = privacyPolicyDO.getVersion();
                                    //Logger.info("Privacy Success body", privacyPolicyDO.getContentUrl().toString());
                                    tvTitle.setText(getString(R.string.Privacy_Policy));
                                    url = privacyPolicyDO.getHtmlView();
                                    wvPrivacyPolicy.loadUrl(url);
                                } else {
                                    if (response.errorBody() != null) {
//                            JSONObject jsonObject = new JSONObject(response.errorBody().string());
//                            Logger.info("Login Error body", jsonObject.getString(JSONConstants.ERROR));
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        @Override
                        public void onFailure(Call<PrivacyPolicyDO> call, Throwable t) {
                            //Logger.info("Privacy Policy", "onFailure: " + t.toString());
                            dismissProgressDialog();
                            Util.showAlertWarning(DWDialogActivity.this, getString(R.string.Sorry),
                                                  getString(R.string.Failure_Unknown_Case), getString(R.string.Ok), okListener);
                        }
                    });
        } else {
            Util.showAlert(this, getString(R.string.We_Need_Internet), getString(R.string.Please_Connect_Internet),
                           getString(R.string.Ok),
                           okListener);
        }
    }
    /**
     * sending Privacy Policy request to server and get the response
     *
     * @throws Exception thrown for network/response  issues
     */
    private void callPrivacyPolicyWithToken() throws Exception {
        if (NetworkUtil.checkInternetConnection(this)) {
            showProgressDialog(getString(R.string.Please_Wait));
            ParentInfoDO parentInfoDO = PlayerUtil.getParentInfo(this);
            ApiClient.getApiInterface(this).privacyPolicyWithTokenAPI(JSONConstants.AUTHORIZATION_BEARER +
                                                                      sharedPrefUtil.getString(SharedPrefUtil.PREF_LOGIN_USER_TOKEN),
                                                                      parentInfoDO.getRole()).enqueue(new Callback<PrivacyPolicyDO>() {
                                                                              @Override
                                                                              public void onResponse(Call<PrivacyPolicyDO> call, Response<PrivacyPolicyDO> response) {
                                                                                  dismissProgressDialog();
                                                                                  try {
                                                                                      if (response.body() != null) {
                                                                                          PrivacyPolicyDO privacyPolicyDO = (PrivacyPolicyDO) response.body();
                                                                                          version = privacyPolicyDO.getVersion();
                                                                                          //Logger.info("Privacy Success body", privacyPolicyDO.getContentUrl().toString());
                                                                                          tvTitle.setText(getString(R.string.Privacy_Policy));
                                                                                          url = privacyPolicyDO.getHtmlView();
                                                                                          wvPrivacyPolicy.loadUrl(url);
                                                                                      } else if (response.code() == Constants.RESPONSE_CODE_UNAUTHORIZED) {
                                                                                          NetworkUtil.callReAuthenticationAPI(DWDialogActivity.this, new ApiCallListener() {
                                                                                                  @Override
                                                                                                  public void onResponse(Object object) {
                                                                                                      if (object == null) {
                                                                                                          try {
                                                                                                              callPrivacyPolicyWithToken();
                                                                                                          } catch (Exception e) {
                                                                                                              e.printStackTrace();
                                                                                                          }
                                                                                                      }
                                                                                                  }
                                                                                                  @Override
                                                                                                  public void onFailure(Object error) {
                                                                                                  }
                                                                                              }, true);
                                                                                      } else {
                                                                                          if (response.errorBody() != null) {
//                            JSONObject jsonObject = new JSONObject(response.errorBody().string());
//                            Logger.info("Login Error body", jsonObject.getString(JSONConstants.ERROR));
                                                                                          }
                                                                                      }
                                                                                  } catch (Exception e) {
                                                                                      e.printStackTrace();
                                                                                  }
                                                                              }
                                                                              @Override
                                                                              public void onFailure(Call<PrivacyPolicyDO> call, Throwable t) {
                                                                                  //Logger.info("Privacy Policy", "onFailure: " + t.toString());
                                                                                  dismissProgressDialog();
                                                                                  Util.showAlertWarning(DWDialogActivity.this, getString(R.string.Sorry),
                                                                                                        getString(R.string.Failure_Unknown_Case), getString(R.string.Ok), okListener);
                                                                              }
                                                                          });
        } else {
            Util.showAlert(this, getString(R.string.We_Need_Internet), getString(R.string.Please_Connect_Internet),
                           getString(R.string.Ok),
                           okListener);
        }
    }
    /**
     * sending Terms and Conditions request to server and get the response
     *
     * @throws Exception thrown for network/response  issues
     */
    private void callTermsAndConditions() throws Exception {
        if (NetworkUtil.checkInternetConnection(this)) {
            showProgressDialog(getString(R.string.Please_Wait));
            ApiClient.getApiInterface(this).termsAndConditionsAPI(/*JSONConstants.AUTHORIZATION_BEARER +
                                                                    sharedPrefUtil.getString(SharedPrefUtil.PREF_LOGIN_USER_TOKEN),*/
                getString(R.string.PARENT)).enqueue(new Callback<PrivacyPolicyDO>() {
                        @Override
                        public void onResponse(Call<PrivacyPolicyDO> call, Response<PrivacyPolicyDO> response) {
                            dismissProgressDialog();
                            try {
                                if (response.body() != null) {
                                    PrivacyPolicyDO privacyPolicyDO = (PrivacyPolicyDO) response.body();
                                    version = privacyPolicyDO.getVersion();
                                    //Logger.info("TermsAndConditions Success body", privacyPolicyDO.getContentUrl().toString());
                                    tvTitle.setText(getString(R.string.Terms_and_Conditions));
                                    url = privacyPolicyDO.getHtmlView();
                                    wvPrivacyPolicy.loadUrl(url);
                                } else {
                                    if (response.errorBody() != null) {
//                            JSONObject jsonObject = new JSONObject(response.errorBody().string());
//                            Logger.info("Login Error body", jsonObject.getString(JSONConstants.ERROR));
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        @Override
                        public void onFailure(Call<PrivacyPolicyDO> call, Throwable t) {
                            //Logger.info("TermsAndConditions", "onFailure: " + t.toString());
                            dismissProgressDialog();
                            Util.showAlertWarning(DWDialogActivity.this, getString(R.string.Sorry),
                                                  getString(R.string.Failure_Unknown_Case), getString(R.string.Ok), okListener);
                        }
                    });
        } else {
            Util.showAlert(this, getString(R.string.We_Need_Internet), getString(R.string.Please_Connect_Internet),
                           getString(R.string.Ok),
                           okListener);
        }
    }
    /**
     * sending Terms and Conditions request to server and get the response
     *
     * @throws Exception thrown for network/response  issues
     */
    private void callTermsAndConditionsWithToken() throws Exception {
        if (NetworkUtil.checkInternetConnection(this)) {
            showProgressDialog(getString(R.string.Please_Wait));
            ParentInfoDO parentInfoDO = PlayerUtil.getParentInfo(this);
            ApiClient.getApiInterface(this).termsAndConditionsWithTokenAPI(JSONConstants.AUTHORIZATION_BEARER +
                                                                           sharedPrefUtil.getString(SharedPrefUtil.PREF_LOGIN_USER_TOKEN),
                                                                           parentInfoDO.getRole()).enqueue(new Callback<PrivacyPolicyDO>() {
                                                                                   @Override
                                                                                   public void onResponse(Call<PrivacyPolicyDO> call, Response<PrivacyPolicyDO> response) {
                                                                                       dismissProgressDialog();
                                                                                       try {
                                                                                           if (response.body() != null) {
                                                                                               PrivacyPolicyDO privacyPolicyDO = (PrivacyPolicyDO) response.body();
                                                                                               version = privacyPolicyDO.getVersion();
                                                                                               //Logger.info("TermsAndConditions Success body", privacyPolicyDO.getContentUrl().toString());
                                                                                               tvTitle.setText(getString(R.string.Terms_and_Conditions));
                                                                                               url = privacyPolicyDO.getHtmlView();
                                                                                               wvPrivacyPolicy.loadUrl(url);
                                                                                           } else if (response.code() == Constants.RESPONSE_CODE_UNAUTHORIZED) {
                                                                                               NetworkUtil.callReAuthenticationAPI(DWDialogActivity.this, new ApiCallListener() {
                                                                                                       @Override
                                                                                                       public void onResponse(Object object) {
                                                                                                           if (object == null) {
                                                                                                               try {
                                                                                                                   callTermsAndConditionsWithToken();
                                                                                                               } catch (Exception e) {
                                                                                                                   e.printStackTrace();
                                                                                                               }
                                                                                                           }
                                                                                                       }
                                                                                                       @Override
                                                                                                       public void onFailure(Object error) {
                                                                                                       }
                                                                                                   }, true);
                                                                                           } else {
                                                                                               if (response.errorBody() != null) {
//                            JSONObject jsonObject = new JSONObject(response.errorBody().string());
//                            Logger.info("Login Error body", jsonObject.getString(JSONConstants.ERROR));
                                                                                               }
                                                                                           }
                                                                                       } catch (Exception e) {
                                                                                           e.printStackTrace();
                                                                                       }
                                                                                   }
                                                                                   @Override
                                                                                   public void onFailure(Call<PrivacyPolicyDO> call, Throwable t) {
                                                                                       //Logger.info("TermsAndConditions", "onFailure: " + t.toString());
                                                                                       dismissProgressDialog();
                                                                                       Util.showAlertWarning(DWDialogActivity.this, getString(R.string.Sorry),
                                                                                                             getString(R.string.Failure_Unknown_Case), getString(R.string.Ok), okListener);
                                                                                   }
                                                                               });
        } else {
            Util.showAlert(this, getString(R.string.We_Need_Internet), getString(R.string.Please_Connect_Internet),
                           getString(R.string.Ok),
                           okListener);
        }
    }
    /**
     * sending Terms and Conditions submit status to update server
     *
     * @throws Exception
     */
    private void callSubmitTermsAndConditions() throws Exception {
        if (isFromLogin) {
            if (NetworkUtil.checkInternetConnection(this)) {
                showProgressDialog(getString(R.string.Please_Wait));
                ApiClient.getApiInterface(this).submitTermsAndConditionsAPI(JSONConstants.AUTHORIZATION_BEARER +
                                                                            sharedPrefUtil.getString(SharedPrefUtil.PREF_LOGIN_USER_TOKEN),
                                                                            PlayerUtil.getParentInfo(DWDialogActivity.this).getId()
                                                                            , version).
                    enqueue(new Callback<ResponseBody>() {
                            @Override
                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                dismissProgressDialog();
                                try {
                                    if (response.body() != null) {
                                        JSONObject jsonObject = new JSONObject(response.body().string());
                                        //Logger.info("Submit TermsAndConditions Success ", jsonObject.getString(JSONConstants.MESSAGE));
                                        Intent intent = new Intent();
                                        intent.putExtra(JSONConstants.MESSAGE, JSONConstants.SUCCESS);
                                        if (!TextUtils.isEmpty(version)) {
                                            intent.putExtra(Constants.EXTRA_TERMS_VERSION, version);
                                        }
                                        setResult(Numerics.ONE, intent);
                                        finish();
                                    } else if (response.code() == Constants.RESPONSE_CODE_UNAUTHORIZED) {
                                        NetworkUtil.callReAuthenticationAPI(DWDialogActivity.this, new ApiCallListener() {
                                                @Override
                                                public void onResponse(Object object) {
                                                    if (object == null) {
                                                        try {
                                                            callSubmitTermsAndConditions();
                                                        } catch (Exception e) {
                                                            e.printStackTrace();
                                                        }
                                                    }
                                                }
                                                @Override
                                                public void onFailure(Object error) {
                                                }
                                            }, true);
                                    } else {
                                        if (response.errorBody() != null) {
                                            JSONObject jsonObject = new JSONObject(response.errorBody().string());
                                            //Logger.info("Submit TermsAndConditions Error ", jsonObject.getString(JSONConstants.ERROR));
                                        }
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                            @Override
                            public void onFailure(Call<ResponseBody> call, Throwable t) {
                                //Logger.info("Submit TermsAndConditions", "onFailure: " + t.toString());
                                dismissProgressDialog();
                                Util.showAlertWarning(DWDialogActivity.this, getString(R.string.Sorry),
                                                      getString(R.string.Failure_Unknown_Case), getString(R.string.Ok), okListener);
                            }
                        });
            } else {
                Util.showAlert(this, getString(R.string.We_Need_Internet), getString(R.string.Please_Connect_Internet),
                               getString(R.string.Ok),
                               null);
            }
        } else {
            Intent intent = new Intent();
            intent.putExtra(JSONConstants.MESSAGE, JSONConstants.SUCCESS);
            if (!TextUtils.isEmpty(version)) {
                intent.putExtra(Constants.EXTRA_TERMS_VERSION, version);
            }
            setResult(Numerics.ONE, intent);
            finish();
        }
    }
    /**
     * sending  Privacy Policy submit status to update server
     *
     * @throws Exception
     */
    private void callSubmitPrivacyPolicy() throws Exception {
        if (isFromLogin) {
            if (NetworkUtil.checkInternetConnection(this)) {
                showProgressDialog(getString(R.string.Please_Wait));
                ApiClient.getApiInterface(this).submitPrivacyPolicyAPI(JSONConstants.AUTHORIZATION_BEARER +
                                                                       sharedPrefUtil.getString(SharedPrefUtil.PREF_LOGIN_USER_TOKEN),
                                                                       PlayerUtil.getParentInfo(DWDialogActivity.this).getId()
                                                                       , version).
                    enqueue(new Callback<ResponseBody>() {
                            @Override
                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                dismissProgressDialog();
                                try {
                                    if (response.body() != null) {
                                        JSONObject jsonObject = new JSONObject(response.body().string());
                                        //Logger.info("Submit PrivacyPolicy Success ", jsonObject.getString(JSONConstants.MESSAGE));
                                        sharedPrefUtil.setBoolean(SharedPrefUtil.PREF_TERMS_AND_CONDITIONS, false);
                                        sharedPrefUtil.setBoolean(SharedPrefUtil.PREF_PRIVACY, false);
                                        Intent intent = new Intent();
                                        intent.putExtra(JSONConstants.MESSAGE, JSONConstants.SUCCESS);
                                        if (!TextUtils.isEmpty(version)) {
                                            intent.putExtra(Constants.EXTRA_PRIVACY_VERSION, version);
                                        }
                                        setResult(Numerics.ONE, intent);
                                        finish();
                                    } else if (response.code() == Constants.RESPONSE_CODE_UNAUTHORIZED) {
                                        NetworkUtil.callReAuthenticationAPI(DWDialogActivity.this, new ApiCallListener() {
                                                @Override
                                                public void onResponse(Object object) {
                                                    if (object == null) {
                                                        try {
                                                            callSubmitPrivacyPolicy();
                                                        } catch (Exception e) {
                                                            e.printStackTrace();
                                                        }
                                                    }
                                                }
                                                @Override
                                                public void onFailure(Object error) {
                                                }
                                            }, true);
                                    } else {
                                        if (response.errorBody() != null) {
                                            JSONObject jsonObject = new JSONObject(response.errorBody().string());
                                            //Logger.info("Submit PrivacyPolicy Error ", jsonObject.getString(JSONConstants.ERROR));
                                        }
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                            @Override
                            public void onFailure(Call<ResponseBody> call, Throwable t) {
                                //Logger.info("Submit PrivacyPolicy", "onFailure: " + t.toString());
                                dismissProgressDialog();
                                Util.showAlertWarning(DWDialogActivity.this, getString(R.string.Sorry),
                                                      getString(R.string.Failure_Unknown_Case), getString(R.string.Ok), okListener);
                            }
                        });
            } else {
                Util.showAlert(this, getString(R.string.We_Need_Internet), getString(R.string.Please_Connect_Internet),
                               getString(R.string.Ok),
                               null);
            }
        } else {
            sharedPrefUtil.setBoolean(SharedPrefUtil.PREF_TERMS_AND_CONDITIONS, false);
            sharedPrefUtil.setBoolean(SharedPrefUtil.PREF_PRIVACY, false);
            Intent intent = new Intent();
            intent.putExtra(JSONConstants.MESSAGE, JSONConstants.SUCCESS);
            if (!TextUtils.isEmpty(version)) {
                intent.putExtra(Constants.EXTRA_PRIVACY_VERSION, version);
            }
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


    @Override
    protected void onPause() {
        super.onPause();
        if (!(isFromSignUp || isFromLogin) && isPlaysetConnected && BluetoothUtil.isPlaysetConnected() &&
            !BluetoothUtil.isFirmwareUpdateInProgress() && !BluetoothUtil.isBootModeEnabled()) {
            Util.IS_APP_RUNNING = false;
            Util.playSound(DWDialogActivity.this, Constants.AUDIO_DISCONNECT);
            BluetoothUtil.restartPlayset();
        }
        if(getIntent().hasExtra(Constants.EXTRA_IS_FROM_GAME)){
            finish();
        }

    }
}