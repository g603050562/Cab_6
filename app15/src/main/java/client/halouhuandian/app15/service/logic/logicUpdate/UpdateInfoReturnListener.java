package client.halouhuandian.app15.service.logic.logicUpdate;

public interface UpdateInfoReturnListener {
    public enum UpdateTypeInfo{
        success,
        error,
        steps,
    }
    void returnInfo(int door ,UpdateTypeInfo type , String info);
    void returnRate(long current  , long total);
}
