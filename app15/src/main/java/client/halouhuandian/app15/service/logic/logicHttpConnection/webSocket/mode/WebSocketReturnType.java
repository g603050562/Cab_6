package client.halouhuandian.app15.service.logic.logicHttpConnection.webSocket.mode;

public enum WebSocketReturnType {
    //绑定成功
    bindSuccess,
    //长连接状态
    onlineType,
    //打开admin后台
    openAdmin,
    //关闭admin后台
    closeAdmin,
    //显示提示框
    showDialog,
    //网络信息返回
    internetInfo,
    //网络升级单个电池
    updateSingleBattery,
    //网络升级单个dcdc
    updateSingleDcdc,
    //网络升级所有dcdc
    updateAllDcdc,
    //网络升级单个acdc
    updateSingleAcdc,
    //网络升级所有acdc
    updateAllAcdc,
    //网络升级所有环境板
    updateEnvironment,
}
