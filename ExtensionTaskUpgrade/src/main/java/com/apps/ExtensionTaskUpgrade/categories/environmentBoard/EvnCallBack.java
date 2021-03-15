package com.apps.ExtensionTaskUpgrade.categories.environmentBoard;

/**
 * Author:      Lee Yeung
 * Create Date: 2020/5/7
 * Description:
 */
public interface EvnCallBack {
    void update(long process, long total, String info, @EvnUpdateStatus int status);
}
