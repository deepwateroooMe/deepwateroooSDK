package com.deepwaterooo.sdk.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.graphics.Paint;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.deepwaterooo.sdk.R;
import com.deepwaterooo.sdk.activities.BaseActivity;
import com.deepwaterooo.sdk.activities.DWBaseActivity;
import com.deepwaterooo.sdk.appconfig.Constants;
import com.deepwaterooo.sdk.appconfig.Logger;
import com.deepwaterooo.sdk.appconfig.Numerics;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Class containing static utility methods for App
 */
public class Util {

    public static boolean IS_APP_RUNNING;
    public static boolean BT_ENABLE_ACTIVITY_SHOWING = false;

    public static String readInputStream(InputStream inputStream) {
        try {
            BufferedReader r = new BufferedReader(new InputStreamReader(inputStream));
            String x = "";
            x = r.readLine();
            String total = "";
            while (x != null) {
                total += x;
                x = r.readLine();
            }
            // Logger.error("response", total);
            return total;
        } catch (Exception e) {
            Logger.error(Constants.ERROR_EXCEPTION, e.toString());
        }
        return null;
    }

    public static int parseInt(String str) {
        try {
            return Integer.parseInt(str);
        } catch (Exception e) {
            Logger.error(Constants.ERROR_EXCEPTION, e.toString());
        }
        return 0;
    }
    public static float parseFloat(String str) {
        try {
            return Float.parseFloat(str);
        } catch (Exception e) {
            Logger.error(Constants.ERROR_EXCEPTION, e.toString());
        }
        return 0;
    }
    public static double parseDouble(String str) {
        try {
            return Double.parseDouble(str);
        } catch (Exception e) {
            Logger.error(Constants.ERROR_EXCEPTION, e.toString());
        }
        return 0;
    }
    public static long parseLong(String str) {
        try {
            return Long.parseLong(str);
        } catch (Exception e) {
            Logger.error(Constants.ERROR_EXCEPTION, e.toString());
        }
        return 0;
    }
    /**
     * Show the Alert dialog to the user for message
     */
    public static void showAlert(final Context context, String title, String message, String btnText,
                                 View.OnClickListener listener) {
        final Dialog dialog = new Dialog(context, R.style.MyTheme_Black_Transparent);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.alert_message);
        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
        TextView tvAlertTitle = (TextView) dialog.findViewById(R.id.tvAlertTitle);
        TextView tvAlertText = (TextView) dialog.findViewById(R.id.tvAlertText);
        Button btnOk = (Button) dialog.findViewById(R.id.btnOk);
        tvAlertTitle.setText(title);
        tvAlertText.setText(message);
        btnOk.setText(btnText);
        btnOk.setTag(dialog);
        btnOk.setMinimumWidth((int) (Constants.getDeviceWidth() * 0.20));
        if (listener != null) {
            btnOk.setOnClickListener(listener);
        } else {
            btnOk.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
        }
        dialog.setCancelable(false);
        if (context instanceof BaseActivity) {
            dialog.setOnDismissListener((BaseActivity) context);
        } else if (context instanceof BaseActivity) {
            dialog.setOnDismissListener((BaseActivity) context);
        }
        playSound(context, Constants.AUDIO_GROWN_UP);
        if (!((Activity) context).isFinishing() && !((Activity) context).isDestroyed()) {
            dialog.getWindow().setDimAmount(1f);
            dialog.show();
        }
    }
    /**
     * @return - object of Dialog
     * Show the Alert dialog to the user for message
     */
    public static Dialog showAlert(final Context context, String title, String message, String btnText,
                                   String second_btnText, View.OnClickListener listenerFirst, View.OnClickListener listenerSecond) {
        final Dialog dialog = new Dialog(context, R.style.MyTheme_Black_Transparent);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.alert_message_twobtns);
        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
        TextView tvAlertTitle = (TextView) dialog.findViewById(R.id.tvAlertTitle);
        TextView tvAlertText = (TextView) dialog.findViewById(R.id.tvAlertText);
        Button btnFirst = (Button) dialog.findViewById(R.id.btnFirst);
        Button btnSecond = (Button) dialog.findViewById(R.id.btnSecond);
        tvAlertTitle.setText(title);
        tvAlertText.setText(message);
        btnFirst.setText(btnText);
        btnSecond.setText(second_btnText);
        btnFirst.setTag(dialog);
        btnSecond.setTag(dialog);
        btnFirst.setMinimumWidth((int) (Constants.getDeviceWidth() * 0.10));
        if (listenerFirst != null) {
            btnFirst.setOnClickListener(listenerFirst);
        } else {
            btnFirst.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
        }
        if (listenerSecond != null) {
            btnSecond.setOnClickListener(listenerSecond);
        } else {
            btnSecond.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
        }
        if (context instanceof BaseActivity) {
            dialog.setOnDismissListener((BaseActivity) context);
        } else if (context instanceof BaseActivity) {
            dialog.setOnDismissListener((BaseActivity) context);
        }
        dialog.setCancelable(false);
        if (!((Activity) context).isFinishing() && !((Activity) context).isDestroyed()) {
            dialog.show();
        }
        return dialog;
    }
    /**
     * Show the Alert dialog to the user for message
     */
    public static void showAlertWarning(final Context context, String title, String message, String btnText,
                                        View.OnClickListener listener) {
        final Dialog dialog = new Dialog(context, R.style.MyTheme_Black_Transparent);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.alert_warning_view);
        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
        LinearLayout llDialog = (LinearLayout) dialog.findViewById(R.id.llDialog);
