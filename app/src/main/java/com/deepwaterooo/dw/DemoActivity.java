package com.deepwaterooo.dw;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.deepwaterooo.sdk.activities.BaseActivity;
import com.deepwaterooo.sdk.appconfig.Constants;
import com.deepwaterooo.sdk.beans.PlayerDO;
import com.deepwaterooo.sdk.networklayer.NetworkUtil;
import com.deepwaterooo.sdk.utils.ApiCallListener;
import com.deepwaterooo.sdk.utils.PlayerImageListener;
import com.deepwaterooo.sdk.utils.PlayerUtil;
import com.deepwaterooo.sdk.appconfig.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

// 这个类的大致意思是说: 模拟几个游戏中会调用到要SDK中方法的按钮,回调SDK中需要启动的活动
public class DemoActivity extends BaseActivity implements ApiCallListener, PlayerImageListener {
    private final String TAG = "DemoActivity";

    private LinearLayout llLetters;
    private Button btnManagePlayset;
    private Button btnLostCharacters;
    private Button btnManagePlayers;
    private Button btnLogout;
    private Button btnSelectChild;
    private TextView tvSelChild;
    private Button btnUploadFile;
    private Button btnDownloadFile;
    private Button btnTermsAndConditions;
    private Button btnPrivacyPolicy;
    private Button btnHelp;
    private Button btnGame;
    private Button btnCredits;
    private ImageView ivSelChild;
    private boolean isClicked = false;
    private final String UPLOAD_FILE_NAME = "sp_game_upload_file.txt";
//    private static final String TAG = DemoActivity.class.getName();
    private boolean isScreenLocked = false;
    private boolean isPlayerClicked = false;
    private boolean isLogout = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);

        btnManagePlayset = (Button) findViewById(R.id.btnManagePlayset);
        btnLostCharacters = (Button) findViewById(R.id.btnLostCharacters);
        btnManagePlayers = (Button) findViewById(R.id.btnManagePlayers);
        btnLogout = (Button) findViewById(R.id.btnLogout);
        btnSelectChild = (Button) findViewById(R.id.btnSelectChild);
        btnGame = (Button) findViewById(R.id.btnGame);
        tvSelChild = (TextView) findViewById(R.id.tvSelChild);

        btnUploadFile = (Button) findViewById(R.id.btnUploadFile);
        btnDownloadFile = (Button) findViewById(R.id.btnDownloadFile);
        btnTermsAndConditions = (Button) findViewById(R.id.btnTermsNConditions);
        btnPrivacyPolicy = (Button) findViewById(R.id.btnPrivacyPolicy);
        btnCredits = (Button) findViewById(R.id.btnCredits);
        btnHelp = (Button) findViewById(R.id.btnHelp);
        ivSelChild = (ImageView) findViewById(R.id.ivSelChild);


        btnManagePlayset.setMinimumWidth((int) (Constants.getDeviceWidth() * 0.4));
        btnLostCharacters.setMinimumWidth((int) (Constants.getDeviceWidth() * 0.4));
        btnManagePlayers.setMinimumWidth((int) (Constants.getDeviceWidth() * 0.4));
        btnLogout.setMinimumWidth((int) (Constants.getDeviceWidth() * 0.3));
        btnSelectChild.setMinimumWidth((int) (Constants.getDeviceWidth() * 0.4));
        btnUploadFile.setMinimumWidth((int) (Constants.getDeviceWidth() * 0.4));
        btnDownloadFile.setMinimumWidth((int) (Constants.getDeviceWidth() * 0.4));
        btnTermsAndConditions.setMinimumWidth((int) (Constants.getDeviceWidth() * 0.4));
        btnPrivacyPolicy.setMinimumWidth((int) (Constants.getDeviceWidth() * 0.4));
        btnHelp.setMinimumWidth((int) (Constants.getDeviceWidth() * 0.4));
        btnCredits.setMinimumWidth((int) (Constants.getDeviceWidth() * 0.4));

        btnManagePlayset.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                finish();
                    if (isClicked) {
                        return;
                    }
                    isClicked = true;
                }
            });
        btnLostCharacters.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isClicked) {
                        return;
                    }
                    isClicked = true;
                }
            });
        btnManagePlayers.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isClicked) {
                        return;
                    }
                    isClicked = true;
