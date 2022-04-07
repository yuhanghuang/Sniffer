package com.hyh.capture;

import jpcap.JpcapCaptor;
import jpcap.NetworkInterface;

public class NetworkAdapter {
    public  String[] devices;
//    静态方法是可以直接通过类名.方法名来进行访问的
    public static NetworkInterface[] getDevices() {
        NetworkInterface[] devices = JpcapCaptor.getDeviceList();
        return devices;
    }
}
