package org.ccb.demo.zookeeper.task;

import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.ccb.demo.zookeeper.common.UpdateCallback;
import org.ccb.demo.zookeeper.common.WatcherCallback;
import org.springframework.stereotype.Component;

/**
 * Created by cuicb on 2019/12/24.
 */
@Component
@Slf4j
public class TaskWatcher {
    public void watch(CuratorFramework client, String path, UpdateCallback updateCallback, WatcherCallback watcherCallback) {

        PathChildrenCache cache = new PathChildrenCache(client, path, true);
        Runnable printCurrentData = () -> cache.getCurrentData().forEach(e -> log.info("{}-{}", e.getPath(), new String(e.getData())));
        printCurrentData.run();
        try {
            cache.start(PathChildrenCache.StartMode.BUILD_INITIAL_CACHE);
            cache.getListenable().addListener((zkClient, event) -> {
                cache.rebuild();
                printCurrentData.run();
                switch (event.getType()) {
                    case CHILD_ADDED:
                        log.info("CHILD_ADDED");
                        updateCallback.callback(cache.getCurrentData());
                        break;
                    case CHILD_UPDATED:
                        log.info("CHILD_UPDATED");
                        updateCallback.callback(cache.getCurrentData());
                        break;
                    case CHILD_REMOVED:
                        log.info("CHILD_REMOVED");
                        updateCallback.callback(cache.getCurrentData());
                        break;
                    case INITIALIZED:
                        log.info("INITIALIZED");
                        break;
                    case CONNECTION_LOST:
                        log.info("CONNECTION_LOST");
                        break;
                    case CONNECTION_RECONNECTED:
                        log.info("CONNECTION_RECONNECTED");
                        break;
                    default:
                        log.info("PathChildrenCacheEvent.Type={}", event.getType());
                        break;
                }
            });

            watcherCallback.callback(cache.getCurrentData());
        } catch (Exception e) {
            log.info("watch failed.", e);
        }
    }
}
