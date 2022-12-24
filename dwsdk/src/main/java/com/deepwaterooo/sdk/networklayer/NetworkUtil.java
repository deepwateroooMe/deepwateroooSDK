package com.deepwaterooo.sdk.networklayer;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import com.deepwaterooo.sdk.R;
import com.deepwaterooo.sdk.activities.BaseActivity;
import com.deepwaterooo.sdk.activities.DWBaseActivity;
import com.deepwaterooo.sdk.activities.authentication.DWForgotPasswordActivity;
import com.deepwaterooo.sdk.appconfig.Constants;
import com.deepwaterooo.sdk.appconfig.JSONConstants;
import com.deepwaterooo.sdk.beans.AppUpdatesDO;
import com.deepwaterooo.sdk.utils.ApiCallListener;
import com.deepwaterooo.sdk.utils.DWActivityUtil;
import com.deepwaterooo.sdk.utils.SharedPrefUtil;
import com.deepwaterooo.sdk.utils.Util;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Class used to check the state of network connectivity in the Android device
 * and also monitors Monitor network connections (Wi-Fi, GPRS, UMTS, etc.)
 */
public class NetworkUtil {

    /**
     * Return true if the Device having internet connection is present or not
     *
     * @param context activity context
     * @return true if the Device having internet connection
     */
    public static boolean checkInternetConnection(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        /**test for connection*/
        if (cm.getActiveNetworkInfo() != null
            && cm.getActiveNetworkInfo().isAvailable()
            && cm.getActiveNetworkInfo().isConnected()) {
            return true;
        }
        return false;
    }

