package one.xingyi.executeTasks;

import java.util.Map;

public record ProcessTaskRequestWithCallback(String processInstanceId, String activityId, String workerId, String taskId,String configName,  Map<String, Object> variables, String callback) {
}
