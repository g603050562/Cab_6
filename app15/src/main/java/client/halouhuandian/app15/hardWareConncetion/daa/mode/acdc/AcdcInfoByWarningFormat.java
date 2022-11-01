package client.halouhuandian.app15.hardWareConncetion.daa.mode.acdc;


import client.halouhuandian.app15.pub.util.UtilPublic;

public class AcdcInfoByWarningFormat {

    //Acdc内部告警
    private String errorStateInSide = "";
    //acdc开启状态
    private String acdcIsOpen = "关闭";
    //数据时间
    private long dataTime = 0;
    //数据地址
    private long addressLong = 0;

    public AcdcInfoByWarningFormat(){
        this.dataTime = System.currentTimeMillis();
    }

    public AcdcInfoByWarningFormat(long addressLong , byte[] data) {
        this.addressLong = addressLong;
        this.dataTime = System.currentTimeMillis();
        onDate(data);
    }

    public String getErrorStateInSide() {
        return errorStateInSide;
    }

    public void setErrorStateInSide(String errorStateInSide) {
        this.errorStateInSide = errorStateInSide;
    }

    public String getAcdcIsOpen() {
        return acdcIsOpen;
    }

    public void setAcdcIsOpen(String acdcIsOpen) {
        this.acdcIsOpen = acdcIsOpen;
    }

    public long getDataTime() {
        return dataTime;
    }

    public void setDataTime(long dataTime) {
        this.dataTime = dataTime;
    }

    private void onDate(byte[] data){
        int acdcStateType = data[0];
        int acdcIsOpen_1_16 = data[1];
        if(acdcStateType == 1 && acdcIsOpen_1_16 % 2 == 1){
            acdcIsOpen = "关闭";
        }else{
            acdcIsOpen = "开启";
        }
        errorStateInSide = UtilPublic.ByteArrToHex(new byte[]{data[4],data[5],data[6],data[7]});
    }
}
