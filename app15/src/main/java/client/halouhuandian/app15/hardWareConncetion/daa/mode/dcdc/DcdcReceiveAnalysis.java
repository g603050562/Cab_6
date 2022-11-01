package client.halouhuandian.app15.hardWareConncetion.daa.mode.dcdc;


import client.halouhuandian.app15.hardWareConncetion.daa.mode.dcdc.DcdcInfoByBaseFormat;
import client.halouhuandian.app15.pub.util.UtilPublic;

public class DcdcReceiveAnalysis {

    public String[] dcdcStopReason = new String[]{"无", "充电模块故障终止", "安卓版离线故障终止", "充电模块开启失败故障终止", "充电模块和BMS通讯失败故障终止", "整包电池电压过高告警终止", "整包电池电压过低告警终止", "电池反接保护终止",
            "电池仓NTC掉线加热故障终止", "充电模块ID重复故障终止", "充电继电器黏连告警终止", "充电继电器驱动失效告警终止", "BMS过压终止", "BMS欠压终止", "BMS充电过流终止", "BMS放电过流终止",
            "BMS短路终止", "BMS过高温终止", "BMS过低温终止", "BMS充电握手失败终止", "BMS MOS击穿终止", "BMS电池温度异常终止", "BMS电池反充终止", "BMS保险丝断开终止",
            "电池包故障终止", "安卓版下发关机终止", "无电池终止", "锁微动异常检测终止", "达到加热目标终止", "加热继电器黏连告警终止", "加热继电器驱动失效告警终止", "充电回路异常断开告警终止",
            "电池BMS数据异常告警终止", "侧微动异常检测告警", "达到soc目标终止","推舱门终止","激活失败终止","电池串数错误终止","Dcdc禁用终止","Acdc全部离线终止",
            "拔电池终止","加热电压异常终止","输出继电器异常终止","加热保护板异常终止","模块连续开启失败终止","加热禁用终止","充电低温故障终止","电池升级终止"};

    private String[] dcdcStateInfo = new String[]{"待机","充电中","故障中","启动中","排队中","加热中","警告中","激活中","激活失败","SOC校准中","禁用中" , "升级中"};

    private DcdcInfoByBaseFormat dcdcInfoByBaseFormat;

    public DcdcReceiveAnalysis(){
        if(dcdcInfoByBaseFormat == null){
            dcdcInfoByBaseFormat = new DcdcInfoByBaseFormat();
        }
    }


