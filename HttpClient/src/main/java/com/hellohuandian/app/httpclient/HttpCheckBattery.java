package com.hellohuandian.app.httpclient;

import android.app.Activity;

import com.hellohuandian.pubfunction.Unit.Md5;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HttpCheckBattery extends Thread {

    private String door = "";
    private String cid = "";
    private Activity activity;
    private IFHttpCheckBatteryLinstener ifHttpCheckBatteryLinstener;


    public HttpCheckBattery(Activity activity, String cid, String door, IFHttpCheckBatteryLinstener ifHttpCheckBatteryLinstener) {
        this.activity = activity;
        this.door = door;
        this.cid = cid;
        this.ifHttpCheckBatteryLinstener = ifHttpCheckBatteryLinstener;
    }

    @Override
    public void run() {
        super.run();

        OkHttpClient mClient = new OkHttpClient.Builder()
                .callTimeout(3000, TimeUnit.MILLISECONDS)
                .connectTimeout(3000, TimeUnit.MILLISECONDS)
                .readTimeout(3000, TimeUnit.MILLISECONDS)
                .writeTimeout(3000, TimeUnit.MILLISECONDS)
                .retryOnConnectionFailure(false)
                .build();

        FormBody body = new FormBody.Builder()
                .add("number", cid)
                .add("door", door)
                .build();
        Request request = new Request.Builder()
                .url(MyApplication.apc + "CabLog/checkBattery")
                .addHeader("aptk", new Md5().getDateToken())
                .post(body)
                .build();
        Call call = mClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                returnMessgae("-1", "网络：JSON：HttpCheckBattery" + e.toString(), "");
                System.out.println("网络：   JSON：HttpCheckBattery" + e.toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String result = response.body().string();
                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(result);
                        System.out.println("网络：   " + jsonObject);

                        String code = jsonObject.getString("status");
                        String messageString = jsonObject.getString("msg");

                        String data = "";
                        if (code.equals("0")) {
                            data = "";
                        } else {
                            data = jsonObject.getString("data");
                        }

                        returnMessgae(code, messageString, data);

                    } catch (JSONException e) {
                        returnMessgae("-1", "网络：JSON：HttpCheckBattery" + e.toString(), "");
                        System.out.println("网络：   JSON：HttpCheckBattery" + e.toString());
                    }

                }
            }
        });
    }

    private void returnMessgae(String code, String str, String data) {
        ifHttpCheckBatteryLinstener.onHttpCheckBatteryResult(code, str, data, door);
    }

}
