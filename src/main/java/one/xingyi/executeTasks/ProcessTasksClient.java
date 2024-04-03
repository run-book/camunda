package one.xingyi.executeTasks;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.concurrent.CompletableFuture;

@Service
public class ProcessTasksClient {
    private final String callback;
    private WebClient webClient;
    @Autowired
    public ProcessTasksClient(WebClient.Builder webClientBuilder, @Value("${fusion.api.url}") String apiUrl, @Value("${fusion.callback.url}") String callback) {
        this.callback = callback;
        this.webClient = webClientBuilder.baseUrl(apiUrl).build();
    }
    public CompletableFuture<String> apply(ProcessTaskRequest request) {
        var withCallback = request.withCallback(callback);
        return webClient.post()
                .uri("/yourEndpoint")
                .bodyValue(withCallback)
                .retrieve()
                .bodyToMono(String.class)
                .doOnSuccess(response -> System.out.println("Response from External API: " + response))
                .toFuture();
    }
}