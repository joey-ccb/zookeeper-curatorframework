package org.ccb.demo.zookeeper.common;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.zookeeper.CreateMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * Created by cuicb on 2019/12/23.
 */
@Component
@Slf4j
public class NodeRegister {
    @Autowired
    private CuratorClient curatorClient;
    @Value("${HOST:127.0.0.1}")
    @Getter
    private String host;
    @Value("${PORT_8080:8080}")
    @Getter
    private String port;

    /**
     * 注册
     *
     * @param root
     */
    public void register(String root) {
        registerAssign(root);
        registerWorke(root);
    }

    private void registerWorke(String root) {
        String node = host + ":" + port;
        createNode(root + "/work/" + node, CreateMode.EPHEMERAL);
        watch(root + "/work/" + node, CreateMode.EPHEMERAL);
    }

    private void registerAssign(String root) {
        String node = host + ":" + port;
        createNode(root + "/assign/" + node, CreateMode.PERSISTENT);
        watch(root + "/assign/" + node, CreateMode.PERSISTENT);
    }

    private void createNode(String node, CreateMode mode) {
        for (int i = 0; i < 3; i++) {
            try {
                log.info("create node={}", node);
                if (CreateMode.PERSISTENT.equals(mode) && curatorClient.getClient().checkExists().forPath(node) != null) {
                    log.info("node is already exists={}", node);
                }
                curatorClient.getClient().create()
                        .creatingParentsIfNeeded()
                        .withMode(mode)
                        .forPath(node, new byte[0]);
                log.info("register success. node{}", node);
                return;
            } catch (Exception e) {
                log.info("register failed, retry time {}...", i);
                try {
                    TimeUnit.SECONDS.sleep(5);
                } catch (InterruptedException interruptedException) {
                    log.error(interruptedException.getMessage(), interruptedException);
                }
            }
        }
    }

    private void watch(String node, CreateMode createMode) {
        PathChildrenCache cache = new PathChildrenCache(curatorClient.getClient(), node, true);
        try {
            cache.start(PathChildrenCache.StartMode.BUILD_INITIAL_CACHE);
            cache.getListenable().addListener((zkClient, event) -> {
                switch (event.getType()) {
                    case CONNECTION_RECONNECTED:
                        log.info("node={} CONNECTION_RECONNECTED", node);
                        createNode(node, createMode);
                        break;
                    default:
                        log.info(" PathChildrenCacheEvent.Type={}", event.getType());
                        break;
                }
            });
        } catch (Exception e) {
            log.info("watch failed.");
        }
    }
}
