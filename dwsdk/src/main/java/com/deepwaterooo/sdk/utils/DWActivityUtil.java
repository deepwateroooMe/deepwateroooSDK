package com.deepwaterooo.sdk.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import com.deepwaterooo.sdk.appconfig.Numerics;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * This class is useful to track the child activity and it will save the details in server/localDB once activity ends.
 */
public class DWActivityUtil {

    private static String currentLetters;
    private static String prevLetters;
    private static boolean isActivityStarted;
    private static long startTime;
    private static long endTime;
    private static String activityGameWord;
    private static String activityOriginalWord;
    private static String activityGameLevel;
    private static String gameScore;
    private static Context mContext;
    private static ArrayList<String> request_types = new ArrayList<>();
    private static long START_TIME = 0;
    private static long END_TIME = 0;

    private static String previousActivityGameWord;
    private static String previousActivityOriginalWord;
    private static String currentChildId;


    /**
     * This will get called when device reads letters from the playset.
     *
     * @param letters
     */
    public static void lettersFromThePlayset(String letters) {
        if (isActivityStarted) {
            currentLetters = letters;
            if (!TextUtils.isEmpty(currentLetters) && currentLetters.trim().length() > Numerics.ZERO) {
                for (int i = Numerics.ZERO; i < currentLetters.length(); i++) {
                    if ((TextUtils.isEmpty(prevLetters) ||
                         (!currentLetters.substring(i, i + 1).equals(prevLetters.substring(i, i + 1))
                             ))
                        && !currentLetters.substring(i, i + 1).equals(" ")) {
                        String letter = currentLetters.substring(i, i + 1);
                        long time = System.currentTimeMillis();
                    }
                }
            }
            prevLetters = letters;
            //previousActivityGameWord = "";
            // previousActivityOriginalWord = "";
        }
    }

    /**
     * This method will get called when new activity wanted to create by passing game word, original word level of game as parameters.
     *
     * @param context      - calling activity for context
     * @param gameWord     - Game word of the current game
     * @param originalWord - Original word with respect to game word.
     * @param level        - Game level.
     */
    public static void startNewActivityWithGameWord(Context context, String gameWord, String originalWord, String level) {
        //Log.i("SPActivityUtil", "startNewActivityWithGameWord -> gameWord :" + gameWord + "  originalWord:" + originalWord + "  level:" + level);
        START_TIME = System.currentTimeMillis();
        mContext = context;
        isActivityStarted = true;
        activityGameWord = gameWord;
        activityOriginalWord = originalWord;
        activityGameLevel = level;
        startTime = System.currentTimeMillis() / Numerics.THOUSAND;

//        //as per AS-407:Child ID should be stored on activity start not activity end
//        currentChildId = PlayerUtil.getSelectedPlayer((Activity) mContext).getId();

        if (activityOriginalWord == null) {
            activityOriginalWord = "";
        }

        if (activityGameWord == null) {
            activityGameWord = "";
        }

        if (activityGameLevel == null) {
            activityGameLevel = "";
        }
    }

    /**
     * This method will get called when new letter is requested.
     *
     * @param letter      - The new requesting letter
     * @param requestType - The letter request type
     */
    public static void requestNewLetterWithType(String letter, String requestType) {
        //Log.i("SPActivityUtil", "requestNewLetterWithType -> letter :" + letter + "  requestType:" + requestType);
        if (TextUtils.isEmpty(letter)) {
            return;
        }
        request_types.clear();
        long reuest_time = System.currentTimeMillis();

        if (!TextUtils.isEmpty(requestType)) {
            requestType = requestType.replaceAll(" ", "");
            if (requestType.split(",").length == Numerics.ONE) {

                request_types.add(requestType.trim());
            } else {
                request_types.addAll(Arrays.asList(requestType.split(",")));
            }
        }
    }

    /**
     * This method will get called to add new request type to the current activity.
     *
     * @param requestType - The letter request type
     */
    public static void addNewRequestType(String requestType) {
        // Log.i("SPActivityUtil", "addNewRequestType -> requestType :" + requestType);
        if (!TextUtils.isEmpty(requestType)) {
            requestType = requestType.replaceAll(" ", "");
            if (requestType.split(",").length == Numerics.ONE) {
                request_types.add(requestType.trim());
            } else {
                request_types.addAll(Arrays.asList(requestType.split(",")));
            }
        }
    }

    /**
     * This method get called when we wanted to end the activity
     *
     * @param score - game score
     */
    public static void endActivityWithScore(String score) {
        // Log.i("SPActivityUtil", "endActivityWithScore -> score :" + score);
        // 这个方法就需要整个的重写呀
    }

    /**
     * This method will returns the number of words placed on the playset slots.
     *
     * @return
     */
    private static String[] numberOfWordsOnTray() {
        String trimLetters = currentLetters.trim();
        String lettersArray[] = trimLetters.split(" ");
        return lettersArray;
    }


    /**
     * This method used to change the game word.
     *
     * @param gameWord - game word
     */
    public static void setGameWord(String gameWord) {
        //Log.i("SPActivityUtil", "setGameWord -->  gameWord:" + gameWord);
        activityGameWord = gameWord;
        //previousActivityGameWord = gameWord;
    }


    /**
     * This method used to change the original word.
     *
     * @param originalWord - original word
     */
    public static void setOriginalWord(String originalWord) {
        //Log.i("SPActivityUtil", "setOriginalWord -->  originalWord:" + originalWord);
        activityOriginalWord = originalWord;
        //previousActivityOriginalWord = originalWord;
    }


    /**
     * This method used to change the gameLevel.
     *
     * @param gameLevel - game level
     */
    public static void setGameLevel(String gameLevel) {
        //Log.i("SPActivityUtil", "setGameLevel -->  gameLevel:" + gameLevel);
        activityGameLevel = gameLevel;
    }

    /**
     * this method used for Letter Lullaby game activities for single letter word
     *
     * @param prevLetters previous letters on the playset
     * @return boolean return true or false for the
     */
    public static boolean isPreviousLettersContainsOriginalWord(String prevLetters) {
        if (!previousActivityOriginalWord.isEmpty() &&
            previousActivityOriginalWord.length() == 1 &&
            previousActivityGameWord.equals(previousActivityOriginalWord) &&
            prevLetters.contains(previousActivityOriginalWord)) {
            return true;
        }
        return false;
    }

    /**
     * this merhod provides Android SDK version
     *
     * @return String sdk version
     */
    public static String getSDKVersion() {
        // return BuildConfig.BUILD_TYPE.VERSION_NAME; // 不知道这里是怎么回事了,先绕一下,回头再来解决
        return "3.2";
    }
    /**
     * this method provides the Game App version
     *
     * @return String game app version
     */
    public static String getGameAppVersion(Context context) {
        String appVersion;
        try {
            PackageManager manager = context.getPackageManager();
            appVersion = manager.getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (Exception e) {
            appVersion = "0.0";
        }
        return appVersion;
    }
}