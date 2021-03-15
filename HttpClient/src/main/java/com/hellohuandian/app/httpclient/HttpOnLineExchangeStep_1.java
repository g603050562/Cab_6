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

public class HttpOnLineExchangeStep_1 extends Thread{

    private IFHttpOnLineExchangeStep1Linstener ifHttpOnLineExchangeStep1Linstener;
    private String cabinet_id, battery_quantity, battery_id, door, battery_all, bar_type;

    public HttpOnLineExchangeStep_1(String cabinet_id, String battery_quantity, String battery_id, String door, String bar_type, String battery_all,IFHttpOnLineExchangeStep1Linstener ifHttpOnLineExchangeStep1Linstener) {
        this.cabinet_id = cabinet_id;
        this.battery_quantity = battery_quantity;
        this.battery_id = battery_id;
        this.door = door;
        this.battery_all = battery_all;
        this.bar_type = bar_type;
        this.ifHttpOnLineExchangeStep1Linstener = ifHttpOnLineExchangeStep1Linstener;
    }

    @Override
    public void run() {
        super.run();


        String path = MyApplication.exchange;

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
        list.add(new BasicNameValuePair("number", cabinet_id));
        list.add(new BasicNameValuePair("in_electric", battery_quantity));
        list.add(new BasicNameValuePair("in_battery", battery_id));
        list.add(new BasicNameValuePair("in_door", door));
        list.add(new BasicNameValuePair("bty_all", battery_all));
        list.add(new BasicNameValuePair("in_volt", bar_type));

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
                    String errorCode = jsonObject.getString("errno");

                    String data = "";
                    if(jsonObject.has("data")){
                        data = jsonObject.getString("data");
                        String is_show = jsonObject.getString("show");
                        returnMessgae( status, messageString,data,errorCode,is_show);
                    }else{
                        returnMessgae( status, messageString,"",errorCode,"1");
                    }

                } catch (JSONException e) {
                    System.out.println("网络：   JSON：HttpOnLineExchangeStep_1" + e.toString());
                    returnMessgae( "-1", "网络：   JSON：HttpOnLineExchangeStep_1" + e.toString(),"","","1");
                }

            } else {
                System.out.println("网络：   JSON：HttpOnLineExchangeStep_1" + httpResponse.getStatusLine().getStatusCode());
                returnMessgae( "-1", "网络：   JSON：HttpOnLineExchangeStep_1" + httpResponse.getStatusLine().getStatusCode(),"","","1");
            }
        } catch (Exception e) {
            System.out.println("网络：   JSON：HttpOnLineExchangeStep_1" + e.toString());
            returnMessgae( "-1", "网络：   JSON：HttpOnLineExchangeStep_1" +  e.toString(),"","","1");
        }
    }

    private void returnMessgae(String code, String str, String data , String errorCode , String is_show) {
        ifHttpOnLineExchangeStep1Linstener.onHttpOnLineExchangeStep1Result(code,str,data,errorCode,door,is_show);
    }
}
