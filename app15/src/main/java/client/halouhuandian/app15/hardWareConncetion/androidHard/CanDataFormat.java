package client.halouhuandian.app15.hardWareConncetion.androidHard;

import client.halouhuandian.app15.pub.util.UtilPublic;


/**
 * can报文 格式 地址 + 数据
 */

public class CanDataFormat {

    private byte[] rawData;

    public CanDataFormat(byte[] rawData){
        this.rawData = rawData;
    }

    public void setRawData(byte[] rawData) {
        this.rawData = rawData;
    }

    public String getAddressByStr() {
        return Long.toHexString(getAddressByLong());
    }

    public long getAddressByLong() {
        long addressLong = (long)(rawData[3] & 0xff) * 256 * 256 * 256 +
                ((long)(rawData[2] & 0xff) * 256 * 256)  +
                ((long)(rawData[1] & 0xff) * 256) +
                ((long)(rawData[0] & 0xff)) ;
        return addressLong;
    }

    public byte[] getData() {
        return new byte[]{rawData[8], rawData[9], rawData[10], rawData[11], rawData[12], rawData[13], rawData[14], rawData[15]};
    }

    public String getDataByStr() {
        return UtilPublic.ByteArrToHex(getData());
    }

    public int getDoor() {
        return rawData[0];
    }

    public byte[] getRawData() {
        return rawData;
    }
}
