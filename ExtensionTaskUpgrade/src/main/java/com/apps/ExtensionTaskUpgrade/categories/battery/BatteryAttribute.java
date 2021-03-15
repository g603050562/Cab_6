package com.apps.ExtensionTaskUpgrade.categories.battery;

/**
 * Author:      Lee Yeung
 * Create Date: 2020-02-21
 * Description: 电池升级用的属性类
 */
public final class BatteryAttribute
{
    public final byte address;
    public final String idCode;
    public final String filePath;
    public final String crcValue;

    public BatteryAttribute(byte address, String idCode, String crcValue, String filePath)
    {
        this.address = address;
        this.idCode = idCode;
        this.crcValue = crcValue;
        this.filePath = filePath;
    }
}
