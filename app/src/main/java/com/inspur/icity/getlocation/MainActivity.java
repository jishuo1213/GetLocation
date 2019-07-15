package com.inspur.icity.getlocation;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.inspur.icity.getlocation.utils.PermissionUtils;
import com.inspur.icity.getlocation.utils.map.LocationManager;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (PermissionUtils.getPermission(this, 100, PermissionUtils.GET_PHONE_LOCATION)) {
            LocationManager.getInstance().getLocation(new BDAbstractLocationListener() {
                @Override
                public void onReceiveLocation(BDLocation bdLocation) {
                    if (bdLocation != null && bdLocation.getLocType() != BDLocation.TypeServerError) {
                        Log.i(TAG, "onReceiveLocation: " + bdLocation.getAddrStr());
                        Log.i(TAG, "onReceiveLocation: " + JSON.toJSON(bdLocation));
                    }
                }
            });
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 100) {
            LocationManager.getInstance().getLocation(new BDAbstractLocationListener() {
                @Override
                public void onReceiveLocation(BDLocation bdLocation) {
                    if (bdLocation != null) {
                        Log.i(TAG, "onReceiveLocation: " + bdLocation.getAddrStr());
                        Log.i(TAG, "onReceiveLocation: " + JSON.toJSON(bdLocation));
                    }
                }
            });
        }
    }
}