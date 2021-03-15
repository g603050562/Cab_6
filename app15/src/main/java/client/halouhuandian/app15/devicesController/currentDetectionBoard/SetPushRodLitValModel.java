package client.halouhuandian.app15.devicesController.currentDetectionBoard;

/**
 * Author:      Lee Yeung
 * Create Date: 2020/12/24
 * Description:
 */
public class SetPushRodLitValModel {

    /**
     * type : setPushRodLitVal
     * name : 设置推杆检测电流阻力阀值
     * litVal : 2.5
     * isAuto : 1
     * md5str : dda214dd1b198c70fe1be3c5a45c19a0-1608788600
     */

    private String type;
    private String name;
    private float litVal;
    private int isAuto;
    private String md5str;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getLitVal() {
        return litVal;
    }

    public void setLitVal(float litVal) {
        this.litVal = litVal;
    }

    public int getIsAuto() {
        return isAuto;
    }

    public void setIsAuto(int isAuto) {
        this.isAuto = isAuto;
    }

    public String getMd5str() {
        return md5str;
    }

    public void setMd5str(String md5str) {
        this.md5str = md5str;
    }
}
