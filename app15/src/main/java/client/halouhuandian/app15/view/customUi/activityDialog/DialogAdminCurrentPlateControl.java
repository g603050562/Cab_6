package client.halouhuandian.app15.view.customUi.activityDialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.jetbrains.annotations.NotNull;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import client.halouhuandian.app15.R;
import client.halouhuandian.app15.hardWareConncetion.environmentPlate.EnvironmentController;
import client.halouhuandian.app15.hardWareConncetion.environmentPlate.EnvironmentDataFormat;
import client.halouhuandian.app15.hardWareConncetion.environmentPlate.EnvironmentReturnDataFormat;
import client.halouhuandian.app15.model.dao.sharedPreferences.CabInfoSp;
import client.halouhuandian.app15.pub.BaseDataDistribution;

public class DialogAdminCurrentPlateControl extends Dialog {

    @BindView(R.id.exit)
    public TextView exit;
    @BindView(R.id.t_1)
    public TextView t_1;
    @BindView(R.id.t_2)
    public TextView t_2;
    @BindView(R.id.t_3)
    public TextView t_3;
    @BindView(R.id.t_4)
    public TextView t_4;
    @BindView(R.id.t_5)
    public TextView t_5;
    @BindView(R.id.t_6)
    public TextView t_6;
    @BindView(R.id.t_7)
    public TextView t_7;
    @BindView(R.id.t_8)
    public TextView t_8;

    @BindView(R.id.current_input)
    public EditText current_input;

    @BindView(R.id.current_submit)
    public TextView current_submit;

    private Context context;
    private Handler handler;

    private BaseDataDistribution.LogicListener environmentControllerListener;
    private EnvironmentDataFormat environmentDataFormat;

    public DialogAdminCurrentPlateControl(@NonNull @NotNull Context context) {
        super(context,R.style.fanControlDialog);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_currentplatecontrol_dialog);
        ButterKnife.bind(this);
        environmentDataFormat = EnvironmentController.getInstance().getEnvironmentDataFormat();
        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                setData();
            }
        };

        EnvironmentController.getInstance().addListener(environmentControllerListener =  new BaseDataDistribution.LogicListener() {
            @Override
            public void returnData(Object object) {
                EnvironmentReturnDataFormat environmentReturnDataFormat = (EnvironmentReturnDataFormat)object;
                if(environmentReturnDataFormat.environmentReturnDataFormatType == EnvironmentReturnDataFormat.EnvironmentReturnDataFormatType.environmentData){
                    environmentDataFormat = (EnvironmentDataFormat)environmentReturnDataFormat.getReturnData();
                    handler.sendMessage(new Message());
                }
            }
        });
        handler.sendMessage(new Message());
    }

    private void setData(){
        t_1.setText(environmentDataFormat.getRunningTime());
        t_2.setText(environmentDataFormat.getCurrentPlateVoltage()+"V");
        t_3.setText(environmentDataFormat.getCurrentPlateElectric()+"A");
        t_4.setText(environmentDataFormat.getCurrentPlateWarningInfo());
        if(environmentDataFormat.getCurrentPlateHardVersion() == -1){
            t_5.setText("环境板内置电流板");
        }else{
            t_5.setText("HV - "+environmentDataFormat.getCurrentPlateHardVersion()+"SV - "+environmentDataFormat.getCurrentPlateSoftVersion());
        }
        t_6.setText(CabInfoSp.getInstance().getCurrentThreshold()+"");
        if(CabInfoSp.getInstance().getCurrentPlateMode() == 0){
            t_7.setText("自动");
        }else{
            t_7.setText("手动");
        }
        double time = (double) CabInfoSp.getInstance().getPutterActivityTime() / 10;
        t_8.setText(time +"S");

        current_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                float inputCurrent = Float.parseFloat(current_input.getText().toString());
                CabInfoSp.getInstance().setCurrentThreshold(inputCurrent);
                EnvironmentController.getInstance().setCurrentPlateParamInit();
                setData();
            }
        });
    }


    @OnClick(R.id.exit)
    public void onViewClickExit(){
        this.dismiss();
    }

    @Override
    protected void onStop() {
        super.onStop();
        EnvironmentController.getInstance().deleteListener(environmentControllerListener);
    }
}
