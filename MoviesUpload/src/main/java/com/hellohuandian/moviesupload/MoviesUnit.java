package com.hellohuandian.moviesupload;

import android.app.Activity;
import android.content.Context;
import android.hardware.Camera;
import android.media.MediaRecorder;
import android.os.Build;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.FrameLayout;

import com.hellohuandian.pubfunction.Unit.LogUtil;
import com.hw.videoprocessor.VideoProcessor;
import com.hw.videoprocessor.util.VideoProgressListener;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MoviesUnit {


    private Activity activity;

    private static final String TAG = "xieyaoyan";
    private Camera mCamera;
    //录像信息输出承载
    private CameraPreview mPreview;
    //摄像头配置文件
    private MediaRecorder mMediaRecorder;

    private boolean isRecording = false;

    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;
    private int mId=-1;
    private String mPath;
    private static String msPath;

    private int moviesThreadState = 0;


    public MoviesUnit(Activity activity) {
        this.activity = activity;
        new MoviesCreateFile(activity);
    }

    public void moviesStart() {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            return;
        }

        Thread thread = new Thread() {
            @Override
            public void run() {
                super.run();

                if (mId == -1) {
                    System.out.println("movies：打开摄像头异常");
                    return;
                }

                MStart();

                while (moviesThreadState == 0) {

                    try {
                        sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    Calendar calendar = Calendar.getInstance();
                    int second = calendar.get(Calendar.SECOND);

                    if (second == 0) {
                        String moviesFilePath = MoviesCreateFile.OutSideSd + "/MyCameraApp";
                        File file = new File(moviesFilePath);
                        File[] subFile = file.listFiles();

                        if (subFile == null) {
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

                        try {

                            MStop();

                            File file_success = new File(mPath);
                            System.out.println("movies：   录制文件保存在: " + mPath + "   文件大小 - " + file_success.length());

                            final String old_path = mPath;
                            final String rar_path = msPath;

                            if (file_success.length() > 0 && old_path != null && rar_path != null) {

                                Thread thread_rar = new Thread() {
                                    @Override
                                    public void run() {
                                        super.run();

                                        try {

                                            VideoProcessor.processor(activity).input(old_path).output(rar_path).outWidth(320).outHeight(240).frameRate(3).progressListener(new VideoProgressListener() {
                                                @Override
                                                public void onProgress(float progress) {
                                                    System.out.println("movies：   压缩进度 - " + progress);
                                                    if (progress == 1) {
                                                        File old_file = new File(old_path);
                                                        File rar_file = new File(rar_path);
                                                        old_file.delete();
                                                        System.out.println("movies：   压缩后大小 - " + rar_file.length());
                                                        System.out.println("movies：   压缩成功 - 删除源文件");
                                                    }
                                                }
                                            }).process();
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }

                                    }
                                };
                                thread_rar.start();


                            }

                            sleep(500);

                            MStart();

                            sleep(500);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }
            }
        };
        thread.start();
    }

    /**
     * 彻底清除movies资源
     */
    public void moviesStop() {
        System.out.println("movies：   停止摄像    isRecording - " + isRecording);
        moviesThreadState = 1;
        if (mPreview != null) {
            mPreview.removeCallBack();
        }
        releaseMediaRecorder();       // if you are using MediaRecorder, release it first
        if (mCamera != null) {
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            releaseCamera();              // release the camera immediately on pause event
            mCamera = null;
        }
    }


    public void MStart() {

        if (!isRecording) {
            if (prepareVideoRecorder(mCamera)) {
                mMediaRecorder.start();
                System.out.println("movies：" + "   将要录制文件 - 保存在:" + mPath);
                isRecording = true;
            } else {
                releaseMediaRecorder();
            }
        }
    }

    public void MStop() {
        if (isRecording) {
            if (mMediaRecorder != null && mCamera != null) {
                mMediaRecorder.stop();  // stop the recording
                releaseMediaRecorder(); // release the MediaRecorder object
                mCamera.lock();         // take camera access back from MediaRecorder
                isRecording = false;
            }
        }
    }


    public boolean onResume(FrameLayout preview) {

        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
        }
        mId = FindFrontCamera();
        System.out.println("movies：   " + "mId - " + mId);
        try {
            mCamera = Camera.open(mId);
        } catch (Exception e) {
            System.out.println("movies：" + "摄像头正在使用" + e.toString());
            return false;
        }
        mPreview = new CameraPreview(activity, mCamera, mId);
        preview.addView(mPreview);
        return true;
    }

    public void onPause() {
        if (mPreview != null) {
            mPreview.removeCallBack();
            releaseMediaRecorder();       // if you are using MediaRecorder, release it first
            releaseCamera();              // release the camera immediately on pause event
        }
    }

    private void releaseMediaRecorder() {
        if (mMediaRecorder != null) {
            try{
                mMediaRecorder.reset();   // clear recorder configuration
                mMediaRecorder.release(); // release the recorder object
                mMediaRecorder = null;
                mCamera.lock();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    private void releaseCamera() {
        if (mCamera != null) {
            mCamera.release();        // release the camera for other applications
            mCamera = null;
        }
    }

    private int FindFrontCamera() {
        int cameraCount = 0;
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        cameraCount = Camera.getNumberOfCameras(); // get cameras number
        System.out.println("cameraCount = " + cameraCount);
        for (int camIdx = 0; camIdx < cameraCount; camIdx++) {
            Camera.getCameraInfo(camIdx, cameraInfo); // get camerainfo
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                LogUtil.I("摄像头："+camIdx);
                // 代表摄像头的方位，目前有定义值两个分别为CAMERA_FACING_FRONT前置和CAMERA_FACING_BACK后置
                return camIdx;
            }
        }
        return -1;
    }

    private boolean prepareVideoRecorder(Camera mCamera) {

        if (mCamera == null) {
            System.out.println("movies：mCamera为空");
            return false;
        }

        mMediaRecorder = new MediaRecorder();
        // Step 1: Unlock and set camera to MediaRecorder
        mCamera.unlock();
        mMediaRecorder.setCamera(mCamera);

        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
        mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mMediaRecorder.setVideoFrameRate(15);
        mMediaRecorder.setVideoSize(640, 480);
        mMediaRecorder.setVideoEncodingBitRate(1 * 640 * 480); //较为清晰，且文件大小为3.26M(30秒)
        mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);  //H263的貌似有点不清晰

        // Step 4: Set output file
        if (getOutputMediaFile(MEDIA_TYPE_VIDEO) == null) {
            return false;
        } else {
            mPath = getOutputMediaFile(MEDIA_TYPE_VIDEO).toString();
        }
        System.out.println("path = " + mPath);
        mMediaRecorder.setOutputFile(mPath);

        // Step 5: Set the preview output
        Surface surface = mPreview.getHolder().getSurface();
        mMediaRecorder.setPreviewDisplay(surface);

        // Step 6: Prepare configured MediaRecorder
        try {
            mMediaRecorder.prepare();
        } catch (IllegalStateException e) {
            Log.d(TAG, "IllegalStateException preparing MediaRecorder: " + e.getMessage());
            releaseMediaRecorder();
            return false;
        } catch (IOException e) {
            Log.d(TAG, "IOException preparing MediaRecorder: " + e.getMessage());
            releaseMediaRecorder();
            return false;
        }

        return true;

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
                System.out.println("movies：" + "创建目录MyCameraApp失败");
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
            System.out.println("movies：" + "创建目录一成功");
        } else {
        }
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
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(file_2 + File.separator + "IMG_" + timeString + "_" + timeStamp + ".jpg");
        } else if (type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(file_2 + File.separator + "VID_" + timeString + "_" + timeStamp + ".mp4");
            msPath = file_2 + File.separator + "VIDS_" + timeString + "_" + timeStamp + ".mp4";

        } else {
            return null;
        }

        return mediaFile;
    }


    public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
        private SurfaceHolder mHolder;
        private Camera mCamera;
        private int mId;

        public CameraPreview(Context context, Camera camera, int id) {
            super(context);
            mCamera = camera;
            mId = id;

            // Install a SurfaceHolder.Callback so we get notified when the
            // underlying surface is created and destroyed.
            mHolder = getHolder();
            mHolder.addCallback(this);
            // deprecated setting, but required on Android versions prior to 3.0
            mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }

        public void surfaceCreated(SurfaceHolder holder) {
            // The Surface has been created, now tell the mCamera where to draw the preview.

            System.out.println("movies：   surfaceCreated()");

            try {
                mHolder.removeCallback(this);
                mCamera.setPreviewCallback(null);
                mCamera.setPreviewDisplay(holder);
                mCamera.startPreview();
            } catch (Exception e) {
                Log.d(TAG, "Error setting mCamera preview: " + e.getMessage());
            }
        }

        public void surfaceDestroyed(SurfaceHolder holder) {

            System.out.println("movies：   surfaceDestroyed()");

            // empty. Take care of releasing the Camera preview in your activity.
            // Surface will be destroyed when we return, so stop the preview.
            if (mCamera != null) {
                // Call stopPreview() to stop updating the preview surface.
                mHolder.removeCallback(this);
                mCamera.setPreviewCallback(null);
                mCamera.stopPreview();
            }
        }

        public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {

            System.out.println("movies：   surfaceChanged()");
            if (mHolder.getSurface() == null) {// preview surface does not exist
                return;
            }
            try {
                mCamera.stopPreview();
            } catch (Exception e) {
                // ignore: tried to stop a non-existent preview
            }
            try {
                mCamera.setPreviewDisplay(mHolder);
                mCamera.startPreview();
            } catch (Exception e) {
                Log.d(TAG, "Error starting mCamera preview: " + e.getMessage());
            }
        }

        public void removeCallBack() {
            mHolder.removeCallback(this);
        }
        public void restartSurfaceview(){
            surfaceDestroyed(mHolder);
            surfaceCreated(mHolder);
        }


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

    public int getmId() {
        return FindFrontCamera();
    }
}