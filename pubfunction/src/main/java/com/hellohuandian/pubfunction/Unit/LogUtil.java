package com.hellohuandian.pubfunction.Unit;

import android.util.Log;

/**
 * Author:      Lee Yeung
 * Create Date: 2020/5/22
 * Description:
 */
public final class LogUtil
{
    private static String TAG = "Log:";
    private static boolean mSimpleLogMode = true;

    private static final String CLASS_METHOD_LINE_FORMAT = "%s.%s()  Line:%d  (%s)";
    private static final String CLASS_METHOD_LINE_FORMAT_SIMPLE = "Line:%d java:%s->";

    private LogUtil()
    {
    }

    public static void I(String str)
    {
        String logText = "";
        StackTraceElement traceElement = Thread.currentThread().getStackTrace()[3];
        if (mSimpleLogMode)
        {
            logText = String.format(CLASS_METHOD_LINE_FORMAT_SIMPLE, traceElement.getLineNumber(),
                    traceElement.getFileName());
        } else
        {
            logText = String.format(CLASS_METHOD_LINE_FORMAT, traceElement.getClassName(),
                    traceElement.getMethodName(), traceElement.getLineNumber(), traceElement.getFileName());
        }

        logText += "  " + str;
        Log.i(TAG, logText);
    }

    public static void E(String str)
    {
        String logText = "";
        StackTraceElement traceElement = Thread.currentThread().getStackTrace()[3];
        if (mSimpleLogMode)
        {
            logText = String.format(CLASS_METHOD_LINE_FORMAT_SIMPLE, traceElement.getLineNumber(),
                    traceElement.getFileName());
        } else
        {
            logText = String.format(CLASS_METHOD_LINE_FORMAT, traceElement.getClassName(),
                    traceElement.getMethodName(), traceElement.getLineNumber(), traceElement.getFileName());
        }

        logText += " " + str;
        Log.e(TAG, logText);
    }

    public static void D(String str)
    {
        String logText = "";
        StackTraceElement traceElement = Thread.currentThread().getStackTrace()[3];
        if (mSimpleLogMode)
        {
            logText = String.format(CLASS_METHOD_LINE_FORMAT_SIMPLE, traceElement.getLineNumber(),
                    traceElement.getFileName());
        } else
        {
            logText = String.format(CLASS_METHOD_LINE_FORMAT, traceElement.getClassName(),
                    traceElement.getMethodName(), traceElement.getLineNumber(), traceElement.getFileName());
        }

        logText += " " + str;
        Log.e(TAG, logText);
    }
}
