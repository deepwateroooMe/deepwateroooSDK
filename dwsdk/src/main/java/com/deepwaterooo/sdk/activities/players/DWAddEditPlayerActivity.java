package com.deepwaterooo.sdk.activities.players;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.PermissionChecker;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.deepwaterooo.sdk.R;
import com.deepwaterooo.sdk.activities.DWBaseActivity;
import com.deepwaterooo.sdk.adapters.AvatarsAdapter;
import com.deepwaterooo.sdk.appconfig.Constants;
import com.deepwaterooo.sdk.appconfig.JSONConstants;
import com.deepwaterooo.sdk.appconfig.Logger;
import com.deepwaterooo.sdk.appconfig.Numerics;
import com.deepwaterooo.sdk.beans.AvatarsDO;
import com.deepwaterooo.sdk.beans.PlayerDO;
import com.deepwaterooo.sdk.networklayer.ApiClient;
import com.deepwaterooo.sdk.networklayer.NetworkUtil;
import com.deepwaterooo.sdk.utils.ApiCallListener;
import com.deepwaterooo.sdk.utils.ApiCallType;
import com.deepwaterooo.sdk.utils.IOUtil;
import com.deepwaterooo.sdk.utils.LocaleHelper;
import com.deepwaterooo.sdk.utils.PlayerUtil;
import com.deepwaterooo.sdk.utils.RoundedCornersTransformation;
import com.deepwaterooo.sdk.utils.SharedPrefUtil;
import com.deepwaterooo.sdk.utils.Util;
import com.soundcloud.android.crop.Crop;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Activity used for add a player or edit a player to your account
 */

public class DWAddEditPlayerActivity extends DWBaseActivity implements View.OnClickListener, ApiCallListener {

    private LinearLayout llBottom;
    private LinearLayout llAddNewPlayer;
    private LinearLayout llAddPicture;
    private LinearLayout llMedia;
    private LinearLayout llTakePicture;
    private LinearLayout llFromLibrary;
    private LinearLayout llChooseAvatar;
    private GridView gvAvatars;
    private ImageView ivPlayerPic;
    private ImageView ivPlayerBack;
    private ImageView ivPlayerDelete;
    private Button btnCancel;
    private Button btnSavePlayer;
    private EditText etFirstName;
    private EditText etLastName;
    private EditText etParentEmail;
    private EditText etStudentGrade;
    private RadioGroup rgIEP;
    private EditText etLanguages;
    //  private TextView tvDD;
    private TextView tvMM;
    //  private TextView tvYYYY;
    private RadioGroup radioGroup;
    private TextView tvTitle;
    private TextView tvFirstName;
    private TextView tvLastName;
    private TextView tvDOB;
    private TextView tvGender;
    private TextView tvParentEmail;
    private TextView tvGrage;
    private TextView tvIEP;
    private TextView tvLanguages;
    private EditText etIEPDescription;
    private RelativeLayout rlRootView;
    // private RelativeLayout rlTeacherFields;
    private ImageView ivDOBClear;
    private EditText etStudentId;
    private TextView tvStudentId;

    private AvatarsAdapter avatarsAdapter;
    private String picturePath;
    private int imageWidth;
    private int imageHeight;
    private Transformation transformation = new RoundedCornersTransformation(Numerics.TEN, Numerics.TEN);
    private SharedPrefUtil sharedPrefUtil;
    private Calendar calendar;
    private int day;
    private int month;
    private int year;
    private int currentDay;
    private int currentMonth;
    private int currentYear;
    private DatePickerDialog datePickerDialog;
    private PlayerDO playerDO;
    private ApiCallType apiCallType;
    private boolean isPermissionGranted = true;
    private Dialog dialogDeleteReadMore;
    private boolean isCamera;
    private ArrayList<String> permissionsRequired = new ArrayList<>();

