package com.hellohuandian.moviesupload;

import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 录像线程
 *
 * @author bcaiw
 */
public class RecordThread extends Thread {

    private MediaRecorder mediarecorder;// 录制视频的类
    private SurfaceHolder surfaceHolder;
    private long recordTime;
    private SurfaceView surfaceview;// 显示视频的控件
    private Camera mCamera;

    public RecordThread(long recordTime, SurfaceView surfaceview, SurfaceHolder surfaceHolder) {
        this.recordTime = recordTime;
        this.surfaceview = surfaceview;
        this.surfaceHolder = surfaceHolder;
    }

    @Override
    public void run() {

        /**
         * 开始录像
         */
        startRecord();

        /**
         * 启动定时器，到规定时间recordTime后执行停止录像任务
         */
        Timer timer = new Timer();
        timer.schedule(new TimerThread(), recordTime);
    }


    /**
     * 获取摄像头实例对象
     *
     * @return
     */
    public Camera getCameraInstance() {
        Camera c = null;
        try {
            c = Camera.open();
        } catch (Exception e) {
            // 打开摄像头错误
            Log.i("info", "打开摄像头错误");
        }
        return c;
    }

    private int FindFrontCamera() {
        int cameraCount = 0;
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        cameraCount = Camera.getNumberOfCameras();
        for (int camIdx = 0; camIdx < cameraCount; camIdx++) {
            Camera.getCameraInfo(camIdx, cameraInfo);
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                return camIdx;
            }
        }
        return -1;
    }

    /**
     * 开始录像
     */
    public void startRecord() {
        mediarecorder = new MediaRecorder();// 创建mediarecorder对象


        int mId = FindFrontCamera();
        try {
            mCamera = Camera.open(mId);
        } catch (Exception e) {
            System.out.println("Camera：摄像头正在使用"+e.toString());
            return;
        }
        System.out.println("Camera： 找到摄像头" + mCamera);
        // 解锁camera
        mCamera.unlock();
        mediarecorder.setCamera(mCamera);

        // 设置录制视频源为Camera(相机)
        mediarecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
        mediarecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

        // 设置录制文件质量，格式，分辨率之类，这个全部包括了
        mediarecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_LOW));
        // 设置视频文件输出的路径
        mediarecorder.setPreviewDisplay(surfaceHolder.getSurface());

        MoviesAppFile appFile = new MoviesAppFile();
        if (appFile.moviesCount() < 4) {
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
            mediarecorder.setOutputFile(appFile.SELFDIR + df.format(new Date()) + ".mp4");
        } else {
            List<String> fileList = appFile.getAllFileName();
            String fileName = fileList.get(0);
            appFile.deleteFile(fileName);
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
            mediarecorder.setOutputFile(appFile.SELFDIR + df.format(new Date()) + ".mp4");
        }

        try {
            // 准备录制
            mediarecorder.prepare();
            // 开始录制
            mediarecorder.start();
        } catch (IllegalStateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * 停止录制
     */
    public void stopRecord() {
        if (mediarecorder != null) {
            // 停止录制
            mediarecorder.stop();
            // 释放资源
            mediarecorder.release();
            mediarecorder = null;

            if (mCamera != null) {
                mCamera.release();
                mCamera = null;
            }
        }
        run();
    }

    /**
     * 定时器
     *
     * @author bcaiw
     */
    class TimerThread extends TimerTask {

        /**
         * 停止录像
         */
        @Override
        public void run() {
            stopRecord();
            this.cancel();
        }
    }
}