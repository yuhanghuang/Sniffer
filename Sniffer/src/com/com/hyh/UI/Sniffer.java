package com.hyh.UI;

import com.hyh.capture.NetworkAdapter;
import com.hyh.capture.PackageCapture;
import com.hyh.capture.PacketAnalyze;
import jpcap.NetworkInterface;
import jpcap.packet.Packet;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**\
 * 用来设置过滤器的主UI界面
 */
public class Sniffer extends JFrame{
    JMenuBar menubar;  //顶部菜单条
    JMenu menulist1,menulist2; //设置网卡 协议的两个菜单
    JMenuItem[] item; //设置网卡菜单的下拉选项
    JMenuItem pro1,pro2; //设置TCP UDP两种协议的过滤
    JButton srcButton,dstButton; //设置源ip与目的ip
    JPanel panel;
    JScrollPane scrollPane;
    JTable table;
    final String[] head = new String[]{
            "时间","源ip","目的ip","协议","字节长度"
    };
    NetworkInterface[] devices; //网卡列表
    Object[][] descriplist = {};
    DefaultTableModel tableModel;
    PackageCapture allpackets;

    public Sniffer() {
        allpackets = new PackageCapture();
        this.setTitle("Sniffer");
        this.setBounds(650,150,1200,1200);
        menubar = new JMenuBar();
        //设置网卡过滤操作
        menulist1 = new JMenu("网卡");
        NetworkInterface[] devices = NetworkAdapter.getDevices();
        item = new JMenuItem[devices.length];//设置item的大小
        for (int i = 0; i < devices.length; i++) {
//            设置网卡提示信息
            item[i] = new JMenuItem(i+1 +": "+devices[i].name+"("+devices[i].description+devices[i].addresses[devices[i].addresses.length-1].address+")");
            System.out.println(devices[i].addresses[0].address);
//            设置网卡监听器
            item[i].addActionListener(
                    new AdapterActionListener(devices[i]));
                    menulist1.add(item[i]);

        }
//        根据协议过滤来进行操作 TCP UDP协议
        menulist2 = new JMenu("协议");
        pro1 = new JMenuItem("TCP");
        pro2 = new JMenuItem("UDP");
//        TCP协议过滤
        pro1.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        allpackets.setFilter("TCP");
//                        清空包 重新进行过滤
                        allpackets.clearpackets();
                        while (tableModel.getRowCount()>0){
                            tableModel.removeRow(tableModel.getRowCount()-1);
                        }
                    }
                }
        );
        pro2.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        allpackets.setFilter("UDP");
                        allpackets.clearpackets();
                        while (tableModel.getRowCount()>0){
                            tableModel.removeRow(tableModel.getRowCount()-1);
                        }
                    }
                }
        );
        menulist2.add(pro1);
        menulist2.add(pro2);
//        根据源IP来进行过滤操作
        srcButton = new JButton("源ip");
        srcButton.addActionListener(
                new ActionListener() {
                    @Override
                    public String toString() {
                        return super.toString();
                    }

                    @Override
                    public void actionPerformed(ActionEvent e) {
                        String src_ip = JOptionPane.showInputDialog("请输入需要过滤的源IP");
                        allpackets.setFilter(src_ip);
                        allpackets.clearpackets();
                        while (tableModel.getRowCount()>0){
                            tableModel.removeRow(tableModel.getRowCount()-1);
                        }
                    }
                }
        );
//        根据目的IP进行过滤
        dstButton = new JButton("目的IP");
        dstButton.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        String dst_ip = JOptionPane.showInputDialog("请输入需要过滤的目的IP");
                        allpackets.setFilter(dst_ip);
                        allpackets.clearpackets();
                        while (tableModel.getRowCount()>0){
                            tableModel.removeRow(tableModel.getRowCount()-1);
                        }
                    }
                }
        );
//       添加菜单
        menubar.add(menulist1);
        menubar.add(menulist2);
        menubar.add(srcButton);
        menubar.add(dstButton);
        setJMenuBar(menubar);

        tableModel = new DefaultTableModel(descriplist,head);
        table = new JTable(tableModel){
            public boolean isCellEditable(int row,int column){
                return false;
            }
        };
        allpackets.setTable(tableModel);
        table.setPreferredScrollableViewportSize(new Dimension(500,60));
        table.setRowHeight(30);
        table.setRowMargin(5);
        table.setRowSelectionAllowed(true);
        table.setSelectionBackground(Color.white);
        table.setSelectionForeground(Color.red);
        table.setShowGrid(true);
        table.doLayout();
        scrollPane = new JScrollPane(table);
        panel = new JPanel(new GridLayout(0,1));
        panel.setPreferredSize(new Dimension(600,300));
        panel.setBackground(Color.black);
        panel.add(scrollPane);
        setContentPane(panel);
        pack();
//        设置鼠标点击事件
        table.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
//                连续点击两次
                if (e.getClickCount()==2){
                    int row = table.getSelectedRow();
                    JFrame frame = new JFrame("数据包信息");
                    JPanel panel = new JPanel();
                    final JTextArea info = new JTextArea(23,42);
                    info.setEditable(false);
                    info.setLineWrap(true);
                    info.setWrapStyleWord(true);
                    frame.add(panel);
                    panel.add(new JScrollPane(info));
                    frame.setBounds(200,200,500,500);
                    frame.setVisible(true);
                    frame.setResizable(false);
                    ArrayList<Packet> packetlist = allpackets.getpacketlist();
                    Map<String,String> hm1 = new HashMap<String,String>();
                    Map<String,String> hm2 = new HashMap<String,String>();
                    Packet packet = packetlist.get(row);
                    info.append("----------------------------------------IP头信息---------------------------------------");
                    info.append("\n");
                    hm1 = new PacketAnalyze(packet).IPanalyze();
                    for (Map.Entry<String,String> me1 : hm1.entrySet()){
                        info.append(me1.getKey()+" : "+me1.getValue()+"\n");
                    }
            }
            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });
        setResizable(false);
        setVisible(true);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });


    }
    /**
     * 在一个java源文件中可以声明多个class。
     * 但是，只能最多有一个类声明为public的。
     * 而且要求声明为public的类的类名必须与源文件名相同。
     */
    private class AdapterActionListener implements ActionListener{
        NetworkInterface device;
        AdapterActionListener(NetworkInterface device){
            this.device =device;
        }
        @Override
        public void actionPerformed(ActionEvent e) {
            allpackets.setDevice(device);
            allpackets.setFilter("");
            new Thread(allpackets).start();
        }
    }
}
