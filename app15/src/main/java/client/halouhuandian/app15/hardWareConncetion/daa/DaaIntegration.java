package client.halouhuandian.app15.hardWareConncetion.daa;


import java.util.ArrayList;
import java.util.Arrays;

import client.halouhuandian.app15.hardWareConncetion.androidHard.BaseDataRegister;
import client.halouhuandian.app15.hardWareConncetion.androidHard.BaseDataReturnListener;
import client.halouhuandian.app15.hardWareConncetion.androidHard.CanDataFormat;
import client.halouhuandian.app15.hardWareConncetion.androidHard.SerialAndCanPortUtilsGeRui;
import client.halouhuandian.app15.hardWareConncetion.daa.mode.DaaDataFormat;
import client.halouhuandian.app15.hardWareConncetion.daa.mode.acdc.AcdcInfoByWarningFormat;
import client.halouhuandian.app15.hardWareConncetion.daa.mode.dcdc.DcdcInfoByStateFormat;
import client.halouhuandian.app15.hardWareConncetion.daa.mode.dcdc.DcdcReceiveAnalysis;
import client.halouhuandian.app15.pub.util.UtilPublic;

/**
 * 解析dcdc和acdc数据
 */
public class DaaIntegration  {

    public interface DaaIntegrationListener{
        void returnData(DaaDataFormat daaDataFormat , ReturnDataType returnDataType , int index); // type - 因为什么类型返回的信息     index - 因为类型里面的第几个下标
    }

    //返回数据类型枚举
    public enum ReturnDataType{
        dcdcInfoByBase,dcdcInfoByState,dcdcInfoByWarning,acdcInfoByState,acdcInfoByWarning
    }

    //返回接口
    private DaaIntegrationListener daaIntegrationListener;
    //DCDC基础信息地址范围
    private long DCDC_BASE_ADDEESS_MAX = 2550294015l; // 980265ff
    private long DCDC_BASE_ADDEESS_MIN = 2550293760l; // 98026500
    //DCDC状态信息地址范围
    private long DCDC_STATE_ADDRESS_MAX = 2550425087l; // 980465ff
    private long DCDC_STATE_ADDRESS_MIN = 2550424832l; // 98046500
    //DCDC和ACDC预警信息地址范围
    private long DCDC_AND_ACDC_ERROR_ADDRESS_MAX = 2550556159l; // 980665ff
    private long DCDC_AND_ACDC_ERROR_ADDRESS_MIN = 2550555904l; // 98066500
    //ACDC状态信息地址范围 - 1
    private long ACDC_STATE_1_ADDRESS_MAX = 2551276883l; // 98116553
    private long ACDC_STATE_1_ADDRESS_MIN = 2551276881l; // 98116551
    //ACDC状态信息地址范围 - 2
    private long ACDC_STATE_2_ADDRESS_MAX = 2551342419l; // 98126553
    private long ACDC_STATE_2_ADDRESS_MIN = 2551342417l; // 98126551
    //DCDC最大
    private int DCDC_MAX = 12;

    //DCDC基础信息数据阵列 处理缓存以及拼装数据
    private byte[][][] dcdcBaseArrays = new byte[DCDC_MAX][18][8];
    //dcdc和acdc数据整体缓存
    private DaaDataFormat daaDataFormat = null;
    //dcdc数据解析类
    private DcdcReceiveAnalysis[] dcdcReceiveAnalysis;

    //数据注册
    private BaseDataRegister baseDataRegister;

