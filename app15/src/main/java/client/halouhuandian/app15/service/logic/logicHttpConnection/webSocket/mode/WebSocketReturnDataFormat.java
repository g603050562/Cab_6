package client.halouhuandian.app15.service.logic.logicHttpConnection.webSocket.mode;

public class WebSocketReturnDataFormat {

    private WebSocketReturnType webSocketReturnType;
    private Object object;

    public WebSocketReturnDataFormat(WebSocketReturnType webSocketReturnType, Object object) {
        this.webSocketReturnType = webSocketReturnType;
        this.object = object;
    }

    public WebSocketReturnType getWebSocketReturnType() {
        return webSocketReturnType;
    }

    public void setWebSocketReturnType(WebSocketReturnType webSocketReturnType) {
        this.webSocketReturnType = webSocketReturnType;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }
}
