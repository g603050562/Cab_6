package client.halouhuandian.app15.service.logic.logicUpdate;

public class UpdateInfoFormat {

    //升级类型
    public enum UpdateType {
        updateBattery,
        updateAcdc,
        updateDcdc,
        updateEnvironment,
    }

    //升级类型
    private UpdateType updateType;
    //升级舱门 如果是全部升级 door就是0
    private int door = -1;
    //升级文件本地路径
    private String filePath = "";
    //升级类型(像是电池区分电池版本 - 如果不需要的话可以为空)
    private String type;

    public UpdateInfoFormat(UpdateType updateType, int door, String filePath , String type) {
        this.updateType = updateType;
        this.door = door;
        this.filePath = filePath;
        this.type = type;
    }

    public UpdateType getUpdateType() {
        return updateType;
    }

    public int getDoor() {
        return door;
    }

    public String getFilePath() {
        return filePath;
    }

    public String getType() {
        return type;
    }
}
