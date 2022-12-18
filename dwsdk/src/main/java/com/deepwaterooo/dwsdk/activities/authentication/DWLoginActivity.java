package com.deepwaterooo.dwsdk.activities.authentication;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
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
import com.deepwaterooo.dwsdk.beans.LoginUserDO;
import com.deepwaterooo.dwsdk.networklayer.ApiClient;
import com.deepwaterooo.dwsdk.networklayer.NetworkUtil;
import com.deepwaterooo.dwsdk.utils.LoginListener;
import com.deepwaterooo.dwsdk.utils.PlayerUtil;
import com.deepwaterooo.dwsdk.utils.SharedPrefUtil;
import com.deepwaterooo.dwsdk.utils.Util;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Activity used for User Login with specified input fields
 */
public class DWLoginActivity extends BaseActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {
    private static final String TAG = "DWLoginActivity";

    private static LoginListener loginListener;
    private EditText etUserName;
    private EditText etPassword;
    private Button btnLogin;
    private String errorMsg;
    private String errorMsgTitle;
    private TextView tvForgotPassword;
    private TextView tvCreateAccount;
    private TextView loginLabel;
    private RelativeLayout rlRootView;
    private ImageView ivSPLogo;
    private ImageView ivPanda;
    private String tokenId;
    private String parentId;
    private SharedPrefUtil sharedPrefUtil;
    private LoginUserDO loginUser;
    private Dialog dialogHelpArea;
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

