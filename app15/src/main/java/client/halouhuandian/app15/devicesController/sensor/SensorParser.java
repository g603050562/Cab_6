package client.halouhuandian.app15.devicesController.sensor;

/**
 * Author:      Lee Yeung
 * Create Date: 2020/3/27
 * Description: 传感器数据解析
 */
class SensorParser {
    private final TemperatureTable temperatureTable = new TemperatureTable();
    private SensorDataBean sensorDataBean;

    public void parse(SensorDataBean sensorDataBean, byte[] bytes) {
        this.sensorDataBean = sensorDataBean;
        if (sensorDataBean != null && bytes != null) {
            parseHardwareVersion(bytes);
            parseSoftwareVersion(bytes);
            parseWaterLevel(bytes);
            parseTemperature1(bytes);
            parseTemperature2(bytes);
            parseTemperature3(bytes);
            parseSmoke(bytes);
            parseAmmeterVoltage(bytes);
            parseAmmeterElectric(bytes);
            parseAmmeterPower(bytes);
            parseAmmeterTotalPower(bytes);
            parseAmmeterPwerCoefficient(bytes);
            parseAmmeterCarbonEmission(bytes);
            parseAmmeterTemperature(bytes);
            parseAmmeterFrequency(bytes);
            parseAmmeterTotalPower_485(bytes);
            parseWaterLevel2(bytes);

            parseOpenDoorButton(bytes);
            parseOpenDoorButton2(bytes);
            parserAirFan1Status(bytes);
            parserAirFan2Status(bytes);
            parserDeviceCurrent(bytes);
            parserDeviceVoltage(bytes);
            parserCurrentBoardThreshold(bytes);
            parserCurrentBoardTransfiniteTime(bytes);
            parserCurrentBoardRecoverTime(bytes);
            parserRunningStatus(bytes);
        }
    }

    private void parseHardwareVersion(byte[] bytes) {
        if (bytes.length > 1) {
            sensorDataBean.setHardwareVersion((short) (bytes[0] & 0xFF));
        }
    }

    private void parseSoftwareVersion(byte[] bytes) {
        if (bytes.length > 3) {
            sensorDataBean.setSoftwareVersion((short) (bytes[3] & 0xFF));
        }
    }

    private void parseWaterLevel(byte[] bytes) {
        if (bytes.length > 7) {
            int value = 0;
            for (int i = 7, j = 0; i >= 4; value |= ((bytes[i] & 0xFF) << (j * 8)), i--, j++) ;
            sensorDataBean.setWaterLevel(((float) value) / 10000);
        }
    }

    private void parseTemperature1(byte[] bytes) {
        if (bytes.length > 48) {
            int value = 0;
            for (int i = 48, j = 0; i >= 45; value |= ((bytes[i] & 0xFF) << (j * 8)), i--, j++) ;
            sensorDataBean.setTemperature1(temperatureTable.queryTemperature(value));
        }
    }

    private void parseTemperature3(byte[] bytes) {
        if (bytes.length > 11) {
            int value = 0;
            for (int i = 11, j = 0; i >= 8; value |= ((bytes[i] & 0xFF) << (j * 8)), i--, j++) ;
            sensorDataBean.setTemperature3(temperatureTable.queryTemperature(value));
        }
    }

    private void parseTemperature2(byte[] bytes) {
        if (bytes.length > 15) {
            int value = 0;
            for (int i = 15, j = 0; i >= 12; value |= ((bytes[i] & 0xFF) << (j * 8)), i--, j++) ;
            sensorDataBean.setTemperature2(temperatureTable.queryTemperature(value));
        }
    }

    private void parseSmoke(byte[] bytes) {
        if (bytes.length > 19) {
            int value = 0;
            for (int i = 19, j = 0; i >= 16; value |= ((bytes[i] & 0xFF) << (j * 8)), i--, j++) ;
            sensorDataBean.setSmoke(((float) value) / 10000);
        }
    }

    private void parseAmmeterVoltage(byte[] bytes) {
        if (bytes.length > 21) {
            int value = 0;
            for (int i = 21, j = 0; i >= 20; value |= ((bytes[i] & 0xFF) << (j * 8)), i--, j++) ;
            sensorDataBean.setAmmeterVoltage(((float) value) / 100);
        }
    }

    private void parseAmmeterElectric(byte[] bytes) {
        if (bytes.length > 23) {
            int value = 0;
            for (int i = 23, j = 0; i >= 22; value |= ((bytes[i] & 0xFF) << (j * 8)), i--, j++) ;
            sensorDataBean.setAmmeterElectric(((float) value) / 100);
        }
    }

    private void parseAmmeterPower(byte[] bytes) {
        if (bytes.length > 25) {
            int value = 0;
            for (int i = 25, j = 0; i >= 24; value |= ((bytes[i] & 0xFF) << (j * 8)), i--, j++) ;
            sensorDataBean.setAmmeterPower(value);
        }
    }

    private void parseAmmeterTotalPower(byte[] bytes) {
        if (bytes.length > 29) {
            int value = 0;
            for (int i = 29, j = 0; i >= 26; value |= ((bytes[i] & 0xFF) << (j * 8)), i--, j++) ;
            sensorDataBean.setAmmeterTotalPower(((float) value) / 3200);
        }
    }

