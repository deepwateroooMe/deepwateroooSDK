package com.deepwaterooo.dw;

// 也相当于是一个游戏端的模拟,模拟游戏中必须的内容,两个类之间实现必要的交互 ?

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.deepwaterooo.sdk.activities.DWBaseActivity;
import com.deepwaterooo.sdk.appconfig.Constants;
import com.deepwaterooo.sdk.utils.ApiCallListener;
import com.deepwaterooo.sdk.utils.PlayerUtil;
import com.deepwaterooo.sdk.utils.SharedPrefUtil;

public class GameActivity extends DWBaseActivity implements ApiCallListener {
    private final String TAG = "GameActivity";

    private LinearLayout llLetters;
    private Button btnClose;
    private TextView tvSelChild;
    private TextView tvGameWord;
    private TextView tvNext;

    private int currentIndex = 0;
//    private int currentWord = 0;

    private int count = 0;
    private boolean isNextClicked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate() ");
        setContentView(R.layout.activity_game);

        llLetters = (LinearLayout) findViewById(R.id.llLetters);
        btnClose = (Button) findViewById(R.id.btnClose);
        tvGameWord = (TextView) findViewById(R.id.tvGameWord);
        tvSelChild = (TextView) findViewById(R.id.tvselecPlayset);
        tvNext = (TextView) findViewById(R.id.tvNext);
        SharedPrefUtil sharedPrefUtil = new SharedPrefUtil(this);

        tvSelChild.setText(sharedPrefUtil.getString(SharedPrefUtil.PREF_PLAYSET_NAME));

        btnClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        tvNext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    isNextClicked = true;
                }
            });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume() ");

//        if (PlayerUtil.getSelectedPlayer(this) != null) {
//            tvSelChild.setText("Selected Child: " + PlayerUtil.getSelectedPlayer(this).getFirstName());
//        } else {
            tvSelChild.setText("Selected Child: ");
