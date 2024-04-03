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
import java.util.logging.Logger;

@Component
public class ExternalTaskWorker {
    private final Logger LOGGER = Logger.getLogger(ExternalTaskWorker.class.getName());
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
    @Scheduled(fixedDelay = 5000)
    public void workOnAmlTasks() {
        System.out.println("Polling for  tasks..." + topics.topics);
        for (String taskName : topics.topics) {
            workOnOneTaskName(taskName);
        }
    }
    public void workOnOneTaskName(String taskName) { //This should be much more paralle and better configured. Easy to do. This is POC though...
        String workerId = "ExternalTaskWorker";
        List<LockedExternalTask> tasks = externalTaskService.fetchAndLock(10, workerId)
                .topic(taskName, 60_000L) // Lock for 60 seconds
                .execute();
        LOGGER.info("   " + taskName + ": " + tasks.size());
        for (LockedExternalTask camundaTask : tasks) {
            String taskId = camundaTask.getId();
            try {
                LOGGER.info("Camunda task id" + taskId);
                String configName = camundaTask.getVariables().getValue("config", String.class);
                Config config = configRepo.readFromName(configName).result().config(); // remember will usually be using sha
                String activityId = camundaTask.getActivityId();
                Config.Task task = config.tasks().get(activityId);
                LOGGER.info("   processing " + taskName + " for " + configName + " and activityId " + activityId + " with task " + task);
                if (task == null) {
                    externalTaskService.handleFailure(taskId, workerId, "No task found for " + taskName, 0, 60_000L);
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
                        taskId,
                        configName,
                        variables);
                LOGGER.info("             " + request);
                processTasksClient.apply(request).thenAccept(response -> {
                    LOGGER.info(MessageFormat.format("             External service is processing task {0} for process instance {1} with config {2}", camundaTask.getActivityId(), camundaTask.getProcessInstanceId(), configName));

                }).exceptionally(
                        throwable -> {
                            System.out.println("             Failed to process " + request + " because " + throwable.getMessage());
                            LOGGER.throwing(ExternalTaskWorker.class.getName(), "workOnOneTaskName", throwable );
                            externalTaskService.handleFailure(taskId, workerId, throwable.getMessage(), 0, 60_000L);
                            return null;
                        }
                );
            } catch (Exception e) {
                externalTaskService.handleFailure(taskId, workerId, e.getMessage(), 0, 60_000L);
            }
        }
    }
}
