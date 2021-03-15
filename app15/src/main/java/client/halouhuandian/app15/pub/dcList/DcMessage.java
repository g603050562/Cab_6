package client.halouhuandian.app15.pub.dcList;

/**
 * Author:      Lee Yeung
 * Create Date: 2020/10/27
 * Description:
 */
public class DcMessage {
    private String type;
    private String url;
    private String name;
    private String fname;
    private String ver;
    private boolean md5str;

    public DcMessage(String type, String url, String name, String fname, String ver) {
        this.type = type;
        this.url = url;
        this.name = name;
        this.fname = fname;
        this.ver = ver;
    }

}
