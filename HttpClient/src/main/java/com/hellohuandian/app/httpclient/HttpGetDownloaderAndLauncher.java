package com.hellohuandian.app.httpclient;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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
public class HttpGetDownloaderAndLauncher extends Thread {

    public interface HttpGetDownloaderAndLauncherListener{
        void returnMessage(String code, String msg, String data);
    }

    private HttpGetDownloaderAndLauncherListener httpGetDownloaderAndLauncherListener ;


    /**
     * @param httpGetDownloaderAndLauncherListener   接口
     */
    public HttpGetDownloaderAndLauncher(HttpGetDownloaderAndLauncherListener httpGetDownloaderAndLauncherListener) {
        this.httpGetDownloaderAndLauncherListener = httpGetDownloaderAndLauncherListener;
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

        FormBody body = new FormBody.Builder()
                .add("token", getMd5Token("1"))
                .add("data", "1")
                .build();
        Request request = new Request.Builder()
                .url("http://47.110.240.148/Log/getVersion")
                .post(body)
                .build();
        Call call = mClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                System.out.println("网络：   JSON：getVersion" + e.toString());
                returnMessgae( "-1",  "网络：   JSON：getVersion" + e.toString(),"");
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
                        System.out.println("网络：   JSON：getVersion" + e.toString());
                        returnMessgae( "-1", "网络：   JSON：getVersion" + e.toString(),"");
                    }
                }
            }
        });
    }

    private void returnMessgae(String code, String str, String data) {
        httpGetDownloaderAndLauncherListener.returnMessage(code,str,data);
    }

    private static String tokenKey = "woshialixiaobudianer";

    public static String getMd5Token(String uid){
        String returnToken = "";
        returnToken = getMD5Str(tokenKey + uid + tokenKey);
        returnToken = getMD5Str(returnToken);
        return returnToken;
    }
    private static String getMD5Str(String str) {
        byte[] digest = null;
        try {
            MessageDigest md5 = MessageDigest.getInstance("md5");
            digest  = md5.digest(str.getBytes("utf-8"));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        //16是表示转换为16进制数
        String md5Str = new BigInteger(1, digest).toString(16);
        return md5Str;
    }

}
