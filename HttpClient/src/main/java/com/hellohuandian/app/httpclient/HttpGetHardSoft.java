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
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.List;

/**
 * 服务器返回控制板升级程序列表
 */
public class HttpGetHardSoft extends Thread {

    private IFHttpGetHardSoftLinstener ifHttpGetHardSoftLinstener;

    public HttpGetHardSoft(IFHttpGetHardSoftLinstener ifHttpGetHardSoftLinstener) {
        this.ifHttpGetHardSoftLinstener = ifHttpGetHardSoftLinstener;
    }

    @Override
    public void run() {
        super.run();
        String path = MyApplication.apc + "ApsHard/soft.html";
        HttpPost httpPost = new HttpPost(path);
        httpPost.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 10000);
        httpPost.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 10000);

        Header header = new Header() {
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
        httpPost.addHeader(header);

        List<NameValuePair> list = new ArrayList<NameValuePair>();
        try {


            HttpEntity entity = new UrlEncodedFormEntity(list, "utf-8");
            httpPost.setEntity(entity);

            HttpClient client = new DefaultHttpClient();
            HttpResponse httpResponse = client.execute(httpPost);

            if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                String result = EntityUtils.toString(httpResponse.getEntity());

                JSONTokener jsonTokener = new JSONTokener(result);
                JSONObject jsonObject = (JSONObject) jsonTokener.nextValue();

                System.out.println(jsonObject);

                String code = jsonObject.getString("status");
                String messageString = jsonObject.getString("msg");

                if (code.equals("1")) {
                    if (jsonObject.has("data")) {
                        String data = jsonObject.getString("data");
                        returnMessgae(code, messageString, data);
                    } else {
                        returnMessgae("-1", messageString,"");
                    }
                } else {
                    returnMessgae("-1", messageString,"");
                }
            } else {
                System.out.println(httpResponse.getStatusLine().getStatusCode());
                returnMessgae("-1","服务器错误：HttpGetHardSoft   代码：" + httpResponse.getStatusLine().getStatusCode(),"");
            }
        } catch (Exception e) {
            System.out.println(e.toString());
            returnMessgae("-1","json解析错误：HttpInputOldBar","");
        }
    }


    private void returnMessgae(String code, String str, String data) {
        ifHttpGetHardSoftLinstener.onHttpGetHardSoftResult(code, str, data);
    }
}
