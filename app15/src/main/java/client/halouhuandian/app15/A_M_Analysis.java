package client.halouhuandian.app15;

import com.hellohuandian.pubfunction.Unit.PubFunction;
import com.hellohuandian.pubfunction.Unit.Unit;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by apple on 2017/12/2.
 * 解析返回帧
 */

public class A_M_Analysis {

    //解析ACDC
    public Map<String, String> analysisData_BAR(byte[] bytes) {

        int TX[] = new int[bytes.length];
        for (int i = 0; i < bytes.length; i++) {
            TX[i] = 0xff & bytes[i];
        }

        /**
         * 第一帧数据
         */
        //底部微动和测微动
        Map<String, String> map = new HashMap<>();
        int d_locks = -1;
        int s_looks = -1;
        if (TX[0] == 0x00) {
            d_locks = 0;
            s_looks = 0;
        } else if (TX[0] == 0x01) {
            d_locks = 0;
            s_looks = 1;
        } else if (TX[0] == 0x11) {
            d_locks = 1;
            s_looks = 1;
        } else if (TX[0] == 0x10) {
            d_locks = 1;
            s_looks = 0;
        }
        map.put("door", d_locks + "");
        map.put("small", s_looks + "");

        //推杆状态
        map.put("push", TX[1] + "");

        //电池壳温度
        int can_item_data_03 = TX[3];
        int can_item_data_02 = TX[2];
        map.put("tem_1", can_item_data_03 * 256 + can_item_data_02 - 500 + "");
        //电池芯温度
        int can_item_data_05 = TX[5];
        int can_item_data_04 = TX[4];
        map.put("tem_2", can_item_data_05 * 256 + can_item_data_04 - 500 + "");
        //推杆状态

        switch (TX[6] & 0xFF) {
            case 98:
            case 99:
                map.put("xiangduibaifenbi", "100");
                break;
            default:
                map.put("xiangduibaifenbi", TX[6] + "");
                break;
        }


        /**
         * 第二帧数据
         */
        //电压
        int can_item_data_08 = TX[8];
        int can_item_data_07 = TX[7];
        map.put("dianya", (can_item_data_08 * 256 + can_item_data_07) + "");

        //电流
        int can_item_data_10 = TX[10];
        int can_item_data_09 = TX[9];
        map.put("dianliu", ((can_item_data_10 * 256 + can_item_data_09) - 4000 + ""));

        //绝对容量百分比
        map.put("jueduibaifenbi", (TX[11] + ""));

        //剩余容量
        map.put("shengyurongliang", (TX[12] + ""));

        //满充容量
        map.put("manchongrongliang", (TX[13] + ""));


        /**
         * 第三帧数据
         */
        //SOH
        map.put("soh", (TX[15] + ""));
        //BMS版本号
        int[] barVers = new int[]{TX[17], TX[16]};
        map.put("berVer", PubFunction.IntArrToHex(barVers) + "");
        //电压
        int can_item_data_18 = TX[18];
        int can_item_data_19 = TX[19];
        map.put("demandPower", (can_item_data_19 * 256 + can_item_data_18) + "");
        //循环次数
        map.put("loops", (TX[20] * 256 + TX[14] + ""));

        /**
         *第四帧到第九帧数据 TX 21
         */
        int[] item = new int[19];
        int can_item_data_22 = TX[22];
        int can_item_data_21 = TX[21];
        int max = can_item_data_22 * 256 + can_item_data_21;
        int min = can_item_data_22 * 256 + can_item_data_21;
        for (int i = 0; i < 19; i++) {
            int can_item_data_h = TX[22 + (i * 2)];
            int can_item_data_l = TX[21 + (i * 2)];
            item[i] = (can_item_data_h * 256 + can_item_data_l);

            if (item[i] > max) {
                max = item[i];//如果有比max大的数就让max记录下大的数
            }
            if (item[i] < min && item[i] > 10) {
                min = item[i];//如果有比min小的数就让min记录下小的数
            }

        }
        //单体最大电压
        map.put("item_max", max + "");
        //单体最小电压
        map.put("item_min", min + "");

        /**
         * 第十帧到第十二帧数据   TX 63
         */
        //电池id
        String BID = "";
        for (int i = 0; i < 16; i++) {
            String str_bid = "";
            if (TX[63 + i] < 16) {
                str_bid = "0" + Integer.toHexString(TX[63 + i]);
            } else {
                str_bid = Integer.toHexString(TX[63 + i]);
            }
            char str_re_bid = 0;
            if (!str_bid.equals("00")) {
                int a_bid = Integer.parseInt(str_bid, 16);
                str_re_bid = Unit.backchar(a_bid);
                BID = BID + str_re_bid;
            } else {
                BID = BID + "0";
            }

        }
        map.put("BID", BID);

        /**
         * 第十六帧到第十七帧 TX 105
         */
        String UID = "";
        int i = 0;
        for (; i < 8; i++) {
            String str_UID = "";
            if (TX[105 + i] < 8) {
                str_UID = "0" + Integer.toHexString(TX[105 + i]);
            } else {
                str_UID = Integer.toHexString(TX[105 + i]);
            }
            char str_re_UID = 0;
            if (!str_UID.equals("00")) {
                int a_UID = Integer.parseInt(str_UID, 16);
                str_re_UID = Unit.backchar(a_UID);
                UID = UID + str_re_UID;
            } else {
                UID = UID + "0";
            }

        }
        map.put("UID", UID);

        //解析采样电压
        i += 6;
        short v = (short) ((TX[105 + i]) | (TX[105 + i + 1] << 8));
        float samplingV = (float) (v - 1000) / 10;
        map.put("samplingV", samplingV + "V");
//        LogUtil.I("采样电压：" + samplingV);

        i += 2;
        int realSoc = TX[105 + i];
        map.put("realSoc", realSoc + "");
//        LogUtil.I("实际soc："+realSoc);

//        System.out.println("CAN - 返回 - BAR ： " + PubFunction.ByteArrToHex(bytes));
        return map;

    }