    private DatePickerDialog.OnDateSetListener myDateListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker arg0, int arg1, int arg2, int arg3) {

                if ((arg1 == currentYear && arg2 == currentMonth && arg3 <= currentDay) ||
                    (arg1 == currentYear && arg2 < currentMonth) ||
                    arg1 < currentYear) {

                    day = arg3;
                    month = arg2;
                    year = arg1;

                    showDate(arg1, arg2 + 1, arg3);
                    arg0.updateDate(year, month, day);
                } else {
                    Toast.makeText(DWAddEditPlayerActivity.this, "You can't select future date.", Toast.LENGTH_LONG).show();
                }

                datePickerDialog.dismiss();
            }
        };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_player);
        sharedPrefUtil = new SharedPrefUtil(this);

        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        currentYear = year;
        currentMonth = month;
        currentDay = day;


        Bitmap bitmapDrawable = BitmapFactory.decodeResource(getResources(), R.drawable.add_picture);
        imageWidth = bitmapDrawable.getWidth();
        imageHeight = bitmapDrawable.getHeight();
        initUI();

        avatarsAdapter = new AvatarsAdapter(this, null);
        gvAvatars.setAdapter(avatarsAdapter);
        gvAvatars.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    picturePath = ((AvatarsDO) view.getTag()).getAssetURL();
                    ivPlayerPic.setTag(picturePath);
                    Picasso.with(DWAddEditPlayerActivity.this).load(picturePath).transform(transformation).into(ivPlayerPic);
                    llAddNewPlayer.setVisibility(View.VISIBLE);
                    llAddPicture.setVisibility(View.GONE);
                }
            });

        try {
            getGenericAvatars();
        } catch (Exception e) {
            e.printStackTrace();
        }

       if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
           if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
               isPermissionGranted = false;
               Util.keepAppAlive();
               requestWriteExternalStoragePermission();
           }
       }
    }

    /**
     * initialising the views used in this activity
     */
    private void initUI() {
        llBottom = (LinearLayout) findViewById(R.id.llBottom);
        llAddNewPlayer = (LinearLayout) findViewById(R.id.llAddNewPlayer);
        llAddPicture = (LinearLayout) findViewById(R.id.llAddPicture);
        llMedia = (LinearLayout) findViewById(R.id.llMedia);
        llTakePicture = (LinearLayout) findViewById(R.id.llTakePicture);
        llFromLibrary = (LinearLayout) findViewById(R.id.llFromLibrary);
        llChooseAvatar = (LinearLayout) findViewById(R.id.llChooseAvatar);
        gvAvatars = (GridView) findViewById(R.id.gvAvatars);
        ivPlayerPic = (ImageView) findViewById(R.id.ivPlayerPic);
        ivPlayerBack = (ImageView) findViewById(R.id.ivPlayerBack);
        btnCancel = (Button) findViewById(R.id.btnCancel);
        btnSavePlayer = (Button) findViewById(R.id.btnSavePlayer);
        etFirstName = (EditText) findViewById(R.id.etAPFirstName);
        etLastName = (EditText) findViewById(R.id.etLastName);
        //  tvDD = (TextView) findViewById(R.id.tvDD);
        tvMM = (TextView) findViewById(R.id.tvMM);
        //  tvYYYY = (TextView) findViewById(R.id.tvYYYY);
        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        ivPlayerDelete = (ImageView) findViewById(R.id.ivPlayerDelete);
        tvTitle = (TextView) findViewById(R.id.tvTitle);
        rlRootView = (RelativeLayout) findViewById(R.id.rlRootView);
        etParentEmail = (EditText) findViewById(R.id.etParentEmail);
        etStudentGrade = (EditText) findViewById(R.id.etGrade);
        etLanguages = (EditText) findViewById(R.id.etLanguage);
        rgIEP = (RadioGroup) findViewById(R.id.rgIEP);
        tvFirstName = (TextView) findViewById(R.id.tvFirstName);
        tvLastName = (TextView) findViewById(R.id.tvLastName);
        tvDOB = (TextView) findViewById(R.id.tvDObTitle);
        tvGender = (TextView) findViewById(R.id.tvGender);
        tvParentEmail = (TextView) findViewById(R.id.tvParentEmail);
        tvGrage = (TextView) findViewById(R.id.tvStudentGrade);
        tvIEP = (TextView) findViewById(R.id.tvIEP);
        tvLanguages = (TextView) findViewById(R.id.tvLanguages);
        etIEPDescription = (EditText) findViewById(R.id.etDescription);
        // rlTeacherFields = (RelativeLayout) findViewById(R.id.rlTeacherFields);
        ivDOBClear = (ImageView) findViewById(R.id.ivDobClear);
        etStudentId = (EditText) findViewById(R.id.etStudentID);
        tvStudentId = (TextView) findViewById(R.id.tvStudentID);
        //we assign default DOB as empty
        showDate(Integer.valueOf(-1), Integer.valueOf(-1), Integer.valueOf(-1));


        if (getIntent().hasExtra(Constants.EXTRA_PLAYER)) {
            playerDO = getIntent().getExtras().getParcelable(Constants.EXTRA_PLAYER);

            if (!TextUtils.isEmpty(playerDO.getProfileURL())) {
                Picasso.with(DWAddEditPlayerActivity.this).load(playerDO.getProfileURL()).transform(transformation).into(ivPlayerPic);
                ivPlayerPic.setTag(playerDO.getProfileURL());
            } else {
                Picasso.with(DWAddEditPlayerActivity.this).load(R.drawable.default_avatar).transform(transformation).into(ivPlayerPic);
            }
            etFirstName.setText(playerDO.getFirstName());
            etLastName.setText(playerDO.getLastName());
            etParentEmail.setText(playerDO.getParentEmail());
            etStudentId.setText(playerDO.getStudentID());
            etStudentGrade.setText(playerDO.getGrade());
            etIEPDescription.setText(playerDO.getIEPDescription());
            etLanguages.setText(playerDO.getLanguage());

            if (playerDO.getGender() != null && playerDO.getGender().equals("M")) {
                ((RadioButton) radioGroup.getChildAt(Numerics.ONE)).setChecked(true);
            } else if (playerDO.getGender() != null && playerDO.getGender().equals("F")) {
                ((RadioButton) radioGroup.getChildAt(Numerics.ZERO)).setChecked(true);
            } else {
                ((RadioButton) radioGroup.getChildAt(Numerics.TWO)).setChecked(true);
            }
            if (playerDO.getIEP() != null && playerDO.getIEP().equals("YES")) {
                ((RadioButton) rgIEP.getChildAt(Numerics.ZERO)).setChecked(true);
                etIEPDescription.setVisibility(View.VISIBLE);
            } else {
                ((RadioButton) rgIEP.getChildAt(Numerics.ONE)).setChecked(true);
                etIEPDescription.setVisibility(View.GONE);
            }

            //2014-03-20T00:00:00.000Z
            if (!TextUtils.isEmpty(playerDO.getDateofBirth()) && playerDO.getDateofBirth().contains("T")) {
                String date[] = playerDO.getDateofBirth().split("T");
                if (date != null && date.length == 2 && date[0].contains("-")) {
                    String day[] = date[0].split("-");
                    if (day != null && day.length == 3) {
                        showDate(Integer.valueOf(day[0]), Integer.valueOf(day[1]), Integer.valueOf(day[2]));
                    } else {
                        showDate(Integer.valueOf(-1), Integer.valueOf(-1), Integer.valueOf(-1));
                    }
                } else {
                    showDate(Integer.valueOf(-1), Integer.valueOf(-1), Integer.valueOf(-1));
                }
            } else {
                showDate(Integer.valueOf(-1), Integer.valueOf(-1), Integer.valueOf(-1));
            }

            ivPlayerDelete.setVisibility(View.VISIBLE);
            btnSavePlayer.setText(getString(R.string.Save));
            tvTitle.setText(getString(R.string.Edit_Player));
        } else {
//            ((RadioButton) radioGroup.getChildAt(Numerics.ZERO)).setChecked(true);
            Picasso.with(DWAddEditPlayerActivity.this).load(R.drawable.default_avatar).transform(transformation).into(ivPlayerPic);
        }

        llTakePicture.setOnClickListener(this);
        llFromLibrary.setOnClickListener(this);
        llChooseAvatar.setOnClickListener(this);
        ivPlayerPic.setOnClickListener(this);
        ivPlayerBack.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        btnSavePlayer.setOnClickListener(this);
        // tvDD.setOnClickListener(this);
        tvMM.setOnClickListener(this);
        // tvYYYY.setOnClickListener(this);
        ivPlayerDelete.setOnClickListener(this);
        ivDOBClear.setOnClickListener(this);

        runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) llAddNewPlayer.getLayoutParams();
                    params.width = (int) (Constants.getDeviceWidth() * 0.65);
                    llAddNewPlayer.setLayoutParams(params);

                    RelativeLayout.LayoutParams imageParams = (RelativeLayout.LayoutParams) ivPlayerPic.getLayoutParams();
                    imageParams.width = imageWidth;
                    imageParams.height = imageHeight;
                    ivPlayerPic.setLayoutParams(imageParams);

                    llBottom.invalidate();
                }
            });

        rlRootView = (RelativeLayout) findViewById(R.id.rlRootView);

        rlRootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    int heightDiff = rlRootView.getRootView().getHeight() - rlRootView.getHeight();
                    if (heightDiff > Util.dpToPx(DWAddEditPlayerActivity.this, Numerics.HUNDRED * Numerics.TWO)) {
                        // if more than 200 dp, it's probably a keyboard...
                    } else {
                        hideSystemUI();
                    }
                }
            });
        rgIEP.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup radioGroup, int i) {
                    if (i == R.id.rbtnYes) {
                        etIEPDescription.setVisibility(View.VISIBLE);
                    } else {
                        etIEPDescription.setVisibility(View.GONE);
                    }
                }
            });

        updateUI();
        if (LocaleHelper.getLanguage(this).equals(Locale.SIMPLIFIED_CHINESE.getLanguage())) {
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) rgIEP.getLayoutParams();
            params.addRule(RelativeLayout.RIGHT_OF, R.id.tvIEP);
        }
    }

    private void updateUI() {
        if (PlayerUtil.getParentInfo(this).getRole().equalsIgnoreCase(JSONConstants.TEACHER)) {
            runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (PlayerUtil.getParentInfo(DWAddEditPlayerActivity.this).getRole().equalsIgnoreCase(JSONConstants.TEACHER)) {
                            tvLastName.setMinWidth(tvParentEmail.getMeasuredWidth());
                            tvDOB.setMinWidth(tvParentEmail.getMeasuredWidth());
                            tvGender.setMinWidth(tvParentEmail.getMeasuredWidth());
                            tvFirstName.setMinWidth(tvParentEmail.getMeasuredWidth());
                            tvParentEmail.setMinWidth(tvParentEmail.getMeasuredWidth());
                            tvStudentId.setMinimumWidth(tvParentEmail.getMeasuredWidth());
                            tvIEP.setMinWidth(tvParentEmail.getMeasuredWidth());
                            tvLanguages.setMinWidth(tvParentEmail.getMeasuredWidth());
                        }
                    }
                });
        } else {
            tvLastName.setVisibility(View.GONE);
            etLastName.setVisibility(View.GONE);
            //  rlTeacherFields.setVisibility(View.GONE);
            hideTeacherFields();
            ((RadioButton) findViewById(R.id.rbtnNo)).setChecked(true);
        }
        if (getIntent().hasExtra(Constants.EXTRA_PLAYER)) {
            playerDO = getIntent().getExtras().getParcelable(Constants.EXTRA_PLAYER);
            if (!TextUtils.isEmpty(playerDO.getProfileURL())) {
                Picasso.with(DWAddEditPlayerActivity.this).load(playerDO.getProfileURL()).transform(transformation).into(ivPlayerPic);
                ivPlayerPic.setTag(playerDO.getProfileURL());
            }
            etFirstName.setText(playerDO.getFirstName());
            if (playerDO.getGender().equals("M")) {
                ((RadioButton) radioGroup.getChildAt(Numerics.ONE)).setChecked(true);
            } else if (playerDO.getGender().equals("F")) {
                ((RadioButton) radioGroup.getChildAt(Numerics.ZERO)).setChecked(true);
            } else {
                ((RadioButton) radioGroup.getChildAt(Numerics.TWO)).setChecked(true);
            }
            //2014-03-20T00:00:00.000Z
            if (!TextUtils.isEmpty(playerDO.getDateofBirth()) && playerDO.getDateofBirth().contains("T")) {
                String date[] = playerDO.getDateofBirth().split("T");
                if (date != null && date.length == 2 && date[0].contains("-")) {
                    String day[] = date[0].split("-");
                    if (day != null && day.length == 3) {
                        showDate(Integer.valueOf(day[0]), Integer.valueOf(day[1]), Integer.valueOf(day[2]));
                    } else {
                        showDate(Integer.valueOf(-1), Integer.valueOf(-1), Integer.valueOf(-1));
                    }
                } else {
                    showDate(Integer.valueOf(-1), Integer.valueOf(-1), Integer.valueOf(-1));
                }
            } else {
                showDate(Integer.valueOf(-1), Integer.valueOf(-1), Integer.valueOf(-1));
            }
            if (PlayerUtil.getParentInfo(this).getRole().equalsIgnoreCase(JSONConstants.TEACHER)) {
                /* etLastName.setText(playerDO.getLastName());
                   etParentEmail.setText(playerDO.getParentEmail());
                   etStudentGrade.setText(playerDO.getStudentGrade());
                   if (!TextUtils.isEmpty(playerDO.getIEP())) {
                   ((RadioButton) rgIEP.findViewWithTag(playerDO.getIEP())).setChecked(true);
                   }
                   etLanguages.setText(playerDO.getLanguages());*/
            }
            ivPlayerDelete.setVisibility(View.VISIBLE);
            btnSavePlayer.setText(getString(R.string.Save));
            tvTitle.setText(getString(R.string.Edit_Player));
        } else {
            ((RadioButton) radioGroup.getChildAt(Numerics.TWO)).setChecked(true);
        }
    }

    /**
     * If user is parent, teacher fields are hiding here.
     */
    public void hideTeacherFields(){
        tvParentEmail.setVisibility(View.GONE);
        tvGrage.setVisibility(View.GONE);
        tvIEP.setVisibility(View.GONE);
        tvLanguages.setVisibility(View.GONE);
        tvStudentId.setVisibility(View.GONE);
        etParentEmail.setVisibility(View.GONE);
        etStudentId.setVisibility(View.GONE);
        etStudentGrade.setVisibility(View.GONE);
        etIEPDescription.setVisibility(View.GONE);
        etLanguages.setVisibility(View.GONE);
        rgIEP.setVisibility(View.GONE);
    }

    /**
     * Listener for activity UI fields click events
     *
     * @param v return clicked view
     */
    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.ivPlayerPic) {
            hideSoftKeyboard(this);
            RelativeLayout.LayoutParams param = (RelativeLayout.LayoutParams) llAddPicture.getLayoutParams();
            param.width = (int) (Constants.getDeviceWidth() * 0.57);
            param.height = (int) (llAddNewPlayer.getHeight());
            llAddPicture.setLayoutParams(param);
            llAddNewPlayer.setVisibility(View.GONE);
            llAddPicture.setVisibility(View.VISIBLE);

            llMedia.setVisibility(View.VISIBLE);
            gvAvatars.setVisibility(View.GONE);

        } else if (v.getId() == R.id.llTakePicture) {
            if (permissionsRequired != null && permissionsRequired.size() > 0) {
                permissionsRequired.clear();
            }else{
                permissionsRequired = new ArrayList<>();
            }
            permissionsRequired.add(Manifest.permission.READ_EXTERNAL_STORAGE);
            permissionsRequired.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            permissionsRequired.add(Manifest.permission.CAMERA);
            isCamera = true;
            checkPermissions();
        } else if (v.getId() == R.id.llFromLibrary) {
            if (permissionsRequired != null && permissionsRequired.size() > 0) {
                permissionsRequired.clear();
            }else{
                permissionsRequired = new ArrayList<>();
            }
            permissionsRequired.add(Manifest.permission.READ_EXTERNAL_STORAGE);
            permissionsRequired.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            isCamera = false;
            checkPermissions();

        } else if (v.getId() == R.id.llChooseAvatar) {

            llMedia.setVisibility(View.GONE);
            gvAvatars.setVisibility(View.VISIBLE);

        } else if (v.getId() == R.id.ivPlayerBack) {

            if (llMedia.getVisibility() == View.VISIBLE) {
                llAddNewPlayer.setVisibility(View.VISIBLE);
                llAddPicture.setVisibility(View.GONE);
            } else {
                llMedia.setVisibility(View.VISIBLE);
                gvAvatars.setVisibility(View.GONE);
            }

        } else if (v.getId() == R.id.btnSavePlayer) {

            if (ValidateFields()) {
                try {
                    btnSavePlayer.setClickable(false);
                    if (playerDO == null) {
                        apiCallType = ApiCallType.ADD_PLAYER;
                        callAddChildApi(sharedPrefUtil.getString(SharedPrefUtil.PREF_LOGIN_USER_TOKEN),
                                        sharedPrefUtil.getString(SharedPrefUtil.PREF_LOGIN_USER_ID));
                    } else {
                        if (PlayerUtil.getParentInfo(this).getRole().equalsIgnoreCase(JSONConstants.TEACHER)) {
                            apiCallType = ApiCallType.EDIT_STUDENT;
                            callEditStudentApi(sharedPrefUtil.getString(SharedPrefUtil.PREF_LOGIN_USER_TOKEN),
                                               sharedPrefUtil.getString(SharedPrefUtil.PREF_LOGIN_USER_ID));
                        }else{
                            apiCallType = ApiCallType.EDIT_PLAYER;
                            callEditChildApi(sharedPrefUtil.getString(SharedPrefUtil.PREF_LOGIN_USER_TOKEN),
                                             sharedPrefUtil.getString(SharedPrefUtil.PREF_LOGIN_USER_ID));
                        }

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else if (v.getId() == R.id.btnCancel) {
            finish();
        } else if (v.getId() == R.id.tvDD || v.getId() == R.id.tvMM || v.getId() == R.id.tvYYYY) {
            hideSoftKeyboard(this);
            if (datePickerDialog == null) {
                if (year == -1 || month == -1 || day == -1) {
                    datePickerDialog = new DatePickerDialog(this, myDateListener, currentYear, currentMonth, currentDay);
                } else {
                    datePickerDialog = new DatePickerDialog(this, myDateListener, year, month - 1, day);
                }
            }
            if (day != -1) {
                datePickerDialog.updateDate(year, month - 1, day);
            }
            Calendar calendar = Calendar.getInstance();
            datePickerDialog.getDatePicker().setMaxDate(calendar.getTimeInMillis() + 1000 * 60 * 5);

            datePickerDialog.show();
        } else if (v.getId() == R.id.ivPlayerDelete) {
            if (NetworkUtil.checkInternetConnection(this)) {
                showDeletePlayerAlert(this);
            } else {
                Util.showAlert(this, getString(R.string.We_Need_Internet), getString(R.string.Please_Connect_Internet), getString(R.string.Ok),
                               null);
            }
        } else if (v.getId() == R.id.ivDobClear) {
            showDate(Integer.valueOf(-1), Integer.valueOf(-1), Integer.valueOf(-1));
        }
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
        // handle result of pick image chooser
        if (requestCode == Constants.PICK_IMAGE_CHOOSER_REQUEST_CODE && resultCode == Activity.RESULT_OK && checkWriteExternalPermission()) {
            Uri imageUri = null;
            if (data != null && data.getData() instanceof Uri) {
                imageUri = data.getData();
            } else {
                imageUri = getCaptureCropImageOutputUri(Constants.PIC_IMAGE_NAME);
            }
            Crop.of(imageUri, getCaptureCropImageOutputUri(Constants.CROP_IMAGE_NAME)).asSquare().start(this);
            Util.keepAppAlive();
        } else if (requestCode == Crop.REQUEST_CROP && checkWriteExternalPermission()) {
            if (resultCode == RESULT_OK) {
                picturePath = getCaptureCropImageOutputUri(Constants.CROP_IMAGE_NAME).getPath();
                Picasso.with(this).invalidate(getCaptureCropImageOutputUri(Constants.CROP_IMAGE_NAME));
                Picasso.with(this).load(Uri.fromFile(new File(picturePath))).transform(transformation).into(ivPlayerPic);
                ivPlayerPic.setTag(picturePath);
                llAddNewPlayer.setVisibility(View.VISIBLE);
                llAddPicture.setVisibility(View.GONE);
            } else {
                Log.e("Image Crop Selection", "Failed");
            }
        } else if (resultCode == Constants.RESULT_PARENTAL_CHECK_SUCCESS) {
            //disconnect playset if we leave game
            //Util.keepAppAlive();
            PlayerUtil.startHelpActivity(DWAddEditPlayerActivity.this);
        }

    }

    @Override
    protected void didNavigatesToMainMenu() {

    }

    /**
     * getting generic Avatars from the server for creating/editing child
     *
     * @throws Exception thrown for network/response  issues
     */
    private void getGenericAvatars() throws Exception {
        ApiClient.getApiInterface(this).getGenericAvatars(JSONConstants.AUTHORIZATION_BEARER +
                                                          sharedPrefUtil.getString(SharedPrefUtil.PREF_LOGIN_USER_TOKEN),
                                                          PlayerUtil.getParentInfo(this).getId()).enqueue(new Callback<List<AvatarsDO>>() {
                                                                  @Override
                                                                  public void onResponse(Call<List<AvatarsDO>> call, Response<List<AvatarsDO>> response) {

                                                                      try {

                                                                          if (response != null && response.body() != null) {
                                                                              avatarsAdapter.refreshData(response.body());
                                                                          } else if (response.code() == Constants.RESPONSE_CODE_UNAUTHORIZED) {
                                                                              NetworkUtil.callReAuthenticationAPI(DWAddEditPlayerActivity.this, new ApiCallListener() {
                                                                                      @Override
                                                                                      public void onResponse(Object object) {

                                                                                          if (object == null) {
                                                                                              try {
                                                                                                  getGenericAvatars();
                                                                                              } catch (Exception e) {
                                                                                                  e.printStackTrace();
                                                                                              }
                                                                                          }
                                                                                      }

                                                                                      @Override
                                                                                      public void onFailure(Object error) {

                                                                                      }
                                                                                  }, true);
                                                                          }
                                                                      } catch (Exception e) {
                                                                          Logger.debug("avatars exception", e.getMessage() + "");
                                                                      }
                                                                  }

                                                                  @Override
                                                                  public void onFailure(Call<List<AvatarsDO>> call, Throwable t) {

                                                                  }
                                                              });
    }

    /**
     * This method used to add child/player to login user
     *
     * @param tokenId  authentication token
     * @param parentId login user id
     * @throws Exception thrown for network/response  issues
     */
    private void callAddChildApi(String tokenId, String parentId) throws Exception {

        if (NetworkUtil.checkInternetConnection(this)) {
            String base64 = "";
            String profileUrl = "";

            if (ivPlayerPic.getTag() != null && !ivPlayerPic.getTag().toString().startsWith("http")) {

                base64 = IOUtil.getBase64Scaled(ivPlayerPic.getTag().toString(), Numerics.TWO_HUNDRED_FORTY, Numerics.TWO_HUNDRED);
            } else if (ivPlayerPic.getTag() != null) {
                profileUrl = ivPlayerPic.getTag().toString();
            }

            showProgressDialog(null);

            RadioButton btn = (RadioButton) radioGroup.findViewById(radioGroup.getCheckedRadioButtonId());
            String dob = "";
            if (month == -1 || year == -1 || day == -1) {
                dob = null; //mm-dd-yyyy
            } else {
                dob = ((month) < Numerics.TEN ? ("0" + (month)) : (month)) + "-" +
                    (day < Numerics.TEN ? ("0" + (day)) : day) + "-" + year;
            }

            ApiClient.getApiInterface(this).addChildAPI(JSONConstants.AUTHORIZATION_BEARER + tokenId,
                                                        parentId, etFirstName.getText().toString().trim(), dob, btn.getTag().toString(), "", profileUrl, base64, getString(R.string.gameID)).
                enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            dismissProgressDialog();
                            btnSavePlayer.setClickable(true);
                            try {
                                if (response.body() != null) {
                                    setResult(RESULT_OK);
                                    finish();
                                } else {
                                    if (response.errorBody() != null) {
                                        if (response.code() == Constants.RESPONSE_CODE_UNAUTHORIZED) {
                                            NetworkUtil.callReAuthenticationAPI(DWAddEditPlayerActivity.this, DWAddEditPlayerActivity.this, true);
                                        }
                                    }
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            dismissProgressDialog();
                            btnSavePlayer.setClickable(true);
                            t.printStackTrace();
                        }
                    });
        } else {
            Util.showAlert(this, getString(R.string.We_Need_Internet), getString(R.string.Please_Connect_Internet), getString(R.string.Ok),
                           null);
            btnSavePlayer.setClickable(true);
        }
    }

    /**
     * This method used to Edit existing child/player to login user
     *
     * @param tokenId  authentication token
     * @param parentId login user id
     * @throws Exception thrown for network/response  issues
     */
    private void callEditChildApi(String tokenId, String parentId) throws Exception {

        if (NetworkUtil.checkInternetConnection(this)) {
            String base64 = "";
            String profileUrl = "";

            if (ivPlayerPic.getTag() != null && !ivPlayerPic.getTag().toString().startsWith("http")) {

                base64 = IOUtil.getBase64Scaled(ivPlayerPic.getTag().toString(), Numerics.TWO_HUNDRED_FORTY, Numerics.TWO_HUNDRED);
            } else if (ivPlayerPic.getTag() != null) {
                profileUrl = ivPlayerPic.getTag().toString();
            }

            showProgressDialog(null);

            RadioButton btn = (RadioButton) radioGroup.findViewById(radioGroup.getCheckedRadioButtonId());
            String dob = "";
            if (year == -1 || month == -1 || day == -1) {
                dob = "";
            } else {
                dob = ((month) < Numerics.TEN ? ("0" + (month)) : (month)) + "-" +
                    (day < Numerics.TEN ? ("0" + (day)) : day) + "-" + year;
            }

            ApiClient.getApiInterface(this).editChildAPI(JSONConstants.AUTHORIZATION_BEARER + tokenId,
                                                         parentId, playerDO.getId(), etFirstName.getText().toString().trim(), dob, btn.getTag().toString(), "", profileUrl, base64).
                enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            dismissProgressDialog();
                            btnSavePlayer.setClickable(true);
                            try {
                                if (response.body() != null) {
                                    PlayerDO prevSelPlayerDO = PlayerUtil.getSelectedPlayer(DWAddEditPlayerActivity.this);
                                    if (prevSelPlayerDO != null && prevSelPlayerDO.getId().equals(playerDO.getId())) {
                                        prevSelPlayerDO.setFirstName(etFirstName.getText().toString());
                                        PlayerUtil.setSelectedPlayer(DWAddEditPlayerActivity.this, prevSelPlayerDO);
                                    }
                                    setResult(RESULT_OK);
                                    finish();
                                    //{"message":"Data updated successfully."}
                                } else {
                                    if (response.errorBody() != null) {
                                        if (response.code() == Constants.RESPONSE_CODE_UNAUTHORIZED) {
                                            NetworkUtil.callReAuthenticationAPI(DWAddEditPlayerActivity.this, DWAddEditPlayerActivity.this, true);
                                        }
                                        JSONObject jsonObject = new JSONObject(response.errorBody().string());
                                    }
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            dismissProgressDialog();
                            btnSavePlayer.setClickable(true);
                            t.printStackTrace();
                        }
                    });
        } else {
            Util.showAlert(this, getString(R.string.We_Need_Internet), getString(R.string.Please_Connect_Internet), getString(R.string.Ok),
                           null);
            btnSavePlayer.setClickable(true);

        }
    }

    /**
     * This method used to delete the selected player
     *
     * @param tokenId  authentication token
     * @param parentId login user id
     * @throws Exception thrown for network/response  issues
     */
    private void callDeleteChildApi(String tokenId, String parentId) throws Exception {
        if (NetworkUtil.checkInternetConnection(this)) {
            showProgressDialog(null);
            ApiClient.getApiInterface(this).deleteChildAPI(JSONConstants.AUTHORIZATION_BEARER + tokenId,
                                                           parentId, playerDO.getId()).enqueue(new Callback<ResponseBody>() {
                                                                   @Override
                                                                   public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                                                       dismissProgressDialog();
                                                                       try {
                                                                           if (response.body() != null) {
                                                                               if (playerDO != null && PlayerUtil.getSelectedPlayer(DWAddEditPlayerActivity.this) != null) {
                                                                                   if (playerDO.getId().equals(PlayerUtil.getSelectedPlayer(DWAddEditPlayerActivity.this).getId())) {
                                                                                       PlayerUtil.setSelectedPlayer(DWAddEditPlayerActivity.this, null);
                                                                                   }
                                                                               }
                                                                               setResult(RESULT_OK);
                                                                               finish();
                                                                           } else {
                                                                               if (response.errorBody() != null) {
                                                                                   if (response.code() == Constants.RESPONSE_CODE_UNAUTHORIZED) {
                                                                                       NetworkUtil.callReAuthenticationAPI(DWAddEditPlayerActivity.this, DWAddEditPlayerActivity.this, true);
                                                                                   }
                                                                                   JSONObject jsonObject = new JSONObject(response.errorBody().string());
                                                                                   if (!jsonObject.getString(JSONConstants.ERROR).isEmpty() &&
                                                                                       jsonObject.getString(JSONConstants.ERROR).equals(JSONConstants.STUDENT_ALREADY_DELETED)) {
                                                                                       setResult(RESULT_OK);
                                                                                       finish();
                                                                                   }
                                                                               }
                                                                           }
                                                                       } catch (IOException e) {
                                                                           e.printStackTrace();
                                                                       } catch (JSONException e) {
                                                                           e.printStackTrace();
                                                                       } catch (Exception e) {
                                                                           e.printStackTrace();
                                                                       }
                                                                   }

                                                                   @Override
                                                                   public void onFailure(Call<ResponseBody> call, Throwable t) {
                                                                       dismissProgressDialog();
                                                                       t.printStackTrace();
                                                                   }
                                                               });
        } else {
            Util.showAlert(this, getString(R.string.We_Need_Internet), getString(R.string.Please_Connect_Internet), getString(R.string.Ok),
                           null);
        }

    }

    /**
     * return true if the given input fields are valid
     *
     * @return true if the given input fields are valid
     */
    private boolean ValidateFields() {
        String errorMsg = "";
        String errorTitle = "";
        boolean isValid = true;
        if (PlayerUtil.getParentInfo(this).getRole().equalsIgnoreCase(JSONConstants.PARENT)) {
            if (etFirstName.getText().toString().trim().length() == Numerics.ZERO) {
                isValid = false;
                errorMsg = getString(R.string.EnterPlayerFirstName);
                errorTitle = getString(R.string.Whoops);
                etFirstName.setText("");
            } else if (tvMM.getText().toString().length() == Numerics.ZERO) {
                isValid = false;
                errorMsg = getString(R.string.EnterPlayerBirthday);
                errorTitle = getString(R.string.Whoops);

            } else if (radioGroup.getCheckedRadioButtonId() == -1) {
                isValid = false;
                errorMsg = getString(R.string.EnterPlayerGender);
                errorTitle = getString(R.string.Whoops);
            }
        } else {
            if (etFirstName.getText().toString().trim().length() == Numerics.ZERO) {
                isValid = false;
                errorMsg = getString(R.string.EnterPlayerFirstName);
                errorTitle = getString(R.string.Whoops);
                etFirstName.setText("");
            } else if (etLastName.getText().toString().trim().length() == Numerics.ZERO) {
                isValid = false;
                errorMsg = getString(R.string.EnterPlayerLastName);
                errorTitle = getString(R.string.Whoops);
                etLastName.setText("");
            } else if (etParentEmail.getText().toString().trim().length() != Numerics.ZERO &&
                       !etParentEmail.getText().toString().matches(Constants.EMAIL_VALIDATE_PATTERN)) {
                isValid = false;
                errorMsg = getString(R.string.Invalid_Email_format);
                errorTitle = getString(R.string.Hmm);
            } else if (radioGroup.getCheckedRadioButtonId() == -1) {
                isValid = false;
                errorMsg = getString(R.string.EnterPlayerGender);
                errorTitle = getString(R.string.Whoops);
            } else if (rgIEP.getCheckedRadioButtonId() == R.id.rbtnYes && etIEPDescription.getText().toString().trim().length() == Numerics.ZERO) {
                isValid = false;
                errorMsg = getString(R.string.EnterPlayerIEPDescription);
                errorTitle = getString(R.string.Whoops);
                etIEPDescription.setText("");
            }

        }

        if (!isValid) {
            Util.showAlertWarning(DWAddEditPlayerActivity.this, errorTitle,
                                  errorMsg, getString(R.string.Ok), null);
        }
        return isValid;
    }

    /**
     * show selected date from the calender
     *
     * @param yy calender year
     * @param mm month of the year
     * @param dd day of the month
     */
    private void showDate(int yy, int mm, int dd) {

        if (yy == -1 || mm == -1 || dd == -1) {
            day = -1;
            month = -1;
            year = -1;
            // tvDD.setText("");
            tvMM.setText("");
            //  tvYYYY.setText("");
        } else {
            day = dd;
            month = mm;
            year = yy;
            // tvDD.setText(String.valueOf(dd));
            ;
            tvMM.setText(String.format("%02d", mm)+"-"+String.format("%02d", dd)+"-"+String.valueOf(yy));
            //  tvYYYY.setText(String.valueOf(yy));
        }

    }

    /**
     * Method used to delete a player
     *
     * @param context context
     */
    private void showDeletePlayerAlert(Context context) {
        final Dialog dialog = new Dialog(context, R.style.MyTheme_Black_Transparent);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
        final RelativeLayout view = (RelativeLayout) getLayoutInflater().inflate(R.layout.alert_player_delete, null);
        dialog.setContentView(view);

        runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) ((LinearLayout) view.getChildAt(Numerics.ZERO)).getLayoutParams();
                    params.width = (int) (Constants.getDeviceWidth() * 0.4);
                }
            });
        TextView tvDeletePlayerInfo = (TextView) dialog.findViewById(R.id.tvDeletePlayerInfo);
        Button btnCancel = (Button) dialog.findViewById(R.id.btnCancel);
        Button btnDelete = (Button) dialog.findViewById(R.id.btnDelete);

        SpannableString ss = new SpannableString(getString(R.string.DeletePlayerInfo));
        ClickableSpan clickableSpan = new ClickableSpan() {
                @Override
                public void onClick(View textView) {
//                dialog.dismiss();
                    showDeletePlayerInfo();
                }

                @Override
                public void updateDrawState(TextPaint ds) {
                    super.updateDrawState(ds);
                    ds.setUnderlineText(false);
                }
            };
        int index = getString(R.string.DeletePlayerInfo).indexOf(getString(R.string.click_here_to_read_more));
        ss.setSpan(clickableSpan, index, index + getString(R.string.click_here_to_read_more).length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        //For UnderLine
        ss.setSpan(new UnderlineSpan(), index, index + getString(R.string.click_here_to_read_more).length(), 0);

        //For Bold
        ss.setSpan(new StyleSpan(Typeface.BOLD), Numerics.ZERO, index, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        //For text color
        ss.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.blue)), index, index + getString(R.string.click_here_to_read_more).length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);

        tvDeletePlayerInfo.setText(ss);
        tvDeletePlayerInfo.setMovementMethod(LinkMovementMethod.getInstance());
        tvDeletePlayerInfo.setHighlightColor(Color.TRANSPARENT);

        btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
        btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        dialog.dismiss();
                        apiCallType = ApiCallType.DELETE_PLAYER;
                        callDeleteChildApi(sharedPrefUtil.getString(SharedPrefUtil.PREF_LOGIN_USER_TOKEN),
                                           sharedPrefUtil.getString(SharedPrefUtil.PREF_LOGIN_USER_ID));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        dialog.setCancelable(false);
        dialog.show();
    }

    /**
     * Method used to delete a player
     */
    private void showDeletePlayerInfo() {
        dialogDeleteReadMore = new Dialog(this, R.style.MyTheme_Black_Transparent);
        dialogDeleteReadMore.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogDeleteReadMore.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
        final RelativeLayout view = (RelativeLayout) getLayoutInflater().inflate(R.layout.alert_player_delete_info, null);
        dialogDeleteReadMore.setContentView(view);
        runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) ((LinearLayout) view.getChildAt(Numerics.ZERO)).getLayoutParams();
                    params.width = (int) (Constants.getDeviceWidth() * 0.6);
                }
            });

        TextView tvContact = (TextView) dialogDeleteReadMore.findViewById(R.id.tvContact);
        TextView tvBack = (TextView) dialogDeleteReadMore.findViewById(R.id.tvBack);

        SpannableString ss = new SpannableString(getString(R.string.DeletePlayerInfoConact));
        ClickableSpan clickableSpan = new ClickableSpan() {
                @Override
                public void onClick(View textView) {
                    new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (dialogDeleteReadMore != null && dialogDeleteReadMore.isShowing()) {
                                    dialogDeleteReadMore.dismiss();
                                }
                            }
                        }, Numerics.HUNDRED * Numerics.FIVE);
                    PlayerUtil.startParentalCheckActivity(DWAddEditPlayerActivity.this, 0);
                }

                @Override
                public void updateDrawState(TextPaint ds) {
                    super.updateDrawState(ds);
                    ds.setUnderlineText(true);
                }
            };
        int index = getString(R.string.DeletePlayerInfoConact).indexOf(getString(R.string.support_mail));
        ss.setSpan(clickableSpan, index, index + getString(R.string.support_mail).length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        //For text color
        ss.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.blue)), index, index + getString(R.string.support_mail).length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);

        tvContact.setText(ss);
        tvContact.setMovementMethod(LinkMovementMethod.getInstance());
        tvContact.setHighlightColor(Color.TRANSPARENT);

        tvBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialogDeleteReadMore.dismiss();
                }
            });
        dialogDeleteReadMore.setCancelable(false);
        dialogDeleteReadMore.show();
    }

    @Override
    public void onResponse(Object object) {
        try {
            if (apiCallType == ApiCallType.ADD_PLAYER) {

                callAddChildApi(sharedPrefUtil.getString(SharedPrefUtil.PREF_LOGIN_USER_TOKEN),
                                sharedPrefUtil.getString(SharedPrefUtil.PREF_LOGIN_USER_ID));

            } else if (apiCallType == ApiCallType.EDIT_PLAYER) {
                callEditChildApi(sharedPrefUtil.getString(SharedPrefUtil.PREF_LOGIN_USER_TOKEN),
                                 sharedPrefUtil.getString(SharedPrefUtil.PREF_LOGIN_USER_ID));

            } else if (apiCallType == ApiCallType.DELETE_PLAYER) {
                callDeleteChildApi(sharedPrefUtil.getString(SharedPrefUtil.PREF_LOGIN_USER_TOKEN),
                                   sharedPrefUtil.getString(SharedPrefUtil.PREF_LOGIN_USER_ID));

            }else if(apiCallType == ApiCallType.EDIT_STUDENT){
                callEditStudentApi(sharedPrefUtil.getString(SharedPrefUtil.PREF_LOGIN_USER_TOKEN),
                                   sharedPrefUtil.getString(SharedPrefUtil.PREF_LOGIN_USER_ID));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onFailure(Object error) {
    }

    /**
     * Get URI to image received from capture  by camera.
     */
    private Uri getCaptureCropImageOutputUri(String path) {
        Uri outputFileUri = null;
        File getImage=null;
        try {
            getImage = new File(Environment.getExternalStorageDirectory() + File.separator + ".sp");
            if (!getImage.exists()) {
                getImage.mkdirs();
            }
        }catch (Exception e){
            getImage = Environment.getExternalStorageDirectory();
        }
        if (getImage != null) {
            outputFileUri = Uri.fromFile(new File(getImage.getPath(), path));
        }
        return outputFileUri;
    }

    /**
     * This method used to Edit existing student to login teacher
     *
     * @param tokenId   authentication token
     * @param teacherId login teacher id
     * @throws Exception thrown for network/response  issues
     */
    private void callEditStudentApi(String tokenId, String teacherId) throws Exception {

        if (NetworkUtil.checkInternetConnection(this)) {
            String base64 = "";
            String profileUrl = "";

            if (ivPlayerPic.getTag() != null && !ivPlayerPic.getTag().toString().startsWith("http")) {

                base64 = IOUtil.getBase64Scaled(ivPlayerPic.getTag().toString(), Numerics.TWO_HUNDRED_FORTY, Numerics.TWO_HUNDRED);
            } else if (ivPlayerPic.getTag() != null) {
                profileUrl = ivPlayerPic.getTag().toString();
            }

            showProgressDialog(null);

            RadioButton btnGender = (RadioButton) radioGroup.findViewById(radioGroup.getCheckedRadioButtonId());
            String dob = "";
            if (year == -1 || month == -1 || day == -1) {
                dob = "";
            } else {
                dob = ((month) < Numerics.TEN ? ("0" + (month)) : (month)) + "-" +
                    (day < Numerics.TEN ? ("0" + (day)) : day) + "-" + year;
            }

            RadioButton btnIep = (RadioButton) rgIEP.findViewById(rgIEP.getCheckedRadioButtonId());
            String description_IEP = ((RadioButton) rgIEP.getChildAt(Numerics.ZERO)).isChecked() ? etIEPDescription.getText().toString() : "";

            ApiClient.getApiInterface(this).editStudentAPI(JSONConstants.AUTHORIZATION_BEARER + tokenId,
                                                           teacherId, playerDO.getId(),
                                                           etFirstName.getText().toString().trim(), etLastName.getText().toString().trim(),
                                                           dob, btnGender.getTag().toString(),etParentEmail.getText().toString(), etStudentGrade.getText().toString(),
                                                           etStudentId.getText().toString(), btnIep.getTag().toString(),
                                                           etLanguages.getText().toString(), description_IEP,
                                                           "", profileUrl, base64).
                enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            dismissProgressDialog();
                            btnSavePlayer.setClickable(true);
                            try {
                                if (response.body() != null) {
                                    PlayerDO prevSelPlayerDO = PlayerUtil.getSelectedPlayer(DWAddEditPlayerActivity.this);
                                    if (prevSelPlayerDO != null && prevSelPlayerDO.getId().equals(playerDO.getId())) {
                                        prevSelPlayerDO.setFirstName(etFirstName.getText().toString().trim());
                                        PlayerUtil.setSelectedPlayer(DWAddEditPlayerActivity.this, prevSelPlayerDO);
                                    }
                                    setResult(RESULT_OK);
                                    finish();
                                } else {
                                    if (response.errorBody() != null) {
                                        if (response.code() == Constants.RESPONSE_CODE_UNAUTHORIZED) {
                                            NetworkUtil.callReAuthenticationAPI(DWAddEditPlayerActivity.this, DWAddEditPlayerActivity.this, true);
                                        }
                                        JSONObject jsonObject = new JSONObject(response.errorBody().string());
                                    }
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            dismissProgressDialog();
                            btnSavePlayer.setClickable(true);
                            t.printStackTrace();
                        }
                    });
        } else {
            Util.showAlert(this, getString(R.string.We_Need_Internet), getString(R.string.Please_Connect_Internet), getString(R.string.Ok),
                           null);
            btnSavePlayer.setClickable(true);
        }

    }


   private void requestWriteExternalStoragePermission() {
       ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, Numerics.ONE);
   }

   // @Override
   // public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
   //     switch (requestCode) {
   //         case Numerics.ONE: {
   //             // If request is cancelled, the result arrays are empty.
   //             if (grantResults.length > Numerics.ZERO
   //                     && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
   //                 if (!isPermissionGranted) {
   //                     isPermissionGranted = true;

   //                 }
   //             } else {

   //                 Toast.makeText(this, "Permission denied.", Toast.LENGTH_LONG).show();
   //             }
   //             return;
   //         }
   //     }
   // }
    private void checkPermissions() {
        if (Build.VERSION.SDK_INT >= 23) {
            //just request the permission
            Util.keepAppAlive();
            ActivityCompat.requestPermissions(DWAddEditPlayerActivity.this, permissionsRequired.toArray(new String[0]), PERMISSION_CALLBACK_CONSTANT);
        }else if(isCamera){
            openCamera();
        }else if(!isCamera){
            fromLibrary();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_CALLBACK_CONSTANT) {
            //check if all permissions are granted
            boolean allgranted = false;
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    allgranted = true;
                } else {
                    allgranted = false;
                    break;
                }
            }
            if (allgranted && isCamera) {
                openCamera();
            } else if(allgranted && !isCamera){
                fromLibrary();
            }else if ((permissionsRequired != null && permissionsRequired.size() == 2) &&
                    (ActivityCompat.shouldShowRequestPermissionRationale(DWAddEditPlayerActivity.this, permissionsRequired.get(0))
                            || ActivityCompat.shouldShowRequestPermissionRationale(DWAddEditPlayerActivity.this, permissionsRequired.get(1)))) {
                showPermissionInfo(DWAddEditPlayerActivity.this, permissions, grantResults,false);
            } else if ((permissionsRequired != null && permissionsRequired.size() == 3) &&
                        (ActivityCompat.shouldShowRequestPermissionRationale(DWAddEditPlayerActivity.this, permissionsRequired.get(0))
                                || ActivityCompat.shouldShowRequestPermissionRationale(DWAddEditPlayerActivity.this, permissionsRequired.get(1))
                                || ActivityCompat.shouldShowRequestPermissionRationale(DWAddEditPlayerActivity.this, permissionsRequired.get(2)))) {
                showPermissionInfo(DWAddEditPlayerActivity.this, permissions, grantResults,false);
            } else {
                showPermissionInfo(DWAddEditPlayerActivity.this,permissions, grantResults,true);
            }

        } else {
            Toast.makeText(getBaseContext(), "Unable to get Permission", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * To open the device camera.
     */
    private void openCamera(){
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "New Picture");
        values.put(MediaStore.Images.Media.DISPLAY_NAME, "New Picture");
        values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        picturePath = getCaptureCropImageOutputUri(Constants.PIC_IMAGE_NAME).toString();
        intent.putExtra(MediaStore.EXTRA_OUTPUT, getCaptureCropImageOutputUri(Constants.PIC_IMAGE_NAME));
        Util.keepAppAlive();
        startActivityForResult(intent, Constants.PICK_IMAGE_CHOOSER_REQUEST_CODE);
    }

    /**
     *To open the device gallery.
     */
    private void fromLibrary(){
        Intent intent = new Intent();
        // Show only images, no videos or anything else
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, getCaptureCropImageOutputUri(Constants.PIC_IMAGE_NAME));
        Util.keepAppAlive();
        startActivityForResult(Intent.createChooser(intent, getString(R.string.SelectPicture)),
                               Constants.PICK_IMAGE_CHOOSER_REQUEST_CODE);
    }
    private boolean checkWriteExternalPermission(){
        int permission = 1;
       if (Build.VERSION.SDK_INT >= 23) {
            permission = ContextCompat.checkSelfPermission(DWAddEditPlayerActivity.this,Manifest.permission.WRITE_EXTERNAL_STORAGE);
       }else {
            permission = PermissionChecker.checkSelfPermission(DWAddEditPlayerActivity.this,  Manifest.permission.WRITE_EXTERNAL_STORAGE);
       }

        if (permission == PermissionChecker.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }
}