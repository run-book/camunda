package one.xingyi.executeTasks;

import java.util.Map;

public record ProcessTaskRequest(String processInstanceId, String businessKey, String task, String workerId, Map<String,Object> variables) {
    ProcessTaskRequestWithCallback withCallback(String callback) {
        return new ProcessTaskRequestWithCallback(processInstanceId, businessKey, task, workerId, variables,callback);
    }
}
