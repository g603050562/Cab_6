package client.halouhuandian.app15.sp;

import android.app.Activity;
import android.content.SharedPreferences;

public class CabInfoSp {

    private Activity activity;
    private SharedPreferences sharedPreferences;

    public CabInfoSp(Activity activity) {
        this.activity = activity;
        sharedPreferences = activity.getSharedPreferences("CabInfo",  Activity.MODE_WORLD_READABLE);
    }

    /**
     * 是否为第一次开机
     *
     * @return
     */
    public boolean getFristOpen() {
        Boolean is_frist = sharedPreferences.getBoolean("is_frist", true);
        return is_frist;
    }

    /**
     * 设置为第一次开机
     */
    public void setFristOpen() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("is_frist", false);
        editor.commit();
    }


    /**
     * 获取存在sp里面的电柜ID
     *
     * @return
     */
    public String getCabinetNumber() {
        String cabNumber = sharedPreferences.getString("cabinetNumber", "00000");
        return cabNumber;
    }

    /**
     * 设置存在sp里面的电柜ID
     */
    public void setCabinetNumber(String cabinetNumber) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("cabinetNumber", cabinetNumber);
        editor.commit();
    }


    /**
     * 获取存在sp里面的400电话
     *
     * @return
     */
    public String getTelNumber() {
        String cabNumber = sharedPreferences.getString("tel", "400-6060-137");
        return cabNumber;
    }

    /**
     * 设置存在sp里面的400电话
     */
    public void setTelNumber(String telNumber) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("tel", telNumber);
        editor.commit();
    }


    /**
     * 获取存在sp里面的长连接绑定ID
     *
     * @return
     */
    public String getLongLinkNumber() {
        String cabNumber = sharedPreferences.getString("longLinkCabId", "00000");
        return cabNumber;
    }

    /**
     * 设置存在sp里面的长连接绑定ID
     */
    public void setLongLinkNumber(String telNumber) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("longLinkCabId", telNumber);
        editor.commit();
    }


    /**
     * 获取存在sp里面的线程保护参数
     * TPT：thread_protection_type 线程保护参数
     *
     * @return
     */
    public String getTPTNumber() {
        String cabNumber = sharedPreferences.getString("thread_protection_type", "1");
        return cabNumber;
    }

    /**
     * 设置存在sp里面的线程保护参数
     */
    public void setTPTNumber(String telNumber) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("thread_protection_type", telNumber);
        editor.commit();
    }

    public void setAddress(String address) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("address", address);
        editor.commit();

    }

    public void setpushrodActSetTime(byte pushrodActSetTime) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("pushrodActSetTime", pushrodActSetTime);
        editor.commit();

    }

    public byte getpushrodActSetTime() {
        return (byte) sharedPreferences.getInt("pushrodActSetTime", 0x32);
    }


    public String getAddress() {
        String address = sharedPreferences.getString("address", "----");
        return address;
    }



    /**
     * 设置sp里面 电柜位置
     */
    public void setAddress_1(String chargeMode){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("setAddress", chargeMode);
        editor.commit();
    }
    /**
     * 获取sp里面 电柜位置
     * @return
     */
    public String getAddress_1(){
        String longLinkCabNumber = sharedPreferences.getString("setAddress","0");
        return longLinkCabNumber;
    }

    /**
     * 设置sp里面 电柜版本
     */
    public void setVersion(String chargeMode){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("setVersion", chargeMode);
        editor.commit();
    }
    /**
     * 获取sp里面 电柜版本
     * @return
     */
    public String getVersion(){
        String longLinkCabNumber = sharedPreferences.getString("setVersion","0");
        return longLinkCabNumber;
    }






    private static final String CURRENT_THRESHOLD = "currentThreshold";

    /**
     * 保存电流检测板阈值
     *
     * @param currentThreshold
     */
    public void setCurrentThreshold(float currentThreshold) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putFloat("CURRENT_THRESHOLD", currentThreshold);
        editor.commit();
    }

    /**
     * 获取电流检测板阈值
     *
     * @return
     */
    public float optCurrentThreshold() {
//        return sharedPreferences.getFloat("CURRENT_THRESHOLD", 10f);
        return sharedPreferences.getFloat("CURRENT_THRESHOLD", 3f);
    }

    public void setAutoSetCurrentDetectionStatus(boolean isAutoSetCurrentDetectionStatus) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("AutoSetCurrentDetectionStatus", isAutoSetCurrentDetectionStatus);
        editor.commit();
    }

    public boolean optAutoSetCurrentDetectionStatus() {
        return sharedPreferences.getBoolean("AutoSetCurrentDetectionStatus", true);
    }

    public void saveHeatMode(String isHeat) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("isHeat", isHeat);
        editor.commit();
    }

    public String optHeatMode() {
        return sharedPreferences.getString("isHeat", "2");
    }
}
