package xmw.exa.util;

public class HtmlUtil {
    private HtmlUtil() {
    }

    public static String createLayout(String title, String message) {
        return "<html>" +
                "<head><title>" + title + "</title></head>" +
                "<body>" + message + "</body></html>";
    }

    public static String createPageLayout(String title, String message) {
        return HtmlUtil.createLayout(title, "<h1>" + title + "</h1>" + "<main>" + message + "</main>");
    }
}
