package com.halouhuandian.DcUpgrade.canExtension;

import java.io.IOException;

/**
 * Author:      Lee Yeung
 * Create Date: 2019-12-23
 * Description:
 */
public interface DeviceIoAction {
    void write(byte[] data) throws IOException;
}
