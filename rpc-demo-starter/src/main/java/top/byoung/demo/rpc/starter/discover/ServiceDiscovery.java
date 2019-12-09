package top.byoung.demo.rpc.starter.discover;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.byoung.demo.rpc.starter.exception.ZkConnectException;
import top.byoung.demo.rpc.starter.model.ProviderInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

/**
 * @description: ServiceDiscovery
 * @author: Yang Weixin
 * @create: 2019/12/05
 */
public class ServiceDiscovery {

    private Logger logger = LoggerFactory.getLogger(ServiceDiscovery.class);
    private volatile List<ProviderInfo> dataList = new ArrayList<>();

    public ServiceDiscovery(String registoryAddress) throws ZkConnectException {
        try {
            ZooKeeper zooKeeper = new ZooKeeper(registoryAddress, 2000, new Watcher() {
                @Override
                public void process(WatchedEvent event) {
                    logger.info("consumer connect zk success!");
                }
            });

            watchNode(zooKeeper);
        } catch (Exception e) {
            throw new ZkConnectException("connect to zk exception," + e.getMessage(), e.getCause());
        }
    }

    public void watchNode(final ZooKeeper zk) {
        try {
            List<String> nodeList = zk.getChildren("/rpc", new Watcher() {
                @Override
                public void process(WatchedEvent event) {
                    if(event.getType().equals(Event.EventType.NodeChildrenChanged)) {
                        watchNode(zk);
                    }
                }
            });

            List<ProviderInfo> providerInfos = new ArrayList<>();
            for (String node: nodeList) {
                byte[] bytes = zk.getData("/rpc/" + node, false , null);
                String[] providerInfo = new String(bytes).split(",");
                if (providerInfo.length == 2) {
                    providerInfos.add(new ProviderInfo(providerInfo[0], providerInfo[1]));
                }
            }

            this.dataList = providerInfos;
            logger.info("获取服务端服务列表成功：${this.dataList}");
        } catch (Exception e) {
            logger.error("watch error,", e);
        }
    }

    public ProviderInfo discover(String providerName) {
        if (dataList.isEmpty()) {
            return null;
        }

        List<ProviderInfo> providerInfos = dataList.stream()
            .filter(one -> providerName.equals(one.getName()))
            .collect(Collectors.toList());

        if (providerInfos.isEmpty()) {
            return null;
        }
        return providerInfos.get(ThreadLocalRandom.current()
            .nextInt(providerInfos.size()));
    }
}
