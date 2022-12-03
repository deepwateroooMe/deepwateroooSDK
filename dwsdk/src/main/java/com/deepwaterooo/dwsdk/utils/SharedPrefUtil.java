package com.deepwaterooo.dwsdk.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashSet;
import java.util.Set;

/**
 * Class containing static utility methods for shared preferences
 */
public class SharedPrefUtil {
    private static final String SHARED_PREF_NAME = "Deepwaterooo";
    public static final String PREF_USER_NAME = "USER_NAME";
    public static final String PREF_USER_PASSWORD = "USER_PASSWORD";
    public static final String PREF_USER_ID = "USER_ID";
    public static final String PREF_UPDATE_MSG = "UPDATE_MSG";
    public static final String PREF_UPDATE_LINK = "UPDATE_LINK";

    public static final String PREF_FILE_PATH = "FILE_PATH";
    public static final String PREF_FIRMWARE_VERSION = "FIRMWARE_VERSION";
    public static final String PREF_PRIVACY = "PRIVACY";
    public static final String PREF_TERMS_AND_CONDITIONS = "TERMS_AND_CONDITIONS";
    public static final String PREF_PLAYSET_NAME = "PLAYSET_NAME";
    public static final String PREF_REMEMBER_PLAYSET = "REMEMBER_PLAYSET";
    public static final String PREF_BATTER_SAVER = "BATTER_SAVER";
    public static final String PREF_PLAYSET_ADDRESS = "PLAYSET_ADDRESS";

    //Login shared preferences
    public static final String PREF_LOGIN_USER_ID = "LOGIN_USER_ID";
    public static final String PREF_LOGIN_USER_TOKEN = "LOGIN_USER_TOKEN";
    public static final String PREF_LOGIN_USER_STATUS = "LOGIN_USER_STATUS";
    public static final String PREF_LOGIN_USER_INFO = "LOGIN_USER_INFO";
    public static final String PREF_PLAYERS_LIMIT = "PLAYERS_LIMIT";

    //Initial screen preferences
    public static final String PREF_HAVE_PLAYSET = "HAVE_PLAYSET";
    public static final String PREF_DO_YOU_HAVE_ACC = "DO_YOU_HAVE_ACCOUNT";
    public static final String PREF_IS_ALL_SET = "IS_ALL_SET";
    public static final String PREF_SELECTED_PLAYER = "SELECTED_PLAYER";

    //Login user bucket url for file download
    public static final String PREF_LOGIN_USER_BUCKET_URL = "LOGIN_USER_BUCKET_URL";
    public static final String PREF_SELECTED_LANGUAGE = "SELECTED_LANGUAGE";
    public static final String PREF_IS_APP_UPDATE_CALLED = "IS_APP_UPDATE_CALLED";
    public static final String PREF_LOGIN_USER_QR_STATUS = "LOGIN_USER_QR_STATUS";

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public SharedPrefUtil(Context context) {
        sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    /**
     * ---------- Retrieving data from Shared Preferences---------------
     */
    public boolean getBoolean(String key) {
        return sharedPreferences.getBoolean(key, false);
    }

    public int getInteger(String key) {
        return sharedPreferences.getInt(key, -1);
    }

    public float getFloat(String key) {
        return sharedPreferences.getFloat(key, -1f);
    }

    public long getLong(String key) {
        return sharedPreferences.getLong(key, -1);
    }

    public String getString(String key) {
        return sharedPreferences.getString(key, null);
    }

    //-----------------------------------------------------------------
    //---------- Storing data to Shared Preferences--------------------
    public void setBoolean(String key, boolean value) {
        editor.putBoolean(key, value);
        editor.commit();
    }

    public void setInteger(String key, int value) {
        editor.putInt(key, value);
        editor.commit();
    }

    public void setFloat(String key, float value) {
        editor.putFloat(key, value);
        editor.commit();
    }

    public void setLong(String key, long value) {
        editor.putLong(key, value);
        editor.commit();
    }

    public void setString(String key, String value) {
        editor.putString(key, value);
        editor.commit();
    }

    public void setStringSet(String key, Set<String> value) {
        editor.putStringSet(key, value);
        editor.commit();
    }

    public Set<String> getStringSet(String key) {
        return sharedPreferences.getStringSet(key, new HashSet<String>());
    }

    /**
     * Delete value from SharedPreference for the given key
     */
    public void deleteFromDW(String key) {
        editor.remove(key);
        editor.commit();
    }

    public boolean hasKey(String key) {
        return sharedPreferences.contains(key);
    }
}