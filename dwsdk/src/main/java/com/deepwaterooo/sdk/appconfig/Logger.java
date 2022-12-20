package com.deepwaterooo.sdk.appconfig;

import android.util.Log;

/**
 * Class used to print the logs and debugging
 */
public class Logger {

    /**
     * For Logs in App
     */
    private static final boolean LOG = true;

    private Logger() {

    }

    /**
     * print a INFO log message
     *
     * @param tag Used to identify the source of a log message.  It usually identifies
     *            the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     */
    public static void info(String tag, String msg) {
        if (LOG) {
            Log.i(tag, msg);
        }
    }

    /**
     * print a WARN log message
     *
     * @param tag Used to identify the source of a log message.  It usually identifies
     *            the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     */
    public static void warn(String tag, String msg) {
        if (LOG) {
            Log.w(tag, msg);
        }
    }

    /**
     * print a ERROR log message
     *
     * @param tag Used to identify the source of a log message.  It usually identifies
     *            the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     */
    public static void error(String tag, String msg) {
        if (LOG) {
            Log.e(tag, msg);
        }
    }

    /**
     * print a DEBUG log message
     *
     * @param tag Used to identify the source of a log message.  It usually identifies
     *            the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     */
    public static void debug(String tag, String msg) {
        if (LOG) {
            Log.d(tag, msg);
        }
    }
}
