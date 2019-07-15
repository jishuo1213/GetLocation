package com.inspur.icity.getlocation.core;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;

import com.inspur.icity.getlocation.utils.map.LocationManager;
import com.inspur.icity.getlocation.utils.net.ICityHttpOperation;

public class LocationApplication extends Application {


    @Override
    public void onCreate() {
        super.onCreate();
        String name = getCurProcessName(this);
        if (name != null && name.equals("com.inspur.icity.getlocation")) {
            init();
        }
    }

    private void init() {
        LocationManager.getInstance().init(this);
        ICityHttpOperation.getInstance();
    }

    private String getCurProcessName(Context context) {
        int pid = android.os.Process.myPid();
        ActivityManager activityManager = (ActivityManager) context.
                getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo appProcessInfo : activityManager.getRunningAppProcesses()) {
            if (appProcessInfo.pid == pid)
                return appProcessInfo.processName;
        }
        return null;
    }

}

