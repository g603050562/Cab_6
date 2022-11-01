package client.halouhuandian.app15.service.logic.logicHttpConnection.http.mode;

public class WebSocketUpdateHardWareDataFormat {

    private int door = 0;
    private String dataPath = "";
    private String type = "";
    private String tel = "";
    private String cabID = "";

    public WebSocketUpdateHardWareDataFormat(int door, String dataPath, String type, String tel, String cabID) {
        this.door = door;
        this.dataPath = dataPath;
        this.type = type;
        this.tel = tel;
        this.cabID = cabID;
    }

    public int getDoor() {
        return door;
    }

    public void setDoor(int door) {
        this.door = door;
    }

    public String getDataPath() {
        return dataPath;
    }

    public void setDataPath(String dataPath) {
        this.dataPath = dataPath;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getCabID() {
        return cabID;
    }

    public void setCabID(String cabID) {
        this.cabID = cabID;
    }

}
