package client.halouhuandian.app15.view.customUi.activityDialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;
import client.halouhuandian.app15.R;
import client.halouhuandian.app15.hardWareConncetion.environmentPlate.fireControl.FireControlSwitcher;
import client.halouhuandian.app15.model.dao.sharedPreferences.CabInfoSp;
import client.halouhuandian.app15.model.dao.sharedPreferences.ForbiddenSp;

public class DialogAdminFireControl extends Dialog {

    public interface DialogAdminFanControlListener{
        void dataReturn();
    }

    @BindViews({R.id.cab_1, R.id.cab_2, R.id.cab_3, R.id.cab_4, R.id.cab_5, R.id.cab_6, R.id.cab_7, R.id.cab_8, R.id.cab_9})
    public List<CheckBox> cabs;

    @BindView(R.id.submit)
    public TextView submit;

    private Context context;

    private  DialogAdminFanControlListener dialogAdminFanControlListener;

    public DialogAdminFireControl(@NonNull @NotNull Context context) {
        super(context,R.style.fanControlDialog);
        this.context = context;
    }

    public DialogAdminFireControl(@NonNull @NotNull Context context , DialogAdminFanControlListener dialogAdminFanControlListener) {
        super(context,R.style.fanControlDialog);
        this.context = context;
        this.dialogAdminFanControlListener = dialogAdminFanControlListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_firecontrol_dialog);
        ButterKnife.bind(this);

        for(int i = 0 ; i < cabs.size() ; i++){
            final int index = i;
            CheckBox checkBox = cabs.get(i);
            int dcdcForbiddenState = ForbiddenSp.getInstance().getDcdcForbidden(i);
            if(dcdcForbiddenState == -1){
                checkBox.setChecked(true);
            }else{
                checkBox.setChecked(false);
            }
            checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    CheckBox itemCheckBox = (CheckBox) view;
                    boolean state = itemCheckBox.isChecked();
                    if(state){
                        itemCheckBox.setChecked(false);
                        ForbiddenSp.getInstance().setDcdcForbidden(index , 1);
                    }else{
                        itemCheckBox.setChecked(true);
                        ForbiddenSp.getInstance().setDcdcForbidden(index , -1);
                    }
                }
            });
        }
    }

    @OnClick(R.id.submit)
    public void onViewClickSubmit(){

        FireControlSwitcher fireControlSwitcher = new FireControlSwitcher();
        boolean isCloseAcdc = false;
        for(int i = 0 ; i < cabs.size() ; i++){
            int dcdcState = ForbiddenSp.getInstance().getDcdcForbidden(i);
            if(dcdcState == -1){
                isCloseAcdc = true;
                fireControlSwitcher.sendOpenCmd(i+1);
            }else{
                fireControlSwitcher.sendCloseCmd(i+1);
            }
        }
        if(isCloseAcdc){
            fireControlSwitcher.closeAcdc();
        }else{
            fireControlSwitcher.openAcdc();
        }

        this.dismiss();
    }


}
