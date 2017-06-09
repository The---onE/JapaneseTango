package com.xmx.tango.common.net;

/**
 * Created by The_onE on 2017/5/26.
 * Get请求回调
 */

public abstract class HttpGetCallback {
    public abstract void success(String result);

    public abstract void fail(Exception e);
}