//                    PlayerUtil.startManagePlayerActivity(DemoActivity.this, Numerics.ZERO);
                    isClicked = false;
                }
            });
        btnLogout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (isClicked) {
                        return;
                    }
                    isClicked = true;
                    PlayerUtil.logoutUser(DemoActivity.this);
                }
            });

        btnSelectChild.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isClicked) {
                        return;
                    }
                    isClicked = true;
//                    PlayerUtil.startSelectPlayerActivity(DemoActivity.this, Numerics.ZERO);
                }
            });

        btnTermsAndConditions.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isClicked) {
                        return;
                    }
                    isClicked = true;
//                    PlayerUtil.showTermsNconditions(DemoActivity.this);
                }
            });
        btnCredits.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isClicked) {
                        return;
                    }
                    isClicked = true;
//                    PlayerUtil.showCredits(DemoActivity.this);
                }
            });

        btnPrivacyPolicy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isClicked) {
                        return;
                    }
                    isClicked = true;
//                    PlayerUtil.showPrivacyPolicy(DemoActivity.this);
                }
            });

        btnUploadFile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isClicked) {
                        return;
                    }

                    isClicked = true;
                    uploadFile();
                }
            });
        btnDownloadFile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isClicked) {
                        return;
                    }
                    isClicked = true;
                    NetworkUtil.downloadFile(DemoActivity.this, DemoActivity.this, UPLOAD_FILE_NAME);
                    //http://download.thinkbroadband.com/50MB.zip
//                NetworkUtil.downloadFileWithURL(DemoActivity.this, DemoActivity.this,
//                        "http://download.thinkbroadband.com/20MB.zip");
                    isClicked = false;
                }
            });

        btnGame.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isClicked) {
                        return;
                    }
                    isClicked = true;
//                    if (PlayerUtil.getSelectedPlayer(DemoActivity.this) == null) {
//                        PlayerUtil.startSelectPlayerActivity(DemoActivity.this, 0);
//                    } else if (!BluetoothUtil.isPlaysetConnected()) {
//                        PlayerUtil.startPlaysetScanActivity(DemoActivity.this);
//                    } else {
                        Intent intent = new Intent(DemoActivity.this, GameActivity.class);
                        startActivityForResult(intent, 0);
//                    }
                }
            });

        btnHelp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    PlayerUtil.startHelpActivity(DemoActivity.this); // 这个暂时保留,可能会想要加点儿短视频guide
                }
            });

    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.e("onResume", "called");
        isClicked = false;
//        if (PlayerUtil.getSelectedPlayer(this) != null) {
//            PlayerUtil.getProfileImageURLString(this, this, PlayerUtil.getSelectedPlayer(this).getProfileURL());
//            tvSelChild.setText("Selected Player: " + PlayerUtil.getSelectedPlayer(this).getFirstName());
//        } else {
            tvSelChild.setText("Selected Player: ");
            ivSelChild.setImageBitmap(null);
