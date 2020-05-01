package com.alamobot;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.ActiveProfiles;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@ActiveProfiles({"local", "test", "componentTest"})
@SpringBootTest(classes = {ComponentTestConfig.class})
@EnableJpaRepositories(
        basePackages = {"com.alamobot.core.persistence"}
)
public @interface ComponentTest {
}
