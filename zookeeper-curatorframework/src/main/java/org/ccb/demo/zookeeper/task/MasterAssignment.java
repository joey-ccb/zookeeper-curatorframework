package org.ccb.demo.zookeeper.task;

import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.ccb.demo.zookeeper.common.CuratorClient;
import org.ccb.demo.zookeeper.common.NodeRegister;
import org.ccb.demo.zookeeper.common.ZookeeperLeaderSelector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * Created by cuicb on 2019/12/24.
 */
@Component
@Slf4j
public class MasterAssignment {
    @Autowired
    private CuratorClient client;
    @Autowired
    private TaskWatcher taskWatcher;
    @Autowired
    private TaskAssigner taskAssigner;
    @Autowired
    private ZookeeperLeaderSelector selector;
    @Autowired
    private Tasks customTasks;
    @Autowired
    private TaskAssignStrategy taskAssignStrategy;
    @Autowired
    private NodeRegister nodeRegister;

    public void watchWorker() {
        log.info("watch worker");
        if (selector.isLeader()) {
            taskWatcher.watch(client.getClient(), selector.getRootPath() + "/work",
                    (List<ChildData> childDataList) -> {
                        if (selector.isLeader()) {
                            taskAssign(childDataList);
                        }
                    }, null);
        }
    }

    private void taskAssign(List<ChildData> childDataList) {
        log.info("taskAssign.");
        taskAssigner.assignTask(client.getClient(),
                selector.getRootPath(),
                childDataList,
                (taskList) -> updateTask(taskList),
                taskAssignStrategy,
                customTasks);
    }

    private void updateTask(List<ChildData> childDataList) {
        log.info("updateTask.");
        update(childDataList, selector.getRootPath() + "/assign/");
    }

    void update(List<ChildData> childDataList, String path) {

        StringBuilder strBuilder = new StringBuilder();
        for (ChildData childData : childDataList) {
            String woker = childData.getPath().replace(path, "");
            final String localHostPort = nodeRegister.getHost() + ":" + nodeRegister.getPort();
            if (!localHostPort.equals(woker)) {
                continue;
            }

            String taskId = new String(childData.getData());

            if (StringUtils.isEmpty(taskId)) {
                log.info("Now have no task.");
                return;
            }

            // 更新任务列表
            log.info("update: TaskId={}, content={}", taskId, strBuilder.toString());
        }
    }
}
