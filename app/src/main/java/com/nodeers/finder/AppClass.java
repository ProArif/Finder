package com.nodeers.finder;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;

public class AppClass extends Application
{


        @Override
        protected void attachBaseContext(Context base) {
            super.attachBaseContext(LocaleHelper.setLocale(base,LocaleHelper.getLanguage(base)));
        }

        @Override
        public void onConfigurationChanged(Configuration newConfig) {
            super.onConfigurationChanged(newConfig);
            LocaleHelper.setLocale(this,LocaleHelper.getLanguage(this));
        }


}
