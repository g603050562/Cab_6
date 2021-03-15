package com.hellohuandian.PduUpgrade;

/**
 * Author:      Lee Yeung
 * Create Date: 2019-12-06
 * Description:
 */
public interface PduUpgradeProgress {
    /**
     * @param currentProgress 当前进度
     * @param totalProgress   总进度
     * @param statusCode      状态码
     * @param codeValue       码值
     * @param info            相关信息
     */
    void onProgress(long currentProgress, long totalProgress, byte statusCode, byte codeValue, String info);
}
