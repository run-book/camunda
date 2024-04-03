package one.xingyi.executeTasks;

import java.util.Map;

public record ProcessTaskRequestWithCallback(String processInstanceId, String businessKey, String task, String workerId, Map<String, Object> variables, String callback) {
}
