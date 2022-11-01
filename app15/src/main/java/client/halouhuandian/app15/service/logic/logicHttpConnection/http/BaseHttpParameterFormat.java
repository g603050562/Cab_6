package client.halouhuandian.app15.service.logic.logicHttpConnection.http;

public class BaseHttpParameterFormat {

    private String name;
    private String data;

    public BaseHttpParameterFormat(String name, String data) {
        this.name = name;
        this.data = data;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
