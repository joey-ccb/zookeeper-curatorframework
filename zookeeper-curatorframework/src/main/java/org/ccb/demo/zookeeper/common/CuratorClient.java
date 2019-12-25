package org.ccb.demo.zookeeper.common;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Created by cuicb on 2019/12/23.
 */
@Component
public class CuratorClient {
    //    @Value("${conf_zk_host:127.0.0.1}")
    @Value("${conf_zk_host:dasauto-cns-db.mxnavi.com}")
    private String zkHost;
    @Value("${conf_zk_port:2181}")
    private int zkPort;
    @Value("${conf_zk_baseSleepTimeMs:5000}")
    private int baseSleepTimeMs;

    private CuratorFramework client;

    private void init() {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(baseSleepTimeMs, 3);
        client = CuratorFrameworkFactory.newClient(zkHost + ":" + zkPort, retryPolicy);
        client.start();
    }

    public CuratorFramework getClient() {
        synchronized (CuratorClient.class) {
            if (client == null) {
                init();
            }
        }
        return client;
    }
}
