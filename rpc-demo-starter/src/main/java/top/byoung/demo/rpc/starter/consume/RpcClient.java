package top.byoung.demo.rpc.starter.consume;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.byoung.demo.rpc.starter.model.RpcRequest;
import top.byoung.demo.rpc.starter.model.RpcResponse;
import top.byoung.demo.rpc.starter.util.RpcDecoder;
import top.byoung.demo.rpc.starter.util.RpcEncoder;

import java.util.concurrent.CompletableFuture;

/**
 * @description: RpcClient
 * @author: Yang Weixin
 * @create: 2019/12/06
 */
public class RpcClient extends SimpleChannelInboundHandler<RpcResponse> {

    private Logger logger = LoggerFactory.getLogger(RpcClient.class);

    private String host;
    private int port;
    private CompletableFuture<String> future;
    private RpcResponse response;

    public RpcClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public RpcResponse send(RpcRequest request) {
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap
                .group(workerGroup)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        socketChannel
                            .pipeline()
                            .addLast(new RpcEncoder(RpcRequest.class))
                            .addLast(new RpcDecoder(RpcResponse.class))
                            .addLast(RpcClient.this);
                    }
                }).option(ChannelOption.SO_KEEPALIVE, true);

            ChannelFuture channelFuture = bootstrap.connect(host, port).sync();
            channelFuture.channel().writeAndFlush(request).sync();

            future = new CompletableFuture<>();
            future.get();

            if (response != null) {
                // 关闭netty连接。
                channelFuture.channel().closeFuture().sync();
            }
            return response;
        } catch (Exception e) {
            logger.error("client send msg error,", e);
            return null;
        } finally {
            workerGroup.shutdownGracefully();
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcResponse rpcResponse) throws Exception {
        logger.info("client get request result,{}", rpcResponse);
        this.response = rpcResponse;
        future.complete("");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("netty client caught exception,", cause);
        ctx.close();
    }
}