    public Map<String, String> analysisData_DCDC(byte[] bytes) {
        int TX[] = new int[bytes.length];
        for (int i = 0; i < bytes.length; i++) {
            TX[i] = 0xff & bytes[i];
        }
        Map<String, String> map = new HashMap<>();
        //DCDC状态
        map.put("DCDC_state", TX[0] + "");
        //电压
        int can_item_data_01 = TX[1];
        int can_item_data_02 = TX[2];
        map.put("DCDC_dianya", (can_item_data_02 * 256 + can_item_data_01) + "");
        //电流
        int can_item_data_03 = TX[3];
        int can_item_data_04 = TX[4];
        map.put("DCDC_dianliu", ((can_item_data_04 * 256 + can_item_data_03 - 4000) + ""));
        //stop
        map.put("DCDC_stop", TX[5] + "");
        //软件版本号
        map.put("DCDC_SV", TX[6] + "");
        //硬件版本号
        map.put("DCDC_HV", TX[7] + "");
//        System.out.println("CAN - 返回 - DCDC ： " + PubFunction.ByteArrToHex(bytes));
        return map;
    }


    public Map<String, String> analysisData_ACDC(byte[] bytes) {
        int TX[] = new int[bytes.length];
        for (int i = 0; i < bytes.length; i++) {
            TX[i] = 0xff & bytes[i];
        }
        Map<String, String> map = new HashMap<>();
        //功率
        int can_item_data_00 = TX[0];
        int can_item_data_01 = TX[1];
        map.put("ACDC_gonglv", ((can_item_data_01 * 256 + can_item_data_00)* 10) + "");
        //电压
        int can_item_data_02 = TX[2];
        int can_item_data_03 = TX[3];
        map.put("ACDC_in_dianya", (can_item_data_03 * 256 + can_item_data_02) + "");
        //电压
        int can_item_data_04 = TX[4];
        int can_item_data_05 = TX[5];
        map.put("ACDC_out_dianya", (can_item_data_05 * 256 + can_item_data_04) + "");
        //电流
        int can_item_data_06 = TX[6];
        int can_item_data_07 = TX[7];
        map.put("ACDC_in_dianliu", ((can_item_data_07 * 256 + can_item_data_06 - 4000) + ""));

//        System.out.println("CAN - 返回 - ACDC ： " + PubFunction.ByteArrToHex(bytes));
        return map;
    }
}
