package one.xingyi;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.juli.logging.Log;
import org.camunda.bpm.engine.ExternalTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Map;
import java.util.logging.Logger;

@RestController
public class CompleteController {
    private static Logger LOGGER = Logger.getLogger(CompleteController.class.getName());
    //    @Autowired
//    private ProcessEngine processEngine;
    @Autowired
    ExternalTaskService externalTaskService;
    @Autowired
    ObjectMapper objectMapper = new ObjectMapper();
    @PostMapping("/complete/{taskId}/{workerId}")
    public ResponseEntity<Object> complete(@PathVariable String taskId, @PathVariable String workerId, @RequestBody Map<String, Object> variables) throws IOException {
        LOGGER.info("Completing task " + taskId + " for worker " + workerId + " with variables " + variables);
        externalTaskService.complete(taskId, workerId, variables);
        return ResponseEntity.ok("");
    }
}
