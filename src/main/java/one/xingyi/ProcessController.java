package one.xingyi;

import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProcessController {

    @Autowired
    private RuntimeService runtimeService;

    @GetMapping("/start")
    public String startProcess() {
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("camunda");
        return "Process instance started: " + processInstance.getId();
    }
}
