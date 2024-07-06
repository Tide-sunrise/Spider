import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter.DefaultHighlightPainter;
import javax.swing.text.Highlighter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Spider_Frame extends JFrame implements ActionListener{

    private JPanel jPanel=new JPanel();
    private JPanel jpl1=new JPanel();
    private JPanel jpl2=new JPanel();
    private JPanel jpl3=new JPanel();

    private JPanel jpl4=new JPanel();
    private JPanel jpl5=new JPanel();
    private JPanel jpl6=new JPanel();
    private JPanel jpl7=new JPanel();
    private JPanel jpl8=new JPanel();
    private JLabel siteWarn=new JLabel("输入网址:");
    private JTextField siteField=new JTextField(25);
    private JScrollPane siteSPane=new JScrollPane(siteField);
    private JButton goSpider=new JButton("开始爬取");
    private JTextArea htmlArea=new JTextArea(15, 25);
    private JScrollPane htmlSPane=new JScrollPane(htmlArea);
    private JTextArea textArea=new JTextArea(15,25);
    private JScrollPane textSPane=new JScrollPane(textArea);
    private JTabbedPane tabPane=new JTabbedPane();
    private JTextArea sensWord=new JTextArea(8,25);
    private JScrollPane wordPane=new JScrollPane(sensWord);
    private JButton openLib=new JButton(" 导入敏感词库");
    private JButton match=new JButton("匹配");
    private JButton showImagesButton = new JButton("查看图片");
    private JButton siteLib=new JButton("导入网址库");
    private JComboBox<String> charset=new JComboBox<String>();
    private String textType="UTF-8";

    private ArrayList<String> wordList=new ArrayList<String>();		//保存敏感词
    private ArrayList<Integer> wordNum=new ArrayList<Integer>();	//保存对应敏感词的出现次数
    //设置正则表达式的匹配符
    private String regExHtml="<[^>]+>";		//匹配标签
    private String regExScript = "<script[^>]*?>[\\s\\S]*?<\\/script>";		//匹配script标签
    private String regExStyle = "<style[^>]*?>[\\s\\S]*?<\\/style>";		//匹配style标签
    private String regExSpace="[\\s]{2,}";	//匹配连续空格或回车等
    private String regExImg="&[\\S]*?;+";	//匹配网页上图案的乱码
    //定义正则表达式
    private Pattern pattern3=Pattern.compile(regExHtml, Pattern.CASE_INSENSITIVE);
    private Pattern pattern1=Pattern.compile(regExScript,Pattern.CASE_INSENSITIVE);
    private Pattern pattern2=Pattern.compile(regExStyle,Pattern.CASE_INSENSITIVE);
    private Pattern pattern4=Pattern.compile(regExSpace, Pattern.CASE_INSENSITIVE);
    private Pattern pattern5=Pattern.compile(regExImg,Pattern.CASE_INSENSITIVE);

    private JToggleButton metalButton;
    private JToggleButton nimbusButton;
    private JButton windowsButton; // 注意：Windows风格需在Windows系统上才有效
    private ImagePager imagePager;
    private static String website;
    public Spider_Frame() throws IOException {
        //设置界面风格
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (ClassNotFoundException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } catch (InstantiationException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } catch (IllegalAccessException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } catch (UnsupportedLookAndFeelException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        this.setTitle("Spider");
        this.setLocation(400, 200);
        this.setSize(900, 500);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        jPanel.setLayout(new BorderLayout());
        metalButton = new JToggleButton("金属材质");
        nimbusButton = new JToggleButton("尼泊尔材质");
        windowsButton = new JButton("Windows原版"); // Windows风格直接作为普通按钮处理
        //添加按钮组
        ButtonGroup group = new ButtonGroup();
        group.add(metalButton);
        group.add(nimbusButton);
        group.add(windowsButton);
        // 设置布局
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(metalButton);
        buttonPanel.add(nimbusButton);
        buttonPanel.add(windowsButton);
        // 添加动作监听器
        metalButton.addActionListener(this);
        nimbusButton.addActionListener(this);
        windowsButton.addActionListener(this);
        // 添加按钮面板到窗体
        add(buttonPanel, BorderLayout.SOUTH);

        // 初始化界面风格
        initializeLookAndFeel();
        //添加编码方式
        charset.addItem("UTF-8");
        charset.addItem("GBK");
        charset.addItem("自动识别");
        charset.setEditable(false);    //设置为不可编辑
        //处理其事件,更新编码方式
        charset.addActionListener(new ActionListener() {
            //获取选择的编码方式,默认情况下为UTF-8
            @Override
            public void actionPerformed(ActionEvent e) {
                // TODO Auto-generated method stub

                textType=(String)charset.getSelectedItem();
            }
        });

        //界面处理，提醒输入网址,爬取按钮,以及编码方式选择
        jpl1.setLayout(new BorderLayout());
        siteWarn.setPreferredSize(new Dimension(70,30));
        siteSPane.setPreferredSize(new Dimension(300, 30));
        goSpider.setPreferredSize(new Dimension(90, 30));
        jpl5.setLayout(new GridLayout(1, 2, 10,10));
        jpl5.add(goSpider);
        jpl5.add(charset);
        jpl1.add(siteWarn,BorderLayout.WEST);
        jpl1.add(siteSPane,BorderLayout.CENTER);
        jpl1.add(jpl5,BorderLayout.EAST);
        //源代码文本,以及处理后的文本框设置
        htmlArea.setEditable(false);
        htmlArea.setLineWrap(true);
        htmlArea.setFont(new Font("宋体", Font.PLAIN, 14));
        jpl2.setLayout(new BorderLayout());
        jpl2.add(htmlSPane,BorderLayout.CENTER);
        //设置布局
        jpl8.setLayout(new GridLayout(2, 1, 10,5));
        jpl8.add(siteLib);
        jpl8.add(openLib);

        jpl3.setLayout(new BorderLayout());
        sensWord.setLineWrap(true);
        sensWord.setEditable(false);
        wordPane.setPreferredSize(new Dimension(6, 400));
        jpl3.add(jpl8,BorderLayout.NORTH);
        jpl3.add(wordPane,BorderLayout.CENTER);
        jpl8.add(match,BorderLayout.SOUTH);
        jpl8.add(showImagesButton,BorderLayout.WEST);
        showImagesButton.addActionListener(e -> {
            String website=siteField.getText();
            if (imagePager == null) {
                // 如果是第一次点击，创建ImagePager实例
                JOptionPane.showMessageDialog(this,
                        "警告：没有有效的爬取网址。",
                        "缺少网址",
                        JOptionPane.WARNING_MESSAGE);
            }else if (imagePager.hasImageUrlChanged(website)){
                JOptionPane.showMessageDialog(this,
                        "警告：网址已更改。",
                        "网址错误",
                        JOptionPane.WARNING_MESSAGE);
            }
            else {
                imagePager.setVisible(true); // 显示ImagePager窗口
            }
        });

        textArea.setFont(new Font("宋体", Font.PLAIN, 14));
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        jpl4.setLayout(new BorderLayout());
        jpl4.add(textSPane,BorderLayout.CENTER);

        tabPane.add("html源代码", jpl2);
        tabPane.add("网页文本",jpl4);
        jpl7.setLayout(new BorderLayout());
        jpl7.add(tabPane,BorderLayout.CENTER);

        jpl6.setLayout(new BorderLayout());
        jpl6.add(jpl7,BorderLayout.CENTER);
        jpl6.add(jpl3,BorderLayout.EAST);

        jPanel.add(jpl1,BorderLayout.NORTH);
        jPanel.add(jpl6,BorderLayout.CENTER);
        this.add(jPanel);
        this.setVisible(true);

        //事件处理
        goSpider.addActionListener(this);
        siteLib.addActionListener(this);
        openLib.addActionListener(this);
        match.addActionListener(this);
    }
    //初始化界面风格
    private void initializeLookAndFeel() {
        try {
            // 这里可以设置初始界面风格，默认使用Metal
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //使用URL爬取网页的html代码
    public String getHtml(String website, String textType) throws IOException {
        this.website=website;
        URL url = new URL(website);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.3");
        connection.setConnectTimeout(10000); // 设置连接超时时间，这里是10秒
        connection.setReadTimeout(10000); // 设置读取超时时间，这里是10秒

        try (InputStream inputStream = connection.getInputStream();
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, length);
            }
            byte[] bytes = outputStream.toByteArray();
            return new String(bytes, textType); // 使用指定的编码转换字节流为字符串
        } finally {
            connection.disconnect();
        }
    }

    //对html进行正则匹配,提取出其中的文本
    public String getText(String str) {
        // 新增提取<meta>标签中的content属性内容
        List<String> metaContents = extractMetaContent(str);

        // 你的原始逻辑处理字符串，去除HTML标签等
        Matcher matcher = pattern1.matcher(str);
        str = matcher.replaceAll("");       // 匹配普通标签
        matcher = pattern2.matcher(str);
        str = matcher.replaceAll("");       // 匹配script标签
        matcher = pattern3.matcher(str);
        str = matcher.replaceAll("");       // 匹配style标签
        matcher = pattern4.matcher(str);
        str = matcher.replaceAll("\n");     // 匹配连续回车或空格
        matcher = pattern5.matcher(str);
        str = matcher.replaceAll("");       // 匹配网页图案出现的乱码

        // 将提取的<meta>标签内容加入到最终文本中，这里简单地追加，具体格式根据需求调整
        if (!metaContents.isEmpty()) {
            for (String content : metaContents) {
                // 根据需要决定如何整合这些内容，比如换行分隔、直接拼接等
                str += "\n" + content;
            }
        }

        return str;                         // 返回整合后的文本内容
    }

    // 新增方法：提取<meta>标签中的content属性内容
    private List<String> extractMetaContent(String html) {
        List<String> contentsWithChinese = new ArrayList<>();
        Pattern metaPattern = Pattern.compile("<meta[^>]*content\\s*=\\s*['\"](.*?)['\"][^>]*>", Pattern.CASE_INSENSITIVE);
        Matcher matcher = metaPattern.matcher(html);

        while (matcher.find()) {
            String content = matcher.group(1).trim(); // 获取并清理content属性的值
            // 使用正则表达式检查content是否包含至少一个中文字符
            if (Pattern.compile("[\\u4e00-\\u9fa5]").matcher(content).find()) {
                contentsWithChinese.add(content);
            }
        }

        System.out.println(contentsWithChinese);
        return contentsWithChinese;
    }
    //从文件中读取敏感词
    public void getLib() {
        JFileChooser fChooser=new JFileChooser();	//文件选择框
        int ok=fChooser.showOpenDialog(this);
        if(ok!=JFileChooser.APPROVE_OPTION)	return;	//判断是否正常选择
        wordList.clear();	//清空之前的记录
        sensWord.setText("");
        File choosenLib=fChooser.getSelectedFile();	//获取选择的文件
        BufferedReader br=null;
        try {	//读取选中文件中的记录
            br=new BufferedReader(new FileReader(choosenLib));
            while(true) {
                String str=br.readLine();
                if(str==null)	break;
                wordList.add(str);	//添加到记录中
                wordNum.add(0);		//设置对应的初始值
                sensWord.append(str+"\n");	//添加到界面中
            }
            br.close();	//关闭文件流
        } catch (FileNotFoundException e1) {
            // TODO Auto-generated catch block
            JOptionPane.showMessageDialog(null, "文件不存在");
            e1.printStackTrace();
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            JOptionPane.showMessageDialog(null, "文件读取失败");
            e1.printStackTrace();
        }
    }

    //高亮显示
    public void showSensword() {

        Highlighter hg = textArea.getHighlighter();
        hg.removeAllHighlights();
        String text = textArea.getText();
        DefaultHighlightPainter painter = new DefaultHighlightPainter(Color.YELLOW);

        // 创建一个HashMap用于统计敏感词出现次数
        Map<String, Integer> wordCount = new HashMap<>();

        for (String str : wordList) {
            int index = 0;
            while ((index = text.indexOf(str, index)) >= 0) {
                try {
                    hg.addHighlight(index, index + str.length(), painter);
                    index += str.length();

                    // 统计敏感词出现次数
                    wordCount.put(str, wordCount.getOrDefault(str, 0) + 1);
                } catch (BadLocationException e) {
                    e.printStackTrace();
                }
            }
        }

        // 构建统计信息的字符串
        StringBuilder message = new StringBuilder("敏感词统计结果：\n");
        for (Map.Entry<String, Integer> entry : wordCount.entrySet()) {
            message.append(entry.getKey()).append(": ").append(entry.getValue()).append("次\n");
        }

        // 弹窗显示统计结果
        displayStatsDialog(wordCount);

    }

    //敏感词统计弹窗
    private void displayStatsDialog(Map<String, Integer> wordCount) {
        // 创建一个新的JDialog作为统计结果显示窗口
        JDialog dialog = new JDialog(this, "敏感词统计", true); // 第二个参数是窗口标题，第三个参数表示是否模态
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(null); // 窗口居中显示

        // 使用JTextArea来展示统计信息，因为它可以很好地显示多行文本
        JTextArea statsTextArea = new JTextArea();
        statsTextArea.setEditable(false); // 设置为不可编辑
        JScrollPane scrollPane = new JScrollPane(statsTextArea);
        dialog.getContentPane().add(scrollPane, BorderLayout.CENTER); // 将滚动面板添加到对话框中

        // 构建统计信息的字符串
        StringBuilder message = new StringBuilder();
        for (Map.Entry<String, Integer> entry : wordCount.entrySet()) {
            message.append(entry.getKey()).append(": ").append(entry.getValue()).append("次\n");
        }
        statsTextArea.setText(message.toString());

        // 添加“确定”按钮，点击后关闭对话框
        JButton okButton = new JButton("确定");
        okButton.addActionListener(e -> dialog.dispose());
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(okButton);
        dialog.getContentPane().add(buttonPanel, BorderLayout.SOUTH);

        // 显示对话框
        dialog.setVisible(true);
    }
    //爬取网址库中的网址
    public void spiderAll() {

        if(wordNum.size()<=0) {		//判断是否选择了敏感词库
            JOptionPane.showMessageDialog(null, "请先选择敏感词库");
            return;
        }
        JFileChooser fChooser=new JFileChooser();	//选择网库文件
        int ok=fChooser.showOpenDialog(this);
        if(ok!=JFileChooser.APPROVE_OPTION)	return;
        File file=fChooser.getSelectedFile();
        new SpiderAll(this, file).start();	//开启线程爬取
    }



    @Override
    public void actionPerformed(ActionEvent e) {
        // TODO Auto-generated method stub
        try {
//        JButton j=(JButton)e.getSource();	//判断操作来源
        Object source = e.getSource();
        if(source==goSpider) {	//爬取单个网址
            String website=siteField.getText();
            new SpiderOne(this,website).start();
        }else if (source==openLib) {		//打开敏感词库
            getLib();
        }else if (source==match){	//匹配单个网址的敏感词高亮显示
            showSensword();

        }else if(source==siteLib) {	//爬取网址库中的全部网址
            spiderAll();
        } else if (source == metalButton) {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } else if (source == nimbusButton) {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } else if (source == windowsButton && UIManager.getSystemLookAndFeelClassName().contains("Windows")) {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
            SwingUtilities.updateComponentTreeUI(this);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    //爬取单个网址线程
    class SpiderOne extends Thread{
        private String website=null;	//网页链接
        private MyProgressBar mpb=null;	//进度条
        //构造函数初始化
        public SpiderOne(JFrame fa,String s) {
            website=s;
            mpb=new MyProgressBar(fa, "Spiding");
        }

        public void run() {
            if(website.length()<=0) {	//判断网址是否正常
                JOptionPane.showMessageDialog(null, "网址不能为空");
                return;
            }
            imagePager=new ImagePager(website);
//            SwingUtilities.invokeLater(() -> imagePager);
            htmlArea.setText("");	//清除文本
            textArea.setText("");
            mpb.setText("爬取"+website+"中...");	//设置进度条界面标题
            mpb.setVisible(true);		//显示进度条
            if(textType.equals("自动识别")){
                textType=WebpageCharsetDetector.wordType(website);
            }
            String html= null;	//开始爬取
            try {
                html = getHtml(website,textType);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            textType="自动识别";
            mpb.dispose();	//关闭进度条
            if(html.length()>0) {	//若爬取正常
                JOptionPane.showMessageDialog(null, "爬取成功");	//提示完成
                htmlArea.append(html);	//显示html源代码
                String text=getText(html);	//匹配网页文本
                textArea.append(text);	//显示网页文本

            }
        }

    }

    //爬取网址库
    class SpiderAll extends Thread{
        private File file=null;		//网址库文本文件
        private MyProgressBar mpb=null;		//进度条
        //构造函数初始化
        public SpiderAll(JFrame fa,File f) {
            file=f;
            mpb=new MyProgressBar(fa, "Spiding");
        }

        public void run() {
            try {
                //读取网址库中的网址
                BufferedReader brr=new BufferedReader(new FileReader(file));
                //将匹配数据写入文本中
                PrintStream ps=new PrintStream(new File("敏感词记录结果.txt"));
                ps.println("敏感词记录如下:");
                int size=wordList.size();
                mpb.setVisible(true);	//显示进度条
                while(true) {

                    String website=brr.readLine();
                    if(website==null)	break;
                    mpb.setText("爬取"+website+"中...");	//设置进度条界面标题
                    ps.println(website+"数据如下: ");
                    String str=WebpageCharsetDetector.wordType(website);
                    String html=getHtml(website,str);	//获取html代码
                    String text=getText(html);		//匹配网页文本
                    saveHtmlToFile(html,website);
                    saveTextToFile(text,website);
                    ps.println("在"+str+"文件编码下：");
                    for(int i=0;i<size;i++) {		//在网页文本中进行匹配
                        String word=wordList.get(i);
                        int index=0,account=0,len=word.length();
                        while((index=text.indexOf(word,index))>=0) {
                            account++;
                            int temp=wordNum.get(i);	//更新数据
                            wordNum.set(i,++temp);
                            index+=len;		//更新匹配条件
                        }
                        ps.println(word+"  出现  "+account+"次");	//写入当前数据
                    }
                    ps.println();

                }
                brr.close();	//关闭文件流
                System.out.println("爬取完毕");
                ps.println("总数据如下:     ");		//写入总数据
                for(int i=0;i<size;i++) {
                    ps.println(wordList.get(i)+"  出现    "+wordNum.get(i)+"次");
                }
                ps.close();		//关闭文件流
                JOptionPane.showMessageDialog(null, "爬取完毕！请打开文件查看!");
            }catch (Exception e) {
                // TODO: handle exception
                JOptionPane.showMessageDialog(null, "爬取失败");
            }finally {
                mpb.dispose();	//关闭进度条
            }
        }
        private void saveHtmlToFile(String text,String website) {
            try {
                File file = new File(STR."\{WebpageCharsetDetector.extractDomain(website)}源代码.txt");
                // 使用BufferedWriter写入文件，提高效率
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                    writer.write(text);
                    System.out.println("HTML源代码已保存至源代码.txt");
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(null, "保存HTML到文件时发生错误：" + e.getMessage());
                    e.printStackTrace();
                }
            } catch (SecurityException se) {
                JOptionPane.showMessageDialog(null, "没有足够的权限保存文件：" + se.getMessage());
                se.printStackTrace();
            }
        }
        private void saveTextToFile(String html,String website) {
            try {
                File file = new File(STR."\{WebpageCharsetDetector.extractDomain(website)}文本.txt");
                // 使用BufferedWriter写入文件，提高效率
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                    writer.write(html);
                    System.out.println("文本已保存");
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(null, "保存HTML到文件时发生错误：" + e.getMessage());
                    e.printStackTrace();
                }
            } catch (SecurityException se) {
                JOptionPane.showMessageDialog(null, "没有足够的权限保存文件：" + se.getMessage());
                se.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws IOException {
        new Spider_Frame();
    }
}



