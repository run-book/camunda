package one.xingyi;

import one.xingyi.domain.Config;
import one.xingyi.executeTasks.ProcessTaskRequest;
import one.xingyi.executeTasks.ProcessTasksClient;
import org.camunda.bpm.engine.ExternalTaskService;
import org.camunda.bpm.engine.externaltask.LockedExternalTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ExternalTaskWorker {
    private final ExternalTaskService externalTaskService;
    private final TopicList topics;
    private final ProcessTasksClient processTasksClient;
    private final ConfigRepo configRepo;
    @Autowired
    public ExternalTaskWorker(ExternalTaskService externalTaskService, TopicList topics, ProcessTasksClient processTasksClient, ConfigRepo configRepo) {
        this.externalTaskService = externalTaskService;
        this.topics = topics;
        this.processTasksClient = processTasksClient;
        this.configRepo = configRepo;
    }
    @Scheduled(fixedDelay = 5000) // Poll every 10 seconds
    public void workOnAmlTasks() {
        System.out.println("Polling for  tasks...");
        for (String taskName : topics.topics) {
            workOnOneTaskName(taskName);
        }
    }
    public void workOnOneTaskName(String taskName) { //This should be much more paralle and better configured. Easy to do. This is POC though...
        System.out.println("   " + taskName);
        String workerId = "ExternalTaskWorker";
        List<LockedExternalTask> tasks = externalTaskService.fetchAndLock(10, workerId)
                .topic(taskName, 60_000L) // Lock for 60 seconds
                .execute();
        for (LockedExternalTask camundaTask : tasks) {
            try {
                String configName = camundaTask.getVariables().getValue("config", String.class);
                Config config = configRepo.readFromName(configName).result().config(); // remember will usually be using sha
                String activityId = camundaTask.getActivityId();
                Config.Task task = config.tasks().get(taskName);
                if (task == null) {
                    externalTaskService.handleFailure(camundaTask.getId(), workerId, "No task found for " + taskName, 0, 60_000L);
                    continue;
                }
                Map<String, Object> variables = new HashMap<>();
                for (String varName : task.variables()) {
                    variables.put(varName, camundaTask.getVariables().getValue(varName, Object.class));
                }
                ProcessTaskRequest request = new ProcessTaskRequest(
                        camundaTask.getProcessInstanceId(),
                        activityId,
                        workerId,
                        configName,
                        variables);
                System.out.println("             " + request);
                processTasksClient.apply(request).thenAccept(response -> {
                    System.out.println(MessageFormat.format("             Processing task {0} for process instance {1} with config {2}", camundaTask.getActivityId(), camundaTask.getProcessInstanceId(), configName));
                    externalTaskService.complete(camundaTask.getId(), workerId);
                }).exceptionally(
                        throwable -> {
                            System.out.println("             Failed to process " + request);
                            externalTaskService.handleFailure(camundaTask.getId(), workerId, throwable.getMessage(), 0, 60_000L);
                            return null;
                        }
                );
            } catch (Exception e) {
                externalTaskService.handleFailure(camundaTask.getId(), workerId, e.getMessage(), 0, 60_000L);
            }
        }
    }
    private boolean callExternalAmlService(String processInstanceId) {
        // Placeholder: implement the call to the actual external AML check service
        // This function should return the result of the AML check
        return true; // Simplified result
    }
}
