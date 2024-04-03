package one.xingyi.executeTasks;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

@Service
public class ProcessTasksClient {
    private final Logger LOGGER = Logger.getLogger(ProcessTasksClient.class.getName());
    private final String serviceEndpoint;
    private final String callback;
    private WebClient webClient;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    public ProcessTasksClient(WebClient.Builder webClientBuilder,
                              @Value("${fusion.api.url}") String apiUrl,
                              @Value("${fusion.api.serviceEndpoint}") String serviceEndpoint,
                              @Value("${fusion.callback.url}") String callback) {
        this.serviceEndpoint = serviceEndpoint;
        this.callback = callback;
        this.webClient = webClientBuilder.baseUrl(apiUrl).build();
    }
    public CompletableFuture<String> apply(ProcessTaskRequest request) throws JsonProcessingException {
        ProcessTaskRequestWithCallback withCallback = request.withCallback(callback);
        String withCallbackJson = objectMapper.writeValueAsString(withCallback);
        LOGGER.info("Sending request to External API: " + withCallback);
        return webClient.post()
                .uri(serviceEndpoint)
                .contentType(MediaType.APPLICATION_JSON) // Setting content-type to application/json
                .bodyValue(withCallbackJson)
                .retrieve()
                .bodyToMono(String.class)
                .doOnSuccess(response -> System.out.println("Response from External API: " + response))
                .toFuture();
    }
}