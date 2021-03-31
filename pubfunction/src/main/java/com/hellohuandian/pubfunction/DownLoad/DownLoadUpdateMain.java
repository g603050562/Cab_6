package com.hellohuandian.pubfunction.DownLoad;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.webkit.MimeTypeMap;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class DownLoadUpdateMain {

    private Context activity;
    private String httpUrl;
    private String fileName = "updata.apk";

    private AsyncTask asyncTaskRateReturn;
    private int asyncTaskRateReturnCode = 0;
    private DownloadManager downloadManager;
    private long mTaskId = 0;
    private int isReboot = 0;

    private IFDownLoadUpdateMainLinstener ifDownLoadUpdateMainLinstener = null;


    public DownLoadUpdateMain(Context activity, String httpUrl) {
        this.activity = activity;
        this.httpUrl = httpUrl;
    }

    public DownLoadUpdateMain(Context activity, String httpUrl,String packageName, IFDownLoadUpdateMainLinstener ifDownLoadUpdateMainLinstener) {
        this.activity = activity;
        this.fileName = packageName;
        this.httpUrl = httpUrl;
        this.ifDownLoadUpdateMainLinstener = ifDownLoadUpdateMainLinstener;
        this.isReboot = 0;
    }

    public DownLoadUpdateMain(Context activity, String httpUrl,String packageName ,int isReboot, IFDownLoadUpdateMainLinstener ifDownLoadUpdateMainLinstener) {
        this.activity = activity;
        this.fileName = packageName;
        this.httpUrl = httpUrl;
        this.ifDownLoadUpdateMainLinstener = ifDownLoadUpdateMainLinstener;
        this.isReboot = isReboot;
    }

    //使用系统下载器下载
    public void downloadAPK() {

        String downloadPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + File.separator;
        File file = new File(downloadPath);
        deleteAllFile(file);

        downloadManager = (DownloadManager) activity.getSystemService(Context.DOWNLOAD_SERVICE);
        //创建下载任务
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(httpUrl));
        request.setAllowedOverRoaming(false);//漫游网络是否可以下载
        // 设置文件类型，可以在下载结束后自动打开该文件
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        String mimeString = mimeTypeMap.getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl(httpUrl));
        request.setMimeType(mimeString);
        //在通知栏中显示，默认就是显示的
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
        request.setVisibleInDownloadsUi(true);
        //sdcard的目录下的download文件夹，必须设置
        request.setDestinationInExternalPublicDir("/download/", fileName);
        //request.setDestinationInExternalFilesDir(),也可 以自己制定下载路径
        // 将下载请求加入下载队列
        downloadManager = (DownloadManager) activity.getSystemService(Context.DOWNLOAD_SERVICE);
        //加入下载队列后会给该任务返回一个long型的id，
        //通过该id可以取消任务，重启任务等等，看上面源码中框起来的方法
        mTaskId = downloadManager.enqueue(request);
        //注册广播接收者，监听下载状态

        asyncTaskRateReturn = new AsyncTask() {

            @Override
            protected Object doInBackground(Object[] objects) {

                int[] bytesAndStatus = new int[]{-1, -1, 0};
                DownloadManager.Query query = new DownloadManager.Query().setFilterById(mTaskId);
                while (asyncTaskRateReturnCode == 0) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Cursor c = null;
                    try {
                        c = downloadManager.query(query);
                        if (c != null && c.moveToFirst()) {
                            bytesAndStatus[0] = c.getInt(c.getColumnIndexOrThrow(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
                            bytesAndStatus[1] = c.getInt(c.getColumnIndexOrThrow(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
                            bytesAndStatus[2] = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS));
                        }
                    } finally {
                        if (c != null) {
                            c.close();
                        }
                    }

                    if(ifDownLoadUpdateMainLinstener!=null){
                        ifDownLoadUpdateMainLinstener.onDownLoadUpdateMainResult(bytesAndStatus);
                    }
                }
                return null;
            }
        };
        asyncTaskRateReturn.execute();

        activity.registerReceiver(receiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }

    private void deleteAllFile(File file) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (int i = 0; i < files.length; i++) {
                files[i].delete();
            }
        }
    }

    //广播接受者，接收下载状态
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //检查下载状态
            checkDownloadStatus();
            context.unregisterReceiver(this);
        }
    };

    //检查下载状态
    private void checkDownloadStatus() {
        asyncTaskRateReturnCode = 1;
        asyncTaskRateReturn.cancel(true);
        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterById(mTaskId);
        //筛选下载任务，传入任务ID，可变参数
        Cursor c = downloadManager.query(query);

        if (c.moveToFirst()) {
            int status = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS));
            switch (status) {
                case DownloadManager.STATUS_PAUSED:
                    System.out.println("网络：   >>>下载暂停");
                case DownloadManager.STATUS_PENDING:
                    System.out.println("网络：   >>>下载延迟");
                case DownloadManager.STATUS_RUNNING:
                    System.out.println("网络：   >>>正在下载");
                    break;
                case DownloadManager.STATUS_SUCCESSFUL:
                    System.out.println("网络：   >>>下载完成");
                    String downloadPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + File.separator + fileName;
                    execRootCmdSilent("pm install -r " + downloadPath);
                    if(isReboot == 1){
                        Thread thread = new Thread(){
                            @Override
                            public void run() {
                                super.run();

                                try {
                                    sleep(5 * 60  * 1000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }

                                execRootCmdSilent("reboot");
                            }
                        };
                        thread.start();
                    }
                    break;
                case DownloadManager.STATUS_FAILED:
                    System.out.println("网络：   >>>下载失败");
                    break;
            }
        }
    }

    private static boolean checkRootExecutable() {
        Process process = null;
        DataOutputStream os = null;
        try {
            process = Runtime.getRuntime().exec("su");
            os = new DataOutputStream(process.getOutputStream());
            os.writeBytes("exit\n");
            os.flush();
            int exitValue = process.waitFor();
            if (exitValue == 0) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            Log.d("*** DEBUG ***", "Unexpected error - Here is what I know: " + e.getMessage());
            return false;
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
                process.destroy();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public int execRootCmdSilent(String cmd) {
        int result = -1;
        DataOutputStream dos = null;

        System.out.println("root：   "+cmd);

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
