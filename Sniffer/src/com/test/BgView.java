package test;

import javax.swing.*;
import java.awt.*;

public abstract class BgView extends JDialog {

    private JLabel numberLabel = new JLabel("水果编号");
    private JLabel nameLabel = new JLabel("水果名称");
    private JLabel pinyinLabel = new JLabel("拼音名称");
    private JLabel priceLabel = new JLabel("水果单价");
    private JLabel unitLabel = new JLabel("计价单位");
    private JLabel inputNumLabel = new JLabel("输入编号");
    private JLabel inputNameLabel = new JLabel("输入名称");

    //添加功能组件
    protected JTextField NumberText = new JTextField(6); //编号文本框
    protected JTextField NameText = new JTextField(6); //名称文本框
    protected JTextField PriceText = new JTextField(6); //单价文本框
    protected JTextField UnitText = new JTextField(6); //计价单位文本框
    protected JTextField PinyinText = new JTextField(6); //拼音文本框，方便快捷搜索商品
    private JButton addBtn = new JButton("添加水果");   //添加按钮
    private JButton delBtn = new JButton("删除水果");   //删除按钮
    private JButton updataBtn = new JButton("修改水果");   //修改按钮
    private JButton findBtn = new JButton("查找水果");   //查找按钮

    public BgView(){
        this(null, true);
    }
    public BgView(Frame owner, boolean modal) {
        super(owner, modal);
        this.init();    //初始化操作
        this.addComponent();    //添加组件
        this.addListener();     //添加监听器
    }



    //初始化操作
    private void init(){
        this.setTitle("超市货物管理！");
        this.setSize(710, 600);
        this.setLocationRelativeTo(null);   //居中显示
        this.setResizable(false);
    }/*init*/

    //添加组件
    private void addComponent() {

        //添加菜单栏
        JMenuBar menuBar = new JMenuBar();  //菜单栏模板
        JMenu fileMenu = new JMenu("文件");   //补充一个文件菜单菜单
        JMenu shopMenu = new JMenu("商品");   //第一个菜单
        JMenu orderMenu = new JMenu("订单");   //第二个菜单
        menuBar.add(fileMenu);
        menuBar.add(shopMenu);
        menuBar.add(orderMenu);

        Dimension dimMenu = new Dimension(50, 30);
        fileMenu.setPreferredSize(dimMenu);
        shopMenu.setPreferredSize(dimMenu);
        orderMenu.setPreferredSize(dimMenu);
        this.setJMenuBar(menuBar);

        //取消布局
        this.setLayout(null);

        //字段标题
        numberLabel.setBounds(60, 330, 70, 25);
        nameLabel.setBounds(60, 370, 70, 25);
        pinyinLabel.setBounds(60, 410, 70, 25);
        priceLabel.setBounds(60, 450, 70, 25);
        unitLabel.setBounds(60, 490, 70, 25);
        this.add(numberLabel);
        this.add(nameLabel);
        this.add(pinyinLabel);
        this.add(priceLabel);
        this.add(unitLabel);

        //增加组件
        NumberText.setBounds(130, 330, 280, 25);
        NameText.setBounds(130, 370, 240, 25);
        PinyinText.setBounds(130, 410, 200, 25);
        PriceText.setBounds(130, 450, 160, 25);
        UnitText.setBounds(130, 490, 120, 25);
        this.add(NumberText);
        this.add(NameText);
        this.add(PinyinText);
        this.add(PriceText);
        this.add(UnitText);

        //添加按钮
        addBtn.setBounds(550, 440, 100,75);
        addBtn.setFocusable(false);
        this.add(addBtn);

    }/*addComponent*/

    //添加监听
    private void addListener() {
    }/*addListener*/
}
