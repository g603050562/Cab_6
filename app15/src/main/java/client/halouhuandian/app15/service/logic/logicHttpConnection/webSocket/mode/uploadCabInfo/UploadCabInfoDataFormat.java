package client.halouhuandian.app15.service.logic.logicHttpConnection.webSocket.mode.uploadCabInfo;

import android.content.Context;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import client.halouhuandian.app15.config.SystemConfig;
import client.halouhuandian.app15.hardWareConncetion.daa.mode.DaaDataFormat;
import client.halouhuandian.app15.hardWareConncetion.daa.mode.acdc.AcdcInfoByStateFormat;
import client.halouhuandian.app15.hardWareConncetion.daa.mode.acdc.AcdcInfoByWarningFormat;
import client.halouhuandian.app15.hardWareConncetion.daa.mode.dcdc.DcdcInfoByBaseFormat;
import client.halouhuandian.app15.hardWareConncetion.daa.mode.dcdc.DcdcInfoByStateFormat;
import client.halouhuandian.app15.hardWareConncetion.daa.mode.dcdc.DcdcInfoByWarningFormat;
import client.halouhuandian.app15.hardWareConncetion.environmentPlate.EnvironmentDataFormat;
import client.halouhuandian.app15.hardWareConncetion.environmentPlate.fan.FanController;
import client.halouhuandian.app15.model.dao.sharedPreferences.CabInfoSp;
import client.halouhuandian.app15.model.dao.sharedPreferences.ForbiddenSp;
import client.halouhuandian.app15.model.dao.sqlLite.ExchangeInfoDB;
import client.halouhuandian.app15.pub.util.UtilBattery;
import client.halouhuandian.app15.pub.util.UtilPublic;
import client.halouhuandian.app15.service.logic.logicNetDBM.DataDistributionCurrentNetDBM;

/**
 * 电池上传舱门信息
 * 字段由后台提供
 */
public class UploadCabInfoDataFormat {


    private Context context;
    private DaaDataFormat daaDataFormat;
    private EnvironmentDataFormat environmentDataFormat;

    public UploadCabInfoDataFormat(Context context, DaaDataFormat daaDataFormat, EnvironmentDataFormat environmentDataFormat) {
        this.context = context;
        this.daaDataFormat = daaDataFormat;
        this.environmentDataFormat = environmentDataFormat;
    }

