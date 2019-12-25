package org.ccb.demo.zookeeper.task;

import org.apache.curator.framework.recipes.cache.ChildData;

import java.util.List;

/**
 * Created by lenovo on 2018/7/4.
 */
public interface TaskAssignCallback {
    void callback(List<ChildData> childDataList);
}
