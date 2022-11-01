package client.halouhuandian.app15.model.dao.sharedPreferences;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * 用于保存电柜的一些小型基本参数
 */

public class CabInfoSp {

    private static volatile CabInfoSp cabInfoSp;
    private CabInfoSp(){};
    public static CabInfoSp getInstance(){
        if(cabInfoSp == null){
            synchronized (CabInfoSp.class){
                if(cabInfoSp == null){
                    cabInfoSp = new CabInfoSp();
                }
            }
        }
        return cabInfoSp;
    }

    private Context context;
    private SharedPreferences sharedPreferences;

    public void init(Context context){
        this.context = context;
        sharedPreferences = context.getSharedPreferences("CabInfo", Activity.MODE_WORLD_READABLE);
    }

    /**
     * 换电程序版本
     * @return
     */
    public String getVersion(){
        String version = sharedPreferences.getString("version", "");
        return version;
    }
    public void setVersion(String version){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("version", version);
        editor.commit();
    }

    /**
     * android板硬件型号
     * @return
     */
    public String getAndroidDeviceModel(){
        String androidDeviceModel = sharedPreferences.getString("androidDeviceModel", "");
        return androidDeviceModel;
    }
    public void setAndroidDeviceModel(String androidDeviceModel){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("androidDeviceModel", androidDeviceModel);
        editor.commit();
    }

    /**
     * android版本
     * @return
     */
    public String getAndroidVersionRelease(){
        String androidVersionRelease = sharedPreferences.getString("androidVersionRelease", "");
        return androidVersionRelease;
    }
    public void setAndroidVersionRelease(String setAndroidVersionRelease){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("androidVersionRelease", setAndroidVersionRelease);
        editor.commit();
    }

    /**
     * 温湿传感器 - 温度
     * @return
     */
    public String getTemMeter(){
        String temMeter = sharedPreferences.getString("temMeter", "");
        return temMeter;
    }
    public void setTemMeter(String temMeter){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("temMeter", temMeter);
        editor.commit();
    }

    /**
     * 温湿传感器 - 湿度
     * @return
     */
    public String getTheMeter(){
        String theMeter = sharedPreferences.getString("theMeter", "");
        return theMeter;
    }
    public void setTheMeter(String theMeter){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("theMeter", theMeter);
        editor.commit();
    }

    /**
     * 电表走字
     * @return
     */
    public String getEleMeter(){
        String eleMeter = sharedPreferences.getString("eleMeter", "");
        return eleMeter;
    }
    public void setEleMeter(String eleMeter){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("eleMeter", eleMeter);
        editor.commit();
    }


    /**
     * 获取存在sp里面的电柜ID - 电柜左上角5位ID 如：04531
     * @return
     */
    public String getCabinetNumber_XXXXX(){
        String cabNumber = sharedPreferences.getString("cabinetNumber_XXXXX","00000");
        return cabNumber;
    }
    public void setCabinetNumber_XXXXX(String cabinetNumber){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("cabinetNumber_XXXXX", cabinetNumber);
        editor.commit();
    }

    /**
     * 获取存在sp里面的clientId 长链接ID
     * @return
     */
    public String getCabinetClientId(){
        String cabNumber = sharedPreferences.getString("clientId","");
        return cabNumber;
    }
    public void setCabinetClientId(String cabinetNumber){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("clientId", cabinetNumber);
        editor.commit();
    }

    /**
     * 获取存在sp里面的电柜ID - 4g卡4600开头号码
     * @return
     */
    public String getCabinetNumber_4600XXXX(){
        String cabNumber = sharedPreferences.getString("getCabinetNumber_4600XXXX","");
        return cabNumber;
    }
    public void setCabinetNumber_4600XXXX(String cabinetNumber){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("getCabinetNumber_4600XXXX", cabinetNumber);
        editor.commit();
    }

    /**
     * 获取存在sp里面的400电话
     * @return
     */
    public String getTelNumber(){
        String cabNumber = sharedPreferences.getString("tel","");
        return cabNumber;
    }
    public void setTelNumber(String telNumber){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("tel", telNumber);
        editor.commit();
    }

    /**
     * 获取存在sp里面的线程保护参数
     * TPT：thread_protection_type 线程保护参数
     * @return
     */
    public String getTPTNumber(){
        String cabNumber = sharedPreferences.getString("thread_protection_type","1");
        return cabNumber;
    }
    public void setTPTNumber(String telNumber){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("thread_protection_type", telNumber);
        editor.commit();
    }

