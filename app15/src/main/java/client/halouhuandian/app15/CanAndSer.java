package client.halouhuandian.app15;

import java.io.FileDescriptor;

public class CanAndSer {

    static {
        System.loadLibrary("CanAndSer");
    }

    public native static FileDescriptor openCan();

    public native static FileDescriptor openSer(String path ,int baudrate);

}
