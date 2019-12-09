package top.byoung.demo.rpc.consumer.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.byoung.demo.rpc.api.service.HelloRpcService;
import top.byoung.demo.rpc.starter.annotation.RpcConsumer;

/**
 * @description: hello rpc controller
 * @author: Yang Weixin
 * @create: 2019/12/09
 */
@RestController
@RequestMapping("/rpc")
public class HelloRpcController {

    @RpcConsumer(providerName = "provider")
    public HelloRpcService helloRpcService;

    @GetMapping("/say")
    public String sayByRpc() {
        return helloRpcService.sayHello();
    }
}