    /**
     * 获取存在sp里面的dbm
     * @return
     */
    public String getDBM(){
        String dbm = sharedPreferences.getString("dbm","0");
        return dbm;
    }
    public void setDBM(String telNumber){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("dbm", telNumber);
        editor.commit();
    }
    /**
     * 设置sp里面 电柜位置
     */
    public void setAddress(String chargeMode){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("setAddress", chargeMode);
        editor.commit();
    }
    public String getAddress(){
        String longLinkCabNumber = sharedPreferences.getString("setAddress","0");
        return longLinkCabNumber;
    }

    /**
     * 获取存在sp里面的最后的电柜数据
     * @return
     */
    public String getLastCabInfo(){
        String lastCabInfo = sharedPreferences.getString("lastCabInfo","");
        return lastCabInfo;
    }
    public void setLastCabInfo(String lastCabInfo){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("lastCabInfo", lastCabInfo);
        editor.commit();
    }


    /**
     * 获取存在sp里面的线程保护参数
     * TPT：thread_protection_type 线程保护参数
     * @return
     */
    public String getMaxPower(){
        String maxPower = sharedPreferences.getString("maxPower","60000");
        return maxPower;
    }
    public void setMaxPower(String maxPower){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("maxPower", maxPower);
        editor.commit();
    }


    /**
     * 设置sp里面 电柜的充电模式
     */
    public void setChargeMode(String chargeMode){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("setChargeMode", chargeMode);
        editor.commit();
    }
    public String getChargeMode(){
        String longLinkCabNumber = sharedPreferences.getString("setChargeMode","0");
        return longLinkCabNumber;
    }













    /**
     * 设置风扇运行状态
     *
     * mode - 1 - 自动     mode - -1 - 只开1    -2 - 只开2   -3 - 全开
     *
     */
    public void setFanActivityMode(int mode){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("fanActivityMode", mode);
        editor.commit();
    }
    public int getFanActivityMode() {
        return sharedPreferences.getInt("fanActivityMode", 1);
    }

    /**
     * 设置风扇1的自动开关阈值
     * @param tem
     */
    public void setFanThreshold_1(int tem){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("fanThreshold_1", tem);
        editor.commit();
    }
    public int getFanThreshold_1() {
        return sharedPreferences.getInt("fanThreshold_1", 30);
    }
    /**
     * 设置风扇2的自动开关阈值
     * @param tem
     */
    public void setFanThreshold_2(int tem){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("fanThreshold_2", tem);
        editor.commit();
    }
    public int getFanThreshold_2() {
        return sharedPreferences.getInt("fanThreshold_2", 40);
    }


    /**
     * 设置电流板控制模式
     * @param mode 0 - 手动    1 - 自动
     */
    public void setCurrentPlateMode(int mode){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("currentPlateMode", mode);
        editor.commit();
    }
    public int getCurrentPlateMode(){
        return sharedPreferences.getInt("currentPlateMode", 0);
    }
    /**
     * 设置电流检测板阈值
     * @param currentThreshold
     */
    public void setCurrentThreshold(float currentThreshold) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putFloat("currentThreshold", currentThreshold);
        editor.commit();
    }
    public float getCurrentThreshold() {
        return sharedPreferences.getFloat("currentThreshold", 4.0f);
    }
    /**
     * 设置电流检测板超限时间
     * @param currentThreshold
     */
    public void setCurrentOutTime(float currentThreshold) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putFloat("currentOutTime", currentThreshold);
        editor.commit();
    }
    public int getCurrentOutTime() {
        return sharedPreferences.getInt("currentOutTime", 800);
    }
    /**
     * 设置电流检测板恢复时间
     * @param currentThreshold
     */
    public void setCurrentRecoveryTime(float currentThreshold) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putFloat("currentRecoveryTime", currentThreshold);
        editor.commit();
    }
    public int getCurrentRecoveryTime() {
        return sharedPreferences.getInt("currentRecoveryTime", 1000);
    }



    /**
     * 推杆持续时间
     * @param putterActivityTime
     */
    public void setPutterActivityTime(int putterActivityTime) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("putterActivityTime", putterActivityTime);
        editor.commit();
    }
    public int getPutterActivityTime() {
        return sharedPreferences.getInt("putterActivityTime", 0x20);
    }


    /**
     * 设置加热模式
     * @param isHeat
     */
    public void setHeatMode(String isHeat) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("isHeat", isHeat);
        editor.commit();
    }

    /**
     * 获取加热模式
     * @return
     */
    public String getHeatMode() {
        return sharedPreferences.getString("isHeat", "2");
    }


    /**
     * 设置sp里面 服务器选择
     */

    public void setServer(String type){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("server", type);
        editor.commit();
    }
    public String getServer(){
        String server = sharedPreferences.getString("server","");
        return server;
    }
}
