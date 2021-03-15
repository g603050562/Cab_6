package client.halouhuandian.app15.core.tools;

/**
 * Author:      Lee Yeung
 * Create Date: 2020/3/9
 * Description: 电池工具类
 */
public final class BatteryUtil {
    /**
     * 是否是绑定的电池
     *
     * @param uid
     * @return true已经绑定，false没有绑定
     */
    public static boolean is8A(String uid) {
        return "AAAAAAAA".equals(uid);
    }
}
