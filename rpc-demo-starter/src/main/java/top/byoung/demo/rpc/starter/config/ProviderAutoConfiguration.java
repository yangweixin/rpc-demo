package top.byoung.demo.rpc.starter.config;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import top.byoung.demo.rpc.starter.annotation.RpcService;
import top.byoung.demo.rpc.starter.discover.RegistryServer;
import top.byoung.demo.rpc.starter.model.RpcRequest;
import top.byoung.demo.rpc.starter.model.RpcResponse;
import top.byoung.demo.rpc.starter.provider.BeanFactory;
import top.byoung.demo.rpc.starter.provider.ServerHandler;
import top.byoung.demo.rpc.starter.util.RpcDecoder;
import top.byoung.demo.rpc.starter.util.RpcEncoder;

import javax.annotation.PostConstruct;
import java.util.Map;

/**
 * @description: ProviderAutoConfiguration
 * @author: Yang Weixin
 * @create: 2019/12/06
 */
@Configuration
@ConditionalOnClass(RpcService.class)
@EnableConfigurationProperties(RpcProperties.class)
public class ProviderAutoConfiguration {

    private Logger logger = LoggerFactory.getLogger(ProviderAutoConfiguration.class);
    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    private RpcProperties rpcProperties;

    @PostConstruct
    public void init() {
        logger.info("rpc server start scanning provider service...");
        Map<String, Object> beanMap = applicationContext.getBeansWithAnnotation(RpcService.class);

        if (null != beanMap && !beanMap.isEmpty()) {
            beanMap.entrySet().forEach(one -> {
                initProviderBean(one.getKey(), one.getValue());
            });
        }

        logger.info("rpc server scan over...");
        if (!beanMap.isEmpty()) {
            startNetty(rpcProperties.getPort());
        }
    }

    private void initProviderBean(String beanName, Object bean) {
        RpcService rpcService = applicationContext.findAnnotationOnBean(beanName, RpcService.class);
        BeanFactory.addBean(rpcService.value(), bean);
    }

    public void startNetty(int port) {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap
                .group( bossGroup, workerGroup )
                .channel(NioServerSocketChannel.class)
                .childHandler(
                    new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast(new RpcDecoder(RpcRequest.class))
                                .addLast(new RpcEncoder(RpcResponse.class))
                                .addLast(new ServerHandler());
                        }
                    }
                ).option(ChannelOption.SO_BACKLOG, 128)
                .childOption(ChannelOption.SO_KEEPALIVE, true);

            ChannelFuture channelFuture =  bootstrap.bind(port).sync();
            logger.info("server started on port : {}", port);
            System.out.println(rpcProperties.getRegisterAddress());
            new RegistryServer(
                rpcProperties.getRegisterAddress(),
                rpcProperties.getTimeout(),
                rpcProperties.getServerName(),
                rpcProperties.getHost(),
                port
            ) .register();

            channelFuture.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
