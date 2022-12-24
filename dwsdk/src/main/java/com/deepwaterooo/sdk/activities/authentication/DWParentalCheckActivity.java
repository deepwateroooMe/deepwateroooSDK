package com.deepwaterooo.sdk.activities.authentication;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.deepwaterooo.sdk.R;
import com.deepwaterooo.sdk.activities.BaseActivity;
import com.deepwaterooo.sdk.appconfig.Constants;
import com.deepwaterooo.sdk.appconfig.Numerics;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Activity used for Parental check by showing dynamic password in letters.
 */
public class DWParentalCheckActivity extends BaseActivity implements View.OnClickListener { // 这个类可以不要,暂时不要,以为再说
    private static final String TAG = "DWParentalCheckActivity";

    private ImageButton ibtnClose, ibtnClearDigit;
    private TextView tvDigits;
    private TextView tvDigit1, tvDigit2, tvDigit3;
    private TextView tvText1, tvText2, tvText3, tvText4, tvText5, tvText6, tvText7, tvText8, tvText9, tvText0;

    private String mNumber;
    private HashMap<Character, String> mapNumberToWord = new HashMap<Character, String>();
    private Animation animation;
    private RelativeLayout llParentalCheck;
    private LinearLayout llParent;
    private List<String> numberStrings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate() start");
        setContentView(R.layout.activity_parental_check);
        intUI();
        numberStrings = Arrays.asList(getResources().getStringArray(R.array.Number));
        crateNumberTOWordMap();
        createRandomNumber();
        Log.d(TAG, "onCreate() end"); 
    }

    protected void didNavigatesToMainMenu() {

    }

    /**
     * Create a 3 digit random number
     */
    private void createRandomNumber() {
        Log.d(TAG, "createRandomNumber() start"); 
        tvDigit1.setText("");
        tvDigit2.setText("");
        tvDigit3.setText("");
        mNumber = String.valueOf(Math.round(Math.random() * 1000));
        String text = "";
        if (mNumber.length() == 3) {
            text = mapNumberToWord.get(mNumber.charAt(0)) + ", " + mapNumberToWord.get(mNumber.charAt(1)) + ", " + mapNumberToWord.get(mNumber.charAt(2));
        } else if (mNumber.length() == 2) {
            text = mapNumberToWord.get('0') + ", " + mapNumberToWord.get(mNumber.charAt(0)) + ", " + mapNumberToWord.get(mNumber.charAt(1));
        } else {
            text = mapNumberToWord.get('0') + ", " + mapNumberToWord.get('0') + ", " + mapNumberToWord.get(mNumber.charAt(0));
        }
        if (mNumber.length() == Numerics.ONE) {
            mNumber = "00" + mNumber;
        } else if (mNumber.length() == Numerics.TWO) {
            mNumber = "0" + mNumber;
        }
        Log.d(TAG, "createRandomNumber() text: " + text); 
        tvDigits.setText(text);
//        SpannableString content = new SpannableString(text);
//        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
//        tvDigits.setText(content);
        Log.d(TAG, "createRandomNumber() end"); 
    }

    /**
     * initialising the views used in this activity
     */
    private void intUI() {
        Log.d(TAG, "intUI() start"); 
        llParentalCheck = (RelativeLayout) findViewById(R.id.llParentalCheck);
        llParent = (LinearLayout) findViewById(R.id.llParent);
        tvDigits = (TextView) findViewById(R.id.tvDigits);
        tvDigit1 = (TextView) findViewById(R.id.tvDigit1);
        tvDigit2 = (TextView) findViewById(R.id.tvDigit2);
        tvDigit3 = (TextView) findViewById(R.id.tvDigit3);
        tvText1 = (TextView) findViewById(R.id.tvText1);
        tvText2 = (TextView) findViewById(R.id.tvText2);
        tvText3 = (TextView) findViewById(R.id.tvText3);
        tvText4 = (TextView) findViewById(R.id.tvText4);
        tvText5 = (TextView) findViewById(R.id.tvText5);
        tvText6 = (TextView) findViewById(R.id.tvText6);
        tvText7 = (TextView) findViewById(R.id.tvText7);
        tvText8 = (TextView) findViewById(R.id.tvText8);
        tvText9 = (TextView) findViewById(R.id.tvText9);
        tvText0 = (TextView) findViewById(R.id.tvText0);

        ibtnClose = (ImageButton) findViewById(R.id.ibtnClose);
        ibtnClearDigit = (ImageButton) findViewById(R.id.ibtnClearDigit);

        tvText1.setOnClickListener(this);
        tvText2.setOnClickListener(this);
        tvText3.setOnClickListener(this);
        tvText4.setOnClickListener(this);
        tvText5.setOnClickListener(this);
        tvText6.setOnClickListener(this);
        tvText7.setOnClickListener(this);
        tvText8.setOnClickListener(this);
        tvText9.setOnClickListener(this);
        tvText0.setOnClickListener(this);

        ibtnClose.setOnClickListener(this);
        ibtnClearDigit.setOnClickListener(this);
        runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) llParentalCheck.getLayoutParams();
                    params.width = (int) (Constants.getDeviceWidth() * 0.40 + Numerics.FORTY);
                }
            });

        llParent.postDelayed(new Runnable() {
                @Override
                public void run() {

                    llParent.setClickable(false);
                }
            }, 1000);
        Log.d(TAG, "intUI() end"); 
    }

    /**
     * Listener for activity UI fields click events
     *
     * @param v return clicked view
     */
    @Override
    public void onClick(View v) {
        Log.d(TAG, "onClick() start"); 
        String digitsText = "";
        Log.d(TAG, "onClick() (v.getId() == R.id.ibtnClose): " + (v.getId() == R.id.ibtnClose));
        Log.d(TAG, "onClick() (v.getId() == R.id.ibtnClearDigit): " + (v.getId() == R.id.ibtnClearDigit));
        if (v.getId() == R.id.ibtnClose) {
            setResult(Constants.RESULT_PARENTAL_CHECK_FAILED);
            finish();
        } else if (v.getId() == R.id.ibtnClearDigit) {
            clearDigitsText();
        } else if (v.getId() == R.id.tvText1) {
            digitsText = tvText1.getText().toString();
        } else if (v.getId() == R.id.tvText2) {
            digitsText = tvText2.getText().toString();
        } else if (v.getId() == R.id.tvText3) {
            digitsText = tvText3.getText().toString();
        } else if (v.getId() == R.id.tvText4) {
            digitsText = tvText4.getText().toString();
        } else if (v.getId() == R.id.tvText5) {
            digitsText = tvText5.getText().toString();
        } else if (v.getId() == R.id.tvText6) {
            digitsText = tvText6.getText().toString();
        } else if (v.getId() == R.id.tvText7) {
            digitsText = tvText7.getText().toString();
        } else if (v.getId() == R.id.tvText8) {
            digitsText = tvText8.getText().toString();
        } else if (v.getId() == R.id.tvText9) {
            digitsText = tvText9.getText().toString();
        } else if (v.getId() == R.id.tvText0) {
            digitsText = tvText0.getText().toString();
        }
        Log.d(TAG, "onClick() (!digitsText.isEmpty()): " + (!digitsText.isEmpty())); 
        if (!digitsText.isEmpty()) {
            Log.d(TAG, "onCl() bef setDigitsText(digitsText)"); 
            setDigitsText(digitsText);
            Log.d(TAG, "onCl() aft setDigitsText(digitsText)"); 
        }
        Log.d(TAG, "onClick() end"); 
    }

    /**
     * method convert number to corresponding word [1 -> one]
     */
    private void crateNumberTOWordMap() {
        if (mapNumberToWord != null) {
            mapNumberToWord.put('0', numberStrings.get(0));
            mapNumberToWord.put('1', numberStrings.get(1));
            mapNumberToWord.put('2', numberStrings.get(2));
            mapNumberToWord.put('3', numberStrings.get(3));
            mapNumberToWord.put('4', numberStrings.get(4));
            mapNumberToWord.put('5', numberStrings.get(5));
            mapNumberToWord.put('6', numberStrings.get(6));
            mapNumberToWord.put('7', numberStrings.get(7));
            mapNumberToWord.put('8', numberStrings.get(8));
            mapNumberToWord.put('9', numberStrings.get(9));
        }
    }

    /**
     * set text for the parent check UI views using number keyboard
     *
     * @param text text
     */
    private void setDigitsText(String text) {
        Log.d(TAG, "setDigitsText() start");
        Log.d(TAG, "setDigitsText() (TextUtils.isEmpty(tvDigit1.getText().toString())): " + (TextUtils.isEmpty(tvDigit1.getText().toString())));
        Log.d(TAG, "setDigitsText() (TextUtils.isEmpty(tvDigit2.getText().toString())): " + (TextUtils.isEmpty(tvDigit2.getText().toString())));
        Log.d(TAG, "setDigitsText() (TextUtils.isEmpty(tvDigit3.getText().toString())): " + (TextUtils.isEmpty(tvDigit3.getText().toString()))); 
        if (TextUtils.isEmpty(tvDigit1.getText().toString())) {
            tvDigit1.setText(text);
        } else if (TextUtils.isEmpty(tvDigit2.getText().toString())) {
            tvDigit2.setText(text);
        } else if (TextUtils.isEmpty(tvDigit3.getText().toString())) {
            tvDigit3.setText(text);
            String numberString = tvDigit1.getText().toString() + tvDigit2.getText().toString() + tvDigit3.getText().toString();
            String randomNumber = getDigit(String.valueOf(mNumber.charAt(0))) +
                getDigit(String.valueOf(mNumber.charAt(1))) +
                getDigit(String.valueOf(mNumber.charAt(2)));
            if (numberString.equals(randomNumber)) {
                setResult(Constants.RESULT_PARENTAL_CHECK_SUCCESS);
                finish();
            } else {
                if (animation == null) {
                    animation = AnimationUtils.loadAnimation(this, R.anim.shake_anim);
                }
                llParentalCheck.startAnimation(animation);
                createRandomNumber();
            }
        }
        Log.d(TAG, "setDigitsText() end"); 
    }

    /**
     * clear the  text for the parent check UI views
     */
    private void clearDigitsText() {
        if (!TextUtils.isEmpty(tvDigit3.getText().toString())) {
            tvDigit3.setText("");
        } else if (!TextUtils.isEmpty(tvDigit2.getText().toString())) {
            tvDigit2.setText("");
        } else if (!TextUtils.isEmpty(tvDigit1.getText().toString())) {
            tvDigit1.setText("");
        }
    }

    /**
     * English to Chinese numbers comparison table
     *
     * @param digit input number string
     * @return result number string
     */
    private String getDigit(String digit) {
        if (digit.equals("0")) {
            return getString(R.string.zero);
        } else if (digit.equals("1")) {
            return getString(R.string.one);
        } else if (digit.equals("2")) {
            return getString(R.string.two);
        } else if (digit.equals("3")) {
            return getString(R.string.three);
        } else if (digit.equals("4")) {
            return getString(R.string.four);
        } else if (digit.equals("5")) {
            return getString(R.string.five);
        } else if (digit.equals("6")) {
            return getString(R.string.six);
        } else if (digit.equals("7")) {
            return getString(R.string.seven);
        } else if (digit.equals("8")) {
            return getString(R.string.eight);
        } else if (digit.equals("9")) {
            return getString(R.string.nine);
        }
        return getString(R.string.zero);
    }

    /**
     * device back navigation control
     */
    @Override
    public void onBackPressed() {
    }
    public void gamePaused(boolean isScreenLocked) {
    }
}
