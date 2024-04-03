package one.xingyi;

import org.camunda.bpm.spring.boot.starter.annotation.EnableProcessApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableProcessApplication("camunda")
@EnableScheduling // we have the external worker here for the POC but only to make it 'less things' running
public class CamundaApplication {
    public static void main(String... args) {
        SpringApplication.run(CamundaApplication.class, args);
    }
}
