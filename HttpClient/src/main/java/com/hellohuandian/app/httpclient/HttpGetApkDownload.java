package com.hellohuandian.app.httpclient;

import android.os.AsyncTask;

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
 * 获取后台 apk 下载地址
 */
public class HttpGetApkDownload extends Thread {

    private IFHttpGetApkDownloadLinstener ifHttpGetApkDownloadLinstener;
    /**
     * @param ifHttpGetApkDownloadLinstener   接口
     */
    public HttpGetApkDownload(IFHttpGetApkDownloadLinstener ifHttpGetApkDownloadLinstener) {
        this.ifHttpGetApkDownloadLinstener = ifHttpGetApkDownloadLinstener;
    }
    @Override
    public void run() {
        super.run();

        OkHttpClient mClient = new OkHttpClient.Builder()
                .callTimeout(5_000, TimeUnit.MILLISECONDS)
                .connectTimeout(5_000, TimeUnit.MILLISECONDS)
                .readTimeout(5_000, TimeUnit.MILLISECONDS)
                .writeTimeout(5_000, TimeUnit.MILLISECONDS)
                .build();

        FormBody body = new FormBody.Builder().build();
        Request request = new Request.Builder()
                .url("https://downapk.halouhuandian.com/ApsSet/apk.html")
                .addHeader("aptk", new Md5().getDateToken())
                .post(body)
                .build();
        Call call = mClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                System.out.println("网络：   JSON：HttpGetApkDownload" + e.toString());
                returnMessgae( "-1",  "网络：   JSON：HttpGetApkDownload" + e.toString(),"");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String result = response.body().string();
                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(result);
                        System.out.println("网络：   " + jsonObject);
                        String messageString = jsonObject.getString("msg");
                        String status = jsonObject.getString("status");
                        String data = "";
                        if(status.equals("0")){
                            data = "";
                        }else{
                            data = jsonObject.getString("data");
                        }

                        returnMessgae( status, messageString,data);

                    } catch (JSONException e) {
                        System.out.println("网络：   JSON：HttpGetApkDownload" + e.toString());
                        returnMessgae( "-1", "网络：   JSON：HttpGetApkDownload" + e.toString(),"");
                    }
                }
            }
        });
    }

    private void returnMessgae(String code, String str, String data) {
        ifHttpGetApkDownloadLinstener.onHttpGetApkDownloadResult(code,str,data);
    }

}
