package com.hellohuandian.PduUpgrade;

import android.support.v4.util.Consumer;

/**
 * Author:      Lee Yeung
 * Create Date: 2019-12-06
 * Description:
 */
public final class CanReader {
    private Consumer<byte[]> consumer;

    public void onRead(byte[] bytes) {
        if (consumer != null) {
            consumer.accept(bytes);
        }
    }

    public void setConsumer(Consumer<byte[]> consumer) {
        this.consumer = consumer;
    }
}
