package client.halouhuandian.app15;

import com.hellohuandian.pubfunction.Unit.Unit;

import java.util.Arrays;


/**
 * Created by apple on 2017/12/2.
 * 编辑发送帧
 */

public class A_M_MakeMessage {

    //查询 帧
    public static int[] make_message_02(int number) {   //number:第几位机器

        byte[] message_b = new byte[]{0, 0, 0, 0, 0, 0};
        message_b[0] = (byte) number;
        message_b[1] = (byte) 02;
        message_b[2] = (byte) 00;
        message_b[3] = (byte) 00;
        message_b[4] = (byte) 00;
        message_b[5] = (byte) 00;

        String str = Unit.getCRC(message_b);
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
        message_i[1] = 02;
        message_i[2] = 0;
        message_i[3] = 0;
        message_i[4] = 0;
        message_i[5] = 0;
        message_i[6] = Integer.parseInt(str_07, 16);
        message_i[7] = Integer.parseInt(str_06, 16);

        return message_i;
    }

    //升级 帧
    public static byte[] make_message_up_byte(int number, int type) { //number:第几位机器    type:控制设备（1：步骤1，2：步骤2）
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
        }

        String str = Unit.getCRC(message_b);
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
        message_i[1] = (byte) 05;
        message_i[2] = (byte) 0;
        message_i[3] = (byte) 04;
        message_i[4] = (byte) 0;
        if (type == 1) {
            message_i[5] = (byte) 00;
        } else if (type == 2) {
            message_i[5] = (byte) 01;
        }
        message_i[6] = (byte) Integer.parseInt(str_07, 16);
        message_i[7] = (byte) Integer.parseInt(str_06, 16);
        return message_i;
    }

    //升级 帧
    public static byte[] make_message_write_id(int number, String uid) { //number:第几位机器

        int door_number = number + 4;

        StringBuffer sbu = new StringBuffer();
        char[] chars = uid.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            if (i != chars.length - 1) {
                sbu.append((int) chars[i]).append(",");
            } else {
                sbu.append((int) chars[i]);
            }
        }
        String[] uids = sbu.toString().split(",");

        byte[] message_b = new byte[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        message_b[0] = (byte) door_number;
        message_b[1] = (byte) 0x05;
        message_b[2] = (byte) 0x00;
        message_b[3] = (byte) 0x0D;
        message_b[4] = (byte) 0x00;
        message_b[5] = (byte) 0x01;
        message_b[6] = (byte) Integer.parseInt(uids[0]);
        message_b[7] = (byte) Integer.parseInt(uids[1]);
        message_b[8] = (byte) Integer.parseInt(uids[2]);
        message_b[9] = (byte) Integer.parseInt(uids[3]);
        message_b[10] = (byte) Integer.parseInt(uids[4]);
        message_b[11] = (byte) Integer.parseInt(uids[5]);
        message_b[12] = (byte) Integer.parseInt(uids[6]);
        message_b[13] = (byte) Integer.parseInt(uids[7]);

        String str = Unit.getCRC(message_b);
        while (true) {
            if (str.length() < 4) {
                str = "0" + str;
            } else {
                break;
            }
        }

        String str_06 = str.substring(0, 2);
        String str_07 = str.substring(2, 4);

        byte[] message_i = new byte[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        message_i[0] = (byte) door_number;
        message_i[1] = (byte) 0x05;
        message_i[2] = (byte) 0x00;
        message_i[3] = (byte) 0x0D;
        message_i[4] = (byte) 0x00;
        message_i[5] = (byte) 0x01;
        message_i[6] = (byte) Integer.parseInt(uids[0]);
        message_i[7] = (byte) Integer.parseInt(uids[1]);
        message_i[8] = (byte) Integer.parseInt(uids[2]);
        message_i[9] = (byte) Integer.parseInt(uids[3]);
        message_i[10] = (byte) Integer.parseInt(uids[4]);
        message_i[11] = (byte) Integer.parseInt(uids[5]);
        message_i[12] = (byte) Integer.parseInt(uids[6]);
        message_i[13] = (byte) Integer.parseInt(uids[7]);
        message_i[14] = (byte) Integer.parseInt(str_07, 16);
        message_i[15] = (byte) Integer.parseInt(str_06, 16);

        return message_i;
    }

    //推杆伸长 - 485
    public static byte[] make_message_Elongation(int number) {   //number:第几位机器

        byte[] message_b = new byte[]{0, 0, 0, 0, 0, 0};
        message_b[0] = (byte) number;
        message_b[1] = (byte) 0x05;
        message_b[2] = (byte) 0x00;
        message_b[3] = (byte) 0x09;
        message_b[4] = (byte) 0x00;
        message_b[5] = (byte) 0x01;

        String str = Unit.getCRC(message_b);
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
        message_i[1] = (byte) 0x05;
        message_i[2] = (byte) 0x00;
        message_i[3] = (byte) 0x09;
        message_i[4] = (byte) 0x00;
        message_i[5] = (byte) 0x01;
        message_i[6] = (byte) Integer.parseInt(str_07, 16);
        message_i[7] = (byte) Integer.parseInt(str_06, 16);

        return message_i;
    }

    //推杆伸长 - can
    public static byte[] make_message_Elongation_byte(int number) {   //number:第几位机器

        byte[] message_b = new byte[]{0, 0, 0, 0, 0, 0};
        message_b[0] = (byte) number;
        message_b[1] = (byte) 0x05;
        message_b[2] = (byte) 0x00;
        message_b[3] = (byte) 0x09;
        message_b[4] = (byte) 0x00;
        message_b[5] = (byte) 0x01;

        String str = Unit.getCRC(message_b);
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
        message_i[1] = 0x05;
        message_i[2] = 0x00;
        message_i[3] = 0x09;
        message_i[4] = 0x00;
        message_i[5] = 0x01;
        message_i[6] = (byte) Integer.parseInt(str_07, 16);
        message_i[7] = (byte) Integer.parseInt(str_06, 16);

        return message_i;
    }

    //推杆收回 - can
    public static byte[] make_message_Shrink(int number) {   //number:第几位机器

        byte[] message_b = new byte[]{0, 0, 0, 0, 0, 0};
        message_b[0] = (byte) number;
        message_b[1] = (byte) 0x05;
        message_b[2] = (byte) 0x00;
        message_b[3] = (byte) 0x09;
        message_b[4] = (byte) 0x00;
        message_b[5] = (byte) 0x00;

        String str = Unit.getCRC(message_b);
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
        message_i[0] = (byte)number;
        message_i[1] = 0x05;
        message_i[2] = 0x00;
        message_i[3] = 0x09;
        message_i[4] = 0x00;
        message_i[5] = 0x00;
        message_i[6] = (byte)Integer.parseInt(str_07, 16);
        message_i[7] = (byte)Integer.parseInt(str_06, 16);

        return message_i;
    }


    //读电池 - 485
    public static byte[] make_message_ReadBar(int number) {   //number:第几位机器

        byte[] message_b = new byte[]{0, 0, 0, 0, 0, 0};
        message_b[0] = (byte) number;
        message_b[1] = (byte) 0x02;
        message_b[2] = (byte) 0x00;
        message_b[3] = (byte) 0x00;
        message_b[4] = (byte) 0x00;
        message_b[5] = (byte) 0x00;

        String str = Unit.getCRC(message_b);
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
        message_i[0] = (byte)number;
        message_i[1] = 0x02;
        message_i[2] = 0x00;
        message_i[3] = 0x00;
        message_i[4] = 0x00;
        message_i[5] = 0x00;
        message_i[6] = (byte)Integer.parseInt(str_07, 16);
        message_i[7] = (byte)Integer.parseInt(str_06, 16);

        return message_i;
    }

    //推杆收回 - can
    public static byte[] make_message_Shrink_byte(int number) {   //number:第几位机器

        byte[] message_b = new byte[]{0, 0, 0, 0, 0, 0};
        message_b[0] = (byte) number;
        message_b[1] = (byte) 0x05;
        message_b[2] = (byte) 0x00;
        message_b[3] = (byte) 0x09;
        message_b[4] = (byte) 0x00;
        message_b[5] = (byte) 0x00;

        String str = Unit.getCRC(message_b);
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
        message_i[1] = 0x05;
        message_i[2] = 0x00;
        message_i[3] = 0x09;
        message_i[4] = 0x00;
        message_i[5] = 0x00;
        message_i[6] = (byte) Integer.parseInt(str_07, 16);
        message_i[7] = (byte) Integer.parseInt(str_06, 16);

        return message_i;
    }


    //查询 帧 - can
    public static byte[] make_message_GetInfo_byte(int number) {   //number:第几位机器
        byte[] message_b = new byte[]{0, 0, 0, 0, 0, 0};
        message_b[0] = (byte) number;
        message_b[1] = (byte) 02;
        message_b[2] = (byte) 00;
        message_b[3] = (byte) 00;
        message_b[4] = (byte) 00;
        message_b[5] = (byte) 00;

        String str = Unit.getCRC(message_b);
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
        message_i[1] = 0x02;
        message_i[2] = 0x0;
        message_i[3] = 0x0;
        message_i[4] = 0x0;
        message_i[5] = 0x0;
        message_i[6] = (byte) Integer.parseInt(str_07, 16);
        message_i[7] = (byte) Integer.parseInt(str_06, 16);
        return message_i;
    }


    //执行 帧 - can
    public static byte[] make_message_05_byte(int number, int type, int action) { //number:第几位机器    type:控制设备（01：门锁，02：电机，03：微动开关，05：推杆）    action：动作（00：关闭，01：打开，（推杆02停止））
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


        String str = Unit.getCRC(message_b);
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
        message_i[1] = 0x05;
        message_i[2] = 0x00;
        message_i[3] = (byte) type;
        message_i[4] = 0x00;
        message_i[5] = (byte) action;
        message_i[6] = (byte) Integer.parseInt(str_07, 16);
        message_i[7] = (byte) Integer.parseInt(str_06, 16);

        return message_i;
    }


    //冒束管 帧 - can
    public static byte[] make_message_is_charge(byte[] order) {
        String str_16 = bytesToHexString(order);
        String str_1 = str_16.substring(0, 2);
        if (str_1.substring(0, 1).equals("0")) {
            str_1 = str_1.substring(1, 2);
        }
        String str_2 = str_16.substring(2, 4);
        if (str_2.substring(0, 1).equals("0")) {
            str_2 = str_2.substring(1, 2);
        }
        int int_1 = Integer.parseInt(str_1, 16);
        int int_2 = Integer.parseInt(str_2, 16);

        byte[] message_b = new byte[]{0x11, 0x05, 0x00, 0x02, (byte) int_1, (byte) int_2};
        String str = Unit.getCRC(message_b);
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
        message_i[0] = (byte) 0x11;
        message_i[1] = 0x05;
        message_i[2] = 0x00;
        message_i[3] = (byte) 0x02;
        message_i[4] = (byte) int_1;
        message_i[5] = (byte) int_2;
        message_i[6] = (byte) Integer.parseInt(str_07, 16);
        message_i[7] = (byte) Integer.parseInt(str_06, 16);

        return message_i;
    }

    public static String bytesToHexString(byte[] src) {
        int b = (src[11] * 2048) + (src[10] * 1024) + (src[9] * 512) + (src[8] * 256) + (src[7] * 128) + (src[6] * 64) + (src[5] * 32) + (src[4] * 16) + (src[3] * 8) + (src[2] * 4) + (src[1] * 2) + (src[0] * 1);
        String hex = Integer.toHexString(b);
        if (hex.length() == 1) {
            hex = "000" + hex;
        } else if (hex.length() == 2) {
            hex = "00" + hex;
        } else if (hex.length() == 3) {
            hex = "0" + hex;
        }
        return hex;
    }


    //升级 帧 - 485
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

        String str = Unit.getCRC(message_b);
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
        message_i[1] = (byte) 05;
        message_i[2] = (byte) 0;
        message_i[3] = (byte) 04;
        message_i[4] = (byte) 0;
        if (type == 1) {
            message_i[5] = (byte) 00;
        } else if (type == 2) {
            message_i[5] = (byte) 01;
        } else if (type == 3) {
            message_i[5] = (byte) 02;
        }
        message_i[6] = (byte) Integer.parseInt(str_07, 16);
        message_i[7] = (byte) Integer.parseInt(str_06, 16);

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


            String str_0607 = Unit.getCRC(message);
            while (true) {
                if (str_0607.length() < 4) {
                    str_0607 = "0" + str_0607;
                } else {
                    break;
                }
            }

            String str_06 = str_0607.substring(0, 2);
            String str_07 = str_0607.substring(2, 4);


            return_message[0] = (byte) 1;
            return_message[1] = (byte) m;
            return_message[2] = (byte) (255 - m);

            int m_size = message.length;
            for (int i = 0; i < message.length; i++) {
                return_message[i + 3] = (byte) message[i];
            }
            return_message[m_size + 3] = (byte) Integer.parseInt(str_07, 16);
            return_message[m_size + 4] = (byte) Integer.parseInt(str_06, 16);

            return return_message;

        } else if (m > 0) {

            byte[] return_message = new byte[1029];
            int[] message = new int[1024];
            Arrays.fill(message, 0);

            for (int i = 0; i < data.length; i++) {
                message[i] = data[i];
            }

            String str_0607 = Unit.getCRC(message);
            while (true) {
                if (str_0607.length() < 4) {
                    str_0607 = "0" + str_0607;
                } else {
                    break;
                }
            }

            String str_06 = str_0607.substring(0, 2);
            String str_07 = str_0607.substring(2, 4);

            return_message[0] = (byte) 2;
            return_message[1] = (byte) m;
            return_message[2] = (byte) (255 - m);

            int m_size = message.length;
            for (int i = 0; i < message.length; i++) {
                return_message[i + 3] = (byte) message[i];
            }

            return_message[m_size + 3] = (byte) Integer.parseInt(str_07, 16);
            return_message[m_size + 4] = (byte) Integer.parseInt(str_06, 16);

            return return_message;

        } else if (m == -1) {
            byte[] message = new byte[1029];
            Arrays.fill(message, (byte) 0);
            message[0] = (byte) 02;
            message[1] = (byte) 0;
            message[2] = (byte) 255;
            return message;
        } else {
            return null;
        }
    }

    public static byte[] make_message_02_byte(int number) {   //number:第几位机器

        byte[] message_b = new byte[]{0, 0, 0, 0, 0, 0};
        message_b[0] = (byte) number;
        message_b[1] = (byte) 0x02;
        message_b[2] = (byte) 0x00;
        message_b[3] = (byte) 0x00;
        message_b[4] = (byte) 0x00;
        message_b[5] = (byte) 0x00;

        String str = Unit.getCRC(message_b);
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
        message_i[0] = (byte)number;
        message_i[1] = (byte)0x02;
        message_i[2] = (byte)0x00;
        message_i[3] = (byte)0x00;
        message_i[4] = (byte)0x00;
        message_i[5] = (byte)0x00;
        message_i[6] = (byte)Integer.parseInt(str_07, 16);
        message_i[7] = (byte)Integer.parseInt(str_06, 16);
        return message_i;
    }
}