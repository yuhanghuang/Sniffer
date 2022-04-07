package com.hyh.capture;

import com.sun.org.apache.bcel.internal.generic.RETURN;
import jpcap.JpcapCaptor;
import jpcap.NetworkInterface;
import jpcap.packet.Packet;
import sun.nio.ch.Net;

import javax.swing.table.DefaultTableModel;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class PackageCapture implements Runnable{

    NetworkInterface device;
    static DefaultTableModel tableModel;
    static String Filter = "";
    static ArrayList<Packet> packetlist = new ArrayList<Packet>();

    public PackageCapture() {
    }

    @Override
    public void run() {
        Packet packet;
        try{
            JpcapCaptor captor = JpcapCaptor.openDevice(device,65535,true,20);
            while (true){
                long staertime = System.currentTimeMillis();
//                抓包时间是600ms
                while (staertime+600>=System.currentTimeMillis()){
                    packet = captor.getPacket();
                    if (packet!=null && TestFilter(packet)){
                        packetlist.add(packet);
                        showTable(packet);
                    }
                }
                Thread.sleep(2000);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

    }

    private void showTable(Packet packet) {
        String[] rowData = getObj(packet);
        tableModel.addRow(rowData);
    }

//将抓取到的包的基本信息显示在列表上，返回信息的String[]形式
    private String[] getObj(Packet packet) {
        String[] data = new String[6];
        if ((packet!=null)&&(new PacketAnalyze(packet).packetClass().size()>=3)){
            Date d =new Date();
            DateFormat df = new SimpleDateFormat("HH:mm:ss");
            data[0]=df.format(d);
            data[1]=new PacketAnalyze(packet).packetClass().get("源IP");
            data[2]=new PacketAnalyze(packet).packetClass().get("目的IP");
            data[3]=new PacketAnalyze(packet).packetClass().get("协议");
            data[4]= String.valueOf(packet.len);
        }
        return data;
    }

    private boolean TestFilter(Packet packet) {
        if (Filter.contains("src_ip")){
            String src_ip = Filter.substring(4,Filter.length());
            if (new PacketAnalyze(packet).packetClass().get("源ip").equals(src_ip)){
                return true;
            }
        }else if (Filter.contains("dst_ip")){
            String dst_ip = Filter.substring(4,Filter.length());
            if (new PacketAnalyze(packet).packetClass().get("目的IP").equals(dst_ip)){
                return true;
            }
        }else if (Filter.contains("TCP")){
            if (new PacketAnalyze(packet).packetClass().get("协议").equals("TCP")){
                return true;
            }
        }else if (Filter.contains("UDP")){
            if (new PacketAnalyze(packet).packetClass().get("协议").equals("UDP")){
                return true;
            }
        }else if (Filter.equals("")){
            return true;
        }
        return false;
    }

    public void setDevice(NetworkInterface device) {
        this.device = device;
    }

    public void setFilter(String s) {
        this.Filter = s;
    }

    public void clearpackets() {
        this.Filter = Filter;
    }

    public void setTable(DefaultTableModel tableModel) {
        this.tableModel = tableModel;
    }

    public static ArrayList<Packet> getpacketlist() {
        return packetlist;
    }
}
