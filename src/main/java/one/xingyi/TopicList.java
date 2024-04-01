package one.xingyi;

import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Component
public class TopicList {
    public final List<String> topics = Collections.unmodifiableList(Arrays.asList("AML"));
}
