package com.hellohuandian.controlpanelupgrade._base.callBack;

/**
 * Author:      Lee Yeung
 * Create Date: 2019-09-07
 * Description:
 */
public interface OnRwAction
{
    void write(byte[] bytes);

    byte[] read();
}
