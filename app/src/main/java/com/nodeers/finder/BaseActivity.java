package com.nodeers.finder;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import dev.b3nedikt.reword.RewordInterceptor;
import dev.b3nedikt.viewpump.ViewPump;

public class BaseActivity extends AppCompatActivity
{
    public BaseActivity context;




    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        context = BaseActivity.this;
//        mySharePreference = MySharePreference.getInstance(context);
        LocaleHelper.setLocale(context, LocaleHelper.getLanguage(context));


    }



}
