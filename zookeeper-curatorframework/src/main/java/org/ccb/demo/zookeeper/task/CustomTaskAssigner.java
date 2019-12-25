package org.ccb.demo.zookeeper.task;

import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by cuicb on 2019/12/25.
 */
@Component
@Slf4j
public class CustomTaskAssigner implements TaskAssigner {
    /**
     * 交集
     *
     * @param workers
     * @param assigns
     * @return
     */
    @Override
    public List<String> activeTaskQueue(List<String> workers, List<String> assigns) {
        workers.retainAll(assigns);
        return workers;
    }

    /**
     * 左差集
     *
     * @param workers
     * @param assigns
     * @return
     */
    @Override
    public List<String> orphanTaskQueue(List<String> workers, List<String> assigns) {
        assigns.removeAll(workers);
        return assigns;
    }

    /**
     * @param client
     * @param rootPath
     * @param workers
     * @param taskAssignCallback
     * @param taskAssignStrategy
     * @param tasks
     */
    @Override
    public void assignTask(CuratorFramework client,
                           String rootPath,
                           List<ChildData> workers,
                           TaskAssignCallback taskAssignCallback,
                           TaskAssignStrategy taskAssignStrategy,
                           Tasks tasks) {
        log.info("assignTask called.");
        try {
            PathChildrenCache cache = new PathChildrenCache(client, rootPath + "/assign", true);
            cache.start(PathChildrenCache.StartMode.BUILD_INITIAL_CACHE);

            // 删除孤儿任务(即有任务,没有worker的情况)
            List<String> orphanQueue = orphanTaskQueue(
                    workers.stream()
                            .map(worker -> (worker.getPath().replace(rootPath + "/work/", "")))
                            .collect(Collectors.toList()),
                    cache.getCurrentData().stream()
                            .map(assignment -> (assignment.getPath().replace(rootPath + "/assign/", "")))
                            .collect(Collectors.toList()));

            orphanQueue.stream().forEach(orphan -> {
                try {
                    log.info("delete orphan:" + rootPath + "/assign/" + orphan);
                    client.delete().forPath(rootPath + "/assign/" + orphan);
                } catch (Exception e) {
                }
            });

            // 获得活动任务队列
            List<String> activeQueue = activeTaskQueue(
                    workers.stream()
                            .map(work -> (work.getPath().replace(rootPath + "/work/", "")))
                            .collect(Collectors.toList()),
                    cache.getCurrentData().stream()
                            .map(assign -> (assign.getPath().replace(rootPath + "/assign/", "")))
                            .collect(Collectors.toList()));

            if (activeQueue.size() != 0) {
                // 按照获得队列分配任务;如若宕机,重启后,仍保证分配到原任务
                List<String> taskGroup = taskAssignStrategy.tasksGroupList(tasks, activeQueue.size());// 全部任务ids
                String noTaskNode = null;
                // 激活的任务Ids
                List<String> activeTasks = new ArrayList<>();
                for (String hostPort : activeQueue) {
                    String taskIdNode = rootPath + "/assign/" + hostPort;
                    try {
                        String taskId = new String(client.getData().forPath(taskIdNode));
                        if (!"".equals(taskId)) {
                            activeTasks.add(taskId);
                        } else {
                            noTaskNode = taskIdNode;
                        }
                    } catch (Exception e) {
                        log.error(e.getMessage(), e);
                    }
                }

                // 分配一个未启动任务;
                taskGroup.removeAll(activeTasks);
                if (!taskGroup.isEmpty() && noTaskNode != null) {
                    client.setData().forPath(noTaskNode, taskGroup.get(0).getBytes());
                }
            }
            cache.rebuild();
            cache.getCurrentData().forEach(childData -> log.info("{}-{}", childData.getPath(), new String(childData.getData())));
            taskAssignCallback.callback(cache.getCurrentData());
        } catch (Exception e) {
            log.info("assignTask exception.");
        }
    }
}
