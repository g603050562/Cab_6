package com.hellohuandian.apps.UpgradeLibrary.core;

/**
 * Author:      Lee Yeung
 * Create Date: 2019-09-08
 * Description:
 */
public interface SerialPortRwAction
{
    void write(byte[] bytes);

    byte[] read();
}