    public String getJsonString() throws JSONException {
        //电柜整体数据
        JSONObject jsonObjectReturn = new JSONObject();
        JSONArray jsonArrayItem = new JSONArray();
        //单个舱门数据
        for (int i = 0; i < SystemConfig.getMaxBattery(); i++) {
            JSONObject jsonObjectItem = new JSONObject();
            DcdcInfoByBaseFormat dcdcInfoByBaseFormat = daaDataFormat.getDcdcInfoByBaseFormat(i);
            DcdcInfoByStateFormat dcdcInfoByStateFormat = daaDataFormat.getDcdcInfoByStateFormat(i);
            DcdcInfoByWarningFormat dcdcInfoByWarningFormat = daaDataFormat.getDcdcInfoByWarningFormat(i);
            //仓门ID
            jsonObjectItem.put("door", i + 1 + "");
            //电池ID
            jsonObjectItem.put("battery", dcdcInfoByBaseFormat.getBID());
            //电池电量 - soc冲突重复 - 后期去掉
            int soc = dcdcInfoByBaseFormat.getBatteryRelativeSurplus();
            jsonObjectItem.put("bty_rate", soc > 100 ? soc - 100 : soc);
            //之前的控制板版本号 - 新柜子无用
            jsonObjectItem.put("soh", "100");
            //电池健康度
            jsonObjectItem.put("soh_2", dcdcInfoByBaseFormat.getBatteryHealthy());
            //电池电量 - bty_rate冲突重复
            int realSoc = dcdcInfoByBaseFormat.getBatteryRealRelativeSurplus();
            jsonObjectItem.put("soc", realSoc > 100 ? 1 : realSoc);
            //电池单体最小电压
            jsonObjectItem.put("volt_min", dcdcInfoByBaseFormat.getItemMin());
            //电池单体最大电压
            jsonObjectItem.put("volt_max", dcdcInfoByBaseFormat.getItemMax());
            //电池压差
            jsonObjectItem.put("vdif", dcdcInfoByBaseFormat.getPressureDifferential());
            //电池循环次数
            jsonObjectItem.put("uses", dcdcInfoByBaseFormat.getLoops());
            //电池温度
            jsonObjectItem.put("wendu", dcdcInfoByBaseFormat.getTemperatureSensorByInner());
            //电池电压
            jsonObjectItem.put("dianya", dcdcInfoByBaseFormat.getBatteryVoltage() * 1000);
            //电池电流
            jsonObjectItem.put("dianliu", dcdcInfoByBaseFormat.getBatteryElectric() * 1000);
            //舱门内部微动
            jsonObjectItem.put("inching", dcdcInfoByBaseFormat.getInchingByInner());
            //舱门外部底部微动
            jsonObjectItem.put("side_inching", dcdcInfoByBaseFormat.getInchingByOuterClose());
            //长链接下发的id
            jsonObjectItem.put("cabid", CabInfoSp.getInstance().getCabinetNumber_XXXXX());
            //电池满充容量
            jsonObjectItem.put("full_cap", dcdcInfoByBaseFormat.getBatteryFullCapacity() * 1000);
            //电池剩余容量
            jsonObjectItem.put("left_cap", dcdcInfoByBaseFormat.getBatteryRemainingCapacity() * 1000);
            //电池壳温度
            jsonObjectItem.put("TEM_2", dcdcInfoByBaseFormat.getTemperatureSensorByOuter());
            //电池UID
            jsonObjectItem.put("uid32", dcdcInfoByBaseFormat.getUID());
            //电池禁用状态
            jsonObjectItem.put("outIn", ForbiddenSp.getInstance().getTargetForbidden(i));
            //数据最后上传时间
            jsonObjectItem.put("lastDataDate", dcdcInfoByBaseFormat.getDataTime());
            //上传电池类型
            jsonObjectItem.put("volt", UtilBattery.isType(dcdcInfoByBaseFormat.getBID()));
            //上传电池version
            String barBer = dcdcInfoByBaseFormat.getBatteryVersion();
            String a = barBer.substring(0, 2);
            String b = barBer.substring(2, 4);
            int a_i = Integer.parseInt(a, 16);
            int b_i = Integer.parseInt(b, 16);
            jsonObjectItem.put("bsv", b_i);
            jsonObjectItem.put("bhv", a_i);
            //上传dcdc软件版本
            jsonObjectItem.put("dcbsv", dcdcInfoByStateFormat.getDcdcSoftwareVersion());
            //上传dcdc硬件版本
            jsonObjectItem.put("dcbhv", dcdcInfoByStateFormat.getDcdcHardWareVersion());
            //上传dcdc对电池的采样电压
            jsonObjectItem.put("collvolt", dcdcInfoByBaseFormat.getSamplingVoltage());
            //上传dcdc的内部警告
            jsonObjectItem.put("inwarn", dcdcInfoByWarningFormat.getErrorStateInSide());
            //上传dcdc外部告警
            jsonObjectItem.put("outwarn", dcdcInfoByWarningFormat.getErrorStateOutSide());
            //上传bms告警
            jsonObjectItem.put("bmswarn", dcdcInfoByWarningFormat.getErrorStateBMS());
            //上传dcdc模块电压
            jsonObjectItem.put("modvolt", dcdcInfoByStateFormat.getDcdcVoltage());
            //上传dcdc模块电流
            jsonObjectItem.put("modele", dcdcInfoByStateFormat.getDcdcElectric());
            //上传dcdc状态
            jsonObjectItem.put("dcdcStatus", dcdcInfoByStateFormat.getDcdcState());
            //上传dcdc终止原因
            jsonObjectItem.put("stopReson", dcdcInfoByStateFormat.getDcdcStopInfo());
            //json整合
            jsonArrayItem.put(jsonObjectItem);
        }

        AcdcInfoByStateFormat acdcInfoByStateFormat_1 = daaDataFormat.getAcdcInfoByStateFormat(0);
        AcdcInfoByStateFormat acdcInfoByStateFormat_2 = daaDataFormat.getAcdcInfoByStateFormat(1);
        AcdcInfoByWarningFormat acdcInfoByWarningFormat_1 = daaDataFormat.getAcdcInfoByWarningFormat(0);
        AcdcInfoByWarningFormat acdcInfoByWarningFormat_2 = daaDataFormat.getAcdcInfoByWarningFormat(1);

        //电柜 imsi id
        jsonObjectReturn.put("number", CabInfoSp.getInstance().getCabinetNumber_4600XXXX());
        //电柜4g信号值
        jsonObjectReturn.put("dbm", DataDistributionCurrentNetDBM.getInstance().getDbm());
        //电柜类型
        jsonObjectReturn.put("cabtype", "6-can-48_60v混合");
        //电柜换电主程序软件版本
        jsonObjectReturn.put("version", CabInfoSp.getInstance().getVersion());
        //电柜舱门信息 - 上面的
        jsonObjectReturn.put("doors", jsonArrayItem);
        //判断是什么样的柜子   1(默认) - 网络换电    2 - 离线换电
        jsonObjectReturn.put("isline", "-1");
        //上传android设备名称，以后好区分android板
        jsonObjectReturn.put("androidSoft", CabInfoSp.getInstance().getAndroidDeviceModel());
        //上传本地数据库还有多少换电数据
        jsonObjectReturn.put("localExchanges", ExchangeInfoDB.getInstance(context).getCount());
        //线程保护
        jsonObjectReturn.put("threadProtectionType", CabInfoSp.getInstance().getTPTNumber());
        //是否存在拓展卡
        int isExistExCard = 0;
        if( UtilPublic.getIsExistExCard()){
            isExistExCard = 1;
        }
        jsonObjectReturn.put("isExCard", isExistExCard);
        //acdc1 主从机类型
        jsonObjectReturn.put("acdc1", acdcInfoByStateFormat_1.getAcdcMasterOrSlave());
        //acdc1 软件版本
        jsonObjectReturn.put("ac1bsv", acdcInfoByStateFormat_1.getAcdcSoftWareVersion());
        //acdc1 硬件版本
        jsonObjectReturn.put("ac1bhv", acdcInfoByStateFormat_1.getAcdcHardWareVersion());
        //acdc1 输入电压
        jsonObjectReturn.put("ac1involt", acdcInfoByStateFormat_1.getAcdcInputVoltage());
        //acdc1 输出电压
        jsonObjectReturn.put("ac1outvolt", acdcInfoByStateFormat_1.getAcdcOutPutVoltage());
        //acdc1 输出功率
        jsonObjectReturn.put("ac1maxkw", acdcInfoByStateFormat_1.getAcdcOutputPower());
        //acdc1 输出电流
        jsonObjectReturn.put("ac1outele", acdcInfoByStateFormat_1.getAcdcOutPutElectric());
        //acdc1 剩余功率
        jsonObjectReturn.put("ac1spkw", acdcInfoByStateFormat_1.getAcdcSurplusPower());
        //acdc1 当前休眠状态
        jsonObjectReturn.put("ac1restate", acdcInfoByStateFormat_1.getAcdcIsSleep());
        //acdc1 内部警告
        jsonObjectReturn.put("ac1inwarn", acdcInfoByWarningFormat_1.getErrorStateInSide());
        //acdc2 主从机类型
        jsonObjectReturn.put("acdc2", acdcInfoByStateFormat_2.getAcdcMasterOrSlave());
        //acdc2 软件版本
        jsonObjectReturn.put("ac2bsv", acdcInfoByStateFormat_2.getAcdcSoftWareVersion());
        //acdc2 硬件版本
        jsonObjectReturn.put("ac2bhv", acdcInfoByStateFormat_2.getAcdcHardWareVersion());
        //acdc2 输如电压
        jsonObjectReturn.put("ac2involt", acdcInfoByStateFormat_2.getAcdcInputVoltage());
        //acdc2 输出电压
        jsonObjectReturn.put("ac2outvolt", acdcInfoByStateFormat_2.getAcdcOutPutVoltage());
        //acdc2 输出功率
        jsonObjectReturn.put("ac2maxkw", acdcInfoByStateFormat_2.getAcdcOutputPower());
        //acdc2 输出电流
        jsonObjectReturn.put("ac2outele", acdcInfoByStateFormat_2.getAcdcOutPutElectric());
        //acdc2 剩余总功率
        jsonObjectReturn.put("ac2spkw", acdcInfoByStateFormat_2.getAcdcSurplusPower());
        //acdc2 当前休眠状态
        jsonObjectReturn.put("ac2restate", acdcInfoByStateFormat_2.getAcdcIsSleep());
        //acdc2 内部警告
        jsonObjectReturn.put("ac2inwarn", acdcInfoByWarningFormat_2.getErrorStateInSide());
        //剩余总功率
        jsonObjectReturn.put("maxpp", acdcInfoByStateFormat_1.getAcdcSurplusPower());

        //电柜内部温度 - 顶部
        jsonObjectReturn.put("envtem1", environmentDataFormat.getTemperature_1());
        //电柜内部温度 - 舱体和舱体附近的温度
        jsonObjectReturn.put("envtem2", environmentDataFormat.getTemperature_2());
        //电柜外部温度 - 环境温度
        jsonObjectReturn.put("envtem3", environmentDataFormat.getTemperature_3());
        //电表用电量
        jsonObjectReturn.put("cab_ele", environmentDataFormat.getUsefulTotalElectricEnergy());
        //水位 - 1
        jsonObjectReturn.put("water1", environmentDataFormat.getWater_1());
        //水位 - 2
        jsonObjectReturn.put("water2", environmentDataFormat.getWater_2());
        //环境板软件版本
        jsonObjectReturn.put("envbsv", environmentDataFormat.getVersion());
        //环境板硬件版本
        jsonObjectReturn.put("envbhv", environmentDataFormat.getFunctionState());
        //烟感
        jsonObjectReturn.put("smoke", environmentDataFormat.getSmoke());

        //电流板状态
        jsonObjectReturn.put("curBoardStatus", environmentDataFormat.getRunningTime());
        //电流板输出电压
        jsonObjectReturn.put("curBoardVout", environmentDataFormat.getCurrentPlateVoltage());
        //电流板输出电流
        jsonObjectReturn.put("curBoardAout", environmentDataFormat.getCurrentPlateElectric());
        //电流板故障告警
        jsonObjectReturn.put("curBoardErr", environmentDataFormat.getCurrentPlateElectricWarning());
        //电流板版本
        jsonObjectReturn.put("curBoardVer", "SV:" + environmentDataFormat.getCurrentPlateSoftVersion() + "/HV:" + environmentDataFormat.getCurrentPlateHardVersion());//电流板版本信息
        //电流板阈值
        jsonObjectReturn.put("curBoardLitVal", CabInfoSp.getInstance().getCurrentThreshold() + "A");
        //电流板阈值模式
        jsonObjectReturn.put("curBoardMode：“", CabInfoSp.getInstance().getCurrentPlateMode() == 0 ? "自动" : "手动");
        //推杆持续时间
        jsonObjectReturn.put("pushRodTime", CabInfoSp.getInstance().getPutterActivityTime() / 10);
        //风扇运行个数
        jsonObjectReturn.put("airFanWorkCount", FanController.getInstance().getActivityFanCount());

        return jsonObjectReturn.toString();
    }
}
