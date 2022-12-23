package com.deepwaterooo.sdk.utils;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import java.util.Locale;

public class LocaleHelper {

    public static Context onAttach(Context context) {
        String lang = getPersistedData(context, Locale.getDefault().getLanguage());
        return setLocale(context, lang);
    }

    public static Context onAttach(Context context, String defaultLanguage) {
        String lang = getPersistedData(context, defaultLanguage);
        return setLocale(context, lang);
    }

    public static String getLanguage(Context context) {
        return getPersistedData(context, Locale.getDefault().getLanguage());
    }

    public static Context setLocale(Context context, String language) {
        //persist(context, language);
        return updateResourcesLegacy(context, language);
    }

    private static String getPersistedData(Context context, String defaultLanguage) {

        SharedPrefUtil sharedPrefUtil = new SharedPrefUtil(context);
        if (sharedPrefUtil.getString(SharedPrefUtil.PREF_SELECTED_LANGUAGE) != null) {
            return sharedPrefUtil.getString(SharedPrefUtil.PREF_SELECTED_LANGUAGE);
        } else if (defaultLanguage.equals(Locale.SIMPLIFIED_CHINESE.getLanguage())) {
            return Locale.SIMPLIFIED_CHINESE.getLanguage();
        } else {
            return Locale.ENGLISH.getLanguage();
        }
    }

    public static void persist(Context context, String language) {
        SharedPrefUtil sharedPrefUtil = new SharedPrefUtil(context);
        sharedPrefUtil.setString(SharedPrefUtil.PREF_SELECTED_LANGUAGE, language);
    }

    private static Context updateResourcesLegacy(Context context, String language) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        Resources resources = context.getResources();
        Configuration configuration = resources.getConfiguration();
        configuration.locale = locale;
        resources.updateConfiguration(configuration, resources.getDisplayMetrics());
        return context;
    }
}