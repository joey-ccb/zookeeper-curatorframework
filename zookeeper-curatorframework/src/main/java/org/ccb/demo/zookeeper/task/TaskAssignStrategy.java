package org.ccb.demo.zookeeper.task;

import java.util.List;
import java.util.Map;

/**
 * Created by cuicb on 2018/6/29.
 */
public interface TaskAssignStrategy<T, E> {

    List<String> tasksGroupList(T tasksInfo, int group);

    Map<String, List<E>> tasksGroupMap(T tasksInfo, int group);
}
