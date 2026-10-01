# baitapweb – Murach email list (Servlet + JSP)

Mỗi lần có người bấm **Join Now**, app lưu vào `EmailList.txt` và gửi mail thông báo cho chủ app (qua Resend).

## Chạy local
    export RESEND_API_KEY=re_xxx MAIL_TO=email_cua_ban@gmail.com
    mvn clean package          # ra target/baitapweb.war
    # chép WAR vào webapps/ của Tomcat 10+, hoặc:
    docker build -t baitapweb . && docker run -p 8080:8080 -e RESEND_API_KEY -e MAIL_TO baitapweb

## Deploy (push là tự deploy)
1. Đẩy repo lên GitHub, mời các bạn cùng nhóm làm collaborator.
2. Render -> New -> Blueprint -> chọn repo (đọc `render.yaml`).
3. Điền `RESEND_API_KEY` và `MAIL_TO` khi được hỏi. Xong.
Từ đó, ai push lên nhánh `main` thì Render tự build và cập nhật web.
