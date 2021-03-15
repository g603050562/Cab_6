package com.hellohuandian.controlpanelupgrade._base.callBack;

/**
 * Author:      Lee Yeung
 * Create Date: 2019-09-07
 * Description:
 */
public interface OnUpgradeProgress {
    void onUpgrade(byte mapAddress, byte statusFlag, String statusInfo, long currentPregress, long totalPregress);
}