    //dcdc基础信息解析
    public DcdcInfoByBaseFormat returnBaseByBytes(byte[] bytes){
        int[] data = bytesToInts(bytes);
        /**
         * 第一帧数据
         */
        //帧地址 - [0]
        dcdcInfoByBaseFormat.setAddress(data[0]);
        //微动状态 - [1]
        if (data[1] == 0x00) {
            dcdcInfoByBaseFormat.setInchingByInner(0);
            dcdcInfoByBaseFormat.setInchingByOuterClose(0);
        } else if (data[1] == 0x01) {
            dcdcInfoByBaseFormat.setInchingByInner(0);
            dcdcInfoByBaseFormat.setInchingByOuterClose(1);

        } else if (data[1] == 0x11) {
            dcdcInfoByBaseFormat.setInchingByInner(1);
            dcdcInfoByBaseFormat.setInchingByOuterClose(1);
        } else if (data[1] == 0x10) {
            dcdcInfoByBaseFormat.setInchingByInner(1);
            dcdcInfoByBaseFormat.setInchingByOuterClose(0);
        }
        //推杆状态 - [2]
        dcdcInfoByBaseFormat.setPutter(data[2]);
        //电池壳温度 - [3][4]
        int tem_1 = data[4] * 256 + data[3] - 500;
        if(tem_1 > 10000){
            tem_1 = -600;
        }
        double tem_1_double = (double)tem_1 / 10;
        dcdcInfoByBaseFormat.setTemperatureSensorByOuter(tem_1_double);
        //电池芯温度 - [5][6]
        int tem_2 =  data[6] * 256 +  data[5] - 500 ;
        if(tem_2 > 10000){
            tem_2 = -600;
        }
        double tem_2_double = (double)tem_2 / 10;
        dcdcInfoByBaseFormat.setTemperatureSensorByInner(tem_2_double);
        //电池相对百分比 - [7]
        int soc = data[7];
        if(soc > 97 && soc <= 100){
            soc = 100;
        }else if(soc > 100){
            soc = soc - 100;
        }

        dcdcInfoByBaseFormat.setBatteryRelativeSurplus(soc);
        /**
         * 第二帧数据
         */
        //电池电压 - [8][9]
        double voltage = (double) (data[9] * 256 + data[8]) / 10;
        dcdcInfoByBaseFormat.setBatteryVoltage(voltage);
        //电池电流 - [10][11]
        double electric = (double) ((data[11] * 256 + data[10]) - 4000) / 10;
        dcdcInfoByBaseFormat.setBatteryElectric(electric);
        //电池绝对容量 - [12]
        dcdcInfoByBaseFormat.setBatteryAbsoluteSurplus(data[12]);
        //电池剩余容量 - [13]
        dcdcInfoByBaseFormat.setBatteryRemainingCapacity(data[13]);
        //电池满充容量 - [14]
        dcdcInfoByBaseFormat.setBatteryFullCapacity(data[14]);
        /**
         * 第三帧数据
         */
        //循环次数 - [15]
        dcdcInfoByBaseFormat.setLoops(data[15]);
        //电池健康比 - [16]
        dcdcInfoByBaseFormat.setBatteryHealthy(data[16]);
        //BMS版本号 - [17][18]
        dcdcInfoByBaseFormat.setBatteryVersion(UtilPublic.ByteArrToHex(new byte[]{(byte) data[18], (byte)data[17]}));
        //需求电压 - [20][19]
        double requirePower = (double) (data[20] * 256 + data[19]) / 10;
        dcdcInfoByBaseFormat.setRequirePower(requirePower);
        //预留 - [21]
        /**
         * 第四帧到第九帧数据
         */
        //串数
        int[] item = new int[19];
        //单体电压初始化
        int itemVol = data[23] * 256 + data[22];
        int max = itemVol;
        int maxIndex = 0;
        int min = itemVol;
        int minIndex = 0;
        for (int i = 0; i < 19; i++) {
            int can_item_data_h = data[23 + (i * 2)];
            int can_item_data_l = data[22 + (i * 2)];
            item[i] = (can_item_data_h * 256 + can_item_data_l);
            if (item[i] > max) {
                max = item[i];//如果有比max大的数就让max记录下大的数
                maxIndex = i+1;
            }
            if (item[i] < min && item[i] != 0) {
                min = item[i];//如果有比min小的数就让min记录下小的数
                minIndex = i+1;
            }
        }
        //单体最大电压
        dcdcInfoByBaseFormat.setItemMax(maxIndex+"_"+max);
        //单体最小电压
        dcdcInfoByBaseFormat.setItemMin(minIndex+"_"+min);
        //电池压差
        dcdcInfoByBaseFormat.setPressureDifferential(max - min);
        /**
         * 第十帧到第十二帧数据
         */
        String BID = "";
        for (int i = 0; i < 16; i++) {
            String str_bid = "";
            if (data[64 + i] < 16) {
                str_bid = "0" + Integer.toHexString(data[64 + i]);
            } else {
                str_bid = Integer.toHexString(data[64 + i]);
            }
            char str_re_bid = 0;
            if (!str_bid.equals("00")) {
                int a_bid = Integer.parseInt(str_bid, 16);
                str_re_bid = UtilPublic.backchar(a_bid);
                BID = BID + str_re_bid;
            } else {
                BID = BID + "0";
            }
        }
        //电池BID
        dcdcInfoByBaseFormat.setBID(BID);
        /**
         * 第十三帧到第十五帧
         */
        String manufacturerInfo = "";
        for (int i = 0; i < 21; i++) {
            String temp = "";
            if (data[71 + i] < 16) {
                temp = "0" + Integer.toHexString(data[71 + i]);
            } else {
                temp = Integer.toHexString(data[71 + i]);
            }
            char tempChar = 0;
            if (!temp.equals("00")) {
                int tempInt = Integer.parseInt(temp, 16);
                tempChar = UtilPublic.backchar(tempInt);
                manufacturerInfo = manufacturerInfo + tempChar;
            } else {
                manufacturerInfo = manufacturerInfo + "0";
            }
        }
        /**
         * 第十六帧到第十七帧
         */
        String UID = "";
        int i = 0;
        for (; i < 8; i++) {
            String str_UID = "";
            if (data[106 + i] < 8) {
                str_UID = "0" + Integer.toHexString(data[106 + i]);
            } else {
                str_UID = Integer.toHexString(data[106 + i]);
            }
            char str_re_UID = 0;
            if (!str_UID.equals("00")) {
                int a_UID = Integer.parseInt(str_UID, 16);
                str_re_UID = UtilPublic.backchar(a_UID);
                UID = UID + str_re_UID;
            } else {
                UID = UID + "0";
            }
        }
        //电池UID
        dcdcInfoByBaseFormat.setUID(UID);
        /**
         * 第十八帧数据
         */
        //电池采样电压 - [121][120]
        double samplingVoltage = (double) ((data[121] * 256 + data[120]) - 1000) / 10;
        if(samplingVoltage < 0){
            samplingVoltage = 0;
        }
        dcdcInfoByBaseFormat.setSamplingVoltage(samplingVoltage);
        //电池实际soc - [122]
        dcdcInfoByBaseFormat.setBatteryRealRelativeSurplus(data[122]);
        //电池壳温度 - [3][4]
        int tem_3 = data[124] * 256 + data[123] - 500;
        if(tem_3 > 10000){
            tem_3 = -600;
        }
        double tem_3_double = (double)tem_3 / 10;
        dcdcInfoByBaseFormat.setTemperatureSensorByOuter2(tem_3_double);

        return dcdcInfoByBaseFormat;
    }

