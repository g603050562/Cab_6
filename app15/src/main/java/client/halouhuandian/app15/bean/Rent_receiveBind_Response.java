package client.halouhuandian.app15.bean;

/**
 * Author:      Lee Yeung
 * Create Date: 2020/11/16
 * Description:
 */
public class Rent_receiveBind_Response {
    /**
     * status : 1
     * msg : 绑定成功！
     * data : {"door":4,"battery":"MFKKKGK12AMM0103","open":1}
     * errno : E0000
     * error :
     */

    private int status;
    private String msg;
    private DataBean data;
    private String errno;
    private String error;


    public int getStatus()
    {
        return status;
    }

    public void setStatus(int status)
    {
        this.status = status;
    }

    public String getMsg()
    {
        return msg;
    }

    public void setMsg(String msg)
    {
        this.msg = msg;
    }

    public DataBean getData()
    {
        return data;
    }

    public void setData(DataBean data)
    {
        this.data = data;
    }

    public String getErrno()
    {
        return errno;
    }

    public void setErrno(String errno)
    {
        this.errno = errno;
    }

    public String getError()
    {
        return error;
    }

    public void setError(String error)
    {
        this.error = error;
    }

    public static class DataBean
    {
        /**
         * door : 4
         * battery : MFKKKGK12AMM0103
         * open : 1
         */

        private int door;
        private String battery;
        private int open;

        public int getDoor()
        {
            return door;
        }

        public void setDoor(int door)
        {
            this.door = door;
        }

        public String getBattery()
        {
            return battery;
        }

        public void setBattery(String battery)
        {
            this.battery = battery;
        }

        public int getOpen()
        {
            return open;
        }

        public void setOpen(int open)
        {
            this.open = open;
        }
    }
}
