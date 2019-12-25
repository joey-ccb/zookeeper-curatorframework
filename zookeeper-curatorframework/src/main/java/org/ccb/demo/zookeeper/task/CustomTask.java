package org.ccb.demo.zookeeper.task;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by cuicb on 2019/12/25.
 */
@Component
public class CustomTask implements Tasks{

    /**
     * 本机任务Id
     */
    private String taskId;
    @Override
    public void load(String json) {

    }
}
