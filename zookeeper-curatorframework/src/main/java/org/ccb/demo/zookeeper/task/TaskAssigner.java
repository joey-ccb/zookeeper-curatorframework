package org.ccb.demo.zookeeper.task;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.ChildData;

import java.util.List;

/**
 * Created by cuicb on 2019/12/25.
 */
public interface TaskAssigner {
    List<String> activeTaskQueue(List<String> workers, List<String> assigns);

    List<String> orphanTaskQueue(List<String> workers, List<String> assigns);

    void assignTask(CuratorFramework client, String rootPath,
                    List<ChildData> workers,
                    TaskAssignCallback taskAssignCallback,
                    TaskAssignStrategy taskAssignStrategy,
                    Tasks tasks);
}
