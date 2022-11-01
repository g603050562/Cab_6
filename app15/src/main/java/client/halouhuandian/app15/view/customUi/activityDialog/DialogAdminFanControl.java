package client.halouhuandian.app15.view.customUi.activityDialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.jetbrains.annotations.NotNull;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import client.halouhuandian.app15.R;
import client.halouhuandian.app15.model.dao.sharedPreferences.CabInfoSp;

public class DialogAdminFanControl extends Dialog {

    public interface DialogAdminFanControlListener{
        void dataReturn();
    }


    @BindView(R.id.submit)
    public TextView submit;
    @BindView(R.id.box_1)
    public CheckBox box_1;
    @BindView(R.id.box_2)
    public CheckBox box_2;
    @BindView(R.id.box_3)
    public CheckBox box_3;
    @BindView(R.id.panelByGradient)
    public LinearLayout panelByGradient;
    @BindView(R.id.edit_1)
    public EditText edit_1;
    @BindView(R.id.edit_2)
    public EditText edit_2;

    private Context context;

    private  DialogAdminFanControlListener dialogAdminFanControlListener;

    public DialogAdminFanControl(@NonNull @NotNull Context context) {
        super(context,R.style.fanControlDialog);
        this.context = context;
    }

    public DialogAdminFanControl(@NonNull @NotNull Context context , DialogAdminFanControlListener dialogAdminFanControlListener) {
        super(context,R.style.fanControlDialog);
        this.context = context;
        this.dialogAdminFanControlListener = dialogAdminFanControlListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_fancontrol_dialog);
        ButterKnife.bind(this);

        int fanState = CabInfoSp.getInstance().getFanActivityMode();
        panelByGradient.setVisibility(View.GONE);
        edit_1.setText(CabInfoSp.getInstance().getFanThreshold_1() + "");
        edit_2.setText(CabInfoSp.getInstance().getFanThreshold_2() + "");
        if(fanState == 1){
            box_1.setChecked(true);
            box_2.setChecked(false);
            box_3.setChecked(false);
            panelByGradient.setVisibility(View.VISIBLE);
        }else if(fanState == -1){
            box_1.setChecked(false);
            box_2.setChecked(true);
            box_3.setChecked(false);
        }else if(fanState == -2){
            box_1.setChecked(false);
            box_2.setChecked(false);
            box_3.setChecked(true);
        }else if(fanState == -3){
            box_1.setChecked(false);
            box_2.setChecked(true);
            box_3.setChecked(true);
        }else if(fanState == -4){
            box_1.setChecked(false);
            box_2.setChecked(false);
            box_3.setChecked(false);
        }
    }

    @OnClick(R.id.submit)
    public void onViewClickSubmit(){
        if(box_1.isChecked()){
            CabInfoSp.getInstance().setFanActivityMode(1);
            CabInfoSp.getInstance().setFanThreshold_1(Integer.parseInt(edit_1.getText().toString()));
            CabInfoSp.getInstance().setFanThreshold_2(Integer.parseInt(edit_2.getText().toString()));
        }else{
            //全开
            if(box_2.isChecked() && box_3.isChecked()){
                CabInfoSp.getInstance().setFanActivityMode(-3);
            }
            //全关
            else if(!box_2.isChecked() && !box_3.isChecked()){
                CabInfoSp.getInstance().setFanActivityMode(-4);
            }
            //1开
            else if(box_2.isChecked() && !box_3.isChecked()){
                CabInfoSp.getInstance().setFanActivityMode(-1);
            }
            //2开
            else if(!box_2.isChecked() && box_3.isChecked()){
                CabInfoSp.getInstance().setFanActivityMode(-2);
            }

        }
        if(dialogAdminFanControlListener!=null){
            dialogAdminFanControlListener.dataReturn();
        }
        this.dismiss();
    }

    @OnClick(R.id.box_1)
    public void onViewClickBox_1(){
        if(!box_1.isChecked()){
            box_1.setChecked(false);
        }else{
            box_1.setChecked(true);
        }
        box_2.setChecked(false);
        box_3.setChecked(false);
        panelByGradient.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.box_2)
    public void onViewClickBox_2(){
        box_1.setChecked(false);
        if(!box_2.isChecked()){
            box_2.setChecked(false);
        }else{
            box_2.setChecked(true);
        }
        panelByGradient.setVisibility(View.GONE);
    }

    @OnClick(R.id.box_3)
    public void onViewClickBox_3(){
        box_1.setChecked(false);
        if(!box_3.isChecked()){
            box_3.setChecked(false);
        }else{
            box_3.setChecked(true);
        }
        panelByGradient.setVisibility(View.GONE);
    }

}
