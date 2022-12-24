package com.deepwaterooo.sdk.appconfig;

/**
 * Class used to place application constant variables and we can use anywhere in the application
 */
public class Constants {

    public static final String PLAYGROUND_URL = "https://deepwaterooo.com/";

    //For Parent playground
    public static final String PARENT_PLAYGROUND_URL_DEV = "https://playground-dev.deepwaterooo.com/";
    public static final String PARENT_PLAYGROUND_URL_QA = "https://playground-qa.deepwaterooo.com/";
    public static final String PARENT_PLAYGROUND_URL_PRODUCTION = "https://playground.deepwaterooo.com/";
    //For Teacher playground
    public static final String TEACHER_PLAYGROUND_URL_DEV = "https://teacher-dev.squarepanda.com/";
    public static final String TEACHER_PLAYGROUND_URL_QA = "https://teacher-qa.squarepanda.com/";
    public static final String TEACHER_PLAYGROUND_URL_PRODUCTION = "https://teacher.squarepanda.com/";

    //https://support.deepwaterooo.com
    //https://deepwaterooo.com/pages/faqs
    public static final String HELP_URL = "https://support.deepwaterooo.com/";

    public static final String SDK_VERSION = "3.2";

    public static final String ERROR_EXCEPTION = "Exception";
    public static final String AUDIO_SELECT_PLAYER = "sounds/dia_select_player.wav";
    private static int deviceWidth;
    private static int deviceHeight;

    public static final String EMAIL_VALIDATE_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-\\+]+)*@"
        + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

    public static int RESPONSE_CODE = 200;
    public static int RESPONSE_CODE_UNAUTHORIZED = 401;
    public static int RESPONSE_CODE_FORBIDDEN = 403;
    public static int RESPONSE_CODE_LOGOUT = 500;
    public static final String TOKEN_ALREADY_EXPIRED = "Token already Expired";

    public static final String EXTRA_EMAIL = "EXTRA_EMAIL";
    public static final String EXTRA_DEVICE_ADDRESS = "EXTRA_DEVICE_ADDRESS";
    public static final String EXTRA_IS_CALIBRATION = "EXTRA_IS_CALIBRATION";
    public static final String EXTRA_IS_FROM_LOGIN = "EXTRA_IS_FROM_LOGIN";
    public static final String EXTRA_IS_FROM_GAME = "EXTRA_IS_FROM_GAME";
    public static final String EXTRA_LOGIN_USER = "EXTRA_LOGIN_USER";
    public static final String EXTRA_DATA = "EXTRA_DATA";
    public static final String EXTRA_PLAYER = "EXTRA_PLAYER";
    public static final String EXTRA_URL = "EXTRA_URL";
    public static final String EXTRA_SELECT_PLAYER = "EXTRA_SELECT_PLAYER";
    public static final String EXTRA_GAME_CONTEXT = "EXTRA_GAME_CONTEXT";
    public static final String EXTRA_IS_FIRST_TIME = "EXTRA_IS_FIRST_TIME";
    public static final String EXTRA_CHOOSE_PLAYSET = "EXTRA_CHOOSE_PLAYSET";
    public static final String EXTRA_IS_CREDITS = "EXTRA_IS_CREDITS";
    public static final String EXTRA_FROM_MENU = "EXTRA_FROM_MENU";
    public static final String EXTRA_PRIVACY_VERSION = "EXTRA_PRIVACY_VERSION";
    public static final String EXTRA_TERMS_VERSION = "EXTRA_TERMS_VERSION";
    public static final String EXTRA_IS_FROM_SIGNUP = "EXTRA_IS_FROM_SIGNUP";
    public static final String EXTRA_IS_FROM_SELECTION_PLAYER = "EXTRA_IS_FROM_SELECTION_PLAYER";
    public static final String EXTRA_IS_FROM_SELECT_PLAYER = "EXTRA_FROM_SELECT_PLAYER";
    public static final String EXTRA_CALL_APP_UPDATES_API ="EXTRA_APP_UPDATES_API";
    public static final String EXTRA_DEVICE_ADV_DATA_ADDRESS = "DEVICE_ADV_DATA_ADDRESS";

    public static final int CAMERA_REQUEST = 1002;
    public static final int PICK_IMAGE_REQUEST = 1003;
    public static final int RESULT_FINISH_APP = 1004;
    public static final int RESULT_PARENTAL_CHECK_SUCCESS = 1005;
    public static final int RESULT_LOGOUT = 1006;
    public static final int RESULT_PARENTAL_CHECK_FAILED = 1007;
    public static final int RESULT_BACK_TO_GAME = 1008;

    public static final int PICK_IMAGE_CHOOSER_REQUEST_CODE = 200;

    public static final int REQUEST_CODE_PARENTAL_CHECK = 100;
    public static final int REQUEST_CODE_CHILD_ADD_TEACHER = 101;


    // Audio sounds while doing actions 音效也是可以封装在这里面的吗,效率更高?
    public static final String AUDIO_ASK = "sounds/dia_ask.wav";
    public static final String AUDIO_GROWN_UP = "sounds/dia_grown_up.wav";
    public static final String AUDIO_LETTERS_OFF = "sounds/dia_letters_off.wav";
    public static final String OFF_LINE_LOGIN_USER_INFO = "loginUserInfo.json";
    public static final String PIC_IMAGE_NAME = "pickImageResult.jpeg";
    public static final String CROP_IMAGE_NAME = "cropImageResult.jpeg";

    private Constants() {
    }
    /**
     * sets the device width
     *
     * @param width device width
     */
    public static void setDeviceWidth(int width) {
        deviceWidth = width;
    }
    /**
     * get the device width
     *
     * @return
     */
    public static int getDeviceWidth() {
        return deviceWidth;
    }
    /**
     * sets the device height
     *
     * @param height device height
     */
    public static void setDeviceHeight(int height) {
        deviceHeight = height;
    }
    /**
     * get the device height
     *
     * @return
     */
    public static int getDeviceHeight() {
        return deviceHeight;
    }
}