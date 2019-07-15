package com.inspur.icity.getlocation.core;

import android.app.job.JobParameters;
import android.app.job.JobService;

import com.alibaba.fastjson.JSONObject;
import com.inspur.icity.getlocation.utils.map.LocationManager;
import com.inspur.icity.getlocation.utils.net.ICityHttpOperation;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

public class LocationSchedulService extends JobService {
    @Override
    public boolean onStartJob(JobParameters params) {
        Disposable d = LocationManager.getInstance().getLocation().subscribe(new Consumer<JSONObject>() {
            @Override
            public void accept(JSONObject jsonObject) throws Exception {
                ICityHttpOperation.ICityRequestBuilder builder = new ICityHttpOperation.ICityRequestBuilder();
                builder.url("https://new.icity24.cn/icity")
                jobFinished(params, false);
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                jobFinished(params, true);
            }
        });
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return false;
    }
}
