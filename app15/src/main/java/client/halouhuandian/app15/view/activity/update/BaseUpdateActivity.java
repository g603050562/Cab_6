package client.halouhuandian.app15.view.activity.update;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import client.halouhuandian.app15.R;
import client.halouhuandian.app15.model.dao.sharedPreferences.CabInfoSp;
import client.halouhuandian.app15.pub.BaseDataDistribution;
import client.halouhuandian.app15.service.logic.logicChangeBatteries.ChangeBatteriesController;
import client.halouhuandian.app15.service.logic.logicHttpConnection.webSocket.WebSocketController;
import client.halouhuandian.app15.view.customUi.activityDialog.DialogUpdateShowInfo;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

public class BaseUpdateActivity extends Activity {

    protected Activity activity;
    protected BaseDataDistribution.LogicListener logicListener;

    //日志显示
    @BindView(R.id.log)
    public EditText logView;
    //进度条
    @BindView(R.id.p_bar)
    public ProgressBar p_bar;
    @BindView(R.id.p_bar_2)
    public ProgressBar p_bar_2;
    //左上角数据
    @BindView(R.id.cabid)
    public TextView cabId;
    @BindView(R.id.tel)
    public TextView tel;
    @BindView(R.id.time)
    public TextView time;
    //头顶title
    @BindView(R.id.title_1)
    public TextView title_1;
    //中间title
    @BindView(R.id.title_2)
    public TextView title_2;

    protected int door = -1;
    protected String path = "";
    protected String type = "";
    protected int maxTime = -1;

    //rxjava
    private Disposable disposable;
    //dialog
    private DialogUpdateShowInfo dialogUpdateShowInfo;

    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        baseInit();
    }

    private void baseInit() {
        this.activity = this;
        setContentView(R.layout.activity_updata_1080p);
        ButterKnife.bind(this);
        cabId.setText(CabInfoSp.getInstance().getCabinetNumber_XXXXX());
        tel.setText(CabInfoSp.getInstance().getTelNumber());
        dialogUpdateShowInfo = new DialogUpdateShowInfo(activity);

        //挂起干扰线程
        WebSocketController.getInstance().hangUp();
        ChangeBatteriesController.getInstance().hangUp();

        door = getIntent().getIntExtra("door",0);
        path = getIntent().getStringExtra("path");
        type = getIntent().getStringExtra("type");

        //时间保护线程 超时退出
        disposable = Observable.interval(1, TimeUnit.SECONDS)
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {

                        if(maxTime > 0){
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    time.setText(maxTime+" S");
                                }
                            });
                            maxTime = maxTime - 1;
                        }else{
                            finishUpdate();
                        }
                    }
                });
    }


    //dialog提示
    protected void showUpdateDialog(final String msg , final int time){
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(activity != null){
                    dialogUpdateShowInfo.show(msg, time);
                }
            }
        });
    }

    //更新日志
    protected void updateLog(final String log){
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
                String sub_log = logView.getText().toString();
                if (sub_log.length() > 350) {
                    sub_log = sub_log.substring(sub_log.length() - 350, sub_log.length());
                }
                logView.setText(sub_log + "\n" + df.format(new Date()) + " - " + log);
                logView.setSelection(logView.getText().length());
            }
        });
    }

    //更新进度条
    protected void updateBar(final long process , final long total){
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                float b = (float) process / total * 100;
                p_bar.setProgress((int) b);
            }
        });
    }
    //更新进度条
    protected void updateBar_2(final long process , final long total){
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                float b = (float) process / total * 100;
                p_bar_2.setProgress((int) b);
            }
        });
    }

    //结束升级后 舱门推杆归位
    protected void finishUpdate(){
        Observable.timer(10,TimeUnit.SECONDS).subscribe(new Consumer<Long>() {
            @Override
            public void accept(Long aLong) throws Exception {
                activity.finish();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dialogUpdateShowInfo.onDestory();
        //取消线程挂起
        WebSocketController.getInstance().hangUpCancel();
        ChangeBatteriesController.getInstance().hangUpCancel();
    }
}
