package top.byoung.demo.rpc.starter.config;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import top.byoung.demo.rpc.starter.annotation.RpcConsumer;
import top.byoung.demo.rpc.starter.consume.RpcProxy;

import java.lang.reflect.Field;

/**
 * @description: ConsumerAutoConfiguration
 * @author: Yang Weixin
 * @create: 2019/12/06
 */
@Configuration
@ConditionalOnClass(RpcConsumer.class)
@EnableConfigurationProperties(RpcProperties.class)
public class ConsumerAutoConfiguration {

    @Autowired
    private RpcProxy rpcProxy;

    @Bean
    public BeanPostProcessor beanPostProcessor() {
        return new BeanPostProcessor() {
            @Override
            public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {

                Class<?> objClz = bean.getClass();
                for (Field field : objClz.getDeclaredFields()) {
                    RpcConsumer rpcConsumer = field.getAnnotation(RpcConsumer.class);
                    if (null != rpcConsumer) {
                        Class<?> type = field.getType();
                        field.setAccessible(true);
                        try {
                            field.set(bean, rpcProxy.create(type, rpcConsumer.providerName()));
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        } finally {
                            field.setAccessible(false);
                        }
                    }
                }

                return bean;
            }
        };
    }
}
