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

import java.util.ArrayList;
import java.util.List;

/**
 * 上传温度预警
 */
public class HttpUploadWriteUidFail extends Thread {

    private String cab_id, battery, uid, extime, door,type;

    /**
     * @param cab_id                           电柜ID（例：04531）
     * @param door                             温度异常舱门
     */
    public HttpUploadWriteUidFail(String cab_id, String battery, String uid, String extime, String door,String type) {
        this.cab_id = cab_id;
        this.door = door;
        this.battery = battery;
        this.uid = uid;
        this.extime = extime;
        this.type = type;
    }

    @Override
    public void run() {
        super.run();
        String path = MyApplication.apc + "Errors/cabinet.html";
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

        JSONObject jsonObject_err = new JSONObject();
        try {
            jsonObject_err.put("battery",battery);
            jsonObject_err.put("uid32",uid);
            jsonObject_err.put("extime",extime);
            jsonObject_err.put("door",door);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        List<NameValuePair> list = new ArrayList<NameValuePair>();
        list.add(new BasicNameValuePair("number", cab_id));
        list.add(new BasicNameValuePair("errors", jsonObject_err.toString()));
        list.add(new BasicNameValuePair("type", type));

        try {
            HttpEntity entity = new UrlEncodedFormEntity(list, "utf-8");
            httpPost.setEntity(entity);
            HttpClient client = new DefaultHttpClient();
            HttpResponse httpResponse = client.execute(httpPost);
            if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                String result = EntityUtils.toString(httpResponse.getEntity());
                JSONTokener jsonTokener = new JSONTokener(result);
                JSONObject jsonObject = (JSONObject) jsonTokener.nextValue();

                System.out.println("网络：   Errors/cabinet.html - "+jsonObject.toString());

            }
        } catch (Exception e) {

        }
    }

}
