package org.ccb.demo.zookeeper.common;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.leader.LeaderSelector;
import org.apache.curator.framework.recipes.leader.LeaderSelectorListener;
import org.apache.curator.framework.state.ConnectionState;
import org.ccb.demo.zookeeper.common.CuratorClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.CountDownLatch;

/**
 * Created by cuicb on 2019/12/23.
 */
@Component
@Slf4j
public class ZookeeperLeaderSelector implements LeaderSelectorListener {
    @Value("${conf_root_path:/root}")
    @Getter
    private String rootPath;
    private volatile CountDownLatch latch;
    @Autowired
    private CuratorClient client;
    private LeaderSelector leaderSelector;

    public void joinLeaderSelector() {

        leaderSelector = new LeaderSelector(client.getClient(), rootPath + "/master", this);
        leaderSelector.autoRequeue();
        leaderSelector.start();
    }

    @Override
    public void takeLeadership(CuratorFramework client) throws Exception {
        latch = new CountDownLatch(1);
        log.info("takeLeadership participants={}", leaderSelector.getParticipants());

        latch.await();
        log.info("takeLeadership lost");
    }

    @Override
    public void stateChanged(CuratorFramework client, ConnectionState connectionState) {
        if (connectionState == ConnectionState.LOST) {
            latch.countDown();
        }
        log.info("stateChanged start..." + connectionState);
    }

    /**
     * 是否为主节点
     *
     * @return
     */
    public boolean isLeader() {
        return this.leaderSelector.hasLeadership();
    }

}
