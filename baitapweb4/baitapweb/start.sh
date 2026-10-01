#!/bin/sh
# Nền tảng cloud truyền cổng qua biến PORT -> cho Tomcat nghe đúng cổng đó
sed -i "s/Connector port=\"8080\"/Connector port=\"${PORT:-8080}\"/" /usr/local/tomcat/conf/server.xml
exec catalina.sh run
