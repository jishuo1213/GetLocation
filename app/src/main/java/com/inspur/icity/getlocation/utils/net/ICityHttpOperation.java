package com.inspur.icity.getlocation.utils.net;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;
import android.util.Log;


import com.inspur.icity.getlocation.utils.net.exception.NetWorkException;
import com.inspur.icity.getlocation.utils.net.exception.RetryFailedException;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;

/**
 * Created by Fan on 2017/9/1.
 * <p>
 * 处理爱城市http请求的类，
 * 会处理一般性的返回错误
 * 在发送请求是会进行包装
 */

public class ICityHttpOperation {


    public static final String KEY_LOAD_STATE = "LOAD_STATE";//上传下载的状态。其中 0 是进度，1是成功 2是失败
    public static final String KEY_VALUE = "VALUE";
    public static final String KEY_EXTRA_DATA = "EXTRA_DATA";

    public static final int STATE_PROGRESS = 0;
    public static final int STATE_SUCCESS = 1;
    public static final int MAX_TIME = 1;

    private static final int ERROR_CODE_TOKEN_1 = 701;
    private static final int ERROR_CODE_TOKEN_2 = 702;
    private static final int ERROR_CODE_TOKEN_3 = 703;
    private static final int ERROR_CODE_BLACKLIST = 500;
    private static final int ERROR_CODE_CLOSE_PHONE_MESSAGE = 704;
    private static final int LOG_OUT = 110;

    private static final int ERROR_CODE_701 = 701; // 安全认证信息缺失
    private static final int ERROR_CODE_702 = 702; // 会话中安全认证信息缺失


    private String accessToken;

    private Observable<JSONObject> getTokenObservable;
    private Observable<JSONObject> logoutObservable;

    private static final ICityHttpOperation ourInstance = new ICityHttpOperation();

    private Context context;

    public static ICityHttpOperation getInstance() {
        return ourInstance;
    }

    private ICityHttpOperation() {
    }

    public String getAccessToken() {
        return !TextUtils.isEmpty(accessToken) ? accessToken : "";
    }

//    public String getOpenToken(){
//        return AESUtils.aesEncrypt(getAccessToken(), AESUtils.PASSWORD);
//    }