//        }
        startActivity();
    }

    private void startActivity() {
//        if (BluetoothUtil.isPlaysetConnected() && PlayerUtil.getSelectedPlayer(this) != null) {
//            tvGameWord.setText(gameWords[count]);
//            tvGameWord.setTextColor(getResources().getColor(R.color.Black));
//            DWActivityUtil.startNewActivityWithGameWord(this, gameWords[count], originalWords[count], "1");
//            DWActivityUtil.requestNewLetterWithType(originalWords[count].substring(currentIndex, currentIndex + 1), "1");
//        }
    }

    public void bleLettersFromPlayset(String letters, byte[] hexData) {
        // super.bleLettersFromPlayset(letters, hexData);

        // boolean isEndActivity = false;
//        for (int i = 0; i < letters.length(); i++) {
//            ((TextView) llLetters.getChildAt(i)).setText(letters.charAt(i) + "");
//            if (BluetoothUtil.isPlaysetConnected() && PlayerUtil.getSelectedPlayer(this) != null && !isEndActivity && letters.trim().length() > 0) {
//                if (gameWords[count].replace("?", letters.substring(0, 1)).equals(originalWords[count])) {
//                    isEndActivity = true;
//                    DWActivityUtil.placedRequestLetter();
//                    DWActivityUtil.endActivityWithScore("1");
//                    tvGameWord.setTextColor(getResources().getColor(R.color.teal));
//                    tvGameWord.setText(originalWords[count]);
//                    Animation animation = new ScaleAnimation(1.0f, 1.5f, 1.0f, 1.5f, 0.5f, 0.5f);
//                    animation.setDuration(1000);
//                    animation.setAnimationListener(new Animation.AnimationListener() {
//                        @Override
//                        public void onAnimationStart(Animation animation) {
//
//                        }
//
//                        @Override
//                        public void onAnimationEnd(Animation animation) {
//
//                            count++;
//                            if (count >= gameWords.length) {
//                                count = 0;
//                            }
//                            startActivity();
//
//                        }
//
//                        @Override
//                        public void onAnimationRepeat(Animation animation) {
//
//                        }
//                    });
//                    tvGameWord.startAnimation(animation);
//
//                } else {
//                    tvGameWord.setTextColor(getResources().getColor(R.color.red));
//                }
//            } else {
//                tvGameWord.setTextColor(getResources().getColor(R.color.Black));
//            }
//        }
        for (int i = 0; i < letters.length(); i++) {
            ((TextView) llLetters.getChildAt(i)).setText(letters.charAt(i) + "");
        }

        if (tvNext.getVisibility() == View.VISIBLE) {
            if (letters.trim().length() > 0) {
                if (isNextClicked) {
                    isNextClicked = false;
                    Toast.makeText(this, "Please clear the letters from the playset before proceeding.", Toast.LENGTH_LONG).show();
                }
                return;
            }
            tvNext.setVisibility(View.GONE);
            startActivity();

            return;
        }

        // if (BluetoothUtil.isPlaysetConnected() && PlayerUtil.getSelectedPlayer(this) != null && !isEndActivity) {
        //     String updatedWord = "";
        //     String origWord = originalWords[count];
        //     if (letters.substring(currentIndex, currentIndex + 1).equals(origWord.substring(currentIndex, currentIndex + 1))) {

        //         updatedWord = "";
        //         for (int i = 0; i < originalWords[count].length(); i++) {
        //             if (i <= currentIndex) {
        //                 updatedWord += origWord.charAt(i);
        //             } else {
        //                 updatedWord += "?";
        //             }
        //         }
        //         tvGameWord.setText(updatedWord);
        //         DWActivityUtil.placedRequestLetter();

        //         if (currentIndex == origWord.length() - 1) {
        //             isEndActivity = true;
        //             DWActivityUtil.setGameLevel("10");
        //             DWActivityUtil.endActivityWithScore("1");
        //             Animation animation = new ScaleAnimation(1.0f, 1.5f, 1.0f, 1.5f, 0.5f, 0.5f);
        //             animation.setDuration(1000);
        //             animation.setAnimationListener(new Animation.AnimationListener() {
        //                     @Override
        //                     public void onAnimationStart(Animation animation) {

        //                     }

        //                     @Override
        //                     public void onAnimationEnd(Animation animation) {

        //                         count++;
        //                         if (count >= gameWords.length) {
        //                             count = 0;
        //                         }
        //                         tvNext.setVisibility(View.VISIBLE);
        //                     }

        //                     @Override
        //                     public void onAnimationRepeat(Animation animation) {

        //                     }
        //                 });
        //             tvGameWord.startAnimation(animation);
        //             currentIndex = 0;
        //         } else {
        //             currentIndex++;
        //             DWActivityUtil.requestNewLetterWithType(originalWords[count].substring(currentIndex, currentIndex + 1), "1");
        //         }
        //     }
//        }
    }

    @Override
    public void onBackPressed() {
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            Uri uri = data.getData();
//            NetworkUtil.uploadFile(this, apiCallListener, uri, "SP_file");
        } else if (resultCode == Constants.RESULT_PARENTAL_CHECK_SUCCESS) {
            PlayerUtil.logoutUser(this);
        }
    }
    @Override
    public void onResponse(Object object) {  }
    @Override
    public void onFailure(Object error) {  }
    public void didfinishSDKscreenflow() {
        super.didfinishSDKscreenflow();
        Toast.makeText(this, "didfinishSDKscreenflow...........", Toast.LENGTH_SHORT).show();
    }
    protected void didNavigatesToMainMenu() {
//        Toast.makeText(this, "didNavigatesToMainMenu", Toast.LENGTH_LONG).show();
        Log.d("GameActivity", ":didNavigatesToMainMenu called");
    }
    @Override
    public void didFinishSdkUserConfiguration() {
        super.didFinishSdkUserConfiguration();
//        Toast.makeText(this, "didFinishSdkUserConfiguration", Toast.LENGTH_LONG).show();
        Log.d("GameActivity", ":didFinishSdkUserConfiguration called");
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
    
// 这一堆乱七八糟的: 原本是为实现抽象基类里所申明过的接口方法,可以不要了    
    public void lettersFromPlayset(String letters, byte[] hexData) {
    }
    public void batteryLevel(String level) {
    }
    public void lcdsStates(byte[] status) {
    }
    public void connectedPlayset() {
    }
    public void availableServices() {
    }
    public void disconnectedPlayset() {
    }
    public void firmwareUpdateStatus(int progress) {
    }
    public void playsetName(String name) {
    }
    public void playsetFirmwareRevision(String rivision) {
    }
    public void playsetModelNumber(String modelNumber) {
    }
    public void playsetHardwareRevision(String hardwareRivision) {
    }
    public void playsetManufacturerName(String manufacturerName) {
    }
    public void gamePaused(boolean isScreenLocked) {
    }
}