    /**
     * this method used to call Re-Authentication token for  login user
     *
     * @param activity context
     * @param listener listener to pass result to caller
     * @throws Exception thrown for network/response  issues
     */
    public static void callReAuthenticationAPI(final Context activity, final ApiCallListener listener, final boolean isProgresNeeded) throws Exception {
        if (NetworkUtil.checkInternetConnection(activity)) {
            final SharedPrefUtil sharedPrefUtil = new SharedPrefUtil(activity);
            if (activity instanceof BaseActivity && isProgresNeeded) {
//                ((BaseActivity) activity).showProgressDialog(activity.getString(Please_Wait)); // 这里可能要涉及到一点儿方法的垂柳
            }
            ApiClient.getApiInterface((DWForgotPasswordActivity) activity).ReAuthenticationAPI(sharedPrefUtil.getString(SharedPrefUtil.PREF_LOGIN_USER_TOKEN)).enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (activity instanceof BaseActivity && isProgresNeeded) {
                            ((BaseActivity) activity).dismissProgressDialog();
                        }
                        try {
                            if (response.body() != null) {
                                JSONObject jsonObject = new JSONObject(response.body().string());
                                //Logger.info("ReAuthentication Success body", " : " + jsonObject.getString(JSONConstants.TOKEN));
                                sharedPrefUtil.setString(SharedPrefUtil.PREF_LOGIN_USER_TOKEN, jsonObject.getString(JSONConstants.TOKEN));
                                listener.onResponse(null);
//
                            } else {
                                if (response.code() == Constants.RESPONSE_CODE_UNAUTHORIZED ||
                                    response.code() == Constants.RESPONSE_CODE_FORBIDDEN) {
                                    if (activity instanceof BaseActivity) {
                                        // PlayerUtil.logoutUser((BaseActivity) activity);
//                                        PlayerUtil.logoutParent((BaseActivity) activity);
                                    }
                                } else if (response.errorBody() != null) {
                                    JSONObject jsonObject = new JSONObject(response.errorBody().string());
                                    //Token already Expired
                                    //Logger.info("ReAuthentication Error body ", jsonObject.getString(JSONConstants.ERR));
                                    listener.onFailure(jsonObject.toString());
                                    if (response.code() == Constants.RESPONSE_CODE_LOGOUT &&
                                        jsonObject.getString(JSONConstants.ERR).equals(Constants.TOKEN_ALREADY_EXPIRED)) {
                                        if (activity instanceof BaseActivity) {
//                                            PlayerUtil.logoutParent((BaseActivity) activity);
                                        }
                                    }
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            listener.onFailure(e.toString());
                        }
                    }
                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        if (activity instanceof BaseActivity && isProgresNeeded) {
                            ((BaseActivity) activity).dismissProgressDialog();
                        }
                        //Logger.info("ReAuthentication", "onFailure: " + t.toString());
                        t.printStackTrace();
                        listener.onFailure(t.toString());
                    }
                });
        } else {
            if (isProgresNeeded) {
                Util.showAlert(activity, activity.getString(R.string.We_Need_Internet),
                               activity.getString(R.string.Please_Connect_Internet), activity.getString(R.string.Ok),
                               null);
            }
            if (listener != null) {
                listener.onFailure(activity.getString(R.string.Please_Connect_Internet));
            }
        }
    }
    /**
     * This method used to upload file to the server
     *
     * @param activity context
     * @param listener listener to pass result to caller
     * @param fileData This parameter accepts file data in byte array
     * @param fileName upload file name
     */
    public static void uploadFile(final DWBaseActivity activity, final ApiCallListener listener, byte[] fileData, String fileName) {
        if (NetworkUtil.checkInternetConnection(activity)) {
            new UploadFileTask(activity, listener, null, fileData, fileName).execute();
        } else {
            if (listener != null) {
                listener.onFailure(activity.getString(R.string.Please_Connect_Internet));
            }
//            Util.showAlert(activity, activity.getString(R.string.We_Need_Internet),
//                    activity.getString(R.string.Please_Connect_Internet), activity.getString(R.string.Ok),
//                    null);
        }
    }
    /**
     * This method used to upload file to the server
     *
     * @param activity context
     * @param listener listener to pass result to caller
     * @param filepath upload file path
     * @param fileName upload file name
     */
    public static void uploadFile(final DWBaseActivity activity, final ApiCallListener listener, String filepath, String fileName) {
        if (NetworkUtil.checkInternetConnection(activity)) {
            new UploadFileTask(activity, listener, filepath, null, fileName).execute();
        } else {
            if (listener != null) {
                listener.onFailure(activity.getString(R.string.Please_Connect_Internet));
            }
//            Util.showAlert(activity, activity.getString(R.string.We_Need_Internet),
//                    activity.getString(R.string.Please_Connect_Internet), activity.getString(R.string.Ok),
//                    null);
        }
    }
    public static class UploadFileTask extends AsyncTask<Void, Void, String> {
        private ApiCallListener listener;
        private DWBaseActivity activity;
        private String filepath;
        private String fileName;
        private byte[] fileData;
        private SharedPrefUtil sharedPrefUtil;
        public UploadFileTask(DWBaseActivity activity, ApiCallListener listener, String filepath, byte[] fileData, String fileName) {
            this.activity = activity;
            this.listener = listener;
            this.filepath = filepath;
            this.fileName = fileName;
            this.fileData = fileData;
            sharedPrefUtil = new SharedPrefUtil(activity);
        }
        @Override
        protected String doInBackground(Void... params) {
            String base64 = null;
            try {
                if (filepath != null) {
                    File file = new File(filepath);
                    FileInputStream fileInputStream;
                    if (file.exists()) {
                        fileInputStream = new FileInputStream(file);
                        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
                        int bufferSize = 1024;
                        byte[] buffer = new byte[bufferSize];
                        int len = 0;
                        while ((len = fileInputStream.read(buffer)) != -1) {
                            byteBuffer.write(buffer, 0, len);
                        }
                        base64 = Base64.encodeToString(byteBuffer.toByteArray(), Base64.DEFAULT).replaceAll("\n", "");
                    }
                } else if (fileData != null) {
                    base64 = Base64.encodeToString(fileData, Base64.DEFAULT).replaceAll("\n", "");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return base64;
        }
        @Override
        protected void onPostExecute(String base64) {
            super.onPostExecute(base64);
            if (base64 != null) {
                try {
                    ApiClient.getApiInterface(activity).uploadFile(JSONConstants.AUTHORIZATION_BEARER +
                                                                   sharedPrefUtil.getString(SharedPrefUtil.PREF_LOGIN_USER_TOKEN),
                                                                   fileName, base64).enqueue(new Callback<ResponseBody>() {
                                                                           @Override
                                                                           public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                                                               try {
                                                                                   if (response.body() != null) {
                                                                                       listener.onResponse(response.body().string());
                                                                                       //Logger.info("uploadFile success", "Success");
                                                                                   } else if (response.code() == Constants.RESPONSE_CODE_UNAUTHORIZED) {
                                                                                       callReAuthenticationAPI(activity, new ApiCallListener() {
                                                                                               @Override
                                                                                               public void onResponse(Object object) {
                                                                                                   if (object == null) {
                                                                                                       new UploadFileTask(activity, listener, null, fileData, fileName).execute();
                                                                                                   }
                                                                                               }
                                                                                               @Override
                                                                                               public void onFailure(Object error) {
                                                                                               }
                                                                                           }, true);
                                                                                   } else {
                                                                                       if (response.errorBody() != null) {
                                                                                           listener.onFailure(response.errorBody().string());
                                                                                           //Logger.info("uploadFile Error body", "Failed");
                                                                                       }
                                                                                   }
                                                                               } catch (Exception e) {
                                                                                   e.printStackTrace();
                                                                                   listener.onFailure(e.toString());
                                                                               }
                                                                           }
                                                                           @Override
                                                                           public void onFailure(Call<ResponseBody> call, Throwable t) {
//                            activity.dismissProgressDialog();
                                                                               t.printStackTrace();
                                                                               listener.onFailure(t.getMessage());
                                                                           }
                                                                       });
                } catch (Exception e) {
//                    activity.dismissProgressDialog();
                    listener.onFailure(e.getMessage());
                    e.printStackTrace();
                }
            } else {
                listener.onFailure("File upload failure due to wrong file path.");
//                activity.dismissProgressDialog();
            }
        }
    }

    /**
     * This method used to down load file from server
     *
     * @param activity context
     * @param listener listener to pass result to caller
     * @param fileName file name to download.
     * @return
     */
    public static void downloadFile(final DWBaseActivity activity, final ApiCallListener listener, final String fileName) {
        if (NetworkUtil.checkInternetConnection(activity)) {
//            activity.showProgressDialog(activity.getString(R.string.Please_Wait));
            try {
                //create file download url with login user bucket url and file name
                SharedPrefUtil sharedPrefUtil = new SharedPrefUtil(activity);
                String url = sharedPrefUtil.getString(SharedPrefUtil.PREF_LOGIN_USER_BUCKET_URL);
                if (url != null && !url.isEmpty()) {
                    ApiClient.getApiInterface(activity).downloadFileWithURL(url + "games/" + fileName).enqueue(new Callback<ResponseBody>() {
                            @Override
                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                        activity.dismissProgressDialog();
                                try {
                                    if (response.body() != null) {
                                        listener.onResponse(response.body());
                                        //Logger.info("Download file", "Success");
                                    } else {
                                        if (response.errorBody() != null) {
                                            listener.onFailure(response.errorBody().string());
                                            //Logger.info("downloadFile errorBody :", response.errorBody().string());
                                        }
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    listener.onFailure(e.toString());
                                }
                            }
                            @Override
                            public void onFailure(Call<ResponseBody> call, Throwable t) {
//                        activity.dismissProgressDialog();
                                t.printStackTrace();
                                listener.onFailure(t.getMessage());
                            }
                        });
                } else {
                    if (listener != null) {
                        listener.onFailure("Error: Download file");
                    }
                }
            } catch (Exception e) {
//                activity.dismissProgressDialog();
                listener.onFailure(e.getMessage());
                e.printStackTrace();
            }
        } else {
            if (listener != null) {
                listener.onFailure(activity.getString(R.string.Please_Connect_Internet));
            }
//            Util.showAlert(activity, activity.getString(R.string.We_Need_Internet),
//                    activity.getString(R.string.Please_Connect_Internet), activity.getString(R.string.Ok),
//                    null);
        }
    }

    /**
     * This method used to download a file using file Url
     *
     * @param activity context
     * @param listener listener to pass result to caller
     * @param fileUrl  url to file download
     */
    public static void downloadFileWithURL(final BaseActivity activity, final ApiCallListener listener, final String fileUrl) {
        if (NetworkUtil.checkInternetConnection(activity)) {
            try {
                ApiClient.getApiInterface((DWForgotPasswordActivity) activity).downloadFileWithURL(fileUrl).enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            try {
                                if (response.body() != null) {
                                    listener.onResponse(response.body());
                                    //Logger.debug("download File with URL", "Success");
                                } else {
                                    if (response.errorBody() != null) {
                                        listener.onFailure(response.errorBody().string());
                                        //Logger.debug("download File with URL errorBody :", response.errorBody().string());
                                    }
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                                listener.onFailure(e.toString());
                            }
                        }
                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            t.printStackTrace();
                            listener.onFailure(t.getMessage());
                        }
                    });
            } catch (Exception e) {
                listener.onFailure(e.getMessage());
                e.printStackTrace();
            }
        } else {
            if (listener != null) {
                listener.onFailure(activity.getString(R.string.Please_Connect_Internet));
            }
//            Util.showAlert(activity, activity.getString(R.string.We_Need_Internet),
//                    activity.getString(R.string.Please_Connect_Internet), activity.getString(R.string.Ok),
//                    null);
        }
    }

    /**
     * this method used to save the response boday as a file with given name
     *
     * @param activity context
     * @param body     file Response body
     * @param fileName name of the file want to save
     * @return saved success or fail
     */
    private static boolean writeResponseBodyToDisk(Activity activity, ResponseBody body, String fileName) {
        try {
            File futureStudioIconFile = new File(activity.getExternalFilesDir(null) + File.separator + fileName);
            InputStream inputStream = null;
            OutputStream outputStream = null;
            try {
                byte[] fileReader = new byte[4096];
                long fileSize = body.contentLength();
                long fileSizeDownloaded = 0;
                inputStream = body.byteStream();
                outputStream = new FileOutputStream(futureStudioIconFile);
                while (true) {
                    int read = inputStream.read(fileReader);
                    if (read == -1) {
                        break;
                    }
                    outputStream.write(fileReader, 0, read);
                    fileSizeDownloaded += read;
                    Log.d("Write File ", "file download: " + fileSizeDownloaded + " of " + fileSize);
                }
                outputStream.flush();
                return true;
            } catch (IOException e) {
                return false;
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (outputStream != null) {
                    outputStream.close();
                }
            }
        } catch (IOException e) {
            return false;
        }
    }

