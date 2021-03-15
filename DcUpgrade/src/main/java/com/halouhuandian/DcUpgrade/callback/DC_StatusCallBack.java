package com.halouhuandian.DcUpgrade.callback;

/**
 * Author:      Lee Yeung
 * Create Date: 2019-12-24
 * Description:
 */
public interface DC_StatusCallBack {
    void onStatusCall(byte address, byte status, String info, long process, long total);
}
