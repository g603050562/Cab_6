package com.hellohuandian.pubfunction.BootupLoadUsbApk;

import android.util.Log;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;

public class LoadUsbApk {

    private String filePath;

    public LoadUsbApk(String filePath){
        this.filePath = filePath;
        start();
    }

    private void start(){
        File file = new File(filePath);
        if(file.exists()){
            System.out.println("files： 文件存在");
            execRootCmdSilent("pm install -r " + filePath);
        }else{
            System.out.println("files： 文件不存在");
        }
    }

    public int execRootCmdSilent(String cmd) {
        int result = -1;
        DataOutputStream dos = null;

        try {
            Process p = Runtime.getRuntime().exec("su");
            dos = new DataOutputStream(p.getOutputStream());

            Log.i("upload_system", cmd);
            dos.writeBytes(cmd + "\n");
            dos.flush();
            dos.writeBytes("exit\n");
            dos.flush();
            p.waitFor();
            result = p.exitValue();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (dos != null) {
                try {
                    dos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

}
