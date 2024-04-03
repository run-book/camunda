package one.xingyi.badideas;

import one.xingyi.AndErrors;
import one.xingyi.ConfigRepo;
import one.xingyi.domain.Config;
import one.xingyi.domain.ConfigAndName;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CallExternalDelegate implements JavaDelegate {
    @Autowired
    private ConfigRepo config;
    @Override
    public void execute(DelegateExecution execution) throws Exception {
        // Access the Config. Note that this is placeholder. We should really use the sha and go to a service. But this captures the idea
        String configName = (String) execution.getVariable("config");
        AndErrors<ConfigAndName> andErrors = config.readFromName(configName);
        Config config = andErrors.result().config();
        String currentActivityName = execution.getCurrentActivityName(); // Gets the name of the BPMN element
        String currentActivityId = execution.getCurrentActivityId(); // Gets the ID of the BPMN element

        String weWillUse = currentActivityId; // probably want this...
        Config.Task task = config.tasks().get(currentActivityName);
        if (task == null) throw new RuntimeException("No task found for " + currentActivityName); //This is just an early fail check. No point in continuing if we can't find the task

        //here we call our external service.

    }
    private String callExternalAmlService(String jsonBlob) {
        // This method would contain the logic to synchronously call your external AML service
        // and return the result of the check.
        // This is a simplified placeholder implementation.
        return "AML Check Result";
    }
}
//@Component("sharedDelegate")
//public class SharedDelegate implements JavaDelegate {
//
//    @Override
//    public void execute(DelegateExecution execution) throws Exception {
//        String currentActivityId = execution.getCurrentActivityId(); // Gets the ID of the BPMN element
//        String currentActivityName = execution.getCurrentActivityName(); // Gets the name of the BPMN element
//
//        switch (currentActivityId) { // Or use currentActivityName
//            case "task1Id":
//                // Logic for Task 1
//                break;
//            case "task2Id":
//                // Logic for Task 2
//                break;
//            // Add more cases as needed for additional tasks
//            default:
//                // Default logic or throw an exception if the task is unknown
//                throw new IllegalArgumentException("Unknown task: " + currentActivityId);
//        }
//    }
//}
