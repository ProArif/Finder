package com.nodeers.finder;

import android.app.Application;
import android.content.Context;

public class AppClass extends Application
{

    public static AppClass appClass;
    public static AppClass getInstance()
    {
        return appClass;
    }


    @Override
    public void onCreate()
    {
        super.onCreate();
        appClass = this;
        MySharePreference.getInstance(this);
    }


    @Override
    protected void attachBaseContext(Context base)
    {
        super.attachBaseContext(LocaleHelper.onAttach(base));
    }

}
