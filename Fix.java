import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.charset.StandardCharsets;

public class Fix {
    public static void main(String[] args) throws Exception {
        String path = "src/main/java/com/samtech/carapp/HtmlRenderer.java";
        String content = new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);

        String o1 = "<img src=\"/logo.png\" alt=\"Autosphere\" height=\"40\" class=\"brand-logo\" style=\"margin-right:10px;\"/>\n                        <h1>Autosphere</h1>";
        String n1 = "<img src=\"/logo.png\" alt=\"Autosphere\" class=\"brand-logo\"/>";
        content = content.replace(o1, n1);

        String o2 = "<img src=\"/logo.png\" alt=\"Autosphere\" height=\"40\" class=\"brand-logo\" style=\"margin-right:10px;\"/>\n                        <h1>Autosphere Seller Panel</h1>";
        String n2 = "<img src=\"/logo.png\" alt=\"Autosphere\" class=\"brand-logo\"/>\n                        <h2 style=\"margin-left: 10px; color: #64748b; font-size: 1.2rem;\">Seller Panel</h2>";
        content = content.replace(o2, n2);

        String o3 = "<img src=\"/logo.png\" alt=\"Autosphere\" height=\"40\" class=\"brand-logo\" style=\"margin-right:10px;\"/>\n                        <h1>Autosphere Admin Panel</h1>";
        String n3 = "<img src=\"/logo.png\" alt=\"Autosphere\" class=\"brand-logo\"/>\n                        <h2 style=\"margin-left: 10px; color: #64748b; font-size: 1.2rem;\">Admin Panel</h2>";
        content = content.replace(o3, n3);

        String o4 = "<img src=\"/logo.png\" alt=\"Autosphere\" height=\"80\" />";
        String n4 = "<img src=\"/logo.png\" alt=\"Autosphere\" style=\"height: 80px; max-width: 100%; object-fit: contain;\" />";
        content = content.replace(o4, n4);

        // Also fix the carriage return issue just in case
        String o1_cr = "<img src=\"/logo.png\" alt=\"Autosphere\" height=\"40\" class=\"brand-logo\" style=\"margin-right:10px;\"/>\r\n                        <h1>Autosphere</h1>";
        content = content.replace(o1_cr, n1);

        String o2_cr = "<img src=\"/logo.png\" alt=\"Autosphere\" height=\"40\" class=\"brand-logo\" style=\"margin-right:10px;\"/>\r\n                        <h1>Autosphere Seller Panel</h1>";
        content = content.replace(o2_cr, n2);

        String o3_cr = "<img src=\"/logo.png\" alt=\"Autosphere\" height=\"40\" class=\"brand-logo\" style=\"margin-right:10px;\"/>\r\n                        <h1>Autosphere Admin Panel</h1>";
        content = content.replace(o3_cr, n3);

        Files.write(Paths.get(path), content.getBytes(StandardCharsets.UTF_8));
        System.out.println("Replaced!");
    }
}
