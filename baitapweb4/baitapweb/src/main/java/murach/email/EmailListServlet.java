package murach.email;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import murach.business.User;
import murach.data.UserIO;
import murach.util.MailUtil;

@WebServlet("/emailList")
public class EmailListServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String url = "/index.jsp";
        String action = request.getParameter("action");
        if (action == null) {
            action = "join";
        }

        if (action.equals("add")) {
            String firstName = clean(request.getParameter("firstName"));
            String lastName  = clean(request.getParameter("lastName"));
            String email     = clean(request.getParameter("email"));

            // Bản đã escape HTML để JSP hiển thị an toàn
            request.setAttribute("user", new User(esc(firstName), esc(lastName), esc(email)));

            if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty()) {
                request.setAttribute("message", "Please fill out all three text boxes.");
            } else if (!validEmail(email)) {
                request.setAttribute("message", "Please enter a valid email address.");
            } else {
                User user = new User(firstName, lastName, email);

                String path = getServletContext().getRealPath("/WEB-INF/EmailList.txt");
                if (path == null) {
                    path = System.getProperty("java.io.tmpdir") + "/EmailList.txt";
                }
                UserIO.add(user, path);

                try {
                    MailUtil.sendNotification(user);
                } catch (Exception e) {
                    // Không làm hỏng trang cảm ơn; xem lỗi trong log của server
                    log("Không gửi được mail thông báo", e);
                }
                url = "/thanks.jsp";
            }
        }

        getServletContext().getRequestDispatcher(url).forward(request, response);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doPost(request, response);
    }

    private static String clean(String s) {
        return s == null ? "" : s.trim().replaceAll("[\\r\\n]+", " ");
    }

    private static boolean validEmail(String email) {
        return email.length() <= 254 && email.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");
    }

    private static String esc(String s) {
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
                .replace("\"", "&quot;").replace("'", "&#39;");
    }
}