    public void addData_19(byte[] bytes){
        int data = bytes[1];
        String str = UtilPublic.int2Binary(data);
        int dataInt = Integer.parseInt(str.substring(str.length() - 1,str.length()));
        dcdcInfoByBaseFormat.setInchingByOuterOpen(dataInt);
    }

    //dcdc状态信息解析
    public DcdcInfoByStateFormat returnStateByBytes(byte[] bytes){
        int[] data = bytesToInts(bytes);
        DcdcInfoByStateFormat dcdcInfoByStateFormat = new DcdcInfoByStateFormat();
        int state = data[0];
        dcdcInfoByStateFormat.setDcdcState(dcdcStateInfo[state]);
        //设置模块输出电压
        double voltage = (double)(data[2] * 256 + data[1]) / 10;
        dcdcInfoByStateFormat.setDcdcVoltage(voltage);
        //设置模块输出电流
        double electric = (double)(data[4] * 256 + data[3] - 4000) / 10;
        dcdcInfoByStateFormat.setDcdcElectric(electric);
        //设置停止原因
        dcdcInfoByStateFormat.setDcdcStopInfo(dcdcStopReason[data[5]]);
        dcdcInfoByStateFormat.setDcdcSoftwareVersion(data[6]+"");
        dcdcInfoByStateFormat.setDcdcHardWareVersion(data[7]+"");
        return dcdcInfoByStateFormat;
    }

    //负数处理
    private int[] bytesToInts(byte[] bytes){
        //负数处理
        int[] data = new int[bytes.length];
        for (int i = 0; i < data.length; i++) {
            data[i] = bytes[i] & 0xff;
        }
        return data;
    }
}
