package client.halouhuandian.app15.pub.dcList;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import client.halouhuandian.app15.R;

/**
 * Author:      Lee Yeung
 * Create Date: 2020/10/27
 * Description:
 */
public class DcDownloadDialog extends Dialog {
    private Gson gson = new Gson();
    private ListView listView;
    private DataAdapter dataAdapter;

    public DcDownloadDialog(@NonNull Context context) {
        this(context, R.style.AlertDialogStyle);
    }

    public DcDownloadDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        init();
    }

    private void init() {
        setContentView(R.layout.admin_download_dialog);
        listView = findViewById(R.id.listview);
        dataAdapter = new DataAdapter();
        listView.setAdapter(dataAdapter);
    }

    public void update(List<DcList> dcListList) {
        dataAdapter.update(dcListList);
    }

    class DataAdapter extends BaseAdapter {
        private List<DcList> dcListList = new ArrayList<>();

        public void update(List<DcList> dcListList) {
            this.dcListList.clear();
            if (dcListList != null) {
                this.dcListList.addAll(dcListList);
                notifyDataSetChanged();
            }
        }

        @Override
        public int getCount() {
            return dcListList != null ? dcListList.size() : 0;
        }

        @Override
        public DcList getItem(int position) {
            return dcListList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final ViewHolder viewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.admin_download_dialog_item, null, false);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            viewHolder.update(getItem(position));

            return convertView;
        }

        class ViewHolder implements View.OnClickListener {
            private DcList dcList;
            private TextView tv_info;

            public ViewHolder(View view) {
                view.setOnClickListener(this);
                tv_info = view.findViewById(R.id.tv_info);
            }

            public void update(DcList dcList) {
                if (dcList != null) {
                    this.dcList = dcList;
                    if (dcList instanceof AcdcList.DataBean) {
                        updateAcdc((AcdcList.DataBean) dcList);
                    } else if (dcList instanceof DcdcList.DataBean) {
                        updateDcdc((DcdcList.DataBean) dcList);
                    }
                }
            }

            public void updateAcdc(AcdcList.DataBean dataBean) {
                tv_info.setText("名称：" + dataBean.getName()
                        + "\n"
                        + "版本：" + dataBean.getVersion()
                        + "\n"
                        + "创建：" + dataBean.getCreate_time()
                        + "\n"
                        + "更新：" + dataBean.getUpdate_time()
                );
            }

            public void updateDcdc(DcdcList.DataBean dataBean) {
                tv_info.setText("名称：" + dataBean.getName()
                        + "\n"
                        + "版本：" + dataBean.getVersion()
                        + "\n"
                        + "创建：" + dataBean.getCreate_time()
                        + "\n"
                        + "更新：" + dataBean.getUpdate_time()
                );
            }

            @Override
            public void onClick(View v) {
                if (dcList instanceof AcdcList.DataBean) {
                    AcdcList.DataBean dataBean = (AcdcList.DataBean) dcList;
                    String data = gson.toJson(new DcMessage("upgradeAcdcAll", dataBean.getUrl(), dataBean.getName(), dataBean.getFname(), dataBean.getVersion()));
                    send(data);
                    dismiss();
                } else if (dcList instanceof DcdcList.DataBean) {
                    DcdcList.DataBean dataBean = (DcdcList.DataBean) dcList;
                    String data = gson.toJson(new DcMessage("upgradeDcdcAll", dataBean.getUrl(), dataBean.getName(), dataBean.getFname(), dataBean.getVersion()));
                    send(data);
                    dismiss();
                }
            }

            private void send(String data) {
                DcDataLink.getInstance().send(data);
            }
        }
    }
}
