package com.deepwaterooo.dwsdk.activities;

/**
 * Activity used for User navigation for Login or Signup
 */
public class DWHaveAccountActivity extends BaseActivity implements OnClickListener {
    private final String TAG = "DWHaveAccountActivity";

    private Button btnYes;
    private Button btnNotYetUser;
    private SharedPrefUtil sharedPrefUtil;
    private boolean isClicked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_have_account);
        NetworkUtil.callGetAppUpdate(this);
        intUI();
        sharedPrefUtil = new SharedPrefUtil(this);
    }

    /**
     * initialising the views used in this activity
     */
    private void intUI() {
        btnYes = (Button) findViewById(R.id.btnYes);
        btnNotYetUser = (Button) findViewById(R.id.btnNotYetUser);

        btnYes.setOnClickListener(this);
        btnNotYetUser.setOnClickListener(this);
        btnYes.setMinimumWidth((int) (Constants.getDeviceWidth() * 0.20));
        btnNotYetUser.setMinimumWidth((int) (Constants.getDeviceWidth() * 0.20));
    }

    /**
     * Listener for activity UI fields click events
     *
     * @param v return clicked view
     */
    @Override
    public void onClick(View v) {

        if (isClicked) {
            return;
        }
        isClicked = true;
        if (v.getId() == R.id.btnYes) {
            Intent intent = new Intent(DWHaveAccountActivity.this, DWLoginActivity.class);
            sharedPrefUtil.setBoolean(SharedPrefUtil.PREF_DO_YOU_HAVE_ACC, true);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivityForResult(intent, Numerics.ZERO);
        } else if (v.getId() == R.id.btnNotYetUser) {
//            Intent intent = new Intent(DWHaveAccountActivity.this, DWSignUpActivity.class);
//            sharedPrefUtil.setBoolean(SharedPrefUtil.PREF_DO_YOU_HAVE_ACC, true);
//            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
//            startActivityForResult(intent, Numerics.ZERO);
            callTeacherAlert(false);
        }
        isClicked = false;
    }

    /**
     * device back navigation control
     */
    @Override
    public void onBackPressed() {
    }

    @Override
    protected void onResume() {
        super.onResume();
        isClicked = false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Constants.RESULT_PARENTAL_CHECK_SUCCESS && requestCode == Numerics.ZERO) {
            PlayerUtil.loadTeacherPortalUrl(this, false);
        }
    }
}