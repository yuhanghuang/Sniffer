package com.hyh.capture;

import jpcap.packet.*;

import java.io.UnsupportedEncodingException;
import java.text.AttributedString;
import java.util.HashMap;
import java.util.Map;

public class PacketAnalyze {
    static Packet packet;
    static HashMap<String,String> att,att1;
    public PacketAnalyze(Packet packet) {
        this.packet =packet;
    }
    public PacketAnalyze(){
        
    }

    public static  HashMap<String,String> packetClass(){
        att1 = new HashMap<String,String>();
        if (packet.getClass().equals(TCPPacket.class)){
            att1 = TCPanalyze();
        }else if(packet.getClass().equals(UDPPacket.class)){
            att1 = UDPanalyze();
        }
        return att;
    }


    public static HashMap<String, String> IPanalyze() {
        att = new HashMap<String,String>();
        if (packet instanceof IPPacket){
            IPPacket ipPacket = (IPPacket) packet;
            att.put("协议",new String("ip"));
            att.put("源IP",ipPacket.src_ip.toString().substring(1,ipPacket.src_ip.toString().length()));
            att.put("目的IP",ipPacket.dst_ip.toString().substring(1,ipPacket.dst_ip.toString().length()));
            att.put("TTL",String.valueOf(ipPacket.hop_limit));
            att.put("头长度",String.valueOf(ipPacket.header.length));
            att.put("是否有其他切片",String.valueOf(ipPacket.more_frag));
        }
        return att;
    }

    public static HashMap<String,String> TCPanalyze(){
        att = new HashMap<String,String>();
        TCPPacket tcpPacket = (TCPPacket) packet;
        EthernetPacket ethernetPacket = (EthernetPacket) packet.datalink;
        att.put("协议",new String("TCP"));
        att.put("源IP",tcpPacket.src_ip.toString().substring(1,tcpPacket.src_ip.toString().length()));
        att.put("源端口",String.valueOf(tcpPacket.src_port));
        att.put("目的IP",tcpPacket.dst_ip.toString().substring(1,tcpPacket.dst_ip.toString().length()));
        att.put("目的端口",String.valueOf(tcpPacket.dst_port));
        att.put("源MAC",ethernetPacket.getSourceAddress());
        att.put("目的MAC",ethernetPacket.getDestinationAddress());
        try {
            att.put("数据",new String(tcpPacket.data,"gbk"));
        }catch (UnsupportedEncodingException e){
            e.printStackTrace();
        }
        return att;
    }
    public static HashMap<String,String> UDPanalyze(){
        att = new HashMap<String,String>();
        UDPPacket udpPacket = (UDPPacket) packet;
        EthernetPacket ethernetPacket = (EthernetPacket) packet.datalink;
        att.put("协议",new String("UDP"));
        att.put("源IP",udpPacket.src_ip.toString().substring(1,udpPacket.src_ip.toString().length()));
        att.put("源端口",String.valueOf(udpPacket.src_port));
        att.put("目的IP",udpPacket.dst_ip.toString().substring(1,udpPacket.dst_ip.toString().length()));
        att.put("目的端口",String.valueOf(udpPacket.dst_port));
        att.put("源MAC",ethernetPacket.getSourceAddress());
        att.put("目的MAC",ethernetPacket.getDestinationAddress());
        try {
            att.put("数据",new String(udpPacket.data,"gbk"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return att;
    }
}
