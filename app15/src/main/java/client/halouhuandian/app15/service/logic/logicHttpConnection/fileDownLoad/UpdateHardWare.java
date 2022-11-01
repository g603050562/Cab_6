package client.halouhuandian.app15.service.logic.logicHttpConnection.fileDownLoad;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.webkit.MimeTypeMap;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;

import client.halouhuandian.app15.pub.RootCmd;

/**
 * Created by apple on 2018/3/20.
 * 下载文件
 */

public class UpdateHardWare {

    public interface DownloadUpdateFileListener {
        void onDownloadUpdateFileReturn(UpdateHardWare content, String dataPath);
    }

    private Context context;
    //下载管理器
    private DownloadManager downloadManager;
    //下载进程id
    private long mTaskId = 999;
    //文件名称
    private String fileName = "";
    //下载路径
    private String url;
    //文件储存文件夹 - 默认download文件夹
    private String saveFile = "download";
    //文件存储绝对路径
    private String saveAbsolutelyPath = "";
    //文件需要移动的文件路径
    private String moveAbsolutelyPath = "";

    private DownloadUpdateFileListener downloadUpdateFileListener;

    //默认download下载文件
    public UpdateHardWare(Context context, String fileName, String url, DownloadUpdateFileListener downloadUpdateFileListener) {
        this.context = context;
        this.fileName = fileName;
        this.url = url;
        this.downloadUpdateFileListener = downloadUpdateFileListener;
        saveAbsolutelyPath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + saveFile + File.separator + fileName;
        deleteFile();
        downloadAPK();
    }

    //指定下载文件夹下载文件
    public UpdateHardWare(Context context, String fileName, String url, String saveFile, DownloadUpdateFileListener downloadUpdateFileListener) {
        this.context = context;
        this.fileName = fileName;
        this.url = url;
        this.saveFile = saveFile;
        if (saveFile.equals("")) {
            this.saveAbsolutelyPath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + fileName;
        } else {
            this.saveAbsolutelyPath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + saveFile + File.separator + fileName;
        }
        this.downloadUpdateFileListener = downloadUpdateFileListener;
        deleteFile();
        downloadAPK();
    }

    //指定下载文件夹下载文件 并且 下载文件夹后移动文件
    public UpdateHardWare(Context context, String fileName, String url, String saveFile, String moveAbsolutelyPath, DownloadUpdateFileListener downloadUpdateFileListener) {
        this.context = context;
        this.fileName = fileName;
        this.url = url;
        this.saveFile = saveFile;
        if (saveFile.equals("")) {
            this.saveAbsolutelyPath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + fileName;
        } else {
            this.saveAbsolutelyPath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + saveFile + File.separator + fileName;
        }
        this.moveAbsolutelyPath = moveAbsolutelyPath;
        this.downloadUpdateFileListener = downloadUpdateFileListener;
        deleteFile();
        downloadAPK();
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


    //使用系统下载器下载
    public void downloadAPK() {

        Calendar cd = Calendar.getInstance();
        mTaskId = cd.get(Calendar.HOUR_OF_DAY) + cd.get(Calendar.MINUTE) + cd.get(Calendar.SECOND);

        downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        //创建下载任务
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setAllowedOverRoaming(true);//漫游网络是否可以下载
        // 设置文件类型，可以在下载结束后自动打开该文件
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        String mimeString = mimeTypeMap.getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl(url));
        request.setMimeType(mimeString);
        //在通知栏中显示，默认就是显示的
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
        request.setVisibleInDownloadsUi(true);
        //sdcard的目录下的download文件夹，必须设置
        request.setDestinationInExternalPublicDir(saveFile, fileName);
        //request.setDestinationInExternalFilesDir(),也可 以自己制定下载路径
        // 将下载请求加入下载队列
        downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        //加入下载队列后会给该任务返回一个long型的id，
        //通过该id可以取消任务，重启任务等等，看上面源码中框起来的方法
        mTaskId = downloadManager.enqueue(request);
        //注册广播接收者，监听下载状态
        context.registerReceiver(receiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }

    //检查下载状态
    private void checkDownloadStatus() {
        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterById(mTaskId);
        //筛选下载任务，传入任务ID，可变参数
        Cursor c = downloadManager.query(query);
        if (c.moveToFirst()) {
            int status = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS));
            switch (status) {
                case DownloadManager.STATUS_PAUSED:
                    System.out.println(">>>下载暂停");
                case DownloadManager.STATUS_PENDING:
                    System.out.println(">>>下载延迟");
                case DownloadManager.STATUS_RUNNING:
                    System.out.println(">>>正在下载");
                case DownloadManager.STATUS_SUCCESSFUL:
                    System.out.println(">>>下载完成 - " + saveAbsolutelyPath + " - " + moveAbsolutelyPath);
                    if (!moveAbsolutelyPath.equals("")) {
                        moveFileByShell(saveAbsolutelyPath, moveAbsolutelyPath);
                        System.out.println(">>>下载完成 - 开始转移文件 " + saveAbsolutelyPath + " - " + moveAbsolutelyPath);
                    }
                    downloadUpdateFileListener.onDownloadUpdateFileReturn(this,saveAbsolutelyPath);
//                    downloadManager.remove(mTaskId);
                    break;
                case DownloadManager.STATUS_FAILED:
                    System.out.println(">>>下载失败");
                    break;
            }
        }
    }

    //删除文件
    private boolean deleteFile() {
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "Download";
        File file = new File(path);
        // 如果文件路径所对应的文件存在，并且是一个文件，则直接删除
        if (file.exists()) {
            File[] files = file.listFiles();
            for(int i = 0 ; i < files.length ; i ++){
                files[i].delete();
            }
            System.out.println("update - 删除文件成功");
            return true;
        } else {
            System.out.println("update - 删除文件失败");
            return false;
        }
    }

    //  内核升级apk路径 - /system/app/RKUpdateService/RKUpdateService.apk
    public void moveFileByShell(String oldPath$Name, String newPathName) {
        String shell = "cp " + oldPath$Name + " " + newPathName;
        try {
            File file = new File(newPathName);
            if (!file.exists()) {
                file.mkdir();
            }
            RootCmd.execRootCmdSilent(shell);
            System.out.println("download：   文件转移状态 - 成功");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("download：   文件转移状态 - 失败 - " + e);
        }
    }

    public void installApk(String downloadPath , boolean isReboot){
        execRootCmdSilent("pm install -r " + downloadPath + " && " + "reboot");
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
