package org.ccb.demo.zookeeper.init;

import org.ccb.demo.zookeeper.common.NodeRegister;
import org.ccb.demo.zookeeper.common.ZookeeperLeaderSelector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * Created by cuicb on 2019/12/23.
 */
@Component
public class Initializer {
    @Autowired
    private ZookeeperLeaderSelector zkLeaderSelector;
    @Autowired
    private NodeRegister register;

    @PostConstruct
    public void init() {
        register.register(zkLeaderSelector.getRootPath());
        zkLeaderSelector.joinLeaderSelector();
    }
}
