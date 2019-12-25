package org.ccb.demo.zookeeper.common;

import org.apache.curator.framework.recipes.cache.ChildData;

import java.util.List;

/**
 * Created by cuicb on 2019/12/25.
 */
public interface UpdateCallback {
    void callback(List<ChildData> childDataList);
}
