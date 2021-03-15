package com.hellohuandian.app.httpclient;

import com.hellohuandian.pubfunction.Unit.LogUtil;
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
 * 离线换电 租电池 反馈
 */
public class HttpOutLineRentBattery extends Thread {

    private String bid;
    private String number;
    private String battery;
    private String door;
    private String rstatus;


    IFHttpOutLineRentBatteryUploadInfoConfirmListener ifHttpOutLineRentBatteryUploadInfoConfirmListener;

    /**
     * @param bid     租电池 长连接反馈的BID
     * @param number  电柜ID（例：04531）
     * @param battery 租赁电池的 电池ID
     * @param door    租赁电池的 舱门号
     */
    public HttpOutLineRentBattery(String bid, String number, String battery, String door, String rstatus) {
        this.bid = bid;
        this.number = number;
        this.battery = battery;
        this.door = door;
        this.rstatus = rstatus;
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
        list.add(new BasicNameValuePair("did", bid));
        list.add(new BasicNameValuePair("number", number));
        list.add(new BasicNameValuePair("battery", battery));
        list.add(new BasicNameValuePair("door", door));
        list.add(new BasicNameValuePair("rstatus", rstatus));

        try {
            HttpEntity entity = new UrlEncodedFormEntity(list, "utf-8");
            httpPost.setEntity(entity);
            HttpClient client = new DefaultHttpClient();
            HttpResponse httpResponse = client.execute(httpPost);
            if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                String result = EntityUtils.toString(httpResponse.getEntity());
                LogUtil.I("租电返回结果：" + result);

                JSONTokener jsonTokener = new JSONTokener(result);
                JSONObject jsonObject = (JSONObject) jsonTokener.nextValue();

                if (ifHttpOutLineRentBatteryUploadInfoConfirmListener != null) {
                    String code = jsonObject.getString("status");
                    String messageString = jsonObject.getString("msg");
                    ifHttpOutLineRentBatteryUploadInfoConfirmListener.onHttpOutLineRentBatteryUploadInfoConfirmResult(code, messageString, "");
                }

            } else {
                ifHttpOutLineRentBatteryUploadInfoConfirmListener.onHttpOutLineRentBatteryUploadInfoConfirmResult("0", "请求错误", httpResponse.getStatusLine().getStatusCode() + "");
            }
        } catch (Exception e) {
            e.printStackTrace();
            ifHttpOutLineRentBatteryUploadInfoConfirmListener.onHttpOutLineRentBatteryUploadInfoConfirmResult("0", "请求错误", e.toString());
        }
    }

    public void setIfHttpOutLineRentBatteryUploadInfoConfirmListener(IFHttpOutLineRentBatteryUploadInfoConfirmListener ifHttpOutLineRentBatteryUploadInfoConfirmListener) {
        this.ifHttpOutLineRentBatteryUploadInfoConfirmListener = ifHttpOutLineRentBatteryUploadInfoConfirmListener;
    }
}