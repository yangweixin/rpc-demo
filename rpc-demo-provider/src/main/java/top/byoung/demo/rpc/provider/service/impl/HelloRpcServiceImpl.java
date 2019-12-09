package top.byoung.demo.rpc.provider.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.byoung.demo.rpc.api.service.HelloRpcService;
import top.byoung.demo.rpc.starter.annotation.RpcService;

/**
 * @description: HelloRpcServiceImpl
 * @author: Yang Weixin
 * @create: 2019/12/09
 */
@RpcService(HelloRpcService.class)
public class HelloRpcServiceImpl implements HelloRpcService {

    private final Logger logger = LoggerFactory.getLogger(HelloRpcService.class);

    @Override
    public String sayHello() {
        logger.info("-----> RPC recieve");
        return "Hello RPC!";
    }
}
