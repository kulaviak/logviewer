LogViewer
==============
Simple Java web application for viewing content of log directory and content of log files. There is no authentication and authorization. 

Configuration
-------------------
In web.xml there is directoryPath property which cofigures which directory should be shown.

How to build
-------------------
In the directory where is pom.xml file run "mvn package". It will create logs.war file in target directory which you can deploy on Tomcat. According to context path that you specify during deployment (If you just deploy logs.war it will be your_server_url/logs ) it will be accessible on your server. 
