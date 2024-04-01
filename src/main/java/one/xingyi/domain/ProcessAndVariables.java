package one.xingyi.domain;

import java.util.Map;

public record ProcessAndVariables (String processId, Map<String,Object> variables){
}
