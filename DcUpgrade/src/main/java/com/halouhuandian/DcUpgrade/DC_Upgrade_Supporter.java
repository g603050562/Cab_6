package com.halouhuandian.DcUpgrade;

import android.support.v4.util.Consumer;
import android.text.TextUtils;

import com.halouhuandian.DcUpgrade.callback.DC_StatusCallBack;
import com.halouhuandian.DcUpgrade.canExtension.CanDeviceIoActionImpl;
import com.halouhuandian.DcUpgrade.canExtension.DeviceIoAction;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * Author:      Lee Yeung
 * Create Date: 2019-12-20
 * Description: DC模块升级支持类，维持单个或者多个策略在单线程中的顺序执行。
 */
public final class DC_Upgrade_Supporter implements DeviceIoAction {
    private HashMap<String, byte[]> upgradeDataMap = new HashMap<>();
    private LinkedList<DC_UpgradeStrategy> dc_upgradeStrategies = new LinkedList<>();

    private volatile boolean isRun = true;

    private DC_StatusCallBack dc_statusCallBack;
    private DC_StatusCallBack mDC_StatusCallBack = new DC_StatusCallBack() {
        @Override
        public void onStatusCall(byte address, byte status, String info, long process, long total) {
            if (dc_statusCallBack != null) {
                dc_statusCallBack.onStatusCall(address, status, info, process, total);
            }
        }
    };

    private Consumer<byte[]> consumer;
    private final Consumer<byte[]> writeConsumer = new Consumer<byte[]>() {
        @Override
        public void accept(byte[] bytes) {
            if (consumer != null) {
                consumer.accept(bytes);
            }
        }
    };

    private final CanDeviceIoActionImpl canDeviceIoAction;

    private DC_Upgrade_Supporter() {
        canDeviceIoAction = new CanDeviceIoActionImpl(this);
    }

    private void run() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (isRun) {
                    DC_UpgradeStrategy dc_upgradeStrategy = dc_upgradeStrategies.poll();
                    if (dc_upgradeStrategy != null) {
                        if (cacheUpgradeFile(dc_upgradeStrategy.getUpgradeFilePath())) {
                            dc_upgradeStrategy.execute_can(canDeviceIoAction, upgradeDataMap.get(dc_upgradeStrategy.getUpgradeFilePath().toLowerCase()));
                        } else {
                            if (mDC_StatusCallBack != null) {
                                // TODO: 2019-12-24 文件路径存在问题
                            }
                        }
                    } else {
                        isRun = false;
                    }
                }
            }
        }).start();
    }

    private boolean cacheUpgradeFile(String upgradeFilePath) {
        if (!TextUtils.isEmpty(upgradeFilePath) && upgradeFilePath.trim().length() > 0) {
            if (!upgradeDataMap.containsKey(upgradeFilePath.trim())) {
                cacheData(upgradeFilePath.trim());
            }
        }
        return upgradeDataMap.containsKey(upgradeFilePath.trim());
    }

    private void cacheData(String filePath) {
        File file = new File(filePath);
        if (!(file != null && file.exists())) {
            return;
        }

        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        if (inputStream == null) {
            return;
        }

        int len = 0;
        try {
            len = inputStream.available();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (len <= 0) {
            return;
        }

        final byte[] DATA = new byte[len];
        try {
            inputStream.read(DATA);
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        upgradeDataMap.put(filePath, DATA);
    }

    public void setWriteConsumer(Consumer<byte[]> consumer) {
        this.consumer = consumer;
    }

    public void setDc_statusCallBack(DC_StatusCallBack dc_statusCallBack) {
        this.dc_statusCallBack = dc_statusCallBack;
    }

    public void addUpgradeTask(DC_UpgradeStrategy dc_upgradeStrategy) {
        if (dc_upgradeStrategy != null) {
            dc_upgradeStrategy.setDc_statusCallBack(mDC_StatusCallBack);
            dc_upgradeStrategies.add(dc_upgradeStrategy);
        }
    }

    public void startUpgrade() {
        if (!isRun) {
            synchronized (this) {
                if (!isRun) {
                    isRun = true;
                    run();
                }
            }
        }
    }

    @Override
    public void write(byte[] data) throws IOException {
        writeConsumer.accept(data);
    }

    public void onRead(byte[] bytes) {
        canDeviceIoAction.parseDispatch(bytes);
    }
}
