package com.apps.ExtensionTaskUpgrade.categories.battery;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Author:      Lee Yeung
 * Create Date: 2020-02-27
 * Description:
 */
public class UpgradeLock
{
    public volatile boolean isContinue;
    public int dataLen;
    public int dataSize;
    public byte[] flagBytes;
    public byte cmdFlag;
    public final ConcurrentHashMap<Byte, Boolean> cmdFlagMap = new ConcurrentHashMap<>();
}
