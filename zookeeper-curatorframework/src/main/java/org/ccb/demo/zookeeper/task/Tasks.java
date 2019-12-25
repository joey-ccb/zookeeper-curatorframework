package org.ccb.demo.zookeeper.task;

/**
 * Created by cuicb on 2019/3/27.
 */
public interface Tasks {
    /**
     * 加载待分配任务
     * @param json
     */
    void load(String json);
}
