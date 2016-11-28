package com.sqsong.gankiosample;

import android.app.Application;
import android.content.Context;

/**
 * Created by 青松 on 2016/11/24.
 */

public class BaseApplication extends Application {

    public static String cacheDir;
    public static Context mAppContext = null;

    @Override
    public void onCreate() {
        super.onCreate();

        mAppContext = getApplicationContext();

        if (getApplicationContext().getExternalCacheDir() != null && ExistSDCard()) {
            cacheDir = getApplicationContext().getExternalCacheDir().toString();
        } else {
            cacheDir = getApplicationContext().getCacheDir().toString();
        }
    }

    private boolean ExistSDCard() {
        return android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
    }

    public static Context getAppContext() {
        return mAppContext;
    }

}