// 这是一个应用内游戏补丁包更新的方式,不再适用于自己的热更新了,可以不用这个方法    
    public static void callGetAppUpdate(Context context) {
        if (NetworkUtil.checkInternetConnection(context)) {
            try {
                final SharedPrefUtil sharedPrefUtil = new SharedPrefUtil(context);
                ApiClient.getApiInterface(context).appUpdateAPI(sharedPrefUtil.getString(SharedPrefUtil.PREF_LOGIN_USER_ID),
                                                                context.getString(R.string.gameID), DWActivityUtil.getSDKVersion(),
                                                                DWActivityUtil.getGameAppVersion(context), "Android").enqueue(new Callback<AppUpdatesDO>() {
                                                                        @Override
                                                                        public void onResponse(Call<AppUpdatesDO> call, Response<AppUpdatesDO> response) {
                                                                            try {
                                                                                if (response != null && response.body() != null) {
                                                                                    sharedPrefUtil.setBoolean(SharedPrefUtil.PREF_IS_APP_UPDATE_CALLED, true);
                                                                                    AppUpdatesDO appUpdatesDO = (AppUpdatesDO) response.body();
//                                                                                    sharedPrefUtil.setString(SharedPrefUtil.PREF_UPDATE_MSG, appUpdatesDO.getUpdateVersionText());
//                                                                                    sharedPrefUtil.setString(SharedPrefUtil.PREF_UPDATE_LINK, appUpdatesDO.getNavigation_link());
                                                                                    if (appUpdatesDO.getSettings() != null ) {
                                                                                        sharedPrefUtil.setBoolean(SharedPrefUtil.PREF_LOGIN_USER_QR_STATUS, Boolean.valueOf(appUpdatesDO.getSettings().getQrCodeStatus()));
                                                                                    }
                                                                                    if (sharedPrefUtil.getBoolean(SharedPrefUtil.PREF_LOGIN_USER_STATUS) && appUpdatesDO.getRole() != null) {
                                                                                        Gson gson = new Gson();
                                                                                        String json = sharedPrefUtil.getString(SharedPrefUtil.PREF_LOGIN_USER_INFO);
                                                                                        if (!TextUtils.isEmpty(json)) {
//                                                                                            ParentInfoDO parentInfoDO = gson.fromJson(json, ParentInfoDO.class);
//                                                                                            parentInfoDO.setRole(appUpdatesDO.getRole());
//                                                                                            sharedPrefUtil.setString(SharedPrefUtil.PREF_LOGIN_USER_INFO, gson.toJson(parentInfoDO));
                                                                                        }
//                                                                                        if (appUpdatesDO.getChildCount() != null) {
//                                                                                            //  sharedPrefUtil.setInteger(SharedPrefUtil.PREF_PLAYERS_LIMIT, Integer.parseInt(appUpdatesDO.getChildCount()));
//                                                                                        }
                                                                                    }
                                                                                } else if (response != null && response.errorBody() != null) {
//                                                                                    Logger.debug("appUpdateAPI", "errorBody : " + response.errorBody().string());
                                                                                }
                                                                            } catch (Exception e) {
                                                                                e.printStackTrace();
                                                                            }
                                                                        }
                                                                        @Override
                                                                        public void onFailure(Call<AppUpdatesDO> call, Throwable t) {
                                                                        }
                                                                    });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}