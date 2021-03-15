package com.halouhuandian.DcUpgrade.canExtension;

import android.support.v4.util.Consumer;

/**
 * Author:      Lee Yeung
 * Create Date: 2019-09-23
 * Description:
 */
public interface CanDeviceIoAction extends DeviceIoAction {
    void registerTimeOut(int id, long timeOutValue);

    void register(int id, Consumer<byte[]> consumer);

    void unRegister(final int id);
}
