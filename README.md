# log-agent 
## 特性
* 自动打印：可在HTTP层、请求接口层、业务逻辑层和请求三方接口时自动打印响应方法执行信息的日志，解决没有详细日志协助问题排查的问题。
* 链路追踪：可根据traceID将每个请求内和不同服务的多个请求的日志进行关联，提高日志可读性和可用性。
* 清晰定义：基于对MDC和WEB业务的理解对系统运行不同场景的日志进行归纳，抽象出良好的日志定义和格式，便于ELK的导入、分析和监控。
## 环境
jdk1.8、maven3.8.4、IntelliJ IDEA(Community Edition)
## 代码结构
* log-agent：基于javaagent和bytebuddy的日志组件，核心逻辑在LogAgent.java；MANIFEST.MF为javaagent机制要求的配置信息。
* log-agent-demo：使用log-agent组件的demo工程，基于spring boot实现的WEB工程，用于测试log-agent组件的日志打印。SimpleController.java内实现GET、POST和调用三方服务（透传）的请求。
## 运行测试
1. 进入log-agent目录（有pom文件的那个）
2. 运行`mvn clean install`
3. 构建成功后在本地maven库目录下生成文件log-agent-0.1.0-jar-with-dependencies.jar 
4. 在log-agent-demo工程启动时增加jvm启动参数：
`-javaagent:本地maven库目录\repository\com\fine\log-agent\0.1.0\log-agent-0.1.0-jar-with-dependencies.jar`
5. 启动log-agent-demo工程
6. 请求：  
`curl --location --request POST 'localhost:8080/hi' --header 'traceID: 112233445566' --form 'name="anna"'`
7. 后台输出日志：
~~~
2022-10-09 18:18:30,401 INFO - [SERVICE] [112233445566] [0] [com.fine.example.log.service.SimpleService] [whatYouSay] [[]] [I am the guy with a song on my lips and love in my heart] [] [] [-]
2022-10-09 18:18:30,402 INFO - [ACCESS] [112233445566] [1] [com.fine.example.log.controller.SimpleController] [hi] [[anna]] [Hi anna! I am the guy with a song on my lips and love in my heart] [] [] [-]
2022-10-09 18:18:30,423 INFO - [HTTP] [112233445566] [72] [javax.servlet.http.HttpServlet] [service] [[[[Ljava.lang.String;@32b2c2a7]]] [null] [http://localhost:8080/hi] [112233445566,3d2414da-c2a3-4163-a3b4-f385a28b0e5b,] [-]
~~~
## 联系方式
javafine@163.com
