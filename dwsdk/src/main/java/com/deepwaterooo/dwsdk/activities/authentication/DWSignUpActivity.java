package com.deepwaterooo.dwsdk.activities.authentication;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.UnderlineSpan;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.deepwaterooo.dwsdk.R;
import com.deepwaterooo.dwsdk.activities.BaseActivity;
import com.deepwaterooo.dwsdk.appconfig.Constants;
import com.deepwaterooo.dwsdk.appconfig.JSONConstants;
import com.deepwaterooo.dwsdk.appconfig.Numerics;
import com.deepwaterooo.dwsdk.networklayer.ApiClient;
import com.deepwaterooo.dwsdk.networklayer.NetworkUtil;
import com.deepwaterooo.dwsdk.utils.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Activity used for User Sign Up with specified input fields
 */
public class DWSignUpActivity extends BaseActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {
    private TextView createAccountLabel;
    private EditText etFirstName;
    private EditText etLastName;
    private EditText etEmail;
    private EditText etPassword;
    private EditText etReTypePassword;
    private Button btnNext;
    private String errorMsg;
    private String errorMsgTitle;
    private TextView tvAlreadyHaveAccount;
    private boolean flagPrivacy = false;
    private RelativeLayout rlRootView;
    private ImageView ivSPLogo;
    private ImageView ivPanda;
    private String privacyVersion = "1.0";
    private String termsVersion = "1.0";
    private CheckBox cbStayUptoDate;
    private RadioButton rbtnEnglish;
    private RadioButton rbtnChinese;
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
        intUI();
    }

    /**
     * initialising the views used in this activity
     */
    private void intUI() {
        setContentView(R.layout.activity_sign_up);
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
        createAccountLabel = (TextView) findViewById(R.id.createAccountLabel);
        etFirstName = (EditText) findViewById(R.id.etFirstName);
        etLastName = (EditText) findViewById(R.id.etLastName);
        etEmail = (EditText) findViewById(R.id.etEmail);
        etPassword = (EditText) findViewById(R.id.etPassword);
        etReTypePassword = (EditText) findViewById(R.id.etReTypePassword);
        btnNext = (Button) findViewById(R.id.btnNext);
        tvAlreadyHaveAccount = (TextView) findViewById(R.id.tvAlreadyHaveAccount);
        cbStayUptoDate = (CheckBox) findViewById(R.id.cbStayUpToDate);
        rbtnEnglish = (RadioButton) findViewById(R.id.rbtnEnglish);
        rbtnChinese = (RadioButton) findViewById(R.id.rbtnChinese);

        updateLanButtons();

        etReTypePassword.post(new Runnable() {
                @Override
                public void run() {
                    LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) etFirstName.getLayoutParams();
                    params.width = (int) (Constants.getDeviceWidth() * 0.35);
                    etFirstName.setLayoutParams(params);
                    etLastName.setLayoutParams(params);
                    etEmail.setLayoutParams(params);
                    etPassword.setLayoutParams(params);
                    etReTypePassword.setLayoutParams(params);
                }
            });
        btnNext.setMinimumWidth((int) (Constants.getDeviceWidth() * 0.25));
        btnNext.setOnClickListener(this);
        tvAlreadyHaveAccount.setOnClickListener(this);
        rbtnEnglish.setOnCheckedChangeListener(this);
        rbtnChinese.setOnCheckedChangeListener(this);

        ivSPLogo = (ImageView) findViewById(R.id.ivSPLogo);
        ivPanda = (ImageView) findViewById(R.id.ivPanda);
        rlRootView = (RelativeLayout) findViewById(R.id.rlRootView);

        rlRootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    Rect r = new Rect();
                    rlRootView.getWindowVisibleDisplayFrame(r);
                    int screenHeight = rlRootView.getRootView().getHeight();
                    int keypadHeight = screenHeight - r.bottom;
                    if (keypadHeight > screenHeight * 0.15) {
                        ivSPLogo.setVisibility(View.GONE);
                        ivPanda.setVisibility(View.GONE);
                    } else {
                        ivSPLogo.setVisibility(View.VISIBLE);
                        ivPanda.setVisibility(View.VISIBLE);
                        hideSystemUI();
                    }
                }
            });

        etReTypePassword.setOnEditorActionListener(new EditText.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView textView, int actionID, KeyEvent keyEvent) {

                    if (EditorInfo.IME_ACTION_DONE == actionID) {
                        btnNext.performClick();
                    }
                    return false;
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
        if (TextUtils.isEmpty(etFirstName.getText().toString().trim())) {
            errorMsg = getString(R.string.Empty_Grownups_First_Name);
            errorMsgTitle = getString(R.string.Whoops);
            etFirstName.setText("");
            valid = false;
        } else if (TextUtils.isEmpty(etLastName.getText().toString().trim())) {
            valid = false;
            errorMsg = getString(R.string.Empty_Grownups_Last_Name);
            errorMsgTitle = getString(R.string.Whoops);
            etLastName.setText("");
        } else if (TextUtils.isEmpty(etEmail.getText().toString())) {
            errorMsg = getString(R.string.Empty_Grown_ups_Email);
            errorMsgTitle = getString(R.string.Whoops);
            valid = false;
        } else if (!etEmail.getText().toString().matches(Constants.EMAIL_VALIDATE_PATTERN)) {
            errorMsg = getString(R.string.Invalid_Email_format);
            errorMsgTitle = getString(R.string.Hmm);
            valid = false;
        } else if (TextUtils.isEmpty(etPassword.getText().toString())) {
            errorMsg = getString(R.string.Provide_Password);
            errorMsgTitle = getString(R.string.Whoops);
            valid = false;
        } else if (etPassword.getText().length() < 8) {
            errorMsg = getString(R.string.Password_Length);
            errorMsgTitle = getString(R.string.Whoops);
            valid = false;
        } else if (TextUtils.isEmpty(etReTypePassword.getText().toString())) {
            errorMsg = getString(R.string.Empty_Retype_Password);
            errorMsgTitle = getString(R.string.Whoops);
            valid = false;
        }/* else if (etReTypePassword.getText().length() < 8) {
            errorMsg = getString(R.string.Password_Length);
            errorMsgTitle = getString(R.string.Whoops);
            valid = false;
            } */else if (!etPassword.getText().toString().equals(etReTypePassword.getText().toString())) {
            errorMsg = getString(R.string.Password_RetypePassword_Match);
            errorMsgTitle = getString(R.string.Oops);
            valid = false;
        }
        if (!valid)
            Util.showAlertWarning(DWSignUpActivity.this, errorMsgTitle,
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
        v.setClickable(false);
        if (v.getId() == R.id.btnNext) {
            hideSoftKeyboard(this);
            if (validateFields()) {
                try {
                    callVerifyEmailAPI();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else if (v.getId() == R.id.tvAlreadyHaveAccount) {
            Intent intent = new Intent(DWSignUpActivity.this, DWLoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivityForResult(intent, Numerics.ZERO);
            //finish();
        }
        v.setClickable(true);
    }

    /**
     * verify the given email with server,is already exist or not
     *
     * @throws Exception
     */
    private void callVerifyEmailAPI() throws Exception {

        if (NetworkUtil.checkInternetConnection(this)) {
            showProgressDialog(getString(R.string.Please_Wait), false);
            ApiClient.getApiInterface(this).verifyEmailAPI(etEmail.getText().toString()).enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        dismissProgressDialog();
                        if (Util.IS_APP_RUNNING || BaseActivity.IS_APP_RUNNING) {
                            try {
                                if (response.body() != null) {
                                    JSONObject jsonObject = new JSONObject(response.body().string());
                                    //{"message":"Email already verified"}
                                    if (jsonObject.getString(JSONConstants.MESSAGE).equals(JSONConstants.EMAIL_ALREADY_VERIFIED)) {
                                        Util.showAlertWarning(DWSignUpActivity.this, getString(R.string.Hmm),
                                                              getString(R.string.Email_Already_Exists), getString(R.string.Ok), null);
                                    }
                                } else {
                                    if (response.errorBody() != null) {
                                        JSONObject jsonObject = new JSONObject(response.errorBody().string());
                                        //{"error":"Email not verified. Please verify it"}
                                        if (jsonObject.getString(JSONConstants.ERROR).equals(JSONConstants.EMAIL_NOT_VERIFIED)) {
                                            Util.showAlertWarning(DWSignUpActivity.this, getString(R.string.Hmm),
                                                                  getString(R.string.Email_Already_Exists), getString(R.string.Ok), null);
                                        } else {
                                            //{"error":"Email not available"}
                                            ShowPrivacyAndTermsDialog(flagPrivacy);
                                        }

                                    }
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        //Logger.info("VerifyEmail", "onFailure: " + t.toString());
                        dismissProgressDialog();
                        if (Util.IS_APP_RUNNING || BaseActivity.IS_APP_RUNNING) {
                            Util.showAlertWarning(DWSignUpActivity.this, getString(R.string.Sorry),
                                                  getString(R.string.Failure_Unknown_Case), getString(R.string.Ok), null);
                        }
                    }
                });
        } else {
            Util.showAlert(this, getString(R.string.We_Need_Internet), getString(R.string.Please_Connect_Internet),
                           getString(R.string.Ok),
                           null);
        }
    }

    /**
     * sending  Sign Up request to server and get the response
     *
     * @throws Exception thrown for network/response  issues
     */
    private void callSignUpAPI() throws Exception {

        if (NetworkUtil.checkInternetConnection(this)) {
            showProgressDialog(getString(R.string.Please_Wait), false);
            ApiClient.getApiInterface(this).
                signUPUserAPI(etFirstName.getText().toString().trim(),
                              etLastName.getText().toString().trim(),
                              etEmail.getText().toString(),
                              etPassword.getText().toString(),
                              "PARENT", getString(R.string.gameID), termsVersion,
                              privacyVersion, String.valueOf(cbStayUptoDate.isChecked())).
                enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            dismissProgressDialog();
                            if (Util.IS_APP_RUNNING || BaseActivity.IS_APP_RUNNING) {

                                try {
                                    if (response.body() != null) {
                                        JSONObject jsonObject = new JSONObject(response.body().string());
                                        //Logger.info("SignUp Success body", jsonObject.getString(JSONConstants.MESSAGE));
                                        Intent intent = new Intent(DWSignUpActivity.this, DWActivateAccountActivity.class);
                                        intent.putExtra(Constants.EXTRA_EMAIL, etEmail.getText().toString());
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                        startActivityForResult(intent, Numerics.ZERO);
                                    } else {
                                        if (response.errorBody() != null) {
                                            JSONObject jsonObject = new JSONObject(response.errorBody().string());
                                            //Logger.info("SignUp Error body", jsonObject.getString(JSONConstants.MESSAGE));
                                            if (jsonObject.getString(JSONConstants.MESSAGE).equals(JSONConstants.EMAIL_ALREADY_EXIST)) {
                                                Util.showAlertWarning(DWSignUpActivity.this, getString(R.string.Hmm),
                                                                      getString(R.string.Email_Already_Exists), getString(R.string.Ok), null);
                                            } else {
                                                Util.showAlertWarning(DWSignUpActivity.this, getString(R.string.Hmm),
                                                                      getString(R.string.Email_Not_Verified), getString(R.string.Ok), null);
                                            }
                                        }
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            //Logger.info("SignUp", "onFailure: " + t.toString());
                            dismissProgressDialog();
                            if (Util.IS_APP_RUNNING || BaseActivity.IS_APP_RUNNING) {

                                Util.showAlertWarning(DWSignUpActivity.this, getString(R.string.Sorry),
                                                      getString(R.string.Failure_Unknown_Case), getString(R.string.Ok), null);
                            }
                        }
                    });
        } else {
            Util.showAlert(this, getString(R.string.We_Need_Internet), getString(R.string.Please_Connect_Internet),
                           getString(R.string.Ok),
                           null);
        }
    }

    /**
     * Alert the  user with Privacy policy Dialog
     *
     * @throws Exception
     */
    private void ShowPrivacyAndTermsDialog(boolean isPrivacy) {
        Intent intent = new Intent(DWSignUpActivity.this, DWDialogActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        intent.putExtra(JSONConstants.PRIVACY, isPrivacy);
        intent.putExtra(Constants.EXTRA_IS_FROM_SIGNUP, true);
        //startActivity(intent);
        Util.keepAppAlive();
        startActivityForResult(intent, Numerics.ONE);
    }

    /**
     * Call Back method  to get the Message form other Activity
     *
     * @param requestCode request code
     * @param resultCode  result code
     * @param data        intent data from form other Activity
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Numerics.ONE && data != null && data.hasExtra(JSONConstants.MESSAGE) &&
            data.getStringExtra(JSONConstants.MESSAGE).equals(JSONConstants.SUCCESS)) {


            if (data.hasExtra(Constants.EXTRA_PRIVACY_VERSION)) {
                privacyVersion = data.getStringExtra(Constants.EXTRA_PRIVACY_VERSION);
            }
            if (data.hasExtra(Constants.EXTRA_TERMS_VERSION)) {
                termsVersion = data.getStringExtra(Constants.EXTRA_TERMS_VERSION);
            }
            if (flagPrivacy) {
                try {

                    callSignUpAPI();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                flagPrivacy = true;
                ShowPrivacyAndTermsDialog(flagPrivacy);
            }
        } else {
            flagPrivacy = false;
        }
    }


    /**
     * device back navigation control
     */
    @Override
    public void onBackPressed() {

    }

    @Override
    protected void attachBaseContext(Context base) {
//        super.attachBaseContext(LocaleHelper.onAttach(base)); // 不处理语言问题
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        if (!isChecked) {
            buttonView.setTextColor(getResources().getColor(R.color.white));
        } else {
            buttonView.setTextColor(getResources().getColor(R.color.MediumPurple));
//            if (buttonView.getId() == R.id.rbtnEnglish) {
//                LocaleHelper.persist(this, Locale.ENGLISH.getLanguage());
//            } else {
//                LocaleHelper.persist(this, Locale.SIMPLIFIED_CHINESE.getLanguage());
//            }

            Resources res = getResources();
            DisplayMetrics dm = res.getDisplayMetrics();
            Configuration conf = res.getConfiguration();
//            conf.locale = new Locale(LocaleHelper.getLanguage(DWSignUpActivity.this));
            res.updateConfiguration(conf, dm);
            onConfigurationChanged(conf);
            //  intUI();

        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        createAccountLabel.setText(getString(R.string.Create_Account));
        etFirstName.setHint(getString(R.string.Firstname));
        etLastName.setHint(getString(R.string.Lastname));
        etEmail.setHint(getString(R.string.Email));
        etPassword.setHint(getString(R.string.Password));
        etReTypePassword.setHint(getString(R.string.Retype_Password));
        SpannableString content = new SpannableString(getString(R.string.Stay_Up_To_Date_Msg));
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        cbStayUptoDate.setText(content);
        btnNext.setText(getString(R.string.Next));
        SpannableString account = new SpannableString(getString(R.string.User_Have_Account));
        account.setSpan(new UnderlineSpan(), 0, account.length(), 0);
        tvAlreadyHaveAccount.setText(account);
        rbtnEnglish.setText(getString(R.string.English));
        rbtnChinese.setText(getString(R.string.Chinese));
        super.onConfigurationChanged(newConfig);
    }

    private void updateLanButtons() {
//        if (LocaleHelper.getLanguage(this).equals(Locale.ENGLISH.getLanguage())) {
//            rbtnEnglish.setChecked(true);
//            rbtnChinese.setTextColor(getResources().getColor(R.color.white));
//            rbtnEnglish.setTextColor(getResources().getColor(R.color.MediumPurple));
//        } else {
            rbtnChinese.setChecked(true);
            rbtnChinese.setTextColor(getResources().getColor(R.color.MediumPurple));
            rbtnEnglish.setTextColor(getResources().getColor(R.color.white));
//        }
    }

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
}