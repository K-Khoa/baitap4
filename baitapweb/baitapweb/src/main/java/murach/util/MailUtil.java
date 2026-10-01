package murach.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Properties;

import jakarta.mail.Address;
import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import murach.business.User;

public class MailUtil {

    private static final Properties CFG = new Properties();

    static {
        try (InputStream in = MailUtil.class.getClassLoader().getResourceAsStream("mail.properties")) {
            if (in != null) {
                CFG.load(new InputStreamReader(in, StandardCharsets.UTF_8));
            }
        } catch (IOException e) {
            // CFG rỗng -> sendNotification sẽ báo lỗi rõ ràng
        }
    }

    /** Gửi mail thông báo có người vừa đăng ký tới mail.to. */
    public static void sendNotification(User user) throws MessagingException {
        final String username = CFG.getProperty("mail.username");
        final String password = CFG.getProperty("mail.password");
        String to = CFG.getProperty("mail.to", username);
        if (username == null || password == null || to == null) {
            throw new MessagingException("Chưa cấu hình mail.properties");
        }

        Properties props = new Properties();
        props.put("mail.smtp.host", CFG.getProperty("mail.smtp.host", "smtp.gmail.com"));
        props.put("mail.smtp.port", CFG.getProperty("mail.smtp.port", "587"));
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.connectiontimeout", "10000");
        props.put("mail.smtp.timeout", "10000");
        props.put("mail.smtp.writetimeout", "10000");

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        String fullName = (user.getFirstName() + " " + user.getLastName()).replaceAll("[\\r\\n]+", " ");
        String time = ZonedDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh"))
                .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));

        String body = "Có người vừa đăng ký email list:\n\n"
                + "First name: " + user.getFirstName() + "\n"
                + "Last name:  " + user.getLastName() + "\n"
                + "Email:      " + user.getEmail() + "\n"
                + "Thời gian:  " + time + "\n";

        MimeMessage msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress(username));
        msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
        msg.setReplyTo(new Address[] { new InternetAddress(user.getEmail(), true) });
        msg.setSubject("Email list: " + fullName, "UTF-8");
        msg.setText(body, "UTF-8");

        Transport.send(msg);
    }
}
