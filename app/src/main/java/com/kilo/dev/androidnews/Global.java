package com.kilo.dev.androidnews;

import com.kilo.dev.androidnews.net.HomePageData;

import java.util.List;

/**
 * Created by ct_OS on 2018-1-2.
 */

public class Global {

    private static Global s = null;

    private Global() {
    }

    public static Global getInstance() {
        if (s == null) { //多次判断,稍微提升了用锁旗位的效率
            synchronized (Global.class) {
                if (s == null)
                    s = new Global(); //延迟加载
            }
        }
        return s;
    }


    public HomePageData getCurrentHomePageData() {
        return currentHomePageData;
    }

    public void setCurrentHomePageData(HomePageData currentHomePageData) {
        this.currentHomePageData = currentHomePageData;
    }

    private static HomePageData currentHomePageData;

}
