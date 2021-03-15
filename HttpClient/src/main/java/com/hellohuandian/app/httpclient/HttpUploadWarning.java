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
 * 上传温度预警
 */
public class HttpUploadWarning extends Thread{

    private IFHttpUploadWarningListener ifHttpUploadWarningListener;
    private String door, cab_id;

    /**
     *
     * @param cab_id   电柜ID（例：04531）
     * @param door     温度异常舱门
     * @param ifHttpUploadWarningListener   接口返回
     */
    public HttpUploadWarning(String cab_id, String door , IFHttpUploadWarningListener ifHttpUploadWarningListener) {
        this.cab_id = cab_id;
        this.door = door;
        this.ifHttpUploadWarningListener = ifHttpUploadWarningListener;
    }

    public HttpUploadWarning(String cab_id, String door) {
        this.cab_id = cab_id;
        this.door = door;
    }

    @Override
    public void run() {
        super.run();
        String path = MyApplication.apc + "Cabinet/relayShortOut.html";
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

        try {
            HttpEntity entity = new UrlEncodedFormEntity(list, "utf-8");
            httpPost.setEntity(entity);
            HttpClient client = new DefaultHttpClient();
            HttpResponse httpResponse = client.execute(httpPost);
            if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                String result = EntityUtils.toString(httpResponse.getEntity());

                JSONTokener jsonTokener = new JSONTokener(result);
                JSONObject jsonObject = (JSONObject) jsonTokener.nextValue();

            }
        } catch (Exception e) {

        }
    }

    private void returnMessgae(String code, String str, String data) {
        ifHttpUploadWarningListener.onHttpUploadWarningResult(code, str, data);
    }

}
