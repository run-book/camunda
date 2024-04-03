package one.xingyi;

import one.xingyi.domain.ConfigAndName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
public class ConfigController {
    private final ConfigRepo config;
    @Autowired
    public ConfigController(ConfigRepo config) {
        this.config = config;
    }
    @GetMapping("/config")
    public ResponseEntity<Object> config(@RequestParam String country, @RequestParam String product, @RequestParam String channel) throws IOException {
        AndErrors<ConfigAndName> andErrors = config.readJsonFile(product, country, channel);

        return !andErrors.errors().isEmpty() ? ResponseEntity.ok(andErrors.result().name()) : ResponseEntity.badRequest().body(andErrors.errors());
    }
}
