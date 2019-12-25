package org.ccb.demo.zookeeper.task;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Created by cuicb on 2019/12/25.
 */
@Component
public class CustomTaskAssignStrategy implements TaskAssignStrategy {
    @Override
    public List<String> tasksGroupList(Object tasksInfo, int group) {
        return null;
    }

    @Override
    public Map<String, List> tasksGroupMap(Object tasksInfo, int group) {
        return null;
    }
}
