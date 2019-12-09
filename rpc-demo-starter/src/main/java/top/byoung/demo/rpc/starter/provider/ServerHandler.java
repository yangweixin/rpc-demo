package top.byoung.demo.rpc.starter.provider;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.byoung.demo.rpc.starter.model.RpcRequest;
import top.byoung.demo.rpc.starter.model.RpcResponse;

import java.lang.reflect.Method;

/**
 * @description: ServerHandler
 * @author: Yang Weixin
 * @create: 2019/12/06
 */
public class ServerHandler extends SimpleChannelInboundHandler<RpcRequest> {

    private static final Logger logger = LoggerFactory.getLogger(ServerHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcRequest request) throws Exception {

        logger.info("provider accept request,{}", request);
        RpcResponse rpcResponse = new RpcResponse();
        rpcResponse.setRequestId(request.getRequestId());

        try {
            Object result = handle(request);
            rpcResponse.setResult(result);
        } catch (Exception e) {
            rpcResponse.setError(e);
        }

        ctx.writeAndFlush(rpcResponse).addListener(ChannelFutureListener.CLOSE);
    }

    private Object handle(RpcRequest request) throws Exception{
        String className = request.getClassName();
        Class cls = Class.forName(className);
        Object obj = BeanFactory.getBean(cls);

        String methodName = request.getMethodName();
        Class<?>[] paramsTypes = request.getParamTypes();
        Object[] params = request.getParams();

        Method method = cls.getMethod(methodName, paramsTypes);
        Object result = method.invoke(obj, params);

        return result;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("netty provider caught error,", cause);
        ctx.close();
    }
}
