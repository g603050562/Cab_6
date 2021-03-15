package com.hellohuandian.controlpanelupgrade.ControlPanel_485_9;

import java.util.Arrays;


/**
 * Created by apple on 2017/12/2.
 * 编辑发送帧
 */

public class A_M_MakeMessage {

    //查询 帧
    public static byte[] make_message_02(int number) {   //number:第几位机器

        byte[] message_b = new byte[]{0, 0, 0, 0, 0, 0};
        message_b[0] = (byte) number;
        message_b[1] = (byte) 02;
        message_b[2] = (byte) 00;
        message_b[3] = (byte) 00;
        message_b[4] = (byte) 00;
        message_b[5] = (byte) 00;

        String str = getCRC(message_b);
        while (true) {
            if (str.length() < 4) {
                str = "0" + str;
            } else {
                break;
            }
        }

        String str_06 = str.substring(0, 2);
        String str_07 = str.substring(2, 4);

        byte[] message_i = new byte[]{0, 0, 0, 0, 0, 0, 0, 0};
        message_i[0] = (byte) number;
        message_i[1] = (byte)02;
        message_i[2] = (byte)0;
        message_i[3] = (byte)0;
        message_i[4] = (byte)0;
        message_i[5] = (byte)0;
        message_i[6] = (byte)Integer.parseInt(str_07, 16);
        message_i[7] = (byte)Integer.parseInt(str_06, 16);

        return message_i;
    }

    //推杆伸长
    public static int[] make_message_Elongation(int number) {   //number:第几位机器

        byte[] message_b = new byte[]{0, 0, 0, 0, 0, 0};
        message_b[0] = (byte) number;
        message_b[1] = (byte) 0x05;
        message_b[2] = (byte) 0x00;
        message_b[3] = (byte) 0x09;
        message_b[4] = (byte) 0x00;
        message_b[5] = (byte) 0x01;

        String str = getCRC(message_b);
        while (true) {
            if (str.length() < 4) {
                str = "0" + str;
            } else {
                break;
            }
        }

        String str_06 = str.substring(0, 2);
        String str_07 = str.substring(2, 4);

        int[] message_i = new int[]{0, 0, 0, 0, 0, 0, 0, 0};
        message_i[0] = number;
        message_i[1] = 0x05;
        message_i[2] = 0x00;
        message_i[3] = 0x09;
        message_i[4] = 0x00;
        message_i[5] = 0x01;
        message_i[6] = Integer.parseInt(str_07, 16);
        message_i[7] = Integer.parseInt(str_06, 16);

        return message_i;
    }

    //推杆收回
    public static int[] make_message_Shrink(int number) {   //number:第几位机器

        byte[] message_b = new byte[]{0, 0, 0, 0, 0, 0};
        message_b[0] = (byte) number;
        message_b[1] = (byte) 0x05;
        message_b[2] = (byte) 0x00;
        message_b[3] = (byte) 0x09;
        message_b[4] = (byte) 0x00;
        message_b[5] = (byte) 0x00;

        String str = getCRC(message_b);
        while (true) {
            if (str.length() < 4) {
                str = "0" + str;
            } else {
                break;
            }
        }

        String str_06 = str.substring(0, 2);
        String str_07 = str.substring(2, 4);

        int[] message_i = new int[]{0, 0, 0, 0, 0, 0, 0, 0};
        message_i[0] = number;
        message_i[1] = 0x05;
        message_i[2] = 0x00;
        message_i[3] = 0x09;
        message_i[4] = 0x00;
        message_i[5] = 0x00;
        message_i[6] = Integer.parseInt(str_07, 16);
        message_i[7] = Integer.parseInt(str_06, 16);

        return message_i;
    }

    //执行 帧
    public static int[] make_message_05(int number, int type, int action) { //number:第几位机器    type:控制设备（01：门锁，02：电机，03：微动开关，05：推杆）    action：动作（00：关闭，01：打开，（推杆02停止））
        byte[] message_b = new byte[]{0, 0, 0, 0, 0, 0};
        message_b[0] = (byte) number;
        message_b[1] = (byte) 05;
        message_b[2] = (byte) 00;

        if (type == 05) {
            type = (byte) 9;
        }

        message_b[3] = (byte) type;
        message_b[4] = (byte) 00;
        message_b[5] = (byte) action;


        String str = getCRC(message_b);
        while (true) {
            if (str.length() < 4) {
                str = "0" + str;
            } else {
                break;
            }
        }

        String str_06 = str.substring(0, 2);
        String str_07 = str.substring(2, 4);

        int[] message_i = new int[]{0, 0, 0, 0, 0, 0, 0, 0};
        message_i[0] = number;
        message_i[1] = 05;
        message_i[2] = 0;
        message_i[3] = type;
        message_i[4] = 0;
        message_i[5] = action;
        message_i[6] = Integer.parseInt(str_07, 16);
        message_i[7] = Integer.parseInt(str_06, 16);


        return message_i;
    }