//        }

        if (!isScreenLocked) {
//            if (PlayerUtil.getParentInfo(this) == null) {
//                if (!isLogout) {
                    PlayerUtil.startSplashScreenActivity(DemoActivity.this);
//                }
//                isLogout = false;
//            } else if (PlayerUtil.getSelectedPlayer(this) == null && !isPlayerClicked) {
//                isPlayerClicked = true;
//                PlayerUtil.startSelectPlayerActivity(this, true, Numerics.ZERO);
//            }
        }
        isScreenLocked = false;
    }

    public void lettersFromPlayset(String letters, byte[] hexData) {
//        Log.d(TAG, "lettersFromPlayset");
//        Toast.makeText(this, " lettersFromPlayset ", Toast.LENGTH_SHORT).show();
    }

    public void batteryLevel(String level) {
        Log.d(TAG, "batteryLevel");
    }

    public void lcdsStates(byte[] status) {
        Log.d(TAG, "lcdsStates");
    }

    public void connectedPlayset() {
        Log.d(TAG, "Conncted Playset");
    }

    public void disconnectedPlayset() {
        Log.d(TAG, "disconnected Playset");
    }

    public void firmwareUpdateStatus(int progress) {
        Log.d(TAG, "firmware Update Status");
    }

    public void playsetName(String name) {
        Log.d(TAG, "playset Name");
    }

    public void playsetFirmwareRevision(String rivision) {
        Log.d(TAG, "playset Firmware Rivision");
    }

    public void playsetModelNumber(String modelNumber) {
        Log.d(TAG, "playset Model Number");
    }

    public void playsetHardwareRevision(String hardwareRivision) {
        Log.d(TAG, "playset Hardware Rivision");
    }

    public void playsetManufacturerName(String manufacturerName) {
        Log.d(TAG, "playset Manufacturer Name");
    }

    @Override
    public void onBackPressed() {

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Constants.RESULT_PARENTAL_CHECK_SUCCESS && PlayerUtil.getParentInfo(this) == null) {
            isLogout = true;
        }
        /*if (requestCode == Constants.PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
          Uri uri = data.getData();
          NetworkUtil.uploadFile(this, apiCallListener, getUploadFilePath(), "SP_file");

          }*/// else if (resultCode == Constants.RESULT_PARENTAL_CHECK_SUCCESS) {
//            PlayerUtil.logoutUser(this);
//        }
    }

    ApiCallListener apiCallListener = new ApiCallListener() {
            @Override
            public void onResponse(Object object) {
                Logger.info("Upload file ", "Success");
            }

            @Override
            public void onFailure(Object error) {

                if (error != null) {
                    Logger.info("Upload file ", "failed. Due to " + error.toString());
                } else {
                    Logger.info("Upload file ", "failed.");

                }

            }
        };

    public void uploadFile() {
//        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//        intent.setType("*/*");
//        Util.keepAppAlive();
//        startActivityForResult(Intent.createChooser(intent, getString(R.string.SelectPicture)), Constants.PICK_IMAGE_REQUEST);
        NetworkUtil.uploadFile(this, apiCallListener, getUploadFilePath(), UPLOAD_FILE_NAME);
        isClicked = false;
    }

    @Override
    public void onResponse(Object object) {
        Logger.info(TAG, "onResponse");
    }

    @Override
    public void onFailure(Object error) {

    }

    @Override
    public void didfinishSDKscreenflow() {
    }

    protected void didNavigatesToMainMenu() {
    }

    protected void onSuccessLogoutEvent() {
    }

    @Override
    public void didFinishSdkUserConfiguration() {
        super.didFinishSdkUserConfiguration();
    }

    public void didSelectedChild(PlayerDO playerDO) {
//        super.didSelectedChild(playerDO);
        Log.d("didSelectedChild", playerDO.getFirstName());
        PlayerUtil.getProfileImageURLString(this, this, playerDO.getProfileURL());
//        Toast.makeText(this, playerDO.getFirstName(), Toast.LENGTH_SHORT).show();
    }

    /**
     * Get URI to image received from capture  by camera.
     */
    private String getUploadFilePath() {
        String outputFileUri = null;
        File getImage = Environment.getExternalStorageDirectory();
        if (getImage != null) {
            outputFileUri = new File(getImage.getPath(), UPLOAD_FILE_NAME).getPath();
        }
        File file = new File(outputFileUri);
        if (!file.exists()) {
            try {
                file.createNewFile();
                FileOutputStream fos = new FileOutputStream(file);
                byte[] data = "SP game image file for upload.".getBytes();
                fos.write(data);
                fos.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return outputFileUri;
    }

    @Override
    public void onImageLoadSuccess(Bitmap bitmap) {
        ivSelChild.setImageBitmap(bitmap);
    }

    @Override
    public void onImageLoadFailed(Bitmap errorBitmap) {
        ivSelChild.setImageBitmap(errorBitmap);
    }

    public void didClearTheCharctersOnBoard() {
    }

    public void gamePaused(boolean isScreenLocked) {
        this.isScreenLocked = isScreenLocked;
        isPlayerClicked = false;
    }
}