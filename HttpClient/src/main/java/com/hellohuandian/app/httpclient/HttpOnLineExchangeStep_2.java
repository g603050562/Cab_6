package com.hellohuandian.app.httpclient;

import android.os.Handler;

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

public class HttpOnLineExchangeStep_2 extends Thread{

    private String trade_id , inching , door;
    private IFHttpOnLineExchangeStep2Linstener ifHttpOnLineExchangeStep2Linstener;

    public HttpOnLineExchangeStep_2(String trade_id, String inching, String door,IFHttpOnLineExchangeStep2Linstener ifHttpOnLineExchangeStep2Linstener){
        this.trade_id = trade_id;
        this.inching = inching;
        this.door = door;
        this.ifHttpOnLineExchangeStep2Linstener = ifHttpOnLineExchangeStep2Linstener;
    }

    @Override
    public void run() {
        super.run();

        String path = MyApplication.apc + "Exchange/taken.html";
        HttpPost httpPost = new HttpPost(path);
        httpPost.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 10000);
        httpPost.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 10000);

        Header header1 = new Header() {
            @Override
            public String getName() {
                return "aptk";
            }

            @Override
            public String getValue() {
                return new Md5().getDateToken();
            }

            @Override
            public HeaderElement[] getElements() throws ParseException {
                return new HeaderElement[0];
            }
        };
        httpPost.addHeader(header1);

        List<NameValuePair> list = new ArrayList<NameValuePair>();
        list.add(new BasicNameValuePair("trade_id", trade_id));
        list.add(new BasicNameValuePair("inching", inching));

        try {
            HttpEntity entity = new UrlEncodedFormEntity(list, "utf-8");
            httpPost.setEntity(entity);
            HttpClient client = new DefaultHttpClient();
            HttpResponse httpResponse = client.execute(httpPost);
            if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                String result = EntityUtils.toString(httpResponse.getEntity());
                JSONObject jsonObject = null;

                try {

                    jsonObject = new JSONObject(result);
                    System.out.println("网络：   " + jsonObject);
                    String messageString = jsonObject.getString("msg");
                    String status = jsonObject.getString("status");

                    String data = "";
                    if(jsonObject.has("data")){
                        data = jsonObject.getString("data");
                        returnMessgae( status, messageString,data);
                    }else{
                        returnMessgae( status, messageString,"");
                    }

                } catch (JSONException e) {
                    System.out.println("网络：   JSON：HttpOnLineExchangeStep_2" + e.toString());
                    returnMessgae( "-1", "网络：   JSON：HttpOnLineExchangeStep_2" + e.toString(),"");
                }

            } else {
                System.out.println("网络：   JSON：HttpOnLineExchangeStep_2" + httpResponse.getStatusLine().getStatusCode());
                returnMessgae( "-1", "网络：   JSON：HttpOnLineExchangeStep_2" + httpResponse.getStatusLine().getStatusCode(),"");
            }
        } catch (Exception e) {
            System.out.println("网络：   JSON：HttpOnLineExchangeStep_2" + e.toString());
            returnMessgae( "-1", "网络：   JSON：HttpOnLineExchangeStep_2" +  e.toString(),"");
        }

    }

    private void returnMessgae(String code, String str, String data) {
        ifHttpOnLineExchangeStep2Linstener.onHttpOnLineExchangeStep2Result(code,str,data,door);
    }
}
