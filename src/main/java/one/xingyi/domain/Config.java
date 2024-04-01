package one.xingyi.domain;

import java.util.Map;

public record Config(String version, Bpmn bpmn,Map<String, Task> tasks) {
    record Task(String service, String serviceDescription, String taskDescription, Request request, Response response) {}
    public record Request(String topic, Kafka kafka, Schema schema, Transformer transformer) {}
    public record Response(String topic, Kafka kafka, Schema schema, Transformer transformer) {}
    public record Kafka(String name, String id) {}
    public record Schema(String name, String id) {}
    public record Transformer(String name, String id) {}
    public record Bpmn(String model) {}
}