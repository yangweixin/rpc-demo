package top.byoung.demo.rpc.starter.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @description: RpcProperties
 * @author: Yang Weixin
 * @create: 2019/12/05
*/
@Configuration
@ConfigurationProperties(prefix = "spring.rpc")
public class RpcProperties {
    private String registerAddress = "server:2181";

    /**
     * rpc服务的端口
     */
    private int port = 21810;
    /**
     * 服务名称
     */
    private String serverName = "rpc";

    /**
     * 服务地址
     */
    private String host = "localhost";

    /**
     * 超时时间
     */
    private int timeout = 2000;

    public String getRegisterAddress() {
        return registerAddress;
    }

    public RpcProperties setRegisterAddress(String registerAddress) {
        this.registerAddress = registerAddress;
        return this;
    }

    public int getPort() {
        return port;
    }

    public RpcProperties setPort(int port) {
        this.port = port;
        return this;
    }

    public String getServerName() {
        return serverName;
    }

    public RpcProperties setServerName(String serverName) {
        this.serverName = serverName;
        return this;
    }

    public String getHost() {
        return host;
    }

    public RpcProperties setHost(String host) {
        this.host = host;
        return this;
    }

    public int getTimeout() {
        return timeout;
    }

    public RpcProperties setTimeout(int timeout) {
        this.timeout = timeout;
        return this;
    }
}
