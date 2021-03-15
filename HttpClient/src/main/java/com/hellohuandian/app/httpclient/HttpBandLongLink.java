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

import java.util.ArrayList;
import java.util.List;

/**
 * 绑定长连接
 *
 */
public class HttpBandLongLink extends Thread {

    private String link_id, cab_id, downloadCabId;
    private IFHttpBandLongLinkLinstener ifHttpBandLongLinkLinstener;

    /**
     *
     * @param link_id  长连接下发ID（例：04531）
     * @param cab_id   柜子4G卡4600开头账号
     * @param downloadCabId  长连接号（例：ac11722426be000c7067）
     * @param ifHttpBandLongLinkLinstener   接口
     */
    public HttpBandLongLink(String link_id, String cab_id, String downloadCabId, IFHttpBandLongLinkLinstener ifHttpBandLongLinkLinstener) {
        this.downloadCabId = downloadCabId;
        this.link_id = link_id;
        this.cab_id = cab_id;
        this.ifHttpBandLongLinkLinstener = ifHttpBandLongLinkLinstener;
    }

    @Override
    public void run() {
        super.run();
        String path = MyApplication.bindlonglink;
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
        list.add(new BasicNameValuePair("client_id", link_id));
        list.add(new BasicNameValuePair("cabid", downloadCabId));

        try {
            HttpEntity entity = new UrlEncodedFormEntity(list, "utf-8");
            httpPost.setEntity(entity);
            HttpClient client = new DefaultHttpClient();
            HttpResponse httpResponse = client.execute(httpPost);
            if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                String result = EntityUtils.toString(httpResponse.getEntity());

                System.out.println("网络：   "+result);

                returnMessage("1", "绑定成功", "");
            } else {
                returnMessage("-1", "服务器错误：HttpBandLongLink - " + httpResponse.getStatusLine().getStatusCode(), "");
            }
        } catch (Exception e) {
            System.out.println(e.toString());
            returnMessage("-1", "JSON错误：HttpBandLongLink - " + e.toString(), "");
        }
    }

    private void returnMessage(String code, String str, String data) {
        if (ifHttpBandLongLinkLinstener != null)
            ifHttpBandLongLinkLinstener.onHttpBandLongLinkResult(code, str, data);
    }
}
