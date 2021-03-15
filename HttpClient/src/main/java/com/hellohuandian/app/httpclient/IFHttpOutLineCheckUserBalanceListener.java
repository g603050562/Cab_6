package com.hellohuandian.app.httpclient;

public interface IFHttpOutLineCheckUserBalanceListener {
    void onHttpOutLineCheckUserBalanceResult(String uid, String door, String code, String str, String data);
}
