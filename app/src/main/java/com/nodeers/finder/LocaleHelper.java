package com.nodeers.finder;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.LocaleList;
import android.preference.PreferenceManager;

import java.util.Locale;

public class LocaleHelper
{

    public static Context setLocale(Context context, String language)
    {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);

        Resources resources = context.getResources();

        Configuration configuration = new Configuration(resources.getConfiguration());
        configuration.setLayoutDirection(locale);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
        {
            configuration.setLocale(locale);
            LocaleList localeList = new LocaleList(locale);
            LocaleList.setDefault(localeList);
            configuration.setLocales(localeList);
        }
        else
        {
            configuration.locale = locale;
            configuration.setLocale(locale);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1)
        {
            return context.createConfigurationContext(configuration);
        }
        else
        {
            resources.updateConfiguration(configuration, resources.getDisplayMetrics());
            return context;
        }
    }

    public static Context onAttach(Context context)
    {
        return setLocale(context, MySharePreference.getInstance(context).getLanguage());
    }
}