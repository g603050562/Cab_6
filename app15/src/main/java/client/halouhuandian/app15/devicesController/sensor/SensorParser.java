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
            parserButton(bytes);
            parseAmmeterTotalPower_485(bytes);
            parseWaterLevel2(bytes);
        }
    }

    private void parseHardwareVersion(byte[] bytes) {
        if (bytes.length > 1) {
            sensorDataBean.setHardwareVersion((short) (bytes[0]& 0xFF));
        }
    }

    private void parseSoftwareVersion(byte[] bytes) {
        if (bytes.length > 3) {
            sensorDataBean.setSoftwareVersion((short) (bytes[3]& 0xFF));
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

    private void parserButton(byte[] bytes) {
        if (bytes.length > 40) {
            byte bitValue = bytes[40];
            byte statusValue = (byte) (bitValue & 0b0010_0000);
            sensorDataBean.setButtonStatus((byte) (statusValue >> 5));

            // TODO: 2020/8/4 解析风扇1，2状态
            byte fan1status = (byte) (bitValue & 0b0000_0010);
            sensorDataBean.setAirFan1Status((byte) (fan1status >> 1));

            byte fan2status = (byte) (bitValue & 0b0000_0100);
            sensorDataBean.setAirFan2Status((byte) (fan2status >> 2));
        }
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

}
