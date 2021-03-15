package com.hellohuandian.app.httpclient;

import com.hellohuandian.pubfunction.Unit.Md5;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
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
 * 上传基站信息
 */
public class HttpUploadGMS extends Thread {

    private String cab_id, MCC, MNC, LAC, CID;


    /**
     *
     * @param cab_id   电柜ID（例：04531）
     * @param MCC      基站信息MCC
     * @param MNC      基站信息MNC
     * @param LAC      基站信息LAC
     * @param CID      基站信息CID
     */
    public HttpUploadGMS(String cab_id, String MCC, String MNC, String LAC, String CID) {
        this.cab_id = cab_id;
        this.MCC = MCC;
        this.MNC = MNC;
        this.LAC = LAC;
        this.CID = CID;
    }

    @Override
    public void run() {
        super.run();
        HttpPost httpPost = new HttpPost(MyApplication.apc+"Cabinet/baseLBS.html");
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
            public HeaderElement[] getElements() {
                return new HeaderElement[0];
            }
        };
        httpPost.addHeader(header1);

        List<NameValuePair> list = new ArrayList<NameValuePair>();
        list.add(new BasicNameValuePair("number", cab_id));
        list.add(new BasicNameValuePair("mcc", MCC));
        list.add(new BasicNameValuePair("mnc", MNC));
        list.add(new BasicNameValuePair("lac", LAC));
        list.add(new BasicNameValuePair("cid", CID));


        try {
            HttpEntity entity = new UrlEncodedFormEntity(list, "utf-8");
            httpPost.setEntity(entity);
            HttpClient client = new DefaultHttpClient();
            HttpResponse httpResponse = client.execute(httpPost);
            if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                String result = EntityUtils.toString(httpResponse.getEntity());

                JSONTokener jsonTokener = new JSONTokener(result);
                JSONObject jsonObject = (JSONObject) jsonTokener.nextValue();

                System.out.println("网络：   返回：" + jsonObject);

            }
        } catch (Exception e) {
        }
    }
}