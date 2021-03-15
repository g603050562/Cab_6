package com.hellohuandian.moviesUpload_3;

import android.app.Activity;
import android.hardware.Camera;
import android.widget.FrameLayout;

import com.hellohuandian.moviesupload.MoviesCreateFile;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MoviesUnit_3 {

    Activity activity;
    FrameLayout frameLayout;
    int moviesThreadCode = 0;

    public MoviesUnit_3(Activity activity, FrameLayout frameLayout) {
        this.activity = activity;
        this.frameLayout = frameLayout;
        new MoviesCreateFile(activity);
        CameraSurfaceView mCameraSurfaceView = new CameraSurfaceView(activity);
        frameLayout.addView(mCameraSurfaceView);
        Thread moviesThread = new Thread() {
            @Override
            public void run() {
                super.run();
                while (moviesThreadCode == 0) {

                    try {
                        sleep(1000);
                        Calendar calendar = Calendar.getInstance();
                        int second = calendar.get(Calendar.SECOND);
                        if (second == 0) {

                            /**
                             * 删除文件
                             */
                            String moviesFilePath = MoviesCreateFile.OutSideSd + "/MyCameraApp";
                            File file = new File(moviesFilePath);
                            File[] subFile = file.listFiles();

                            if (subFile == null) {
                                System.out.println("movies：   找不到文件夹 - return");
                                return;
                            }

                            String timeStamp_1 = new SimpleDateFormat("yyyyMMdd").format(new Date());
                            for (int iFileLength = 0; iFileLength < subFile.length; iFileLength++) {
                                //获取日子信息 比较相差几天
                                String filename_1 = subFile[iFileLength].getName();
                                Map<String, Long> diff_1 = dateDiff(filename_1, timeStamp_1, "yyyyMMdd");
                                Long diff_1_day = diff_1.get("day");
                                System.out.println("movies：   相差" + diff_1_day + "天");

                                //相差几天开始判断
                                if (diff_1_day >= 2) {

                                    String moviesFilePath_1 = MoviesCreateFile.OutSideSd + "/MyCameraApp" + File.separator + filename_1;
                                    File file_1 = new File(moviesFilePath_1);
                                    File[] subFile_1 = file_1.listFiles();
                                    if (subFile_1 == null) {
                                        return;
                                    }
                                    //如果日子里面还有 每个小时的时间 就每一分钟删除一个小时的视频
                                    if (subFile_1.length > 0) {
                                        String filename_2 = subFile_1[0].getName();
                                        String moviesFilePath_2 = MoviesCreateFile.OutSideSd + "/MyCameraApp" + File.separator + filename_1 + File.separator + filename_2;
                                        deleteDir(moviesFilePath_2);
                                        System.out.println("movies：" + "   正在删除" + timeStamp_1 + filename_2 + "文件夹");
                                        break;
                                    } else { //如果日子里面没有时间了 删掉这个文件夹
                                        deleteDir(moviesFilePath_1);
                                    }
                                }
                            }
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        System.out.println("movies：   ERROR - " + e.toString());
                    }
                }
            }
        };
        moviesThread.start();

    }

    public void onDestroy() {
        moviesThreadCode = 1;
    }














    public Map<String, Long> dateDiff(String startTime, String endTime, String format) {
        // 按照传入的格式生成一个simpledateformate对象
        SimpleDateFormat sd = new SimpleDateFormat(format);
        long nd = 1000 * 24 * 60 * 60;// 一天的毫秒数
        long nh = 1000 * 60 * 60;// 一小时的毫秒数
        long nm = 1000 * 60;// 一分钟的毫秒数
        long ns = 1000;// 一秒钟的毫秒数
        long diff;
        long day = 0;
        long hour = 0;
        long min = 0;
        long sec = 0;
        try {
            // 获得两个时间的毫秒时间差异
            diff = sd.parse(endTime).getTime() - sd.parse(startTime).getTime();
            day = diff / nd;// 计算差多少天
            hour = diff % nd / nh;// 计算差多少小时
            min = diff % nd % nh / nm;// 计算差多少分钟
            sec = diff % nd % nh % nm / ns;// 计算差多少秒
            // 输出结果
            System.out.println("时间相差：" + day + "天" + hour + "小时" + min + "分钟" + sec + "秒。");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Map<String, Long> map = new HashMap<>();
        map.put("day", day);
        map.put("hour", hour);
        map.put("min", min);
        map.put("sec", sec);
        return map;
    }

    public static void deleteDir(String path) {
        File dir = new File(path);
        deleteDirWihtFile(dir);
    }

    //删除文件及文件夹
    public static void deleteDirWihtFile(File dir) {
        if (dir == null || !dir.exists() || !dir.isDirectory())
            return;
        for (File file : dir.listFiles()) {
            if (file.isFile())
                file.delete(); // 删除所有文件
            else if (file.isDirectory())
                deleteDirWihtFile(file); // 递规的方式删除文件夹
        }
        dir.delete();// 删除目录本身
    }


    /**
     * Create a File for saving an image or video
     */
    private static String getOutputMediaFile() {

        File mediaStorageDir = new File(MoviesCreateFile.OutSideSd, "MyCameraApp");
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                System.out.println("movies：" + "创建目录MyCameraApp失败");
                return null;
            }
        }

        String timeStamp_1 = new SimpleDateFormat("yyyyMMdd").format(new Date());
        String timeStamp_2 = new SimpleDateFormat("HH").format(new Date());

        String path_1 = mediaStorageDir.getPath() + File.separator + timeStamp_1;
        String path_2 = path_1 + File.separator + timeStamp_2;

        File file_2 = new File(path_2);
        if (!file_2.exists()) {
            //创建文件夹
            file_2.mkdirs();
            System.out.println("movies：" + "创建目录一成功");
        } else {
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        String timeString = Calendar.getInstance().getTimeInMillis() + "";
        String msPath = file_2 + File.separator + "VIDS_" + timeString + "_" + timeStamp + ".mp4";


        return msPath;
    }


    /**
     * Create a File for saving an image or video
     */
    private static File getOutputMediaFile(int type) {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(MoviesCreateFile.OutSideSd, "MyCameraApp");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                System.out.println("movies：   创建目录MyCameraApp失败");
                return null;
            }
        }

        String timeStamp_1 = new SimpleDateFormat("yyyyMMdd").format(new Date());
        String timeStamp_2 = new SimpleDateFormat("HH").format(new Date());

        String path_1 = mediaStorageDir.getPath() + File.separator + timeStamp_1;
        String path_2 = path_1 + File.separator + timeStamp_2;

        File file_1 = new File(path_1);
        if (!file_1.exists()) {
            //创建文件夹
            file_1.mkdirs();
            System.out.println("movies：   创建目录一成功");
        } else {
        }
        File file_2 = new File(path_2);
        if (!file_2.exists()) {
            //创建文件夹
            file_2.mkdirs();
            System.out.println("movies：   创建目录一成功");
        } else {
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        String timeString = Calendar.getInstance().getTimeInMillis() + "";
        File mediaFile = new File(file_2 + File.separator + "VID_" + timeString + "_" + timeStamp + ".mp4");

        return mediaFile;
    }


    private int FindFrontCamera() {
        int cameraCount = 0;
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        cameraCount = Camera.getNumberOfCameras(); // get cameras number
        System.out.println("cameraCount = " + cameraCount);
        for (int camIdx = 0; camIdx < cameraCount; camIdx++) {
            Camera.getCameraInfo(camIdx, cameraInfo); // get camerainfo
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                // 代表摄像头的方位，目前有定义值两个分别为CAMERA_FACING_FRONT前置和CAMERA_FACING_BACK后置
                return camIdx;
            }
        }
        return -1;
    }

}
