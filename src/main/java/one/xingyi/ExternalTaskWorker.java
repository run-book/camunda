package one.xingyi;

import org.camunda.bpm.engine.ExternalTaskService;
import org.camunda.bpm.engine.externaltask.LockedExternalTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ExternalTaskWorker {
    private final ExternalTaskService externalTaskService;
    private final TopicList topics;
    @Autowired
    public ExternalTaskWorker(ExternalTaskService externalTaskService, TopicList topics) {
        this.externalTaskService = externalTaskService;
        this.topics = topics;
    }
    @Scheduled(fixedDelay = 10000) // Poll every 10 seconds
    public void workOnAmlTasks() {
    }
    public void workOnOneTask(String taskName) {
        List<LockedExternalTask> tasks = externalTaskService.fetchAndLock(10, "ExternalTaskWorker")
                .topic(taskName, 60_000L) // Lock for 60 seconds
                .execute();
        for (LockedExternalTask task : tasks) {
            try {
                // Simulate calling an external microservice to perform the AML check
                // Assume this is an asynchronous operation
                String processInstanceId = task.getProcessInstanceId();
                Object variables = task.getVariables().get("variables"); // need to understand the type store
                //now invoke the external task with the task name and the process instance
            } catch (Exception e) {
                // Handle failure (e.g., business error, exception from the external service)
                externalTaskService.handleFailure(task.getId(), "amlWorker", e.getMessage(), 0, 60_000L);
            }
        }
    }
    private boolean callExternalAmlService(String processInstanceId) {
        // Placeholder: implement the call to the actual external AML check service
        // This function should return the result of the AML check
        return true; // Simplified result
    }
}
