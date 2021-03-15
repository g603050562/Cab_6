package com.hellohuandian.app.httpclient;

import com.hellohuandian.pubfunction.Unit.Md5;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 离线换电 租电池成功后 返回信息给服务器
 */
public class HttpWriteUidRet extends Thread {

    private String number, door, battery, uid32;


    /**
     *
     * @param number   电柜ID(例：04531)
     * @param door     租赁电池的 舱门号
     * @param battery  租赁电池的 电池ID
     * @param uid32    租赁电池的 UID32
     */
    public HttpWriteUidRet(String number, String door, String battery, String uid32) {

        this.number = number;
        this.door = door;
        this.battery = battery;
        this.uid32 = uid32;
    }

    @Override
    public void run() {
        super.run();


        OkHttpClient mClient = new OkHttpClient.Builder()
                .callTimeout(5000, TimeUnit.MILLISECONDS)
                .connectTimeout(5000, TimeUnit.MILLISECONDS)
                .readTimeout(5000, TimeUnit.MILLISECONDS)
                .writeTimeout(5000, TimeUnit.MILLISECONDS)
                .retryOnConnectionFailure(false)
                .build();

        FormBody body = new FormBody.Builder()
                .add("battery", battery)
                .add("number", number)
                .add("door", door)
                .add("uid32", uid32)
                .build();
        Request request = new Request.Builder()
                .url(MyApplication.apc + "Cabinet/writeUidRet.html")
                .addHeader("aptk", new Md5().getDateToken())
                .post(body)
                .build();
        Call call = mClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                System.out.println("网络：   JSON：HttpWriteUidRet" + e.toString());
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String result = response.body().string();
                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(result);
                        System.out.println("网络：   " + jsonObject);
                    } catch (JSONException e) {
                        System.out.println("网络：   JSON：HttpWriteUidRet" + e.toString());
                    }

                }
            }
        });
    }
}
