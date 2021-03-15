package com.hellohuandian.apps.UpgradeLibrary.messages;

/**
 * Author:      Lee Yeung
 * Create Date: 2019-09-07
 * Description: 升级版本信息
 */
public abstract class UpgradeMessage
{
    private byte address;

    public UpgradeMessage(byte address)
    {
        this.address = address;
    }

    public byte getAddress()
    {
        return address;
    }
}
