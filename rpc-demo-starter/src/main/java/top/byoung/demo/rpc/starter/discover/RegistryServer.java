package top.byoung.demo.rpc.starter.discover;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.byoung.demo.rpc.starter.Constant.ZKConstant;
import top.byoung.demo.rpc.starter.exception.ZkConnectException;

/**
 * @description: RegistryServer
 * @author: Yang Weixin
 * @create: 2019/12/06
 */
public class RegistryServer {

    private Logger logger = LoggerFactory.getLogger(RegistryServer.class);

    private String zkAddr;
    private int zkTimeout;
    private String serverName;
    private String host;
    private int port;

    public RegistryServer(String zkAddr, int zkTimeout, String serverName, String host, int port) {
        this.zkAddr = zkAddr;
        this.serverName = serverName;
        this.host = host;
        this.port = port;
        this.zkTimeout = zkTimeout;
    }

    public void register() throws ZkConnectException {
        try {
            ZooKeeper zooKeeper = new ZooKeeper(zkAddr, zkTimeout, event -> {
                logger.info("registry zk connect success...");
            });

            if (zooKeeper.exists(ZKConstant.ZK_ROOT_DIR , false) == null) {
                zooKeeper.create(
                    ZKConstant.ZK_ROOT_DIR, ZKConstant.ZK_ROOT_DIR.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT
                );
            }

            zooKeeper.create(ZKConstant.ZK_ROOT_DIR + "/" + serverName,
                (serverName + ","+ host + ":" + port).getBytes(),
                ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
            logger.info("provider register success {}", serverName);
        } catch (Exception e) {
            e.printStackTrace();
//            throw new ZkConnectException("register to zk exception," + e.getMessage(), e.getCause());
        }
    }

}
