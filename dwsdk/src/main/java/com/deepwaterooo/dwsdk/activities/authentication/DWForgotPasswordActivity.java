package com.deepwaterooo.dwsdk.activities.authentication;

import android.app.Dialog;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.deepwaterooo.dwsdk.R;
import com.deepwaterooo.dwsdk.activities.BaseActivity;
import com.deepwaterooo.dwsdk.appconfig.Constants;
import com.deepwaterooo.dwsdk.appconfig.JSONConstants;
import com.deepwaterooo.dwsdk.networklayer.ApiClient;
import com.deepwaterooo.dwsdk.networklayer.NetworkUtil;
import com.deepwaterooo.dwsdk.utils.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Activity used for re-set User password using registered e-mail
 */
public class DWForgotPasswordActivity extends BaseActivity implements View.OnClickListener {
    private EditText etFPEmail;
    private Button btnFPCancel;
    private Button btnFPSubmit;
    private String errorMsg;
    private String errorMsgTitle;
    private ImageView ivPanda;
    private ImageView ivDWLogo;
    private RelativeLayout rlRootView;
    private View rootView;
    private ViewGroup contentContainer;
    private ViewTreeObserver viewTreeObserver;
    private ViewTreeObserver.OnGlobalLayoutListener listener;
    private Rect contentAreaOfWindowBounds = new Rect();
    private FrameLayout.LayoutParams rootViewLayout;
    private int usableHeightPrevious = 0;
    private View mDecorView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        if(savedInstanceState!=null){
            Util.getDeviceDimensions(this);
        }
        intUI();
    }

    /**
     * initialising the views used in this activity
     */
    private void intUI() {
        mDecorView = getWindow().getDecorView();
        contentContainer =
            (ViewGroup) this.findViewById(android.R.id.content);

        listener = new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    possiblyResizeChildOfContent();
                }
            };

        rootView = contentContainer.getChildAt(0);
        rootViewLayout = (FrameLayout.LayoutParams)
            rootView.getLayoutParams();
        etFPEmail = (EditText) findViewById(R.id.etFPEmail);
        btnFPSubmit = (Button) findViewById(R.id.btnFPSubmit);
        btnFPCancel = (Button) findViewById(R.id.btnFPCancel);
        etFPEmail.setMinimumWidth((int) (Constants.getDeviceWidth() * 0.35));
        etFPEmail.setMaxWidth((int) (Constants.getDeviceWidth() * 0.35));
        ivPanda = (ImageView) findViewById(R.id.ivPanda);
        ivDWLogo = (ImageView) findViewById(R.id.ivSPLogo);
        btnFPSubmit.setOnClickListener(this);
        btnFPCancel.setOnClickListener(this);

        rlRootView = (RelativeLayout) findViewById(R.id.rlRootView);

        rlRootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    Rect r = new Rect();
                    rlRootView.getWindowVisibleDisplayFrame(r);
                    int screenHeight = rlRootView.getRootView().getHeight();
                    int keypadHeight = screenHeight - r.bottom;
                    if (keypadHeight > screenHeight * 0.15) {
                        ivPanda.setVisibility(View.GONE);
                        ivDWLogo.setVisibility(View.GONE);
                    } else {
                        ivPanda.setVisibility(View.VISIBLE);
                        ivDWLogo.setVisibility(View.VISIBLE);
                        hideSystemUI();
                    }
                }
            });
    }

    /**
     * return true if the given input fields are valid
     *
     * @return true if the given input fields are valid
     */
    private boolean validateFields() {
        errorMsg = null;
        errorMsgTitle = null;
        boolean valid = true;
        if (TextUtils.isEmpty(etFPEmail.getText().toString())) {
            errorMsg = getString(R.string.Empty_Username);
            errorMsgTitle = getString(R.string.Whoops);
            valid = false;
        } else if (!etFPEmail.getText().toString().matches(Constants.EMAIL_VALIDATE_PATTERN)) {
            errorMsg = getString(R.string.Invalid_Email_format);
            errorMsgTitle = getString(R.string.Hmm);
            valid = false;
        }
        if (!valid)
            Util.showAlertWarning(DWForgotPasswordActivity.this, errorMsgTitle,
                                  errorMsg, getString(R.string.Ok), null);
        return valid;
    }

    /**
     * Listener for activity UI fields click events
     *
     * @param v return clicked view
     */
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnFPSubmit) {
            hideSoftKeyboard(this);
            if (validateFields()) {
                try {
                    if (NetworkUtil.checkInternetConnection(DWForgotPasswordActivity.this)) {
                        callForgotPasswordAPI();

                    } else {
                        Util.showAlert(DWForgotPasswordActivity.this, getString(R.string.We_Need_Internet),
                                       getString(R.string.Please_Connect_Internet), getString(R.string.Ok), null);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (errorMsg != null) {
                //Toast.makeText(this, errorMsg, Toast.LENGTH_LONG).show();
            }
        } else if (v.getId() == R.id.btnFPCancel) {
//            Intent intent = new Intent(this, DWLoginActivity.class);
//            startActivity(intent);
            finish();
        }
    }

    /**
     * sending forgot password request with registered email to reset the password
     */
    private void callForgotPasswordAPI() throws Exception {
        showProgressDialog(getString(R.string.Please_Wait), false);
        ApiClient.getApiInterface(this).forgotPasswordAPI(etFPEmail.getText().toString()).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    dismissProgressDialog();
                    if (Util.IS_APP_RUNNING || BaseActivity.IS_APP_RUNNING) {

                        try {
                            if (response.body() != null) {
                                JSONObject jsonObject = new JSONObject(response.body().string());
                                //Logger.info("Forgot Password Success ", jsonObject.getString(JSONConstants.MESSAGE));
                                Util.showAlertWarning(DWForgotPasswordActivity.this, getString(R.string.Sent),
                                                      getString(R.string.Forgot_Password_Success), getString(R.string.Ok), okListener);

                            } else {
                                if (response.errorBody() != null) {
                                    JSONObject jsonObject = new JSONObject(response.errorBody().string());
                                    //Logger.info("Forgot Password Error ", jsonObject.getString(JSONConstants.ERROR));
                                    if (jsonObject.getString(JSONConstants.ERROR).equals(JSONConstants.EMAIL_NOT_FOUND)) {
                                        Util.showAlertWarning(DWForgotPasswordActivity.this, getString(R.string.Sorry),
                                                              getString(R.string.User_does_not_exits), getString(R.string.Ok), null);
                                    } else {
                                        Util.showAlertWarning(DWForgotPasswordActivity.this, getString(R.string.Sorry),
                                                              getString(R.string.Email_Not_Active), getString(R.string.Ok), null);
                                    }

                                }
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            //Logger.info("Forgot Password", "onFailure: " + e.toString());
                            e.printStackTrace();
                        }
                    }

                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    //Logger.info("Forgot Password", "onFailure: " + t.toString());
                    dismissProgressDialog();
                    if (Util.IS_APP_RUNNING || BaseActivity.IS_APP_RUNNING) {

                        Util.showAlertWarning(DWForgotPasswordActivity.this, getString(R.string.Sorry),
                                              getString(R.string.Failure_Unknown_Case), getString(R.string.Ok), null);
                    }
                }
            });
    }

    View.OnClickListener okListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((Dialog) v.getTag()).dismiss();
