package client.halouhuandian.app15.pub.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import client.halouhuandian.app15.R;
import client.halouhuandian.app15.config.SystemConfig;
import client.halouhuandian.app15.hardWareConncetion.daa.mode.DaaDataFormat;
import client.halouhuandian.app15.hardWareConncetion.daa.mode.dcdc.DcdcInfoByBaseFormat;
import client.halouhuandian.app15.model.dao.sharedPreferences.ForbiddenSp;

/**
 * Author:      Lee Yeung
 * Create Date: 2020/3/9
 * Description: 电池工具类
 */
public final class UtilBattery {

    /**
     * 是否是绑定的电池
     * @param uid
     * @return true已经绑定，false没有绑定
     */
    public static boolean is8A(String uid) {
        return "AAAAAAAA".equals(uid);
    }
    public static boolean is16F(String bid) { return "FFFFFFFFFFFFFFFF".equals(bid);}
    public static boolean is160(String bid) { return "0000000000000000".equals(bid);}

    /**
     * 检测是不是有效舱门
     * @param door
     * @return
     */
    public static boolean isValidDoor(int door) {
        return door >= 1 && door <= SystemConfig.getMaxBattery();
    }

    /**
     * 判断电池是什么类型的 48 or 60
     * @param bid
     * @return
     */
    public static String isType(String bid){
        String top = bid.substring(0,1);
        if(top.equals("M")){
            return "60";
        }else if(top.equals("N")){
            return "48";
        }else{
            return "60";
        }
    }

    /**
     * 获取一个空仓们
     * @param forbiddenSp
     * @param daaDataFormat
     * @return
     */

    public static int getEmptyDoor(ForbiddenSp forbiddenSp , DaaDataFormat daaDataFormat){

        int emptyDoor = -1;
        DcdcInfoByBaseFormat[] dcdcInfoByBaseFormats =  daaDataFormat.getDcdcInfoByBaseFormats();
        List<Integer> emptyDoorList = new ArrayList<>();

        for (int i = 0; i < SystemConfig.getMaxBattery(); i++) {

            int is_stop = forbiddenSp.getTargetForbidden(i);
            String bid = dcdcInfoByBaseFormats[i].getBID();
            int innerInching = dcdcInfoByBaseFormats[i].getInchingByInner();
            int outerInching = dcdcInfoByBaseFormats[i].getInchingByOuterClose();

            if (innerInching == 0 && outerInching == 1 && bid.equals("0000000000000000") && is_stop == 1) {
                emptyDoor = i + 1;
                emptyDoorList.add(emptyDoor);
            }
        }

        if(emptyDoorList.size() > 0){
            int randomIndex = new Random().nextInt(emptyDoorList.size());
            emptyDoor = emptyDoorList.get(randomIndex);
        }

        return emptyDoor;
    }

    /**
     * 获取一个已经打开的空仓门
     *
     * @return
     */
    public static int obtainOpenedEmptyDoor(ForbiddenSp forbiddenSp , DaaDataFormat daaDataFormat) {

        int emptyDoor = -1;
        DcdcInfoByBaseFormat[] dcdcInfoByBaseFormats =  daaDataFormat.getDcdcInfoByBaseFormats();

        for (int i = 0; i < SystemConfig.getMaxBattery(); i++) {

            int is_stop = forbiddenSp.getTargetForbidden(i);
            int outerInching = dcdcInfoByBaseFormats[i].getInchingByOuterClose();

            if (outerInching == 0 && is_stop == 1) {
                emptyDoor = i + 1;
                break;
            }
        }
        return emptyDoor;
    }


}
