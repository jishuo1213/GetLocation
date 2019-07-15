package com.inspur.icity.getlocation.utils.map;

/**
 * Created by Fan on 2017/9/21.
 */

public class LocationFailedException extends Exception {
    public LocationFailedException() {
        super("定位失败，返回为null");
    }
}
