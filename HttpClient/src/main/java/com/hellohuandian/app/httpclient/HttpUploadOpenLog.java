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
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.List;

/**
 ** 上传弹出电池记录
 ** 只要弹出舱门都得上报信息给服务器
 */
public class HttpUploadOpenLog extends Thread {

    private String cab_id, door, battery, info;


    /**
     *
     * @param cab_id   电柜ID（例：04531）
     * @param door     弹出的电池舱门号
     * @param battery  电池ID
     * @param info     什么原因弹出的
     */
    public HttpUploadOpenLog(String cab_id, String door, String battery, String info) {

        this.cab_id = cab_id;
        this.door = door;
        this.battery = battery;
        this.info = info;
    }

    @Override
    public void run() {
        super.run();
        String path = MyApplication.apc + "CabLog/addCabEjectLog";
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
        list.add(new BasicNameValuePair("number", cab_id));
        list.add(new BasicNameValuePair("door", door));
        list.add(new BasicNameValuePair("battery", battery));
        list.add(new BasicNameValuePair("remark", info));

        try {
            HttpEntity entity = new UrlEncodedFormEntity(list, "utf-8");
            httpPost.setEntity(entity);
            HttpClient client = new DefaultHttpClient();
            HttpResponse httpResponse = client.execute(httpPost);
            if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                String result = EntityUtils.toString(httpResponse.getEntity());

                JSONTokener jsonTokener = new JSONTokener(result);
                JSONObject jsonObject = (JSONObject) jsonTokener.nextValue();


            } else {
                System.out.println(httpResponse.getStatusLine().getStatusCode());
            }
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }
}
