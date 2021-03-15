package com.hellohuandian.moviesupload;

import android.app.Activity;
import android.content.Context;
import android.os.Environment;
import android.os.storage.StorageManager;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;
import static android.content.Context.STORAGE_SERVICE;

/**
 * Created by Administrator on 2016/3/10 0010.
 */
public class MoviesCreateFile {

    private Activity context;
    public static String OutSideSd = "";

    public MoviesCreateFile(Activity context) {
        this.context = context;
        init();
    }

    private void init() {

        OutSideSd = getExternalFileDir(context);
        System.out.println("files：" + OutSideSd + "   外部存储数 - "+getExtSDCardPathList().size());
        System.out.println("files: "+ getExtSDCardPathList().toString());

        if (getExtSDCardPathList().size() > 1 && getExtSDCardPathList().get(1).equals("/mnt/external_sd")){
            File dir2 = new File(OutSideSd);
            System.out.println(dir2);
            if (!dir2.exists()) {
                try {
                    //在指定的文件夹中创建文件
                    dir2.mkdirs();
                    System.out.println("files：" + "测试文件创建成功！！");
                } catch (Exception e) {
                    System.out.println("files：" + e.toString());
                }
            } else {
                System.out.println("files："+"测试文件已经被创建！");
            }
        }
    }

    /**
     * 获取外置SD卡路径
     */
    public static List<String> getExtSDCardPathList() {
        List<String> paths = new ArrayList<String>();
        String extFileStatus = Environment.getExternalStorageState();
        File extFile = Environment.getExternalStorageDirectory();
        //首先判断一下外置SD卡的状态，处于挂载状态才能获取的到
        if (extFileStatus.equals(Environment.MEDIA_MOUNTED) && extFile.exists() && extFile.isDirectory() && extFile.canWrite()) {
            //外置SD卡的路径
            paths.add(extFile.getAbsolutePath());
        }
        try {
            Runtime runtime = Runtime.getRuntime();
            Process process = runtime.exec("mount");
            InputStream is = process.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String line = null;
            int mountPathIndex = 1;
            while ((line = br.readLine()) != null) {
                // format of sdcard file system: vfat/fuse
                if ((!line.contains("fat") && !line.contains("fuse") && !line
                        .contains("storage"))
                        || line.contains("secure")
                        || line.contains("asec")
                        || line.contains("firmware")
                        || line.contains("shell")
                        || line.contains("obb")
                        || line.contains("legacy") || line.contains("data")) {
                    continue;
                }
                String[] parts = line.split(" ");
                int length = parts.length;
                if (mountPathIndex >= length) {
                    continue;
                }
                String mountPath = parts[mountPathIndex];
                if (!mountPath.contains("/") || mountPath.contains("data")
                        || mountPath.contains("Data")) {
                    continue;
                }
                File mountRoot = new File(mountPath);
                if (!mountRoot.exists() || !mountRoot.isDirectory()
                        || !mountRoot.canWrite()) {
                    continue;
                }
                boolean equalsToPrimarySD = mountPath.equals(extFile
                        .getAbsolutePath());
                if (equalsToPrimarySD) {
                    continue;
                }
                //扩展存储卡即TF卡或者SD卡路径
                paths.add(mountPath);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return paths;
    }


    public static String getTFSDCardPath() {
        List<String> list = getExtSDCardPathList();
        if(list.size() > 1){
            return list.get(1);
        }else{
            return list.get(0);
        }
    }


    /**
     * 获取外置SD卡存储文件的绝对路径
     * Android 4.4以后
     *
     * @param context
     */
    public static String getExternalFileDir(Context context) {
        StringBuilder sb = new StringBuilder();
        File file = context.getExternalCacheDir();     //此句代码一定要，在内部存储空间创建对应的data目录，但不存储文件
        if (file.exists()) {
            sb.append(getTFSDCardPath().toString()).append("/Android/data/").append(context.getPackageName()).append("/cache").append(File.separator).toString();
        } else {
            sb.append(getTFSDCardPath().toString()).append("/Android/data/").append(context.getPackageName()).append("/cache").append(File.separator).toString();
        }
        return sb.toString();
    }


}
