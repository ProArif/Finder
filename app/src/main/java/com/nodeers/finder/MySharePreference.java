package com.nodeers.finder;


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class MySharePreference
{
    public static MySharePreference instance;
    public static SharedPreferences pref;


    public MySharePreference(Context context)
    {
        if (context != null)
        {
            pref = PreferenceManager.getDefaultSharedPreferences(context);
        }
        else
        {
            pref = PreferenceManager.getDefaultSharedPreferences(AppClass.getInstance());
        }
    }

    public static MySharePreference getInstance(Context context)
    {
        if (instance == null || pref == null)
        {
            instance = new MySharePreference(context);
        }
        return instance;
    }


    public String getLanguage()
    {
        return pref.getString("appLanguage", "en");
    }

    public void setLanguage(String b)
    {
        pref.edit().putString("appLanguage", b).apply();
    }
}