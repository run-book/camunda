package one.xingyi;

import com.fasterxml.jackson.databind.ObjectMapper;
import one.xingyi.domain.Config;
import one.xingyi.domain.ConfigAndName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.List;

@Component
public class ConfigRepo {
    public final String filePattern = "target/{0}/{1}/{2}/{0}-{1}-{2}.json";
    @Autowired
    ObjectMapper objectMapper = new ObjectMapper();
    public String toString(String name) throws IOException {
        try (InputStream stream = getClass().getClassLoader().getResourceAsStream(name)) {
            byte[] bytes = stream.readAllBytes();
            return new String(bytes);
        }
    }
    public AndErrors<ConfigAndName> readJsonFile(String product, String country, String channel) throws IOException {
        String name = MessageFormat.format(filePattern, product, country, channel);
        try {
            String configString = toString(name);
            Config config = objectMapper.readValue(configString, Config.class);
            return new AndErrors<>(new ConfigAndName(config, name), List.of());
        } catch (Exception e) {
            return new AndErrors<>(null, List.of("Error reading JSON file " + name + ": " + e.getMessage()));
        }
    }
}
