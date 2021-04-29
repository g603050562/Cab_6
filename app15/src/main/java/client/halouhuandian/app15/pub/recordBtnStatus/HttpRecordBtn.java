package client.halouhuandian.app15.pub.recordBtnStatus;

import android.support.v4.util.Consumer;

import com.hellohuandian.app.httpclient.MyApplication;
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
 * Author:      Lee Yeung
 * Create Date: 2020/10/26
 * Description:
 */
public class HttpRecordBtn extends Thread {

    private String cab_id;
    private String info;
    Consumer<String> ifHttpGetTelLinstener;

    /**
     * @param cab_id                电柜编号（例：04531）
     * @param ifHttpGetTelLinstener 接口
     */
    public HttpRecordBtn(String cab_id,String info, Consumer<String> ifHttpGetTelLinstener) {
        this.cab_id = cab_id;
        this.ifHttpGetTelLinstener = ifHttpGetTelLinstener;
    }

    @Override
    public void run() {
        super.run();
        String path = MyApplication.apc + "Errors/cabinet.html";
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
        list.add(new BasicNameValuePair("number", cab_id));
        list.add(new BasicNameValuePair("erros", info));
        list.add(new BasicNameValuePair("type", "20"));
        try {
            HttpEntity entity = new UrlEncodedFormEntity(list, "utf-8");
            httpPost.setEntity(entity);
            HttpClient client = new DefaultHttpClient();
            HttpResponse httpResponse = client.execute(httpPost);
            if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                String result = EntityUtils.toString(httpResponse.getEntity());

                returnMessgae(result);

            } else {
            }
        } catch (Exception e) {
        }
    }

    private void returnMessgae(String data) {
        if (ifHttpGetTelLinstener != null)
            ifHttpGetTelLinstener.accept( data);
    }
}
