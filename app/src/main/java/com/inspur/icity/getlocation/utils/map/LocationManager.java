package com.inspur.icity.getlocation.utils.map;

import android.content.Context;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.inspur.icity.getlocation.utils.net.exception.RetryFailedException;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class LocationManager {

    private static final String TAG = "LocationManager";

    LocationService locationService;

    private static final LocationManager ourInstance = new LocationManager();

    public static LocationManager getInstance() {
        return ourInstance;
    }

    private LocationManager() {
    }


    public void init(Context context) {
        synchronized (LocationManager.class) {
            if (locationService == null) {
                locationService = new LocationService(context);
            }
        }
    }

    public Observable<JSONObject> getLocation() {

        return Observable.create((ObservableOnSubscribe<BDLocation>) e -> {
            locationService.setLocationOption(locationService.getMyLocationClientOption());
            locationService.registerListener(new BDAbstractLocationListener() {
                @Override
                public void onReceiveLocation(BDLocation bdLocation) {
                    if (bdLocation != null && bdLocation.getLocType() != BDLocation.TypeServerError) {
                        e.onNext(bdLocation);
                        e.onComplete();
                    } else {
                        e.onError(new LocationFailedException());
                    }
                }
            });
            locationService.start();

        }).subscribeOn(Schedulers.io()).map(bdLocation -> (JSONObject) JSON.toJSON(bdLocation)).onErrorResumeNext((Function<Throwable, ObservableSource<? extends JSONObject>>) Observable::error).
                retryWhen(throwableObservable -> throwableObservable.zipWith(Observable.range(1, 3),
                        (throwable, integer) -> {
                            if (integer == 3) {
                                return new RetryFailedException(throwable);
                            }
                            return throwable;
                        }).flatMap((Function<Throwable, ObservableSource<?>>) throwable -> {
                    if (throwable instanceof RetryFailedException) {
                        return Observable.error(throwable.getCause());
                    }
                    return Observable.just(1);
                }));
    }

    public void stopLocation() {
        locationService.stop();
    }
}
