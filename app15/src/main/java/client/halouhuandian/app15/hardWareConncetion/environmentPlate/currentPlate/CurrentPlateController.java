package client.halouhuandian.app15.hardWareConncetion.environmentPlate.currentPlate;

/**
 * 电流检测板控制类
 * 兼容环境板集成电流板版本
 */
public class CurrentPlateController {

    public interface CurrentPlateControllerListener {
        void returnData(CurrentPlateDataFormat currentPlateDataFormat);
    }

    //单例
    private static volatile CurrentPlateController currentPlateController;

    private CurrentPlateController() {
    }

    public static CurrentPlateController getInstance() {
        if (currentPlateController == null) {
            synchronized (CurrentPlateController.class) {
                if (currentPlateController == null) {
                    currentPlateController = new CurrentPlateController();
                }
            }
        }
        return currentPlateController;
    }

    //接口返回
    private CurrentPlateControllerListener currentPlateControllerListener;
    //数据整合解析类
    private CurrentPlateIntegration currentPlateIntegration;
    //电流板数据模型
    private CurrentPlateDataFormat mCurrentPlateDataFormat;
    //设置阈值参数 - 防止无效设置
    private float currentThreshold = -1;

    //初始化
    public void init(CurrentPlateControllerListener currentPlateControllerListener) {
        this.currentPlateControllerListener = currentPlateControllerListener;
        onStart();
    }

    private void onStart() {
        if (currentPlateIntegration == null) {
            currentPlateIntegration = new CurrentPlateIntegration(new CurrentPlateIntegration.CurrentPlateIntegrationListener() {
                @Override
                public void returnData(CurrentPlateDataFormat currentPlateDataFormat) {
                    mCurrentPlateDataFormat = currentPlateDataFormat;
                    if (currentPlateControllerListener != null) {
                        currentPlateControllerListener.returnData(currentPlateDataFormat);
                    }
                }
            });
        }
    }

//    //开放设置阈值 - 根据最低温度
//    public float setCurrentThresholdByTemperature(float temperature) {
//        float litVal = 3;
//        if (temperature >= 25) {
//            litVal = 4;
//        } else if (temperature >= 10) {
//            litVal = 4;
//            litVal += 1;
//        } else if (temperature >= -5) {
//            litVal = 5;
//            litVal += 1;
//        } else if (temperature >= -10) {
//            litVal = 6;
//            litVal += 1;
//        } else if (temperature >= -25) {
//            litVal = 7.5f;
//            litVal += 1;
//        } else if (temperature >= -40) {
//            litVal = 10;
//            litVal += 1;
//        }
//        if (currentThreshold != litVal) {
//            currentThreshold = litVal;
//            setCurrentPlateParam(litVal, 800, 1000);
//        }
//        return litVal;
//    }

    //返回数据模型
    public CurrentPlateDataFormat getCurrentPlateDataFormat() {
        return mCurrentPlateDataFormat;
    }
}
