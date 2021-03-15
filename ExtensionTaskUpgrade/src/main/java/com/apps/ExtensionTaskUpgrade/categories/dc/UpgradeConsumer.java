package com.apps.ExtensionTaskUpgrade.categories.dc;


import android.support.v4.util.Consumer;

/**
 * Author:      Lee Yeung
 * Create Date: 2019-12-20
 * Description:
 */
public abstract class UpgradeConsumer implements Consumer<byte[]>
{
    private final String upgradeFilePath;

    public UpgradeConsumer(String upgradeFilePath)
    {
        this.upgradeFilePath = upgradeFilePath;
    }

    public String getUpgradeFilePath()
    {
        return upgradeFilePath;
    }
}
