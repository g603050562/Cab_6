package com.hellohuandian.PduUpgrade;

/**
 * Author:      Lee Yeung
 * Create Date: 2019-12-06
 * Description:
 */
public final class PduUpgradeStatusCode {
    /**
     * pdu升级运行
     */
    public static final byte PDU_UPGRADE_RUN = 0x01;

    //进入PDU升级模式
    public static final byte PDU_UPGRADE_MODE = 0x10;
    //单包连接
    public static final byte PDU_UPGRADE_CONNECTING = 0x11;
    //单包结束
    public static final byte PDU_UPGRADE_PACKET_FINISH = 0X12;
    //全包结束
    public static final byte PDU_UPGRADE_ALL_FINISH = 0X13;
}
