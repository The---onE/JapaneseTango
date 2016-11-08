package com.xmx.tango.Tools.Data.SQL;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;

/**
 * Created by The_onE on 2016/1/11.
 */
public abstract class InsertCallback {

    public abstract void proceeding(int index);

    public abstract void success(int total);
}
