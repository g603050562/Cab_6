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
 * 获取后台400电话
 */
public class HttpBaiDu extends Thread {

    IFHttpBaiduLinstener ifHttpBaiduLinstener;

    public interface IFHttpBaiduLinstener{
        void onHttpOnBaiDuResult(String data);
    }

    public HttpBaiDu(IFHttpBaiduLinstener ifHttpBaiduLinstener) {
        this.ifHttpBaiduLinstener = ifHttpBaiduLinstener;
    }

    @Override
    public void run() {
        super.run();
        String path = "http://www.baidu.com";
        HttpPost httpPost = new HttpPost(path);
        httpPost.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 10000);
        httpPost.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 10000);
        httpPost.setHeader("Content-type", "application/json");
        List<NameValuePair> list = new ArrayList<NameValuePair>();
        try {
            HttpEntity entity = new UrlEncodedFormEntity(list, "utf-8");
            httpPost.setEntity(entity);
            HttpClient client = new DefaultHttpClient();
            HttpResponse httpResponse = client.execute(httpPost);
            if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                String result = EntityUtils.toString(httpResponse.getEntity());
                returnMessgae("百度 - "+result);

            } else {
                returnMessgae("百度 - "+httpResponse.getStatusLine().getStatusCode()+"");
            }
        } catch (Exception e) {
            returnMessgae( "百度 - "+"json解析错误：HttpGetTel"+e.toString());
        }
    }

    private void returnMessgae(String data) {
        ifHttpBaiduLinstener.onHttpOnBaiDuResult(data);
    }
}
