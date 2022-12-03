package com.deepwaterooo.dwsdk.activities.authentication;

import android.content.Intent;
import android.os.Bundle;
import android.provider.SyncStateContract;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.deepwaterooo.dwsdk.R;
import com.deepwaterooo.dwsdk.activities.BaseActivity;
import com.deepwaterooo.dwsdk.appconfig.Constants;
import com.deepwaterooo.dwsdk.appconfig.Numerics;
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
 * Activity used for User account activation
 */
public class DWActivateAccountActivity extends BaseActivity implements View.OnClickListener {
    private final String TAG = "DWActivateAccountActivity";

    private Button btnResendEmail;
    private Button btnTakeMeLogin;
    private TextView tvMailSentTo;
    private String mEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activate_account);
        intUI();
    }
    /**
     * initialising the views used in this activity
     */
    private void intUI() {
        btnResendEmail = (Button) findViewById(R.id.btnResendEmail);
        btnTakeMeLogin = (Button) findViewById(R.id.btnTakeMeLogin);
        tvMailSentTo = (TextView) findViewById(R.id.tvMailSentTo);
        mEmail = getIntent().getStringExtra(Constants.EXTRA_EMAIL);
        tvMailSentTo.setText(String.format(getString(R.string.Activate_Account_Text), mEmail));
        btnResendEmail.setMinimumWidth((int) (Constants.getDeviceWidth() * 0.35));
        btnTakeMeLogin.setMinimumWidth((int) (Constants.getDeviceWidth() * 0.35));
        btnResendEmail.setOnClickListener(this);
        btnTakeMeLogin.setOnClickListener(this);
    }
    /**
     * Listener for activity UI fields click events
     *
     * @param v return clicked view
     */
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnTakeMeLogin) {
            Intent intent = new Intent(DWActivateAccountActivity.this, DWLoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            //startActivity(intent);
            startActivityForResult(intent, Numerics.ZERO);
        } else if (v.getId() == R.id.btnResendEmail) {
            try {
                btnResendEmail.setClickable(false);
                callResendEmail();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    /**
     * request the server to re-send the mail for verification
     *
     * @throws Exception
     */
    private void callResendEmail() throws Exception {
        if (NetworkUtil.checkInternetConnection(this)) {
            showProgressDialog(getString(R.string.Please_Wait), false);
            ApiClient.getApiInterface(this).resendEmailAPI(mEmail)
                .enqueue(new Callback<ResponseBody>() { // 
                    @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            dismissProgressDialog();
                            try {
                                if (response.body() != null) {
                                    JSONObject jsonObject = new JSONObject(response.body().string());
                                    //Logger.info("ResendEmail Success ", jsonObject.getString(JSONConstants.MESSAGE));
                                    Util.showAlertWarning(DWActivateAccountActivity.this, getString(R.string.Sent),
                                                          getString(R.string.Resend_Email_Message), getString(R.string.Ok), null);
                                } else {
                                    if (response.errorBody() != null) {
                                        JSONObject jsonObject = new JSONObject(response.errorBody().string());
                                        //Logger.info("ResendEmail Message ", jsonObject.getString(JSONConstants.MESSAGE));
                                        Util.showAlertWarning(DWActivateAccountActivity.this, getString(R.string.Hmm),
                                                              getString(R.string.Resend_Email_Already_Activated), getString(R.string.Ok), null);
                                    }
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            btnResendEmail.setClickable(true);
                        }
                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            //Logger.info("VerifyEmail", "onFailure: " + t.toString());
                            dismissProgressDialog();
                            Util.showAlertWarning(DWActivateAccountActivity.this, getString(R.string.Sorry),
                                                  getString(R.string.Failure_Unknown_Case), getString(R.string.Ok), null);
                            btnResendEmail.setClickable(true);
                        }
                    });
        } else {
            Util.showAlert(this, getString(R.string.We_Need_Internet), getString(R.string.Please_Connect_Internet),
                           getString(R.string.Ok),
                           null);
            btnResendEmail.setClickable(true);
        }
    }
    /**
     * device back navigation control
     */
    @Override
    public void onBackPressed() {}
}