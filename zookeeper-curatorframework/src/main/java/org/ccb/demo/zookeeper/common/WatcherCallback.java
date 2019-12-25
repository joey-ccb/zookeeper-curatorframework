package org.ccb.demo.zookeeper.common;

import org.apache.curator.framework.recipes.cache.ChildData;

import java.util.List;

/**
 * Created by cuicb on 2019/12/24.
 */
public interface WatcherCallback {
    void callback(List<ChildData> childDataList);
}