//        llDialog.setMinimumWidth((int) (Constants.getDeviceWidth() * 0.4));
        ViewGroup.LayoutParams params = llDialog.getLayoutParams();
        params.width = (int) (Constants.getDeviceWidth() * 0.4);
        llDialog.setLayoutParams(params);
        TextView tvAlertTitle = (TextView) dialog.findViewById(R.id.tvAlertTitle);
        TextView tvAlertText = (TextView) dialog.findViewById(R.id.tvAlertText);
        Button btnOk = (Button) dialog.findViewById(R.id.btnOk);
        tvAlertTitle.setText(title);
        tvAlertText.setText(message);
        btnOk.setText(btnText);
        btnOk.setTag(dialog);
        if (listener != null) {
            btnOk.setOnClickListener(listener);
        } else {
            btnOk.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
        }
        dialog.setCancelable(false);
        if (context instanceof BaseActivity) {
            dialog.setOnDismissListener((BaseActivity) context);
        } else if (context instanceof BaseActivity) {
            dialog.setOnDismissListener((BaseActivity) context);
        }
        if (!((Activity) context).isFinishing() && !((Activity) context).isDestroyed()) {
            dialog.show();
        }
    }
    public static float dpToPx(Context context, float valueInDp) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, valueInDp, metrics);
    }
    public static void keepAppAlive() {
        new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    IS_APP_RUNNING = true;
                }
            }, Numerics.FIVE * Numerics.HUNDRED);
    }
    /**
     * Plays the sound of the audio file which is pass as the name to this method. This name should not be consists the extension.
     * Need to place the audio file in the assets directory of application.
     *
     * @param context   -
     * @param file_name
     */
    public static void playSound(Context context, String file_name) {
        AssetFileDescriptor afd = null;
        MediaPlayer mediaPlayer = null;
        try {
            mediaPlayer = new MediaPlayer();
            afd = context.getAssets().openFd(file_name);
            mediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            afd.close();
            mediaPlayer.prepare();
            mediaPlayer.setVolume(100, 100);
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        mp.reset();
                        mp.release();
                    }
                });
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * This method used to save Login user players info while login successful for
     * providing user info in offline.
     *
     * @param context      context
     * @param jsonResponse json string to save in the file
     */
    public static void savePlayersListToFile(Context context, String jsonResponse) {
        try {
            FileWriter fileWriter = new FileWriter(context.getFilesDir().getPath() + "/" + Constants.OFF_LINE_LOGIN_USER_INFO);
            fileWriter.write(jsonResponse);
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException e) {
            Log.e("savePlayersListToFile", "Error in Writing: " + e.getLocalizedMessage());
        }
    }
    /**
     * This method used to save child activity to a file at device cache
     *
     * @param context      context
     * @param jsonResponse json string to save in the file
     */
    public static void saveChildActivityToFile(Context context, String jsonResponse) {
    }
    /**
     * This method used to get the Login user players info in offline.
     * If user info already exist.
     *
     * @param context context
     * @return json string from the file
     */
    public static String getChildActivityFromFile(Context context, String fileName) {
        return "I love my dear cousin~!!!";
    }
    public static List<String> getListOfChildActivitiesFromFile(Context context) {
        List<String> activities = new ArrayList<>();
        File file = new File(context.getFilesDir().getPath() + "/Activity");
        if (file.exists()) {
            activities.addAll(Arrays.asList(file.list()));
        }
        return activities;
    }
    public static void deleteChildActivityFromFile(Context context) {
        List<String> files = Util.getListOfChildActivitiesFromFile(context);
        for (String fille : files) {
            File file = new File(context.getFilesDir().getPath() + "/Activity/" + fille);
            if (file.exists()) {
                file.delete();
            }
        }
    }
    /**
     * get the device dimensions while launching the application
     */
    public static void getDeviceDimensions(Activity activity) {
        Display display = activity.getWindowManager().getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);
        Constants.setDeviceWidth(point.x);
        Constants.setDeviceHeight(point.y);
    }

    /**
     * Show the teacher account Alert dialog to the user for message
     */
    public static void showAlertTeacherAccount(final Context context, String title, String message1,
                                               String message2, String btnText,
                                               View.OnClickListener listener1, View.OnClickListener listener2, View.OnClickListener listener3) {
        final Dialog dialog = new Dialog(context, R.style.MyTheme_Black_Transparent);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        dialog.setContentView(R.layout.alert_teacher_account);
        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);

        LinearLayout llDialog = (LinearLayout) dialog.findViewById(R.id.llDialog);
        ViewGroup.LayoutParams params = llDialog.getLayoutParams();
        params.width = (int) (Constants.getDeviceWidth() * 0.35);
        llDialog.setLayoutParams(params);
        TextView tvAlertTitle = (TextView) dialog.findViewById(R.id.tvAlertTitle);
        TextView tvAlertText1 = (TextView) dialog.findViewById(R.id.tvAlertText1);
        TextView tvAlertText2 = (TextView) dialog.findViewById(R.id.tvAlertText2);

        Button btnBack = (Button) dialog.findViewById(R.id.btnBack);

        tvAlertTitle.setText(title);
        tvAlertText1.setText(message1);
        tvAlertText2.setText(message2);

        tvAlertText1.setTag(dialog);
        tvAlertText2.setTag(dialog);

        if (message1.equals(context.getString(R.string.create_a_teacher))) {
            tvAlertText1.setPaintFlags(tvAlertText1.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        } else {
            tvAlertText1.setTextColor(context.getResources().getColor(R.color.Gray));
        }

        if (message2.equals(context.getString(R.string.not_a_teacher))) {
            tvAlertText2.setPaintFlags(tvAlertText2.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        }

        btnBack.setText(btnText);
        btnBack.setTag(dialog);
        if (listener3 == null) {
            btnBack.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
        } else {
            btnBack.setOnClickListener(listener3);
        }

        if (listener1 != null) {
            tvAlertText1.setOnClickListener(listener1);
        }

        if (listener2 != null) {
            tvAlertText2.setOnClickListener(listener2);
        }

        dialog.setCancelable(false);

        if (context instanceof BaseActivity) {
            dialog.setOnDismissListener((BaseActivity) context);
        } else if (context instanceof DWBaseActivity) {
            dialog.setOnDismissListener((DWBaseActivity) context);
        }

        if (!((Activity) context).isFinishing() && !((Activity) context).isDestroyed()) {
            dialog.show();
        }
    }

    /**
     * This method used to get the Login user players info in offline.
     * If user info already exist.
     *
     * @param context context
     * @return json string from the file
     */
    public static String getPlayersListFromFile(Context context) {
        try {
            File file = new File(context.getFilesDir().getPath() + "/" + Constants.OFF_LINE_LOGIN_USER_INFO);
            //check whether file exists
            FileInputStream fileInputStream = new FileInputStream(file);
            int size = fileInputStream.available();
            byte[] buffer = new byte[size];
            fileInputStream.read(buffer);
            fileInputStream.close();
            return new String(buffer);
        } catch (IOException e) {
            Log.e("LoginUserInfoFromFile", "Error in Reading: " + e.getLocalizedMessage());
            return null;
        }
    }
}