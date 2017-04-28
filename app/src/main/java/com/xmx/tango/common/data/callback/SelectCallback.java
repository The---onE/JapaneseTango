package com.xmx.tango.common.data.callback;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.xmx.tango.common.data.cloud.ICloudEntity;

import java.util.List;

/**
 * Created by The_onE on 2016/5/29.
 */
public abstract class SelectCallback<Entity extends ICloudEntity> {

    public abstract void success(AVObject user, List<Entity> entities);

    public abstract void syncError(int error);

    public abstract void syncError(AVException e);
}
