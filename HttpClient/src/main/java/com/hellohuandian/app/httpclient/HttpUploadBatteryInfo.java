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
 * 上传电柜信息日志
 */
public class HttpUploadBatteryInfo extends Thread {

    private String cab_id;
    private String upload_str;

    private IFHttpUploadBatteryInfoListener ifHttpUploadBatteryInfoListener;


    /**
     *
     * @param cab_id   电柜ID（例：04531）
     * @param upload_str   数据信息 json格式
     * @param ifHttpUploadBatteryInfoListener   接口返回
     */
    public HttpUploadBatteryInfo(String cab_id, String upload_str, IFHttpUploadBatteryInfoListener ifHttpUploadBatteryInfoListener) {
        this.upload_str = upload_str;
        this.cab_id = cab_id;
        this.ifHttpUploadBatteryInfoListener = ifHttpUploadBatteryInfoListener;
    }

    @Override
    public void run() {
        super.run();
        String path = MyApplication.log + "Log/logs.html";
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
        list.add(new BasicNameValuePair("jsons", upload_str));

        try {
            HttpEntity entity = new UrlEncodedFormEntity(list, "utf-8");
            httpPost.setEntity(entity);
            HttpClient client = new DefaultHttpClient();
            HttpResponse httpResponse = client.execute(httpPost);
            if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                String result = EntityUtils.toString(httpResponse.getEntity());

                JSONTokener jsonTokener = new JSONTokener(result);
                JSONObject jsonObject = (JSONObject) jsonTokener.nextValue();

                System.out.println("网络：   日志 - "+jsonObject);

                String code = jsonObject.getString("status");
                String messageString = jsonObject.getString("msg");
                String data = "";
                if(code.equals("0")){
                    data = "";
                }else{
                    data = "";
                }
                returnMessgae(code, messageString, data);
            } else {
                System.out.println(httpResponse.getStatusLine().getStatusCode());
                returnMessgae("-1", "网络：服务器错误：HttpAdminReturnUploadInfo" + httpResponse.getStatusLine().getStatusCode(), "");
            }
        } catch (Exception e) {
            System.out.println(e.toString());
            returnMessgae("-1", "网络：json解析错误：HttpAdminReturnUploadInfo", "");
        }
    }


    private void returnMessgae(String code, String str, String data) {
        ifHttpUploadBatteryInfoListener.onHttpUploadBatteryInfoResult(code, str, data);
    }
}