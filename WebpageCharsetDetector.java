import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
public class WebpageCharsetDetector {
    public static String wordType(String url) {

        try {
            // 连接到URL并获取Document对象
            Document doc = Jsoup.connect(url).get();

            // 查找<meta>标签中声明的charset属性
            Element metaCharset = doc.select("meta[charset]").first();
            if (metaCharset != null) {
                return metaCharset.attr("charset");
            }

            // 如果没有直接的charset属性，尝试从content属性中解析
            Pattern pattern = Pattern.compile("charset\\s*=\\s*([\\w-]+)");
            for (Element meta : doc.select("meta")) {
                String content = meta.attr("content");
                Matcher matcher = pattern.matcher(content);
                if (matcher.find()) {
                    return matcher.group(1).toLowerCase(); // 返回匹配到的编码类型
                }
            }


            return null; // 没有找到明确的编码声明
        } catch (IOException e) {
            System.err.println("Error fetching URL: " + url);
            e.printStackTrace();
            return null;
        }

    }
    public static String extractDomain(String urlString) {
        int startIndex = urlString.indexOf("//") + 2; // 查找"www."并定位到其后
        if (startIndex <= 3) { // 如果未找到"www."
            return ""; // 返回空字符串
        }

        int endIndex = urlString.length()-1;


        return urlString.substring(startIndex, endIndex).replaceAll("/","斜杠"); // 提取子字符串
    }
}