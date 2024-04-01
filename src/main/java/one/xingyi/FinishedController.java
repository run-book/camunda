package one.xingyi;

import org.camunda.bpm.engine.ExternalTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FinishedController {
    @Autowired
    private ExternalTaskService externalTaskService;
//    @PostMapping("/finished")
//    public ResponseEntity<?> completeExternalTask(@RequestParam String taskName,@RequestParam String taskId) {
//        externalTaskService.complete(taskId, Collections.singletonMap("variableName", variableValue));
//        return ResponseEntity.ok().build();
//    }


}
