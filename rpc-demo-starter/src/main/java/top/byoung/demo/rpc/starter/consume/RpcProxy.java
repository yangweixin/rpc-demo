package top.byoung.demo.rpc.starter.consume;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import top.byoung.demo.rpc.starter.discover.ServiceDiscovery;
import top.byoung.demo.rpc.starter.model.ProviderInfo;
import top.byoung.demo.rpc.starter.model.RpcRequest;
import top.byoung.demo.rpc.starter.model.RpcResponse;

import java.lang.reflect.Proxy;
import java.util.UUID;

/**
 * @description: RpcProxy
 * @author: Yang Weixin
 * @create: 2019/12/06
 */
@Component
public class RpcProxy {

    @Autowired
    private ServiceDiscovery serviceDiscovery;

    @SuppressWarnings("unchecked")
    public <T> T create(Class<?> interfaceClass, String providerName) {
        return (T) Proxy.newProxyInstance(interfaceClass.getClassLoader(), new Class<?>[]{ interfaceClass },
            (proxy, method, args) -> {

                RpcRequest request = new RpcRequest();
                request.setRequestId(UUID.randomUUID().toString())
                    .setClassName(method.getDeclaringClass().getName())
                    .setMethodName(method.getName())
                    .setParamTypes(method.getParameterTypes())
                    .setParams(args);

                ProviderInfo providerInfo = serviceDiscovery.discover(providerName);
                String[] addrInfo = providerInfo.getAddr().split(":");
                String host = addrInfo[0];
                int port = Integer.parseInt(addrInfo[1]);

                RpcClient rpcClient = new RpcClient(host, port);
                RpcResponse response = rpcClient.send(request);
                if (response.isError()) {
                    throw response.getError();
                } else {
                    return response.getResult();
                }
            });
    }

    public void setServiceDiscovery(ServiceDiscovery serviceDiscovery) {
        this.serviceDiscovery = serviceDiscovery;
    }
}
