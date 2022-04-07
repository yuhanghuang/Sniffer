# 网络嗅探器报告

## 1、环境介绍

### 1.1 实验环境

Windows10+IDEA+winpcap_3.1.3+jpcap.jar_64bit。

此处需要注意的是由于我的电脑是64bit机器，因此使用的jpcap.jar包也是需要64位的。

### 1.2 Jpcap介绍

Jpcap是一个应用在JAVA中的jar包，它并不是真正去实现对数据链路层的控制，而是调用了winpcap/libpcap来提供一个公共接口来对数据链路层的信息进行访问，因此本实验开始之前还需要再本机上安装wincap软件。

下图是对jpcap.jar包反编译后的的源码书结构的展示。由于而不能软件主要是实现了对指定网卡,UDP协议、TCP协议的过滤以及对IP报文头的解析，因此需要使用DtalinkPacket.java，EthernetPacket.java，IPPacket.java，Packet.java，TCPPacket.java，UDPPacket.java，JpcapCaptor.java，NetWorkInterface这几个源文件。

![image-20220407165359863](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\image-20220407165359863.png)

## 2、实验设计

本项目完成的网络嗅探器实现了如下几个功能：

1. 可以选择过滤的网卡

2. 可以根据协议来进行过滤，比如TCP协议/UDP协议。

3. 可以根据源IP/或者是目的IP来进行过滤

4. 对于抓去的数据包可以再主界面展示抓包时间，源IP，目的IP，协议，包的长度等信息。

5. 对于展示具体的某条数据包信息点击可以得到详细的IP头部信息。

   

### 2.1 实验效果介绍

网络嗅探器流程图如下：

<center></center>

<center>![image-20220407191848752](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\image-20220407191848752.png)</center>

 <center> 图2.1 网络嗅探器流程图</center>

网络你嗅探器的运行后的主界面如下：

<center>
    ![image-20220407192020338](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\image-20220407192020338.png)
</center>



 <center>图2.2 网络嗅探器的主界面</center>>

对具体报文的IP头部解析如下：

<center>
    ![image-20220407192404306](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\image-20220407192404306.png)
</center>



<center>图2.3 报文的IP头部解析</center>

### 2.2 核心功能代码介绍

#### 2.2.1 主UI界面代码介绍

对于UI的实现，本项目主要是使用了java.awt包以及javax.swing包的各种API。

```java
   /**
   *菜单栏的UI设置
   **/
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

	this.setTitle("Sniffer");
    this.setBounds(650,150,1200,1200);
    menubar = new JMenuBar();

	/**
	*主界面按行展示抓取的数据包的展示UI
	**/
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
```

#### 2.2.2 抓包核心代码实现

对于主界面的各个控件设置完成之后，需要对每个控件设置额外的监听事件，由于我们第一步操作就是需要选择网卡，那么就需要先设置网卡的监听事件。

```java
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
```

主要是调用了addActionListener()方法,

```java
 //AbstractButton.java文件
public void addActionListener(ActionListener l) {
        listenerList.add(ActionListener.class, l);
    }

//ActionListener.java文件
public interface ActionListener extends EventListener {

    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed(ActionEvent e);


```

从上面源码分析追溯可以看出需要重写actionPerformed方法。

设置完网卡的过滤之后需要设置对于协议的过滤。

```java
//以TCP协议为例
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
```

接下来是对源头IP/目的IP来设置监听事件，同样是重写actionPerformed方法。

```java
//以源IP为例
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
```

接下来是设置某条详细的数据包分析的鼠标点击事件。

```java
 public void mouseClicked(MouseEvent e) {
//                连续点击两次
                if (e.getClickCount()==2){
                    int row = table.getSelectedRow();
                    JFrame frame = new JFrame("数据包信息");
                    JPanel panel = new JPanel();
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
```

以上过程就完成了对各个控件的监听，有了监听之后便需要设置具体的抓包。

如果需要对具体数据包的抓取，主要是调用了JpcapCaptor.java文件里的各项API，核心是利用getPacket()方法,同时可以设置何时才开始抓取我们的数据包。

```java
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
```

#### 2.2.3 报文的分析代码

该模块分为IP数据包，TCP报文，UDP报文几个过程来进行分析，核心代码如下。

```java
//IP报文的分析
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

//TCP协议的分析
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
```

将解析后的信息都设置到att对象后然后展示在UI上面即可。

## 3、总结

通过以上对网络嗅探器的设计，让我对TCP协议，UDP协议，IP协议都有了一个更加清楚的了解，由于有了底层的第三方库的实现，因此开发的工作主要是熟悉各种API的使用以及UI界面的设计等方面。