    public void setAccessToken(String accessToken) {
//        LogProxy.i(TAG, "setAccessToken: " + accessToken);
//        accessToken = "ecc5ed4f-5a10-4c56-85ed-4f5a106c561a";
//        PreferencesHelper.getInstance().writeToPreferences(PreferencesHelper.KEY_ACCESS_TOKEN, accessToken);
        this.accessToken = accessToken;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public static class ICityRequestBuilder extends HttpOperation.IRequestBuilder {

        private long reTryDelay;
        private int maxRetryTimes;

        private boolean isNeedAddRefer;

        public ICityRequestBuilder() {
            super();
            reTryDelay = 0;
            maxRetryTimes = MAX_TIME;
            isNeedAddRefer = false;
        }

        @Override
        public ICityRequestBuilder url(String url) {
            super.url(url);
            if (isNeedAddRefer) {
                JSONObject referHeader = new JSONObject();
                try {
                    referHeader.put("Referer", new URL(url).getHost());
                    headers(referHeader);
                } catch (JSONException | MalformedURLException e) {
                    e.printStackTrace();
                }
            }
            return this;
        }

        @Override
        public ICityRequestBuilder params(ArrayMap<String, String> params) {
            super.params(params);
            return this;
        }

        @Override
        public ICityRequestBuilder params(JSONObject params) {
            super.params(params);
            return this;
        }

        @Override
        public ICityRequestBuilder get() {
            super.get();
            return this;
        }

        @Override
        public ICityRequestBuilder post() {
            super.post();
            return this;
        }

        @Override
        public HttpOperation.IRequestBuilder requestId(String requestId) {
            return super.requestId(requestId);
        }

        public ICityRequestBuilder isHaveHeader(boolean isHaveHeader) {
//            if (isHaveHeader)
//                headers(getCommonHeaders());
            return this;
        }

        public ICityRequestBuilder setHeader(ArrayMap<String, String> header) {
            headers(header);
            return this;
        }

        @Override
        public ICityRequestBuilder isCacheResponse(boolean isUseCache) {
            super.isCacheResponse(isUseCache);
            return this;
        }

        @Override
        public ICityRequestBuilder cachePath(String path) {
            super.cachePath(path);
            return this;
        }

        public ICityRequestBuilder retryWhenFailed(boolean retryWhenFailed) {
            if (!retryWhenFailed) {
                maxRetryTimes = 0;
            }
            return this;
        }

        public ICityRequestBuilder retryDelay(long delay) {
            this.reTryDelay = delay;
            return this;
        }

        public ICityRequestBuilder maxRetryTimes(int times) {
            this.maxRetryTimes = times;
            return this;
        }

        @Override
        protected void checkRequestArgs(boolean isRx) {
            super.checkRequestArgs(isRx);

        }

        public Observable<String> execute() {
            checkRequestArgs(true);
            return ICityHttpOperation.getInstance().request(this);
        }

//        private ArrayMap<String, String> getCommonHeaders() {
//            ArrayMap<String, String> header = new ArrayMap<>();
//            header.put("access_token", ICityHttpOperation.getInstance().getAccessToken());
//            header.put("version", BuildConfig.VERSION_NAME);
//            header.put("build", BuildConfig.VERSION_CODE + "");
//            header.put("os","android");
//            header.put("pushToken", PreferencesHelper.getInstance().readStringPreference(PreferencesHelper.JPUSH_REGISTER_ID));
//            header.put("cityCode", PreferencesHelper.getInstance().getCurrentUser().getCityCode());
//            try {
//                if (url != null) {
//                    header.put("Referer", new URL(url).getHost());
//                } else {
//                    isNeedAddRefer = true;
//                }
//            } catch (MalformedURLException e) {
//                e.printStackTrace();
//            }
//            LogProxy.d(TAG, "getCommonHeaders: access_token=" + header.get("access_token") + ",pushToken=" + header.get("pushToken"));
//            return header;
//        }
    }

//    public static class ICityLoadFileRequestBuilder extends HttpOperation.LoadFileRequestBuilder {
//
//        private long reTryDelay;
//        private int maxRetryTimes;
//
//        public ICityLoadFileRequestBuilder() {
//            super();
//            maxRetryTimes = 0;
//            reTryDelay = 0;
//        }
//
//        public ICityLoadFileRequestBuilder isHaveHeader(boolean isHaveHeader) {
////            if (isHaveHeader)
////                headers(getCommonHeaders());
//            return this;
//        }
//
////        private ArrayMap<String, String> getCommonHeaders() {
////            ArrayMap<String, String> header = new ArrayMap<>();
////            header.put("access_token", ICityHttpOperation.getInstance().getAccessToken());
////            header.put("version", BuildConfig.VERSION_NAME);
////            header.put("build", BuildConfig.VERSION_CODE + "");
////            header.put("pushToken", PreferencesHelper.getInstance().readStringPreference(PreferencesHelper.JPUSH_REGISTER_ID));
////            header.put("cityCode", PreferencesHelper.getInstance().getCurrentUser().getCityCode());
////            return header;
////        }
//
//        @Override
//        public ICityLoadFileRequestBuilder download() {
//            super.download();
//            return this;
//        }
//
//        @Override
//        public ICityLoadFileRequestBuilder to(String destFilePath) {
//            super.to(destFilePath);
//            return this;
//        }
//
//        @Override
//        public ICityLoadFileRequestBuilder upload() {
//            super.upload();
//            return this;
//        }
//
//        @Override
//        public ICityLoadFileRequestBuilder from(String localFilePath, String fileKey, String fileName) {
//            super.from(localFilePath, fileKey, fileName);
//            return this;
//        }
//
//        @Override
//        public ICityLoadFileRequestBuilder url(String url) {
//            super.url(url);
//            return this;
//        }
//
//        @Override
//        public ICityLoadFileRequestBuilder params(ArrayMap<String, String> params) {
//            super.params(params);
//            return this;
//        }
//
//        @Override
//        public ICityLoadFileRequestBuilder params(JSONObject params) {
//            super.params(params);
//            return this;
//        }
//
//        @Override
//        public ICityLoadFileRequestBuilder requestId(String requestId) {
//            super.requestId(requestId);
//            return this;
//        }
//
//        public ICityLoadFileRequestBuilder retryWhenFailed(boolean retryWhenFailed) {
//            if (!retryWhenFailed) {
//                maxRetryTimes = 0;
//            }
//            return this;
//        }
//
//        public ICityLoadFileRequestBuilder retryDelay(long delay) {
//            this.reTryDelay = delay;
//            return this;
//        }
//
//        public ICityLoadFileRequestBuilder maxRetryTimes(int times) {
//            this.maxRetryTimes = times;
//            return this;
//        }
//
//        public Observable<JSONObject> execute() {
//            return ICityHttpOperation.getInstance().request(this);
//        }
//
//    }
//

//    private Observable<JSONObject> request(ICityLoadFileRequestBuilder iCityLoadFileRequestBuilder) {
//        return iCityLoadFileRequestBuilder.rxExec().onErrorResumeNext(throwable -> {
////            LogProxy.d(TAG, iCityLoadFileRequestBuilder.toString() + "====" + throwable.getMessage() + "====" + throwable.getClass().getName());
//            if (throwable instanceof IOException) {
//                return Observable.error(new NetWorkException(throwable));
//            }
//            return Observable.error(throwable);
//        }).retryWhen(throwableObservable -> throwableObservable.zipWith(Observable.range(1,
//                iCityLoadFileRequestBuilder.maxRetryTimes + 1), (throwable, integer) -> {
//            if (integer == iCityLoadFileRequestBuilder.maxRetryTimes + 1) {
//                //                        throw new Exception(throwable);
//                return new RetryFailedException(throwable);
//            }
//            return throwable;
//        }).flatMap((Function<Throwable, ObservableSource<?>>) throwable -> {
//            if (throwable instanceof RetryFailedException) {
//                return Observable.error(throwable.getCause());
//            }
//            return Observable.timer(iCityLoadFileRequestBuilder.reTryDelay, TimeUnit.MICROSECONDS);
//        }));
//    }

    private static final String TAG = "ICityHttpOperation";

    private Observable<String> request(ICityRequestBuilder requestBuilder) {
        return requestBuilder.rxExec().doOnNext(jsonObject -> {
            switch (jsonObject.optInt(HttpOperation.KEY_return_type)) {
                case HttpOperation.ReturnType.STRING:
                    try {
                        String response = jsonObject.optString(HttpOperation.KEY_return_value);
                        if (TextUtils.isEmpty(response)) {
                            return;
                        }
                        JSONObject responseValue = new JSONObject(response);
                        Log.i(TAG, "request: " + requestBuilder.url + "  access_token: " + (requestBuilder.headers == null ? "" : requestBuilder.headers.optString("access_token")));
                        Log.i(TAG, " response:" + responseValue.toString());
                    } catch (JSONException e) {
                        return;
                    }
                case HttpOperation.ReturnType.CACHE:

                    break;
                case HttpOperation.ReturnType.FILE:
                    break;
            }
        }).onErrorResumeNext(throwable -> {
            Log.d(TAG, requestBuilder.toString() + "=====" + throwable.getMessage() + "=====" + throwable.getClass().getName());
            if (throwable instanceof IOException) {
                return Observable.error(new NetWorkException(throwable));
            }
            return Observable.error(throwable);
        }).map(jsonObject ->
                jsonObject.optString(HttpOperation.KEY_return_value)
        ).retryWhen(throwableObservable -> throwableObservable.zipWith(Observable.range(1, requestBuilder.maxRetryTimes + 1), (throwable, integer) -> {
            if (integer == requestBuilder.maxRetryTimes + 1) {
                return new RetryFailedException(throwable);
            } else {
                return throwable;
            }
        }).flatMap((Function<Throwable, ObservableSource<?>>) throwable -> {
            if (throwable instanceof RetryFailedException) {
                return Observable.error(throwable.getCause());
            } else {
                return Observable.timer(requestBuilder.reTryDelay, TimeUnit.MICROSECONDS);
            }
        }));
    }


    public Observable<JSONObject> multiRequest(ArrayList<ICityRequestBuilder> requestList) {
        ArrayList<Observable<String>> observList = new ArrayList<>();
        for (ICityRequestBuilder builder : requestList) {
            observList.add(builder.execute());
        }
        return Observable.mergeDelayError(new Iterable<Observable<String>>() {
            @NonNull
            @Override
            public Iterator<Observable<String>> iterator() {
                return observList.iterator();
            }
        }).map(new Function<String, JSONObject>() {
            @Override
            public JSONObject apply(String s) throws Exception {
                return null;
            }
        });
    }

//    private Observable<JSONObject> getToken(ICityRequestBuilder requestBuilder) {
//        LogProxy.i(TAG, "getToken: request:" + requestBuilder.url + ":getToken");
//        if (getTokenObservable == null) {
//            JSONObject params = PreferencesHelper.getInstance().getCheckTokenParams(Constants.CHECKTOKEN_ON);
//            HttpOperation.IRequestBuilder getTokenBuilder = new HttpOperation.IRequestBuilder();
//            getTokenBuilder.url(ServerUrl.CHECK_TOKEN).post().params(params);
//            getTokenObservable = getTokenBuilder.rxExec().doOnNext(jsonObject -> {
//                JSONObject responseValue = new JSONObject(jsonObject.optString(HttpOperation.KEY_return_value));
//                LogProxy.i(TAG, "getToken: " + responseValue.toString());
//                if ("0000".equals(responseValue.optString("code"))) {
//                    JSONObject tokenObject = responseValue.optJSONObject("data");
//                    if (TextUtils.equals("guest", tokenObject.optString("scope")) && PreferencesHelper.getInstance().getCurrentUser().isLogin()) {
//                        PreferencesHelper.getInstance().refreshUserInfo(context, UserInfoBean.logout(PreferencesHelper.getInstance().getCurrentUser()));
//                    }
//                    ICityHttpOperation.this.accessToken = tokenObject.optString("access_token");
//                    requestBuilder.isHaveHeader(true);
//                    getTokenObservable = null;
//                } else {
//                    getTokenObservable = null;
//                    throw new Exception("调用失败");
//                }
////                if (responseValue.optString("state").equals("1")) {
////                    JSONObject tokenObject = responseValue.optJSONArray("result").optJSONObject(0);
////                    if(TextUtils.equals("guest",tokenObject.optString("scope")) && PreferencesHelper.getInstance().getCurrentUser().isLogin()){
////                        PreferencesHelper.getInstance().refreshUserInfo(context,UserInfoBean.logout(PreferencesHelper.getInstance().getCurrentUser()));
////                    }
////                    ICityHttpOperation.this.accessToken = tokenObject.optString("access_token");
////                    requestBuilder.isHaveHeader(true);
////                    getTokenObservable = null;
////                } else {
////                    getTokenObservable = null;
////                    throw new Exception("调用失败");
////                }
//            }).replay();
//            ((ConnectableObservable) getTokenObservable).connect();
//        }
//        return getTokenObservable;
//    }

//    private synchronized Observable<JSONObject> logout(ICityRequestBuilder requestBuilder, int type) {
//        LogProxy.i(TAG, "logout: " + requestBuilder.toString());
//        if (logoutObservable == null && PreferencesHelper.getInstance().getCurrentUser().isLogin()) {
//            this.accessToken = "";
//            PreferencesHelper.getInstance().writeToPreferences(PreferencesHelper.KEY_ACCESS_TOKEN, "");
//            JSONObject params = PreferencesHelper.getInstance().getCheckTokenParams(Constants.CHECKTOKEN_ON);
//            HttpOperation.IRequestBuilder getTokenBuilder = new HttpOperation.IRequestBuilder();
//            getTokenBuilder.url(ServerUrl.CHECK_TOKEN).post().params(params);
//            logoutObservable = getTokenBuilder.rxExec().doOnNext(new Consumer<JSONObject>() {
//                @Override
//                public void accept(JSONObject jsonObject) throws Exception {
//                    JSONObject responseValue = new JSONObject(jsonObject.optString(HttpOperation.KEY_return_value));
//
//                    if ("0000".equals(responseValue.optString("code"))) {
//                        JSONObject dataJson = responseValue.optJSONObject("data");
//                        if (TextUtils.equals("guest", dataJson.optString("scope"))) {
//                            //退出登录，用户变成游客状态
//                            UnLoginUserInfoBean unLoginUserInfoBean = UserInfoBean.logout(PreferencesHelper.getInstance().getCurrentUser());
//                            unLoginUserInfoBean.isLoginTypeChange = true;
//                            ICityHttpOperation.getInstance().setAccessToken(dataJson.optString("access_token"));
//                            requestBuilder.isHaveHeader(true);
//                            PreferencesHelper.getInstance().refreshUserInfo(context, unLoginUserInfoBean);
//                            LogProxy.i(TAG, "accept: " + responseValue.toString());
////                                context.startActivity(new Intent(context, LogoutActivity.class));
//                            Bundle args = new Bundle();
//                            args.putInt("type", type);
//                            ActivityLifecycleListener.getInstance().startActivity(LogoutActivity.class, args);
//                            PreferencesHelper.getInstance().writeToPreferences(PreferencesHelper.KEY_SIGN_NO, "");
//                        }
//                        /**
//                         * 清空社保卡signNO
//                         */
//                        PreferencesHelper.getInstance().writeToPreferences(PreferencesHelper.KEY_SIGN_NO, "");
//                    }
//
////                    TokenBean tokenBean = FastJsonUtils.getObject(responseValue.toString(), TokenBean.class);
////                    if (TextUtils.equals("1", tokenBean.state)) {
////                        if (tokenBean.getResult() != null && tokenBean.getResult().size() != 0) {
////                            TokenBean.ResultBean resultBean = tokenBean.getResult().get(0);
////                            if (TextUtils.equals("login", resultBean.getScope())) {
////                                //登录状态
////
////                            } else if (TextUtils.equals("guest", resultBean.getScope())) {
////                                //退出登录，用户变成游客状态
////                                UnLoginUserInfoBean unLoginUserInfoBean = UserInfoBean.logout(PreferencesHelper.getInstance().getCurrentUser());
////                                unLoginUserInfoBean.isLoginTypeChange = true;
////                                ICityHttpOperation.getInstance().setAccessToken(resultBean.getAccess_token());
////                                requestBuilder.isHaveHeader(true);
////                                PreferencesHelper.getInstance().refreshUserInfo(context, unLoginUserInfoBean);
////                                Log.i(TAG, "accept: " + responseValue.toString());
//////                                context.startActivity(new Intent(context, LogoutActivity.class));
////                                Bundle args = new Bundle();
////                                args.putInt("type", type);
////                                ActivityLifecycleListener.getInstance().startActivity(LogoutActivity.class, args);
////                            }
////
////                        }
////                    }
//                    logoutObservable = null;
//                }
//            }).replay();
//            ((ConnectableObservable) logoutObservable).connect();
//            return logoutObservable;
//        } else {
//            return Observable.error(new NetWorkException());
//        }
//    }
}
