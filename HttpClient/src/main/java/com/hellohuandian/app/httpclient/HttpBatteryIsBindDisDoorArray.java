package com.hellohuandian.app.httpclient;

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

/**
 * 判断插入的电池是否是绑定电池
 */
public class HttpBatteryIsBindDisDoorArray extends Thread {

    private String number = "";
    private String jsons = "";
    private IFHttpBatteryIsBindDisDoorArrayLinstener ifHttpBatteryIsBindDisDoorArrayLinstener;
    /**
     *
     * @param number 电柜编号（例：04531）
     * @param ifHttpBatteryIsBindDisDoorArrayLinstener 接口
     */
    public HttpBatteryIsBindDisDoorArray(String number, String jsons , IFHttpBatteryIsBindDisDoorArrayLinstener ifHttpBatteryIsBindDisDoorArrayLinstener) {
        this.number = number;
        this.jsons = jsons;
        this.ifHttpBatteryIsBindDisDoorArrayLinstener = ifHttpBatteryIsBindDisDoorArrayLinstener;
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
                .add("number",number)
                .add("jsons",jsons)
                .build();
        Request request = new Request.Builder()
                .url(MyApplication.apc + "Check/startCheck.html")
                .addHeader("aptk", new Md5().getDateToken())
                .post(body)
                .build();
        Call call = mClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                returnMessgae("-1", "网络：JSON：HttpBatteryIsBindDisDoorArray" + e.toString(), "");
                System.out.println("网络：   JSON：HttpBatteryIsBindDisDoorArray" + e.toString());
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
                        if(code.equals("0")){
                            data = "";
                        }else{
                            data = jsonObject.getString("data");
                        }
                        returnMessgae(code, messageString,data);
                    } catch (JSONException e) {
                        returnMessgae("-1", "网络：JSON：HttpBatteryIsBindDisDoorArray" + e.toString(), "");
                        System.out.println("网络：   JSON：HttpBatteryIsBindDisDoorArray" + e.toString());
                    }

                }
            }
        });
    }

    private void returnMessgae(String code, String str, String data) {
        ifHttpBatteryIsBindDisDoorArrayLinstener.onHttpBatteryIsBindDisDoorArrayResult(code,str,data);
    }
}

