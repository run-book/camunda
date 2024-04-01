package one.xingyi;

import one.xingyi.domain.Config;
import one.xingyi.domain.ConfigAndName;
import one.xingyi.domain.ProcessAndVariables;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Map;

@RestController
public class StartController {
    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private ConfigRepo config;
    @Autowired
    ObjectMapper objectMapper = new ObjectMapper();
    @PostMapping("/start")
    public ResponseEntity<Object> startProcess(@RequestParam String country, @RequestParam String product, @RequestParam String channel, @RequestBody String payload) throws IOException {
        AndErrors<ConfigAndName> andErrors = config.readJsonFile(product, country, channel);
        if (andErrors.hasErrors())
            return ResponseEntity.badRequest().body(andErrors.errors().toString());
        Config config = andErrors.result().config();
        String bpmn = config.bpmn().model();
//        Map<String, Object> configForVariables = objectMapper.readValue(s, Map.class); //actually would get the sha of this and use that... shorter. But for the demo it's nice to send this around.
        Map<String, Object> variables = Map.of(
                "country", country,
                "product", product,
                "channel", channel,
                "payload", payload,
                "config", andErrors.result().name() //This should actually be the 'id' of the bpmn. But for the demo we'll use the name
        );
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(bpmn, variables);
        ProcessAndVariables result = new ProcessAndVariables(processInstance.getId(), variables);
        return ResponseEntity.ok(result);
    }
}
