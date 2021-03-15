package client.halouhuandian.app15;

import java.math.BigInteger;

/**
 * Author:      Lee Yeung
 * Create Date: 2020/7/18
 * Description:
 */
public final class BitUtil {
    public static String bit32To10(String num) {
        int f = 32;
        int t = 10;
        return new BigInteger(num, f).toString(t);
    }
}
