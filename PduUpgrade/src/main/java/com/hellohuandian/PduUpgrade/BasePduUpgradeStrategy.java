package com.hellohuandian.PduUpgrade;

/**
 * Author:      Lee Yeung
 * Create Date: 2019-12-06
 * Description:
 */
public abstract class BasePduUpgradeStrategy {
    protected String filePath;
    protected final byte address;

    BasePduUpgradeStrategy(byte address) {
        this.address = address;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public abstract void upgrade();

    /**
     * CRC-16/MODBUS
     *
     * @param data
     * @param offset
     * @param len
     * @return
     */
    protected final short crc16(byte[] data, int offset, int len) {
        int crc = 0xFFFF;
        int j;
        for (int i = offset; i < len; i++) {
            crc = ((crc & 0xFF00) | (crc & 0x00FF) ^ (data[i] & 0xFF));
            for (j = 0; j < 8; j++, crc = ((crc & 0x0001) > 0) ? (crc >> 1) ^ 0xA001 : (crc >> 1)) ;
        }

        return (short) (crc & 0xFFFF);
    }
}
