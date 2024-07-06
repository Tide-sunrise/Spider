import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.net.URL;
public class ImageExtractor {

    public static List<String> getImageUrlList(String weburl) {
        List<String> urls = new ArrayList<>();
        urls.add(weburl); // 添加你想要分析的网页URL
        List<String> imageUrls = new ArrayList<>(); // 创建一个ArrayList来存储图片URL

        for (String url : urls) {
            try {
                // 使用Jsoup连接到URL并获取HTML文档
                Document document = Jsoup.connect(url).get();

                // 选择所有的<img>标签
                Elements imageElements = document.select("img");

                // 遍历所有<img>标签，将每个图片的src属性添加到imageUrls列表中
                for (Element imageElement : imageElements) {
                    String imageUrl = imageElement.attr("src");

                    // 检查是否以http:或https:开头，以避免重复添加
                    if (!imageUrl.startsWith("http:") && !imageUrl.startsWith("https:")&&!imageUrl.isEmpty()) {
                        // 如果URL不以http://或https://开头，假设其为相对路径，并添加https:
                        imageUrl = "https:" + imageUrl;

                    }
                    URL urlss = new URL(imageUrl);
                    HttpURLConnection connection = (HttpURLConnection) urlss.openConnection();
                    connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.3");
                    BufferedImage image = ImageIO.read(connection.getInputStream());
                    if(!imageUrl.isEmpty()&&image != null) {
                        imageUrls.add(imageUrl); // 将修正后的图片URL添加到列表}
                    }
                }
                LinkedHashSet<String> set = new LinkedHashSet<>(imageUrls);
                imageUrls = new ArrayList<>(set);
                // 打印当前URL下找到的所有图片URL
                System.out.println("从 " + url + " 提取的图片URL:");
                for (String imgUrl : imageUrls) {
                    System.out.println(imgUrl);
                }
                System.out.println("------------------------");
                return imageUrls;

            } catch (IOException e) {
                // 处理可能发生的网络连接或读取异常
                System.err.println("对于URL " + url + " 发生错误: " + e.getMessage());
            }
        }
            return urls;
    }
}