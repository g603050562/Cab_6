package client.halouhuandian.app15.upgrade.dc.acdc;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.apps.ExtensionTaskUpgrade.categories.dc.DC_UpgradeProgram;

import java.util.ArrayList;
import java.util.List;

import client.halouhuandian.app15.R;

/**
 * Author:      Lee Yeung
 * Create Date: 2020/7/27
 * Description:
 */
public class AcdcAdapter extends BaseAdapter {

    private final List<AcdcUpgradeModel> acdcUpgradeModels = new ArrayList<>();
    private LayoutInflater layoutInflater;
    private List<ViewHolder> viewHolders = new ArrayList<>();

    public AcdcAdapter(Context context) {
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return acdcUpgradeModels.size();
    }

    public void initData(List<AcdcUpgradeModel> acdcUpgradeModels) {
        if (acdcUpgradeModels != null) {
            this.acdcUpgradeModels.clear();
            this.acdcUpgradeModels.addAll(acdcUpgradeModels);
            notifyDataSetChanged();
        }
    }

    public void notifyDataSetChanged(int address) {
        viewHolders.get(address - 0x51).update();
    }

    @Override
    public AcdcUpgradeModel getItem(int position) {
        return acdcUpgradeModels.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.item_process_layout, parent, false);
            vh = new ViewHolder(convertView);
            viewHolders.add(vh);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }

        vh.update(getItem(position));

        return convertView;
    }

    class ViewHolder {

        private AcdcUpgradeModel dcdcUpgradeModel;

        public ProgressBar pb_process;
        public TextView tv_number;
        public TextView tv_precent;
        public TextView tv_info;

        public ViewHolder(View convertView) {
            pb_process = convertView.findViewById(R.id.pb_process);
            tv_precent = convertView.findViewById(R.id.tv_precent);
            tv_number = convertView.findViewById(R.id.tv_number);
            tv_info = convertView.findViewById(R.id.tv_info);
        }

        public void update(AcdcUpgradeModel dcdcUpgradeModel) {
            if (dcdcUpgradeModel != null) {
                this.dcdcUpgradeModel = dcdcUpgradeModel;

                tv_number.setText((dcdcUpgradeModel.getAddress() - 0x50) + "号：");
                tv_info.setText(dcdcUpgradeModel.getStatusInfo());
                pb_process.setProgress((int) (dcdcUpgradeModel.getProcess() / (float) dcdcUpgradeModel.getTotal() * 100));
                if (dcdcUpgradeModel.getTotal() > 0) {
                    tv_precent.setText("%" + ((int) (dcdcUpgradeModel.getProcess() / (float) dcdcUpgradeModel.getTotal() * 100)));
                }

                switch (dcdcUpgradeModel.getStatus()) {
                    case DC_UpgradeProgram.DCUpgradeStatus.FAILED:
                        tv_info.setTextColor(Color.RED);
                        break;
                }
            }
        }

        public void update() {
            update(dcdcUpgradeModel);
        }
    }
}
