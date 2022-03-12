package com.nodeers.finder;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class BaseActivity extends AppCompatActivity
{
    public BaseActivity context;
    public MySharePreference mySharePreference;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        context = BaseActivity.this;
        mySharePreference = MySharePreference.getInstance(context);
        LocaleHelper.setLocale(context, mySharePreference.getLanguage() );
    }

    protected void attachBaseContext(Context base)
    {
        super.attachBaseContext(LocaleHelper.onAttach(base));
    }

}
