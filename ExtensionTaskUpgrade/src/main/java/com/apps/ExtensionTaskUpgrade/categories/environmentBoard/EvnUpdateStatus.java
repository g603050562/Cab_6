package com.apps.ExtensionTaskUpgrade.categories.environmentBoard;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Author:      Lee Yeung
 * Create Date: 2020/5/7
 * Description:
 */
@IntDef({
        EvnUpdateStatus.START, EvnUpdateStatus.PROCESS, EvnUpdateStatus.SUCCESSED, EvnUpdateStatus.FAILED})
@Retention(RetentionPolicy.SOURCE)
public @interface EvnUpdateStatus {
    int START = 1;
    int PROCESS = 2;
    int SUCCESSED = 3;
    int FAILED = 4;
}