    public DaaIntegration(DaaIntegrationListener daaIntegrationListener){
        this.daaIntegrationListener = daaIntegrationListener;

        ArrayList<long[]> rangeList = new ArrayList<>();
        rangeList.add(new long[]{DCDC_BASE_ADDEESS_MIN ,DCDC_BASE_ADDEESS_MAX });
        rangeList.add(new long[]{DCDC_STATE_ADDRESS_MIN ,DCDC_STATE_ADDRESS_MAX });
        rangeList.add(new long[]{DCDC_AND_ACDC_ERROR_ADDRESS_MIN ,DCDC_AND_ACDC_ERROR_ADDRESS_MAX });
        rangeList.add(new long[]{ACDC_STATE_1_ADDRESS_MIN ,ACDC_STATE_1_ADDRESS_MAX });
        rangeList.add(new long[]{ACDC_STATE_2_ADDRESS_MIN ,ACDC_STATE_2_ADDRESS_MAX });
        baseDataRegister = new BaseDataRegister(rangeList, new BaseDataReturnListener() {
            @Override
            public void returnData(CanDataFormat canDataFormat) {
                onCanResultApp(canDataFormat);
            }
        });
        SerialAndCanPortUtilsGeRui.getInstance().addListener(baseDataRegister);

        daaDataFormat = new DaaDataFormat();
        dcdcReceiveAnalysis = new DcdcReceiveAnalysis[DCDC_MAX];
        for(int i = 0 ; i < DCDC_MAX ; i ++){
            dcdcReceiveAnalysis[i] = new DcdcReceiveAnalysis();
        }
        Arrays.fill(dcdcBaseArrays, initArrays(18));
    }

    public void onDestroy(){
        SerialAndCanPortUtilsGeRui.getInstance().deleteListener(baseDataRegister);
    }

