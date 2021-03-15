package com.apps.ExtensionTaskUpgrade.core.dispatchers;

import com.apps.ExtensionTaskUpgrade.core.ioAction.CanDeviceIoAction;
import com.apps.ExtensionTaskUpgrade.core.ioAction.DeviceIoAction;

/**
 * Author:      Lee Yeung
 * Create Date: 2020-02-18
 * Description:
 */
public abstract class DispatchExecuteProgram
{
    final void execute(DeviceIoAction deviceIoAction)
    {
        if (deviceIoAction != null)
        {
            if (deviceIoAction instanceof CanDeviceIoAction)
            {
                execute_can((CanDeviceIoAction) deviceIoAction);
            } else
            {
                execute_sp(deviceIoAction);
            }
        }
    }

    protected void execute_sp(DeviceIoAction deviceIoAction)
    {

    }

    protected void execute_can(CanDeviceIoAction canDeviceIoAction)
    {

    }
}
