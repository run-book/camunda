package one.xingyi.executeTasks;

import java.util.Map;

public record ProcessTaskRequest(String processInstanceId, String activityId, String workerId,String taskId, String configName, Map<String, Object> variables) {
    ProcessTaskRequestWithCallback withCallback(String callback) {
        return new ProcessTaskRequestWithCallback(processInstanceId, activityId, workerId,taskId,configName, variables, callback);
    }
}
