# log-agent 
***
## 环境
jdk1.8、maven3.8.4

## 代码结构
* log-agent：基于javaagent和bytebuddy的日志组件，核心逻辑在LogAgent.java；MANIFEST.MF为javaagent机制要求的配置信息。
* log-agent-demo：使用log-agent组件的demo工程，基于spring boot实现的WEB工程，用于测试log-agent组件的日志打印。SimpleController.java内实现GET、POST和调用三方服务（透传）的请求。
## 运行测试
1. 进入log-agent目录（有pom文件的那个）
2. 运行mvn clean install
3. 构建成功后生成文件.\target\log-agent-0.1.0-jar-with-dependencies.jar 

4. 在log-agent-demo工程启动时增加jvm启动参数：--javaagent:D:\repository\com\fine\log-agent\0.1.0\log-agent-0.1.0-jar-with-dependencies.jar
5. 启动
6. 请求：curl --location --request POST 'localhost:8080/hi' --header 'accessID: 112233445566' --form 'name="anna"'
7. 后台输出日志：
![运行日志](./img/demoLog.png)


