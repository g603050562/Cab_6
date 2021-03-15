package com.apps.ExtensionTaskUpgrade.core.ioAction;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Author:      Lee Yeung
 * Create Date: 2020-02-19
 * Description: 设备通讯类型
 */
@IntDef({DeviceIoType.SERIAL_PORT, DeviceIoType.CANBUS})
@Retention(RetentionPolicy.SOURCE)
public @interface DeviceIoType
{
    int SERIAL_PORT = 100;
    int CANBUS = 200;
}
