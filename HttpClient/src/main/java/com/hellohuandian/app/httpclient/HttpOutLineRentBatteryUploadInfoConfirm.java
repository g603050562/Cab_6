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
 * 离线换电 租电池 上传电池信息
 */
public class HttpOutLineRentBatteryUploadInfoConfirm extends Thread {


    String did = "";
    String number = "";
    String rstatus ="";
    String door = "";
    String battery  = "";
    String electric = "";

    IFHttpOutLineRentBatteryUploadInfoConfirmListener ifHttpOutLineRentBatteryUploadInfoConfirmListener;

    public HttpOutLineRentBatteryUploadInfoConfirm(String did, String number ,String rstatus , String door ,String battery , String electric ,   IFHttpOutLineRentBatteryUploadInfoConfirmListener ifHttpOutLineRentBatteryUploadInfoConfirmListener) {
        this.did = did;
        this.number = number;
        this.rstatus = rstatus;
        this.door = door;
        this.battery = battery;
        this.electric = electric;
        this.ifHttpOutLineRentBatteryUploadInfoConfirmListener = ifHttpOutLineRentBatteryUploadInfoConfirmListener;
    }


    @Override
    public void run() {
        super.run();
        String path = MyApplication.api + "Rent/confirmBindv3.html";
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
        list.add(new BasicNameValuePair("did", did));
        list.add(new BasicNameValuePair("number", number));
        list.add(new BasicNameValuePair("rstatus", rstatus));
        list.add(new BasicNameValuePair("door", door));
        list.add(new BasicNameValuePair("battery", battery));
        list.add(new BasicNameValuePair("electric", electric));


        try {
            HttpEntity entity = new UrlEncodedFormEntity(list, "utf-8");
            httpPost.setEntity(entity);
            HttpClient client = new DefaultHttpClient();
            HttpResponse httpResponse = client.execute(httpPost);
            if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                String result = EntityUtils.toString(httpResponse.getEntity());

                JSONTokener jsonTokener = new JSONTokener(result);
                JSONObject jsonObject = (JSONObject) jsonTokener.nextValue();

                System.out.println("网络：   "+jsonObject.toString());

                String code = jsonObject.getString("status");
                String messageString = jsonObject.getString("msg");
                String data = "";
                if(code.equals("0")){
                    data = "";
                }else{
                    data = jsonObject.getString("data");
                }
                returnMessgae(code, messageString, data);

            } else {
                System.out.println(httpResponse.getStatusLine().getStatusCode());
                returnMessgae("-1", "网络：服务器错误：HttpOutLineRentBatteryUploadInfoConfirm" + httpResponse.getStatusLine().getStatusCode(), "");
            }
        } catch (Exception e) {
            System.out.println(e.toString());
            returnMessgae("-1", "网络：json解析错误：HttpOutLineRentBatteryUploadInfoConfirm", "");

        }
    }

    private void returnMessgae(String code, String str, String data) {
        ifHttpOutLineRentBatteryUploadInfoConfirmListener.onHttpOutLineRentBatteryUploadInfoConfirmResult(code, str, data);
    }

}