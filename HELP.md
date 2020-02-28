# 基于本地服务器的简易文件服务使用说明

### 配置与启动

修改系统配置文件：src/main/resources/application.properties

* server.port：服务器端口号
* spring.servlet.multipart.max-file-size：单文件限制大小
* spring.servlet.multipart.max-request-size：整个请求限制大小
* spring.servlet.multipart.location：接收文件的临时目录（默认在系统临时目录，长时间不用会被系统自动删除）
* spring.mvc.static-path-pattern：静态文件请求路径模式
* spring.resources.static-locations：将该文件夹作为静态资源文件夹扫描
* files.upload.path：文件上传路径

上诉配置仅需要修改files.upload.path为您的实际路径，若不配置该项，则默认使用SpringBoot自带tomcat容器的位置
spring.servlet.multipart.location根据服务器情况按需配置

### 使用方法

调用接口：IP:PORT/file，入参file，内容为文件，即可完成上传操作。接口返回文件访问路径。
调用接口：IP:PORT/files，入参files，内容为文件数组，即可完成多文件上传操作。接口返回各个文件的访问路径。
额外功能：当上传的文件是视频mp4时候，接口会返回视频缩略图的访问路径。