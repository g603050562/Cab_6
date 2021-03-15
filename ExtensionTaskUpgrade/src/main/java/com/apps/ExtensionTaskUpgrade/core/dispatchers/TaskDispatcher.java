package com.apps.ExtensionTaskUpgrade.core.dispatchers;

import com.apps.ExtensionTaskUpgrade.core.canExtension.CanDeviceIoActionImpl;
import com.apps.ExtensionTaskUpgrade.core.ioAction.DeviceIoAction;
import com.apps.ExtensionTaskUpgrade.core.ioAction.DeviceIoType;

import java.util.HashMap;

/**
 * Author:      Lee Yeung
 * Create Date: 2020-02-19
 * Description:
 */
public final class TaskDispatcher
{
    private static TaskDispatcher taskDispatcher;
    private final HashMap<Integer, DeviceIoAction> deviceIoActionHashMap = new HashMap<>();

    public static TaskDispatcher getInstance()
    {
        if (taskDispatcher == null)
        {
            synchronized (TaskDispatcher.class)
            {
                if (taskDispatcher == null)
                {
                    taskDispatcher = new TaskDispatcher();
                }
            }
        }
        return taskDispatcher;
    }

    public void addDeviceIoAction(@DeviceIoType int deviceType, DeviceIoAction deviceIoAction)
    {
        if (deviceIoAction != null)
        {
            switch (deviceType)
            {
                case DeviceIoType.CANBUS:
                    deviceIoActionHashMap.put(deviceType, new CanDeviceIoActionImpl(deviceIoAction));
                    break;
                case DeviceIoType.SERIAL_PORT:
                default:
                    deviceIoActionHashMap.put(deviceType, deviceIoAction);
                    break;
            }
        }
    }

    public void dispatch(DispatchExecuteProgram dispatchExecuteProgram)
    {
        dispatch(DeviceIoType.SERIAL_PORT, dispatchExecuteProgram);
    }

    public void dispatch(@DeviceIoType int deviceType, DispatchExecuteProgram dispatchExecuteProgram)
    {
        if (dispatchExecuteProgram != null)
        {
            dispatchExecuteProgram.execute(deviceIoActionHashMap.get(deviceType));
        }
    }

    public void notifyRead(@DeviceIoType int deviceType)
    {
        DeviceIoAction deviceIoAction = deviceIoActionHashMap.get(deviceType);
        if (deviceIoAction != null)
        {
            switch (deviceType)
            {
                case DeviceIoType.CANBUS:
                    ((CanDeviceIoActionImpl) deviceIoAction).parseDispatch();
                    break;
            }
        }
    }
}