    public void onCanResultApp(CanDataFormat canDataFormat){
        //can地址
        long addressLong = canDataFormat.getAddressByLong();
        //can数据
        byte[] canData = canDataFormat.getData();
        //DCDC基础数据解析
        if (addressLong >= DCDC_BASE_ADDEESS_MIN && addressLong <= DCDC_BASE_ADDEESS_MAX) {
            //DCDC数据舱门
            int dcdcIndex = (int)(addressLong - DCDC_BASE_ADDEESS_MIN) - 1;
            //DCDC数据帧相对地址
            int dcdcItemIndex = canData[0];
            if (dcdcItemIndex == 1) {
                dcdcBaseArrays[dcdcIndex][0] = canData;
            } else if (dcdcItemIndex == 2) {
                dcdcBaseArrays[dcdcIndex][1] = canData;
            } else if (dcdcItemIndex == 3) {
                dcdcBaseArrays[dcdcIndex][2] = canData;
            } else if (dcdcItemIndex == 4) {
                dcdcBaseArrays[dcdcIndex][3] = canData;
            } else if (dcdcItemIndex == 5) {
                dcdcBaseArrays[dcdcIndex][4] = canData;
            } else if (dcdcItemIndex == 6) {
                dcdcBaseArrays[dcdcIndex][5] = canData;
            } else if (dcdcItemIndex == 7) {
                dcdcBaseArrays[dcdcIndex][6] = canData;
            } else if (dcdcItemIndex == 8) {
                dcdcBaseArrays[dcdcIndex][7] = canData;
            } else if (dcdcItemIndex == 9) {
                dcdcBaseArrays[dcdcIndex][8] = canData;
            } else if (dcdcItemIndex == 10) {
                dcdcBaseArrays[dcdcIndex][9] = canData;
            } else if (dcdcItemIndex == 11) {
                dcdcBaseArrays[dcdcIndex][10] = canData;
            } else if (dcdcItemIndex == 12) {
                dcdcBaseArrays[dcdcIndex][11] = canData;
            } else if (dcdcItemIndex == 13) {
                dcdcBaseArrays[dcdcIndex][12] = canData;
            } else if (dcdcItemIndex == 14) {
                dcdcBaseArrays[dcdcIndex][13] = canData;
            } else if (dcdcItemIndex == 15) {
                dcdcBaseArrays[dcdcIndex][14] = canData;
            } else if (dcdcItemIndex == 16) {
                dcdcBaseArrays[dcdcIndex][15] = canData;
            } else if (dcdcItemIndex == 17) {
                dcdcBaseArrays[dcdcIndex][16] = canData;
            } else if (dcdcItemIndex == 18) {
                //判断接收到0x12 证明一个帧的报文发送完毕了  取出并拼装所有报文并清空  得先判断里面有没有空值 如果有空值的话 证明报文不完整 废弃掉

                dcdcBaseArrays[dcdcIndex][17] = canData;
                //建立临时数组
                byte[][] tempArrays = dcdcBaseArrays[dcdcIndex];
                int isHaveNull = 0;
                for (int i = 0; i < tempArrays.length; i++) {
                    if (tempArrays[i][0] == 0) {
                        isHaveNull = 1;
                        break;
                    }
                }
                //没有空值
                if (isHaveNull == 0) {
                    //本应该是126的(7*18) 手动开头添加一位 放置本数据帧是哪个舱门的
                    byte[] returnOrder = new byte[127];
                    returnOrder[0] = (byte) (dcdcIndex + 1);
                    for (int i = 0; i < tempArrays.length; i++) {
                        for (int j = 0; j < tempArrays[i].length - 1; j++) {
                            returnOrder[(i * 7) + (j+1)] = tempArrays[i][j + 1];
                        }
                    }
                    //数据校验
                    daaDataFormat.setDcdcInfoByBaseFormat(dcdcIndex , dcdcReceiveAnalysis[dcdcIndex].returnBaseByBytes(returnOrder));
                    daaIntegrationListener.returnData(daaDataFormat , ReturnDataType.dcdcInfoByBase , dcdcIndex);

                }
                //清空数据
                dcdcBaseArrays[dcdcIndex] = initArrays(18);
            } else if(dcdcItemIndex == 19){
                dcdcReceiveAnalysis[dcdcIndex].addData_19(canData);
            }
        }

        //DCDC状态数据解析
        if (addressLong >= DCDC_STATE_ADDRESS_MIN && addressLong <= DCDC_STATE_ADDRESS_MAX) {
            //DCDC数据舱门
            int dcdcIndex = (int)(addressLong - DCDC_STATE_ADDRESS_MIN) - 1;
            DcdcInfoByStateFormat dcdcInfoByStateFormat = dcdcReceiveAnalysis[dcdcIndex].returnStateByBytes(canData);
            daaDataFormat.getDcdcInfoByStateFormats()[dcdcIndex] = dcdcInfoByStateFormat;
            daaIntegrationListener.returnData(daaDataFormat , ReturnDataType.dcdcInfoByState , dcdcIndex);
        }

        //DCDC和ACDC预警数据解析
        if (addressLong >= DCDC_AND_ACDC_ERROR_ADDRESS_MIN && addressLong <= DCDC_AND_ACDC_ERROR_ADDRESS_MAX) {
            //ACDC - 1 - 预警信息
            if(addressLong == 2550555985l){
                AcdcInfoByWarningFormat acdcInfoByWarningFormat = new AcdcInfoByWarningFormat(addressLong, canData);
                daaDataFormat.getAcdcInfoByWarningFormats()[0] = acdcInfoByWarningFormat;
                daaIntegrationListener.returnData(daaDataFormat , ReturnDataType.acdcInfoByWarning , 0);
            }
            //ACDC - 2 - 预警信息
            else if(addressLong == 2550555986l){
                AcdcInfoByWarningFormat acdcInfoByWarningFormat = new AcdcInfoByWarningFormat(addressLong ,canData);
                daaDataFormat.getAcdcInfoByWarningFormats()[1] = acdcInfoByWarningFormat;
                daaIntegrationListener.returnData(daaDataFormat , ReturnDataType.acdcInfoByWarning, 0);
            }
            //ACDC - 3 - 预警信息
            else if(addressLong == 2550555987l){
                AcdcInfoByWarningFormat acdcInfoByWarningFormat = new AcdcInfoByWarningFormat(addressLong ,canData);
                daaDataFormat.getAcdcInfoByWarningFormats()[2] = acdcInfoByWarningFormat;
                daaIntegrationListener.returnData(daaDataFormat , ReturnDataType.acdcInfoByWarning, 0);
            }
            //dcdc - 预警信息
            else {
                //DCDC数据舱门
                int dcdcIndex =  (int)(addressLong - DCDC_AND_ACDC_ERROR_ADDRESS_MIN) - 1;
                //DCDC类型
                int type = canData[0];
                byte[] data = new byte[]{canData[4],canData[3],canData[2],canData[1]};
                if (type == 1) { // 内部告警
                    daaDataFormat.getDcdcInfoByWarningFormats()[dcdcIndex].setErrorStateInSide(UtilPublic.ByteArrToHex(data));
                } else if (type == 2) { // 外部告警
                    daaDataFormat.getDcdcInfoByWarningFormats()[dcdcIndex].setErrorStateOutSide(UtilPublic.ByteArrToHex(data));
                } else if (type == 3) { // BMS告警
                    daaDataFormat.getDcdcInfoByWarningFormats()[dcdcIndex].setErrorStateBMS(UtilPublic.ByteArrToHex(data));
                }
                daaIntegrationListener.returnData(daaDataFormat , ReturnDataType.dcdcInfoByWarning , dcdcIndex);
            }

        }

        //ACDC状态信息解析 状态帧 - 1    2s返回
        if (addressLong >= ACDC_STATE_1_ADDRESS_MIN && addressLong <= ACDC_STATE_1_ADDRESS_MAX) {
            //负数处理
            int[] data = new int[canData.length];
            for (int i = 0; i < data.length; i++) {
                data[i] = canData[i] & 0xff;
            }
            int acdcIndex =  (int)(addressLong - ACDC_STATE_1_ADDRESS_MIN);
            //解析最大输出功率
            double outputPower = (double)(data[1] * 256 + data[0]) / 100;
            daaDataFormat.getAcdcInfoByStateFormats()[acdcIndex].setAcdcOutputPower(outputPower);
            //解析输入电压
            double inputVoltage = (double)(data[3] * 256 + data[2]) / 10;
            daaDataFormat.getAcdcInfoByStateFormats()[acdcIndex].setAcdcInputVoltage(inputVoltage);
            //解析输出电压
            double outPutVoltage = (double)(data[5] * 256 + data[4]) / 10;
            daaDataFormat.getAcdcInfoByStateFormats()[acdcIndex].setAcdcOutPutVoltage(outPutVoltage);
            //解析输出电流
            double outputElectric = (double) ((data[7] * 256 + data[6]) - 4000) / 10;
            daaDataFormat.getAcdcInfoByStateFormats()[acdcIndex].setAcdcOutPutElectric(outputElectric);
            //处理时间
            daaDataFormat.getAcdcInfoByStateFormats()[acdcIndex].setDataTime(System.currentTimeMillis());
            //是否休眠
            String isSleep = outputElectric != 0 ? "未休眠" : outputElectric == 0 ? "休眠" : "";
            daaDataFormat.getAcdcInfoByStateFormats()[acdcIndex].setAcdcIsSleep(isSleep);
            daaIntegrationListener.returnData(daaDataFormat , ReturnDataType.acdcInfoByState , acdcIndex);
        }
        //ACDC状态信息解析 状态帧 - 2    20s返回一次 跟上面信息无法同时处理
        if (addressLong >= ACDC_STATE_2_ADDRESS_MIN && addressLong <= ACDC_STATE_2_ADDRESS_MAX) {
            //负数处理
            int[] data = new int[canData.length];
            for (int i = 0; i < data.length; i++) {
                data[i] = canData[i] & 0xff;
            }
            int acdcIndex =  (int)(addressLong - ACDC_STATE_2_ADDRESS_MIN);
            daaDataFormat.getAcdcInfoByStateFormats()[acdcIndex].setAcdcSoftWareVersion(data[0]+"");
            daaDataFormat.getAcdcInfoByStateFormats()[acdcIndex].setAcdcHardWareVersion(data[1]+"");
            byte v = (byte) (data[2] & 0xFF);
            String whichAc = v == 0 ? "Master" : v == 1 ? "Slave" : "";
            daaDataFormat.getAcdcInfoByStateFormats()[acdcIndex].setAcdcMasterOrSlave(whichAc);
            //设置剩余总功率
            double surplusPower = (double)(data[4] * 256 + data[3]) / 100;
            daaDataFormat.getAcdcInfoByStateFormats()[acdcIndex].setAcdcSurplusPower(surplusPower);
            daaIntegrationListener.returnData(daaDataFormat , ReturnDataType.acdcInfoByState , acdcIndex);
        }
    }

    //初始化数组
    protected byte[][] initArrays(int len) {
        byte[] byte_8 = new byte[8];
        Arrays.fill(byte_8, (byte) 0);
        byte[][] byte_len_8 = new byte[len][8];
        Arrays.fill(byte_len_8, byte_8);
        return byte_len_8;
    }

}
