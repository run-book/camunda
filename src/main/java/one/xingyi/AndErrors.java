package one.xingyi;

import java.util.List;

public record AndErrors<T>(T result, List<String> errors) {
    boolean hasErrors() {
        return !errors.isEmpty();
    }
}
