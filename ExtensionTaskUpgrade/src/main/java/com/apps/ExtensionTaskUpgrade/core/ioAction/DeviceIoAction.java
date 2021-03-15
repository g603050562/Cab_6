package com.apps.ExtensionTaskUpgrade.core.ioAction;

import java.io.IOException;

/**
 * Author:      Lee Yeung
 * Create Date: 2020-02-19
 * Description:
 */
public interface DeviceIoAction
{
    void write(byte[] data) throws IOException;

    byte[] read() throws IOException;

}
