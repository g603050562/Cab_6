package client.halouhuandian.app15.upgrade;

import client.halouhuandian.app15.MyApplication;

/**
 * Author:      Lee Yeung
 * Create Date: 2020-02-28
 * Description:
 */
public abstract class CanSerialDataReceiver implements MyApplication.IFResultAppLinstener
{
    public CanSerialDataReceiver()
    {
        init();
    }

    public abstract void init();

    @Override
    public void onCanResultApp(byte[] canData)
    {

    }

    @Override
    public void onSerialResultApp(byte[] serData)
    {

    }
}