    private void parseAmmeterPwerCoefficient(byte[] bytes) {
        if (bytes.length > 31) {
            int value = 0;
            for (int i = 31, j = 0; i >= 30; value |= ((bytes[i] & 0xFF) << (j * 8)), i--, j++) ;
            sensorDataBean.setAmmeterPwerCoefficient(((float) value) / 1000);
        }
    }

    private void parseAmmeterCarbonEmission(byte[] bytes) {
        if (bytes.length > 35) {
            int value = 0;
            for (int i = 35, j = 0; i >= 32; value |= ((bytes[i] & 0xFF) << (j * 8)), i--, j++) ;
            sensorDataBean.setAmmeterCarbonEmission(((float) value) / 1000);
        }
    }

    private void parseAmmeterTemperature(byte[] bytes) {
//        if (bytes.length > 37) {
//            int value = 0;
//            for (int i = 36, j = 0; i >= 37; value |= ((bytes[i] & 0xFF) << (j * 8)), i--, j++) ;
//            sensorDataBean.setAmmeterTemperature(((float) value) / 1000);
//        }
    }

    private void parseAmmeterFrequency(byte[] bytes) {
        if (bytes.length > 39) {
            int value = 0;
            for (int i = 39, j = 0; i >= 38; value |= ((bytes[i] & 0xFF) << (j * 8)), i--, j++) ;
            sensorDataBean.setAmmeterFrequency(((float) value) / 100);
        }
    }

    /**
     * 解析开门按键按钮
     *
     * @param bytes
     */
    private void parseOpenDoorButton(byte[] bytes) {
        byte statusValue = (byte) (bytes[40] & 0b0010_0000);
        sensorDataBean.setOpenDoorButtonStatus((byte) (statusValue >> 5));
    }

    /**
     * 解析开门按键按钮2
     *
     * @param bytes
     */
    private void parseOpenDoorButton2(byte[] bytes) {
        byte statusValue = (byte) (bytes[40] & 0b0100_0000);
        sensorDataBean.setOpenDoorButtonStatus2((byte) (statusValue >> 6));
    }

    /**
     * 解析风扇1开关状态
     *
     * @param bytes
     */
    private void parserAirFan1Status(byte[] bytes) {
        byte fan1status = (byte) (bytes[40] & 0b0000_0010);
        sensorDataBean.setAirFan1Status((byte) (fan1status >> 1));
    }

    /**
     * 解析风扇2开关状态
     *
     * @param bytes
     */
    private void parserAirFan2Status(byte[] bytes) {
        byte fan2status = (byte) (bytes[40] & 0b0000_0100);
        sensorDataBean.setAirFan2Status((byte) (fan2status >> 2));
    }

    /**
     * 解析注意：低位在前，高位在后
     *
     * @param bytes
     */
    private void parseAmmeterTotalPower_485(byte[] bytes) {
        if (bytes.length > 44) {
            int value = 0;
            for (int i = 41, j = 0; i <= 44; value |= (((bytes[i] & 0xFF) - 0x33) << (j * 8)), i++, j++)
                ;
            try {
                sensorDataBean.setAmmeterTotalPower_485(Float.parseFloat(Integer.toHexString(value)) / 100);
            } catch (NumberFormatException e) {
                sensorDataBean.setAmmeterTotalPower_485(0);
            }
        }
    }

    private void parseWaterLevel2(byte[] bytes) {
        if (bytes.length > 52) {
            int value = 0;
            for (int i = 52, j = 0; i >= 49; value |= ((bytes[i] & 0xFF) << (j * 8)), i--, j++) ;
            sensorDataBean.setWaterLevel2(((float) value) / 10000);
        }
    }


    /**
     * 解析设备电流
     *
     * @param bytes
     */
    private void parserDeviceCurrent(byte[] bytes) {
        final int value = ((bytes[42] & 0xFF)) | ((bytes[41] & 0xFF) << 8);
        sensorDataBean.setDeviceCurrent((float) value / 100);
    }

    /**
     * 解析设备电压
     *
     * @param bytes
     */
    private void parserDeviceVoltage(byte[] bytes) {
        final int value = (bytes[44] & 0xFF) | ((bytes[43] & 0xFF) << 8);
        sensorDataBean.setDeviceVoltage((float) value / 100);
    }

    /**
     * 解析当前电流阈值
     *
     * @param bytes
     */
    private void parserCurrentBoardThreshold(byte[] bytes) {
        final int value = (bytes[54] & 0xFF) | ((bytes[53] & 0xFF) << 8);
        sensorDataBean.setCurrentBoardThreshold(value / 100f);
    }

    /**
     * 解析电流超限时间
     *
     * @param bytes
     */
    private void parserCurrentBoardTransfiniteTime(byte[] bytes) {
        final int value = (bytes[55] & 0xFF);
        sensorDataBean.setCurrentBoardTransfiniteTime(value * 10);
    }

    /**
     * 解析电流板继电器恢复时间
     *
     * @param bytes
     */
    private void parserCurrentBoardRecoverTime(byte[] bytes) {
        final int value = (bytes[56] & 0xFF);
        sensorDataBean.setCurrentBoardRecoverTime(value * 10);
    }

    /**
     * 解析电流板运行状态
     *
     * @param bytes
     */
    private void parserRunningStatus(byte[] bytes) {
        sensorDataBean.setCurrentBoardRunningStatus((byte) (bytes[57] & 0xFF));
    }
}
