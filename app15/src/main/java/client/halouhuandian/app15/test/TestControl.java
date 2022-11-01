package client.halouhuandian.app15.test;

import android.app.Activity;

import client.halouhuandian.app15.test.testBattery.TestBattery;
import client.halouhuandian.app15.test.testBattery.TestBatteryEnum;

public class TestControl {

    private static volatile TestControl testControl = null;
    private TestControl(){};
    public static TestControl getInstance(){
        if(testControl == null){
            synchronized (TestControl.class){
                if(testControl == null){
                    testControl = new TestControl();
                }
            }
        };
        return testControl;
    };

    private TestBattery testBattery;

    public void batteryUpdate(Activity activity , TestBatteryEnum testBatteryEnum){
        if(testBattery == null){
            testBattery = new TestBattery();
        }
        if(testBatteryEnum == TestBatteryEnum.dengBoBatteryUpdate){
            testBattery.DengBoBatteryTest(activity);
        }
    }
}
