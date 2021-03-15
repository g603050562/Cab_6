package client.halouhuandian.app15.pub.dcList;

import com.hellohuandian.app.httpclient.longLink.OpenLongLink;
import com.hellohuandian.pubfunction.Unit.LogUtil;

/**
 * Author:      Lee Yeung
 * Create Date: 2020/10/27
 * Description:
 */
public final class DcDataLink {
    private static final DcDataLink DC_DATA_LINK = new DcDataLink();
    private OpenLongLink.IFHttpOpenLongLinkLinstener ifHttpOpenLongLinkLinstener;

    public static DcDataLink getInstance() {
        return DC_DATA_LINK;
    }

    public void setIFHttpOpenLongLinkLinstener(OpenLongLink.IFHttpOpenLongLinkLinstener ifHttpOpenLongLinkLinstener) {
        this.ifHttpOpenLongLinkLinstener = ifHttpOpenLongLinkLinstener;
        LogUtil.I("DcDataLink"+ifHttpOpenLongLinkLinstener);
    }

    public void send(String data) {
        if (ifHttpOpenLongLinkLinstener != null)
        {
            ifHttpOpenLongLinkLinstener.onHttpReturnDataResult(data);
        }
    }
}

