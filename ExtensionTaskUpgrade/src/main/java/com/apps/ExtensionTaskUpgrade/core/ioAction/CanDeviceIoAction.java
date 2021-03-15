package com.apps.ExtensionTaskUpgrade.core.ioAction;


import android.support.v4.util.Consumer;

/**
 * Author:      Lee Yeung
 * Create Date: 2020-02-20
 * Description:
 */
public interface CanDeviceIoAction extends DeviceIoAction {
    void registerTimeOut(final long id, long timeOutValue);

    void register(final long id, Consumer<byte[]> consumer);

    void unRegister(final long id);
}
