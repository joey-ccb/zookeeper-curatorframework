package org.ccb.demo.zookeeper.task;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by cuicb on 2019/12/25.
 */
@Data
@NoArgsConstructor
public class SelfTaskInfo {
    private String taskId;
    private String taskContent;
}
