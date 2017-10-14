package com.xmx.tango.common.data.sync;

import com.xmx.tango.common.data.cloud.ICloudEntity;
import com.xmx.tango.common.data.sql.ISqlEntity;

/**
 * Created by The_onE on 2016/5/29.
 */
public interface ISyncEntity extends ICloudEntity, ISqlEntity {
    String getCloudId();

    void setCloudId(String id);
}
