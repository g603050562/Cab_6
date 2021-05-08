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
    byte openMicroswitch = -1;

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

    public void setOpenMicroswitch(byte openMicroswitch) {
        this.openMicroswitch = openMicroswitch;
    }

    /**
     * 开门微动是否正常
     *
     * @return
     */
    public boolean isOpenMicroswitchNormal()
    {
        return openMicroswitch != -1;
    }


    /**
     * 开门微动是否被压住
     *
     * @return
     */
    public boolean isOpenMicroswitchPressed() {
        return openMicroswitch == 1;
    }
}
