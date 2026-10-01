package murach.util;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import murach.business.User;

/**
 * Gửi mail thông báo qua Resend bằng HTTPS (cổng 443),
 * nên chạy được cả trên các host chặn SMTP (Render free, Railway...).
 * Cấu hình bằng biến môi trường: RESEND_API_KEY, MAIL_TO, (tuỳ chọn) MAIL_FROM.
 */
public class MailUtil {

    private static final HttpClient HTTP = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10)).build();

    public static void sendNotification(User user) throws IOException, InterruptedException {
        String apiKey = System.getenv("RESEND_API_KEY");
        String to = System.getenv("MAIL_TO");
        String from = System.getenv().getOrDefault("MAIL_FROM", "Email List <onboarding@resend.dev>");
        if (apiKey == null || apiKey.isBlank() || to == null || to.isBlank()) {
            throw new IOException("Chưa đặt biến môi trường RESEND_API_KEY / MAIL_TO");
        }

        String time = ZonedDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh"))
                .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
        String body = "Có người vừa đăng ký email list:\n\n"
                + "First name: " + user.getFirstName() + "\n"
                + "Last name:  " + user.getLastName() + "\n"
                + "Email:      " + user.getEmail() + "\n"
                + "Thời gian:  " + time + "\n";

        String json = "{\"from\":" + q(from)
                + ",\"to\":[" + q(to) + "]"
                + ",\"reply_to\":" + q(user.getEmail())
                + ",\"subject\":" + q("Email list: " + user.getFirstName() + " " + user.getLastName())
                + ",\"text\":" + q(body) + "}";

        HttpRequest req = HttpRequest.newBuilder(URI.create("https://api.resend.com/emails"))
                .timeout(Duration.ofSeconds(15))
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8))
                .build();

        HttpResponse<String> res = HTTP.send(req, HttpResponse.BodyHandlers.ofString());
        if (res.statusCode() / 100 != 2) {
            throw new IOException("Resend trả về " + res.statusCode() + ": " + res.body());
        }
    }

    /** Biến chuỗi thành chuỗi JSON (có escape). */
    private static String q(String s) {
        StringBuilder sb = new StringBuilder("\"");
        for (char c : s.toCharArray()) {
            switch (c) {
                case '"':  sb.append("\\\""); break;
                case '\\': sb.append("\\\\"); break;
                case '\n': sb.append("\\n");  break;
                case '\r': sb.append("\\r");  break;
                case '\t': sb.append("\\t");  break;
                default:
                    if (c < 0x20) sb.append(String.format("\\u%04x", (int) c));
                    else sb.append(c);
            }
        }
        return sb.append('"').toString();
    }
}
