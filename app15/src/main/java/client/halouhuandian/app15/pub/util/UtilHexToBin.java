package client.halouhuandian.app15.pub.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class UtilHexToBin {

    private static final int ByteSize = 200 * 1024; //读取的字节数
    private static final int headIndex = 0x0800;
    private int endIndex = 0;

    public UtilHexToBin (){
    }

    public byte[] onStart(String filePath){

        //200k固定长度的数组
        byte[] bytes = new byte[ByteSize];
        File mFile = new File(filePath);

        InputStream inputStream = null;
        BufferedReader bufferedReader = null;

        try {
            if (mFile == null) {
                System.out.println("文件为空");
                return null;
            }
            inputStream = new FileInputStream(mFile);
            //转成 reader 以 行 为单位读取文件
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            //当前行字符串
            String hexLineStr = null;
            //当前行的地址位
            Integer hexLineAddress = null;
            //初始化数组为全FFF
            bytes = fillFToByteArray(bytes);

            while ((hexLineStr = bufferedReader.readLine())!=null){
                //地址为转换成十进制
                hexLineAddress = Integer.parseInt(hexLineStr.substring(3,7),16);
                //获取数据部分
                String data = hexLineStr.substring(9,hexLineStr.length() - 2);
                //行内大小
                int innerLineCount = data.length() / 2;
                if(hexLineAddress + innerLineCount > endIndex){
                    endIndex = hexLineAddress + innerLineCount;
                }
                for(int i = 0 ; i < data.length() / 2 ; i++){
                    bytes[hexLineAddress + i] = (byte) Integer.parseInt(data.substring(i*2 , (i*2)+2) , 16);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if(inputStream!=null){
                    inputStream.close();
                }
                if(bufferedReader!=null){
                    bufferedReader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        int returnBytesLength = endIndex - headIndex;
        byte[] returnBytes = new byte[returnBytesLength];
        for(int i = 0 ; i < returnBytes.length ; i++){
            returnBytes[i] = bytes[headIndex+i];
        }

        return returnBytes;
    }

    //初始化数组成全F
    public byte[] fillFToByteArray(byte[] b){
        for(int i = 0 ; i < b.length ; i++){
            if(i % 2 == 1){
                b[i] = (byte) Integer.parseInt("3F",16);
            }else{
                b[i] = (byte) Integer.parseInt("FF",16);
            }
        }
        return b;
    }

}
