import javax.swing.*;
import java.awt.*;

//进度条设计
class MyProgressBar extends JDialog {

    private static final long serialVersionUID = 1L;
    private JPanel jPanel=new JPanel();
    private JProgressBar jpb=new JProgressBar();	//进度条
    private JLabel curSpiding=new JLabel();	//显示当前网址
    //构造函数初始化,设置父窗口以及标题
    public MyProgressBar(JFrame f,String title) {
        super(f,title);
        this.setLocation(f.getWidth()/2+(int)f.getLocation().getX()/2-80, f.getHeight()/2+(int)f.getLocation().getY()/2-30);
        this.setSize(400,100);
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        jpb.setString("冲刺冲刺...");
        jpb.setIndeterminate(true);		//设置进度条为不确定模式
        jpb.setStringPainted(true);
        jpb.setBorderPainted(false);
        jpb.setForeground(Color.RED);	//设置进度条颜色
        jpb.setBackground(Color.BLUE);	//设置背景
        curSpiding.setPreferredSize(new Dimension(200, 30));

        //界面布局
        jPanel.setLayout(new BorderLayout());
        jPanel.add(curSpiding,BorderLayout.NORTH);
        jPanel.add(jpb,BorderLayout.CENTER);
        this.add(jPanel);
    }

    //设置当前爬取网址接口
    public void setText(String text) {curSpiding.setText(text);}
}