import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;

public class ImagePager extends JFrame {

    private List<String> imageURLs; // 图片URL列表
    private int currentIndex = 0; // 当前显示图片的索引
    private JLabel imageLabel;
    private String website;
    private JLabel pageNumberLabel; // 新增：用于显示当前页码的标签
    public ImagePager(String website) {

        super("图片翻页展示");
        this.website=website;
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        setLocationRelativeTo(null); // 窗口居中显示

        // 在contentPane添加显示页码的标签



        // 示例图片URL列表，请替换为实际图片URL
        imageURLs = ImageExtractor.getImageUrlList(website);

        System.out.println(imageURLs);

        // ...更多图片URL
        pageNumberLabel = new JLabel("第 " + (currentIndex + 1) + " 页 / 共 " + imageURLs.size() + " 页");
        pageNumberLabel.setHorizontalAlignment(SwingConstants.CENTER); // 文本居中显示

        JPanel contentPane = new JPanel(new BorderLayout());
        contentPane.add(pageNumberLabel, BorderLayout.NORTH); // 页码标签放置在顶部
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2)); // 用于放置按钮的面板
        buttonPanel.setPreferredSize(new Dimension(150, 50)); // 调整按钮面板大小
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // 外边距
        buttonPanel.setBackground(Color.WHITE); // 设置背景颜色以便与内容区分（可选）

        JButton backButton = new JButton("< 上一页");
        JButton forwardButton = new JButton("下一页 >");

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showPreviousImage();
            }
        });

        forwardButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showNextImage();
            }
        });

        // 设置按钮尺寸
        Dimension buttonSize = new Dimension(80, 25);
        backButton.setPreferredSize(buttonSize);
        forwardButton.setPreferredSize(buttonSize);

        buttonPanel.add(backButton);
        buttonPanel.add(forwardButton);

        contentPane.add(buttonPanel, BorderLayout.SOUTH); // 按钮面板放置在底部
        contentPane.add(imageLabel = new JLabel(), BorderLayout.CENTER); // 图片居中

        setContentPane(contentPane);
        loadAndShowImage(currentIndex);
        imageURLs  = filterNonFileUrls(imageURLs );
        System.out.println(imageURLs);
        if(imageURLs.size()>0) {
            setVisible(true);
        }else{
            JOptionPane.showMessageDialog(this,
                    "未爬取到任何图片",
                    "",
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    // ...其他方法保持不变
    public static List<String> filterNonFileUrls(List<String> urls) {
        // 使用Java 8 Stream API进行过滤
        List<String> filteredUrls = urls.stream()
                .filter(url -> url.contains(".") && !url.endsWith("/") && url.lastIndexOf('.') > url.lastIndexOf('/'))
                .collect(Collectors.toList());

        return filteredUrls;
    }

// 确保ImageExtractor类正确导入或定义getImageURLs方法


    public boolean hasImageUrlChanged(String newUrls) {
        if (newUrls == null || this.website == null) {
            return false; // 如果任一方为空，不认为是变更
        }
        return !this.website.equals(newUrls); // 比较两个列表是否相等
    }
    private void showPreviousImage() {
        if (currentIndex > 0) {
            currentIndex--;
            loadAndShowImage(currentIndex);
        }
    }

    private void showNextImage() {
        if (currentIndex < imageURLs.size() - 1) {
            currentIndex++;
            loadAndShowImage(currentIndex);
        }
    }

    private void loadAndShowImage(int index) {
        try {
            System.out.println(index);
            if(index==0) return;
            URL url = new URL(imageURLs.get(index));
            BufferedImage image = ImageIO.read(url);
            if (image == null) {
                System.err.println("无法从URL加载图片: " + url);
                return; // 或者处理错误的其他方式，比如显示默认图片或错误图标
            }
            ImageIcon icon = new ImageIcon(image);
            imageLabel.setIcon(icon);

            // 可以调整图像大小以适应标签
            imageLabel.revalidate(); // 重新验证布局
            imageLabel.repaint(); // 重绘组件
            pageNumberLabel.setText("第 " + (index + 1) + " 页 / 共 " + imageURLs.size() + " 页");
            imageLabel.revalidate();
            imageLabel.repaint();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "加载图片失败: " + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        }
    }


}