    //升级 帧
    public static byte[] make_message_up(int number, int type) { //number:第几位机器    type:控制设备（1：步骤1，2：步骤2）
        byte[] message_b = new byte[]{0, 0, 0, 0, 0, 0};
        message_b[0] = (byte) number;
        message_b[1] = (byte) 05;
        message_b[2] = (byte) 00;
        message_b[3] = (byte) 04;
        message_b[4] = (byte) 00;

        if (type == 1) {
            message_b[5] = (byte) 00;
        } else if (type == 2) {
            message_b[5] = (byte) 01;
        } else if (type == 3) {
            message_b[5] = (byte) 02;
        }

        String str = getCRC(message_b);
        while (true) {
            if (str.length() < 4) {
                str = "0" + str;
            } else {
                break;
            }
        }

        String str_06 = str.substring(0, 2);
        String str_07 = str.substring(2, 4);

        byte[] message_i = new byte[]{0, 0, 0, 0, 0, 0, 0, 0};
        message_i[0] = (byte) number;
        message_i[1] = (byte)05;
        message_i[2] = (byte)0;
        message_i[3] = (byte)04;
        message_i[4] = (byte)0;

        if (type == 1) {
            message_i[5] = (byte) 00;
        } else if (type == 2) {
            message_i[5] = (byte) 01;
        } else if (type == 3) {
            message_i[5] = (byte) 02;
        }

        message_i[6] = (byte)Integer.parseInt(str_07, 16);
        message_i[7] = (byte)Integer.parseInt(str_06, 16);

        return message_i;
    }


    public static byte[] make_message_send_file(String name, long size, int data[], int m) {

        if (m == 0) {

            byte[] return_message = new byte[133];
            int[] message = new int[128];
            Arrays.fill(message, 0);
            int name_int_long = name.length();
            for (int i = 0; i < name_int_long; i++) {
                String a = name.substring(i, i + 1);
                byte[] b_a = a.getBytes();
                message[i] = (int) b_a[0];
            }
            message[name_int_long] = 0;

            String size_str = size + "";
            int size_int_long = size_str.length();
            for (int i = 0; i < size_int_long; i++) {
                String b = size_str.substring(i, i + 1);
                byte[] b_b = b.getBytes();
                message[name_int_long + 1 + i] = (int) b_b[0];
            }


            String str_0607 = getCRC(message);
            while (true) {
                if (str_0607.length() < 4) {
                    str_0607 = "0" + str_0607;
                } else {
                    break;
                }
            }

            String str_06 = str_0607.substring(0, 2);
            String str_07 = str_0607.substring(2, 4);


            return_message[0] = (byte)1;
            return_message[1] = (byte)m;
            return_message[2] = (byte)(255 - m);

            int m_size = message.length;
            for (int i = 0; i < message.length; i++) {
                return_message[i + 3] = (byte)message[i];
            }
            return_message[m_size + 3] = (byte)Integer.parseInt(str_07, 16);
            return_message[m_size + 4] = (byte)Integer.parseInt(str_06, 16);

            return return_message;

        } else if (m > 0) {

            byte[] return_message = new byte[1029];
            int[] message = new int[1024];
            Arrays.fill(message, 0);

            for (int i = 0; i < data.length; i++) {
                message[i] = data[i];
            }

            String str_0607 = getCRC(message);
            while (true) {
                if (str_0607.length() < 4) {
                    str_0607 = "0" + str_0607;
                } else {
                    break;
                }
            }

            String str_06 = str_0607.substring(0, 2);
            String str_07 = str_0607.substring(2, 4);

            return_message[0] = (byte)2;
            return_message[1] = (byte)m;
            return_message[2] = (byte)(255 - m);

            int m_size = message.length;
            for (int i = 0; i < message.length; i++) {
                return_message[i + 3] = (byte)message[i];
            }

            return_message[m_size + 3] = (byte)Integer.parseInt(str_07, 16);
            return_message[m_size + 4] = (byte)Integer.parseInt(str_06, 16);

            return return_message;

        } else if (m == -1) {
            byte[] message = new byte[1029];
            Arrays.fill(message, (byte) 0);
            message[0] = (byte)02;
            message[1] = (byte)0;
            message[2] = (byte)255;
            return message;
        } else {
            return null;
        }
    }

    public static String getCRC(byte[] bytes) {
        int CRC = 0x0000ffff;
        int POLYNOMIAL = 0x0000a001;
        int i, j;
        for (i = 0; i < bytes.length; i++) {
            CRC ^= ((int) bytes[i] & 0x000000ff);
            for (j = 0; j < 8; j++) {
                if ((CRC & 0x00000001) != 0) {
                    CRC >>= 1;
                    CRC ^= POLYNOMIAL;
                } else {
                    CRC >>= 1;
                }
            }
        }
        return Integer.toHexString(CRC);
    }

    public static String getCRC(int[] bytes) {
        int CRC = 0x0000ffff;
        int POLYNOMIAL = 0x0000a001;
        int i, j;
        for (i = 0; i < bytes.length; i++) {
            CRC ^= ( bytes[i] & 0x000000ff);
            for (j = 0; j < 8; j++) {
                if ((CRC & 0x00000001) != 0) {
                    CRC >>= 1;
                    CRC ^= POLYNOMIAL;
                } else {
                    CRC >>= 1;
                }
            }
        }
        return Integer.toHexString(CRC);
    }
}