//            Intent intent = new Intent(DWForgotPasswordActivity.this, DWLoginActivity.class);
//            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
//            startActivity(intent);
                finish();
            }
        };

    /**
     * device back navigation control
     */
    @Override
    public void onBackPressed() {}

    @Override
    protected void onPause() {
        super.onPause();
        if (viewTreeObserver.isAlive()) {
            viewTreeObserver.removeOnGlobalLayoutListener(listener);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (viewTreeObserver == null || !viewTreeObserver.isAlive()) {
            viewTreeObserver = rootView.getViewTreeObserver();
        }
        viewTreeObserver.addOnGlobalLayoutListener(listener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        rootView = null;
        contentContainer = null;
        viewTreeObserver = null;
    }

    private void possiblyResizeChildOfContent() {
        contentContainer.getWindowVisibleDisplayFrame(contentAreaOfWindowBounds);

        int usableHeightNow = contentAreaOfWindowBounds.height();

        if (usableHeightNow != usableHeightPrevious) {
            rootViewLayout.height = usableHeightNow;
            rootView.layout(contentAreaOfWindowBounds.left,
                            contentAreaOfWindowBounds.top, contentAreaOfWindowBounds.right, contentAreaOfWindowBounds.bottom);
            rootView.requestLayout();

            usableHeightPrevious = usableHeightNow;
        } else {

            this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        }
    }

    @Override
    public void didFinishSdkUserConfiguration() {

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }
}
