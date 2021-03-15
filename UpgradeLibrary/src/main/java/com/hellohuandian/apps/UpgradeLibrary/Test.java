package com.hellohuandian.apps.UpgradeLibrary;

import com.hellohuandian.apps.UpgradeLibrary.core.SerialPortRwAction;
import com.hellohuandian.apps.UpgradeLibrary.core.BatteryUpgradeDispatcher;
import com.hellohuandian.apps.UpgradeLibrary.messages.battery.BatteryUpgradeMessage;
import com.hellohuandian.apps.UpgradeLibrary.messages.battery.UpgradeCallBack;

/**
 * Author:      Lee Yeung
 * Create Date: 2019-09-20
 * Description:
 */
public class Test
{
    public static void main(String[] args)
    {
        BatteryUpgradeDispatcher.getInstance().init(new SerialPortRwAction()
        {
            @Override
            public void write(byte[] bytes)
            {

            }

            @Override
            public byte[] read()
            {
                return new byte[0];
            }
        });

        BatteryUpgradeMessage batteryUpgradeMessage = new BatteryUpgradeMessage((byte) 0x05);
        batteryUpgradeMessage.setFilePath("path");
        batteryUpgradeMessage.setUpgradeCallBack(new UpgradeCallBack()
        {
            @Override
            public void onUpgradeBefore(byte address)
            {

            }

            @Override
            public void onUpgrade(byte address, long process, long total)
            {

            }

            @Override
            public void onError(byte address, String errorInfo)
            {

            }

            @Override
            public void onUpgradeAfter(byte address)
            {

            }
        });
        BatteryUpgradeDispatcher.getInstance().dispatch(batteryUpgradeMessage);
    }

    class Sp implements SerialPortRwAction
    {

        @Override
        public void write(byte[] bytes)
        {

        }

        @Override
        public byte[] read()
        {
            return new byte[0];
        }
    }
}
