package com.kilo.dev.androidnews.common;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.google.gson.Gson;
import com.zhouyou.http.EasyHttp;



/**
 * Created by ct_OS on 2017-12-31.
 */

public class BaseActivity extends AppCompatActivity {
    protected BaseActivity mActivity;
    protected Gson gson = new Gson();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = this;
    }
}
