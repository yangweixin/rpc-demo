package top.byoung.demo.rpc.starter.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import top.byoung.demo.rpc.starter.consume.RpcProxy;
import top.byoung.demo.rpc.starter.discover.ServiceDiscovery;
import top.byoung.demo.rpc.starter.exception.ZkConnectException;

/**
 * @description: RpcAutoConfiguration
 * @author: Yang Weixin
 * @create: 2019/12/05
 */
@Configuration
@EnableConfigurationProperties(RpcProperties.class)
public class RpcAutoConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(RpcAutoConfiguration.class);

    @Autowired
    private RpcProperties rpcProperties;

    @Bean
    @ConditionalOnMissingBean
    public ServiceDiscovery serviceDiscovery() {
        ServiceDiscovery serviceDiscovery = null;
        try {
            serviceDiscovery = new ServiceDiscovery(rpcProperties.getRegisterAddress());
        } catch (ZkConnectException e) {
            logger.error("zk connect failed:", e);
        }

        return serviceDiscovery;
    }

    @Bean
    @ConditionalOnMissingBean
    public RpcProxy rpcProxy() {
        RpcProxy rpcProxy = new RpcProxy();
        rpcProxy.setServiceDiscovery(serviceDiscovery());
        return rpcProxy;
    }
}
