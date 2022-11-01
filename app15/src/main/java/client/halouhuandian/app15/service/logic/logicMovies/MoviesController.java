package client.halouhuandian.app15.service.logic.logicMovies;

import android.app.Activity;
import android.content.Context;
import android.hardware.Camera;
import android.media.MediaRecorder;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import client.halouhuandian.app15.model.dao.fileSave.LocalLog;
import client.halouhuandian.app15.pub.util.UtilFilesDirectory;

public class MoviesController {

    //上下文
    private Context context;
    //摄像头
    private Camera mCamera;
    //录像信息输出承载
    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;
    private SurfaceHolder.Callback callback;

    //录制视频配置文件
    private MediaRecorder mMediaRecorder;
    //录制文件参数
    private boolean isRecordingMovies = false;
    //输出文件路径
    private String outPutString = "";

    //录制循环视频
    private Thread recordingThread = null;
    //录制循环视频参数
    private boolean recordingThreadCode = true;

    public MoviesController(Activity activity) {
        recordingThreadCode = true;
        this.context = context;
    }

    public void init() {
        //如果摄像头不为空 释放资源
        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
        }
        try {
            mCamera = android.hardware.Camera.open(findFrontCamera());
        } catch (Exception e) {
            System.out.println("movies：" + "摄像头正在使用" + e.toString());
        }
        //删除老旧视频
        deleteLongTimeMovies(UtilFilesDirectory.EXTERNAL_MOVIES_DIR);
        //初始化摄像头和承载界面
        setSurfaceViewHolder();
        //初始化录制视频
        openThread();
    }


    //onResume
    public void onResume(SurfaceView surfaceView) {
        this.surfaceView = surfaceView;
        init();
    }

    //onPause
    public void onPause() {
        stopRecordVideo();
        mCamera.setPreviewCallback(null);
        mCamera.stopPreview();
        mCamera.release();
        mCamera = null;
        surfaceHolder.removeCallback(callback);
        callback = null;
        surfaceHolder = null;
        surfaceView = null;
    }

    //onDestroy
    public void onDestroy() {
        recordingThreadCode = false;
        stopRecordVideo();
        onPause();
    }


    /**
     * 拍摄相关
     */

    //获得前置摄像头
    private int findFrontCamera() {
        int cameraCount = 0;
        android.hardware.Camera.CameraInfo cameraInfo = new android.hardware.Camera.CameraInfo();
        cameraCount = android.hardware.Camera.getNumberOfCameras(); // get cameras number
        for (int camIdx = 0; camIdx < cameraCount; camIdx++) {
            android.hardware.Camera.getCameraInfo(camIdx, cameraInfo); // get camerainfo
            if (cameraInfo.facing == android.hardware.Camera.CameraInfo.CAMERA_FACING_FRONT) {
                // 代表摄像头的方位，目前有定义值两个分别为CAMERA_FACING_FRONT前置和CAMERA_FACING_BACK后置
                return camIdx;
            }
        }
        return -1;
    }

    //承载和摄像头的关系
    private void setSurfaceViewHolder() {
        surfaceHolder = surfaceView.getHolder();
        callback = new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                // TODO Auto-generated method stub
                Log.i("camera", "surface destroyed");
                surfaceHolder = holder;
                try {
                    mCamera.setPreviewDisplay(surfaceHolder);
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    Log.e("camera", "preview failed.");
                    e.printStackTrace();
                }
                mCamera.startPreview();
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int i, int i1, int i2) {
                // TODO Auto-generated method stub
                Log.i("camera", "surface changed.");
                surfaceHolder = holder;
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                // TODO Auto-generated method stub
                Log.i("camera", "surface destroyed.");
                surfaceHolder = null;
            }
        };
        surfaceHolder.addCallback(callback);
    }


    /**
     * 录制相关
     */

    private boolean prepareVideoRecorder(Camera mCamera) {
        //摄像头为空返回
        if (mCamera == null) {
            System.out.println("movies：mCamera为空");
            return false;
        }

        //配置录像参数
        mMediaRecorder = new MediaRecorder();
        mCamera.unlock();
        mMediaRecorder.setCamera(mCamera);

        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
        mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mMediaRecorder.setVideoFrameRate(15);
        mMediaRecorder.setVideoSize(640, 480);
        mMediaRecorder.setVideoEncodingBitRate(1 * 640 * 480); //较为清晰，且文件大小为3.26M(30秒)
        mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);  //H263的貌似有点不清晰

        //获得文件输出路径
        if (getOutputMediaFile() == null) {
            return false;
        } else {
            outPutString = getOutputMediaFile().toString();
        }
        System.out.println("movies - path - " + outPutString);
        mMediaRecorder.setOutputFile(outPutString);

        //准备就绪
        try {
            mMediaRecorder.prepare();
        } catch (Exception e) {
            System.out.println("movies - error - " + outPutString);
            releaseMediaRecorder();
            return false;
        }
        return true;
    }


    //循环线程 1分钟为分界线开启或者关闭录制
    private void openThread() {
        if (recordingThread == null) {
            recordingThread = new Thread() {
                @Override
                public void run() {
                    super.run();

                    boolean isNear = false;
                    Calendar isNearCalendar = Calendar.getInstance();
                    int isNearSecond = isNearCalendar.get(Calendar.SECOND);
                    if(isNearSecond > 50){
                        isNear = true;
                    }

                    while (recordingThreadCode == true) {
                        try {
                            //整点开始
                            sleep(1000);
                            Calendar calendar = Calendar.getInstance();
                            int second = calendar.get(Calendar.SECOND);
                            if (second == 0) {
                                //获取目标路径
                                String moviesFilePath = UtilFilesDirectory.EXTERNAL_MOVIES_DIR;
                                deleteLongTimeMovies(moviesFilePath);
                                //停止录制再开始录制
                                if(mCamera!=null){
                                    if(isNear){
                                        isNear = false;
                                    }else{
                                        stopRecordVideo();
                                        sleep(500);
                                        startRecordVideo();
                                    }
                                }
                            }
                        } catch (Exception e) {
                            System.out.println(e.toString());
                            e.printStackTrace();
                        }
                    }
                }
            };
            recordingThread.start();
        }
    }

    private void deleteLongTimeMovies(String moviesFilePath){
        System.out.println("movies：   正在删除老旧视频");
        try {
            File file = new File(moviesFilePath);
            File[] subFile = file.listFiles();
            //如果文件为空 就退出吧
            if (subFile == null) {
                return;
            }
            //删除时间太久的视频
            String timeStamp_1 = new SimpleDateFormat("yyyyMMdd").format(new Date());
            for (int iFileLength = 0; iFileLength < subFile.length; iFileLength++) {

                //获取日子信息 比较相差几天
                String filename_1 = subFile[iFileLength].getName();
                Map<String, Long> diff = UtilFilesDirectory.dateDiff(filename_1, timeStamp_1, "yyyyMMdd");

                Long diff_day = diff.get("day");
                Long diff_hour = diff.get("hour");
                long allHour = diff_day * 24 + diff_hour;
                System.out.println("movies：   相差" + allHour + "小时");

                //相差几天开始判断
                if (allHour > 12) {
                    String moviesFilePath_1 = moviesFilePath + File.separator + filename_1;
                    File file_1 = new File(moviesFilePath_1);
                    File[] subFile_1 = file_1.listFiles();
                    if (subFile_1 == null) {
                        return;
                    }
                    //如果日子里面还有 每个小时的时间 就每一分钟删除一个小时的视频
                    if (subFile_1.length > 0) {
                        String filename_2 = subFile_1[0].getName();
                        String moviesFilePath_2 = moviesFilePath + File.separator + filename_1 + File.separator + filename_2;
                        UtilFilesDirectory.deleteDir(moviesFilePath_2);
                        System.out.println("movies：" + "   正在删除" + timeStamp_1 + filename_2 + "文件夹");
                        break;
                    } else { //如果日子里面没有时间了 删掉这个文件夹
                        UtilFilesDirectory.deleteDir(moviesFilePath_1);
                    }
                }
            }
        }catch (Exception e){
            LocalLog.getInstance().writeLog("视频文件 - 删除失败");
        }
    }


    //开始录制视频
    private void startRecordVideo() {
        if (!isRecordingMovies) {
            if (prepareVideoRecorder(mCamera)) {
                System.out.println("movies - " + mMediaRecorder.toString());
                mMediaRecorder.start();
                System.out.println("movies - 将要录制文件 - 保存在:" + outPutString);
                isRecordingMovies = true;
            } else {
                releaseMediaRecorder();
            }
        }
    }

    //停止录制视频
    private void stopRecordVideo() {
        if (isRecordingMovies) {
            if (mMediaRecorder != null && mCamera != null) {
                mMediaRecorder.stop();
                releaseMediaRecorder();
                mCamera.lock();
                isRecordingMovies = false;
            }
        }
    }

    //获得录制文件输出地址
    private static String getOutputMediaFile() {

        File mediaStorageDir = new File(UtilFilesDirectory.EXTERNAL_MOVIES_DIR);
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                System.out.println("movies - error - 创建目录MyCameraApp失败");
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
            System.out.println("movies - success - 创建目录一成功");
        }
        File file_2 = new File(path_2);
        if (!file_2.exists()) {
            //创建文件夹
            file_2.mkdirs();
            System.out.println("movies - success - 创建目录一成功");
        }
        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        String timeString = Calendar.getInstance().getTimeInMillis() + "";
        return file_2 + File.separator + "VID_" + timeString + "_" + timeStamp + ".mp4";
    }


    private void releaseMediaRecorder() {
        if (mMediaRecorder != null) {
            mMediaRecorder.reset();
            mMediaRecorder.release();
            mMediaRecorder = null;
            mCamera.lock();
        }
    }
}
