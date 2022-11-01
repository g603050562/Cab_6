package client.halouhuandian.app15.config;

public class SystemConfig {

    //服务器参数
    private static String HELLO = "halouhuandian";
    private static String MIXIANG = "mixiang";
    //最大电池数
    private static int maxBattery = 9;
    //最大Ac数
    private static int macAcdc = 3;

    public enum serverEnum{
        hello,
        mixiang,
    }

    //更改服务器时 ：需要把urlmap里面的静态数据一起更改
    public static serverEnum getServer(String serverInfo){
        if(serverInfo.indexOf(HELLO)!=-1){
            return serverEnum.hello;
        }else if(serverInfo.indexOf(MIXIANG)!=-1){
            return serverEnum.mixiang;
        }else {
            return serverEnum.mixiang;
        }
    }

    public static void setMaxBattery(int mMaxBattery){
        if(mMaxBattery <= 9){
            macAcdc = 2;
        }else if(mMaxBattery > 9){
            macAcdc = 3;
        }
        maxBattery = mMaxBattery;
    }

    public static int getMaxBattery(){
        return maxBattery;
    }

    public static int getMaxAcdc(){
        return macAcdc;
    }
}
