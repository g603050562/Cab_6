package client.halouhuandian.app15.devicesController.rod;

/**
 * Author:      Lee Yeung
 * Create Date: 2020/11/26
 * Description:
 */
public class BatteryDataModel {
    public final int doorNumber;
    private byte sideMicroswitch = -1;
    private long rodActionTime;

    public BatteryDataModel(int doorNumber) {
        this.doorNumber = doorNumber;
    }

    public void setSideMicroswitch(byte sideMicroswitch) {
        this.sideMicroswitch = sideMicroswitch;
    }

    public boolean isSideMicroswitchPressed() {
        return sideMicroswitch == 1;
    }

    public long getRodActionTime() {
        return rodActionTime;
    }

    public void setRodActionTime(long rodActionTime) {
        this.rodActionTime = rodActionTime;
    }
}
