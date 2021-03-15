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
 * 服务器返回电柜下载app二维码 地址链接
 */
public class HttpGetQcode extends Thread {

    private String number = "";
    private IFHttpGetQcodeLinstener ifHttpGetQcodeLinstener;

    /**
     *
     * @param number 电柜编号（例：04531）
     * @param ifHttpGetQcodeLinstener 接口
     */
    public HttpGetQcode(String number, IFHttpGetQcodeLinstener ifHttpGetQcodeLinstener) {
        this.number = number;
        this.ifHttpGetQcodeLinstener = ifHttpGetQcodeLinstener;
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
                .build();
        Request request = new Request.Builder()
                .url(MyApplication.apc + "Cabinet/qrcode.html")
                .addHeader("aptk", new Md5().getDateToken())
                .post(body)
                .build();
        Call call = mClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                returnMessgae("-1", "网络：JSON：HttpGetQcode" + e.toString(), "");
                System.out.println("网络：   JSON：HttpGetQcode" + e.toString());
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
                        returnMessgae("-1", "网络：JSON：HttpGetQcode" + e.toString(), "");
                        System.out.println("网络：   JSON：HttpGetQcode" + e.toString());
                    }

                }
            }
        });
    }

    private void returnMessgae(String code, String str, String data) {
        if (ifHttpGetQcodeLinstener != null)
            ifHttpGetQcodeLinstener.onHttpGetQcodeResult(code,str,data);
    }
}