    public static void setListener(LoginListener listener) {
        loginListener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState!=null){
            Util.getDeviceDimensions(this);
        }
        intUI();
        NetworkUtil.callGetAppUpdate(this);
        sharedPrefUtil = new SharedPrefUtil(this);
    }

    /**
     * initialising the views used in this activity
     */
    private void intUI() {
        setContentView(R.layout.activity_login);
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
        etUserName = (EditText) findViewById(R.id.etUserName);
        etPassword = (EditText) findViewById(R.id.etPassword);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        tvForgotPassword = (TextView) findViewById(R.id.tvForgotPassword);
        tvCreateAccount = (TextView) findViewById(R.id.tvCreateAccount);
        loginLabel = (TextView) findViewById(R.id.loginLabel);
        rbtnEnglish = (RadioButton) findViewById(R.id.rbtnEnglish);
        rbtnChinese = (RadioButton) findViewById(R.id.rbtnChinese);

        updateLanButtons();

        etUserName.setMinimumWidth((int) (Constants.getDeviceWidth() * 0.35));
        etPassword.setMinimumWidth((int) (Constants.getDeviceWidth() * 0.35));
        etUserName.setMaxWidth((int) (Constants.getDeviceWidth() * 0.35));
        etPassword.setMaxWidth((int) (Constants.getDeviceWidth() * 0.35));
        btnLogin.setMinimumWidth((int) (Constants.getDeviceWidth() * 0.25));

        btnLogin.setOnClickListener(this);
        tvForgotPassword.setOnClickListener(this);
        tvCreateAccount.setOnClickListener(this);
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

        etPassword.setOnEditorActionListener(new EditText.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView textView, int actionID, KeyEvent keyEvent) {
                    Log.d(TAG, "onEditorAction() start");
                    Log.d(TAG, "onEditorAction() (EditorInfo.IME_ACTION_DONE == actionID): " + (EditorInfo.IME_ACTION_DONE == actionID));
                    if (EditorInfo.IME_ACTION_DONE == actionID) {
                        Log.d(TAG, "onEditorAction() bef btnLogin.performClick()");
                        btnLogin.performClick();
                        Log.d(TAG, "onEditorAction() aft btnLogin.performClick()");

                    }
                    Log.d(TAG, "onEditorAction() end"); 
                    return false;
                }
            });
    }

    @Override
    protected void onResume() {
        super.onResume();
        etUserName.setText("");
        etPassword.setText("");
        etUserName.requestFocus();
        if (viewTreeObserver == null || !viewTreeObserver.isAlive()) {
            viewTreeObserver = rootView.getViewTreeObserver();
        }
        viewTreeObserver.addOnGlobalLayoutListener(listener);
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
        if (TextUtils.isEmpty(etUserName.getText().toString())) {
            errorMsg = getString(R.string.Empty_Username);
            errorMsgTitle = getString(R.string.Whoops);
            valid = false;
        } else if (!etUserName.getText().toString().matches(Constants.EMAIL_VALIDATE_PATTERN)) {
            errorMsg = getString(R.string.Invalid_Email_format);
            errorMsgTitle = getString(R.string.Oops);
            valid = false;
        } else if (TextUtils.isEmpty(etPassword.getText().toString())) {
            valid = false;
            errorMsg = getString(R.string.Empty_Password);
            errorMsgTitle = getString(R.string.Whoops);
        } else if (etPassword.getText().length() < 8) {
            valid = false;
            errorMsg = getString(R.string.Invalid_Password);
            errorMsgTitle = getString(R.string.Oops);
        }
        if (!valid)
            Util.showAlertWarning(DWLoginActivity.this, errorMsgTitle,
                                  errorMsg, getString(R.string.Ok), null);
        return valid;
    }

    /**
     * sending  login request to server and get the response
     *
     * @throws Exception thrown for network/response  issues
     */
    private void callLoginAPI() throws Exception {
        showProgressDialog(getString(R.string.Please_Wait), false);
        ApiClient
            .getApiInterface(this)
            .loginUserAPI(etUserName.getText().toString(), etPassword.getText().toString(),
                          getString(R.string.gameID)).enqueue(new Callback<LoginUserDO>() {
                                  @Override
                                  public void onResponse(Call<LoginUserDO> call, Response<LoginUserDO> response) {
                                      dismissProgressDialog();
                                      if (BaseActivity.IS_APP_RUNNING || Util.IS_APP_RUNNING) {
                                          try {
                                              if (response.body() != null) {
                                                  loginUser = (LoginUserDO) response.body();

                                                  if (loginListener != null) {
                                                      loginListener.didFinishSdkUserConfiguration();
//                                if (loginUser.getPlayer().size() == Numerics.ONE) {
//                                    loginListener.didSelectedChild(loginUser.getPlayer().get(Numerics.ZERO));
//                                }
//                                loginListener = null;
                                                  }

                                                  if (!loginUser.getTerms() && !loginUser.getPrivacy()) {
                                                      setUpGame(loginUser, true);
                                                  } else {
                                                      setUpGame(loginUser, false);
                                                      updatePolicyAndTerms();
                                                  }
                                              } else if (response.code() == 423) {
//                            Util.showAlertWarning(DWLoginActivity.this, getString(R.string.Resp_423_Title),
//                                    getString(R.string.Resp_423_msg), getString(R.string.Ok), null);
                                                  showHelpAreaDialog();
                                              } else {
                                                  if (response.errorBody() != null) {
                                                      JSONObject jsonObject = new JSONObject(response.errorBody().string());
                                                      //Logger.info("Login Error body", jsonObject.getString(JSONConstants.ERROR));
                                                      if (jsonObject.getString(JSONConstants.ERROR).equals(JSONConstants.USERNAME_PASSWORD_MISMATCH)) {
                                                          Util.showAlertWarning(DWLoginActivity.this, getString(R.string.Oops),
                                                                                getString(R.string.Invalid_Password), getString(R.string.Ok), null);
                                                          btnLogin.setClickable(true);
                                                      } else if (jsonObject.getString(JSONConstants.ERROR).equals(JSONConstants.USER_EMAIL_NOTVERIFIED)) {
                                                          Util.showAlertWarning(DWLoginActivity.this, getString(R.string.Whoops),
                                                                                getString(R.string.Email_not_verified), getString(R.string.Ok), null);
                                                          btnLogin.setClickable(true);
                                                      } else {
//                                    Util.showAlertWarning(DWLoginActivity.this, getString(R.string.Oops),
//                                            getString(R.string.User_does_not_exits), getString(R.string.Ok), null);
                                                          showHelpAreaDialog();
                                                      }

                                                  } else {
                                                      btnLogin.setClickable(true);
                                                  }

                                              }
                                          } catch (IOException e) {
                                              btnLogin.setClickable(true);
                                              e.printStackTrace();
                                          } catch (JSONException e) {
                                              btnLogin.setClickable(true);
                                              e.printStackTrace();
                                          }
                                      }
                                  }

                                  @Override
                                  public void onFailure(Call<LoginUserDO> call, Throwable t) {
                                      //Logger.info("Login", "onFailure: " + t.toString());
                                      dismissProgressDialog();
                                      btnLogin.setClickable(true);
                                      if (BaseActivity.IS_APP_RUNNING || Util.IS_APP_RUNNING) {
                                          Util.showAlertWarning(DWLoginActivity.this, getString(R.string.Sorry),
                                                                getString(R.string.Failure_Unknown_Case), getString(R.string.Ok), null);
                                      }
                                  }
                              });
    }

    /**
     * Listener for activity UI fields click events
     *
     * @param v return clicked view
     */
    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.btnLogin) {
            hideSoftKeyboard(this);
            if (validateFields()) {
                try {
                    if (NetworkUtil.checkInternetConnection(DWLoginActivity.this)) {
                        btnLogin.setClickable(false);
                        callLoginAPI();
                    } else {
                        Util.showAlert(DWLoginActivity.this, getString(R.string.We_Need_Internet),
                                       getString(R.string.Please_Connect_Internet), getString(R.string.Ok), null);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (errorMsg != null) {
            }
        } else if (v.getId() == R.id.tvCreateAccount) {
//            finish();
            /* Intent intent = new Intent(DWLoginActivity.this, SPSignUpActivity.class);
               intent.putExtra(Constants.EXTRA_IS_FROM_LOGIN, true);
               intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
               startActivityForResult(intent, Numerics.ZERO);*/
            hideSoftKeyboard(this);
        } else if (v.getId() == R.id.tvForgotPassword) {
//            finish();
            hideSoftKeyboard(this);
            Intent intent = new Intent(DWLoginActivity.this, DWForgotPasswordActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivityForResult(intent, Numerics.ZERO);
        }
    }

    /**
     * device back navigation control
     */
    @Override
    public void onBackPressed() {}

    /**
     * this method used to set up for the game
     */
    private void setUpGame(LoginUserDO loginUserDO, boolean isTermsPrivacyUpgraded) {
        Gson gson = new Gson();
//        ParentInfoDO parentInfoDO = gson.fromJson(gson.toJson(loginUserDO), ParentInfoDO.class);
//        sharedPrefUtil.setString(SharedPrefUtil.PREF_LOGIN_USER_INFO, gson.toJson(parentInfoDO));
        sharedPrefUtil.setBoolean(SharedPrefUtil.PREF_LOGIN_USER_STATUS, true);
        sharedPrefUtil.setString(SharedPrefUtil.PREF_LOGIN_USER_ID, loginUserDO.getId());
        sharedPrefUtil.setString(SharedPrefUtil.PREF_LOGIN_USER_TOKEN, loginUserDO.getTokenId());
        sharedPrefUtil.setInteger(SharedPrefUtil.PREF_PLAYERS_LIMIT, loginUserDO.getPlayerLimit());
        sharedPrefUtil.setString(SharedPrefUtil.PREF_LOGIN_USER_BUCKET_URL, loginUserDO.getBucketUrl());
//        sharedPrefUtil.setBoolean(SharedPrefUtil.PREF_LOGIN_USER_QR_STATUS, Boolean.valueOf(loginUserDO.getSettings().getQrCodeStatus()));
        if (isTermsPrivacyUpgraded) {

            if (loginUserDO.getPlayerCount() == Numerics.ZERO) {
            }
//            else {
//                //TODO: 19/6/17 needs to change logic based on Login API
//                PlayerUtil.setSelectedPlayer(this, loginUserDO.getPlayer().get(Numerics.ZERO));
//                Intent intent = new Intent(DWLoginActivity.this, SPPlaysetScanActivity.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
//                startActivityForResult(intent, Numerics.ZERO);
//            }
        }
    }

    private void updatePolicyAndTerms() {
        if (loginUser.getTerms()) {
            ShowPrivacyAndTermsDialog(false);
        } else if (loginUser.getPrivacy()) {
            ShowPrivacyAndTermsDialog(true);
        }
    }

    /**
     * Alert the  user with Privacy policy Dialog
     *
     * @throws Exception
     */
    private void ShowPrivacyAndTermsDialog(boolean isPrivacy) {
        Intent intent = new Intent(this, DWDialogActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        intent.putExtra(JSONConstants.PRIVACY, isPrivacy);
        intent.putExtra(Constants.EXTRA_IS_FROM_LOGIN, true);
        //startActivity(intent);
        Util.keepAppAlive();
        startActivityForResult(intent, Numerics.ONE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Numerics.ONE && data != null && data.hasExtra(JSONConstants.MESSAGE)) {

            if (data.getStringExtra(JSONConstants.MESSAGE).equals(JSONConstants.SUCCESS)) {

                if (data.hasExtra(Constants.EXTRA_PRIVACY_VERSION)) {
                    setUpGame(loginUser, true);
                }

                if (data.hasExtra(Constants.EXTRA_TERMS_VERSION)) {
                    if (loginUser.getPrivacy()) {
                        ShowPrivacyAndTermsDialog(true);
                    } else {
                        setUpGame(loginUser, true);
                    }
                }
            } else {
                btnLogin.setClickable(true);
                sharedPrefUtil.setString(SharedPrefUtil.PREF_LOGIN_USER_INFO, null);
                sharedPrefUtil.setBoolean(SharedPrefUtil.PREF_LOGIN_USER_STATUS, false);
                sharedPrefUtil.setString(SharedPrefUtil.PREF_LOGIN_USER_ID, null);
                sharedPrefUtil.setString(SharedPrefUtil.PREF_LOGIN_USER_TOKEN, null);
                sharedPrefUtil.setInteger(SharedPrefUtil.PREF_PLAYERS_LIMIT, 0);
            }

        } /*else if (resultCode == Constants.RESULT_PARENTAL_CHECK_SUCCESS) {
            PlayerUtil.startHelpActivity(DWLoginActivity.this);
            }*/
//        else if (resultCode == Constants.RESULT_PARENTAL_CHECK_SUCCESS && requestCode == Numerics.TWO) {
//            PlayerUtil.startHelpActivity(DWLoginActivity.this);
//        }
//        else if (resultCode == Constants.RESULT_PARENTAL_CHECK_SUCCESS && requestCode == Numerics.ZERO) {
//            PlayerUtil.loadTeacherPortalUrl(this, false);
//        }
    }

    private void showHelpAreaDialog() {
        dialogHelpArea = new Dialog(DWLoginActivity.this, R.style.MyTheme_Black_Transparent);
        dialogHelpArea.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogHelpArea.setContentView(R.layout.alert_warning_view);
        dialogHelpArea.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                                            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);

        LinearLayout llDialog = (LinearLayout) dialogHelpArea.findViewById(R.id.llDialog);
        ViewGroup.LayoutParams params = llDialog.getLayoutParams();
        params.width = (int) (Constants.getDeviceWidth() * 0.43);
        llDialog.setLayoutParams(params);
        TextView tvAlertTitle = (TextView) dialogHelpArea.findViewById(R.id.tvAlertTitle);
        TextView tvAlertText = (TextView) dialogHelpArea.findViewById(R.id.tvAlertText);
        Button btnOk = (Button) dialogHelpArea.findViewById(R.id.btnOk);

        SpannableString ss = new SpannableString(getString(R.string.Login_Mail_Not_Found_Message));
        ClickableSpan clickableSpan = new ClickableSpan() {
                @Override
                public void onClick(View textView) {
                    if (dialogHelpArea != null && dialogHelpArea.isShowing()) {
                        dialogHelpArea.dismiss();
                        btnLogin.setClickable(true);
                    }
                }

                @Override
                public void updateDrawState(TextPaint ds) {
                    super.updateDrawState(ds);
                    ds.setUnderlineText(true);
                }
            };
        int index = getString(R.string.Login_Mail_Not_Found_Message).indexOf(getString(R.string.support_mail));
        ss.setSpan(clickableSpan, index, index + getString(R.string.support_mail).length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.blue)),
                   index, index + getString(R.string.support_mail).length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        tvAlertTitle.setText(getString(R.string.Login_Mail_Not_Found_Title));
        tvAlertText.setText(ss);
        tvAlertText.setGravity(Gravity.LEFT);
        tvAlertText.setMovementMethod(LinkMovementMethod.getInstance());
        tvAlertText.setHighlightColor(Color.TRANSPARENT);
        btnOk.setText(getString(R.string.Back));
        btnOk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialogHelpArea.dismiss();
                    btnLogin.setClickable(true);
                }
            });
        dialogHelpArea.setCancelable(false);
        if (!dialogHelpArea.isShowing()) {
            dialogHelpArea.show();
        }
    }

    @Override
    protected void attachBaseContext(Context base) {
//        super.attachBaseContext(LocaleHelper.onAttach(base));
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
//            conf.locale = new Locale(LocaleHelper.getLanguage(DWLoginActivity.this));
            res.updateConfiguration(conf, dm);
            onConfigurationChanged(conf);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        loginLabel.setText(getString(R.string.Log_In));
        etUserName.setHint(getString(R.string.Email_Hint));
        etPassword.setHint(getString(R.string.Password));
        btnLogin.setText(getString(R.string.Log_In));
        SpannableString content = new SpannableString(getString(R.string.Forgot_Password));
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        tvForgotPassword.setText(content);
        SpannableString account = new SpannableString(getString(R.string.New_User_Account));
        account.setSpan(new UnderlineSpan(), 0, account.length(), 0);
        tvCreateAccount.setText(account);
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
    protected void onDestroy() {
        super.onDestroy();
        rootView = null;
        contentContainer = null;
        viewTreeObserver = null;
        if (dialogHelpArea != null && dialogHelpArea.isShowing()) {
            dialogHelpArea.dismiss();
        }
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
}