package ReactiveRestServiceFunc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class Application {

    /*
        @SpringBootApplication is a convenience annotation that adds all of the following:
        @Configuration: Tags the class as a source of bean definitions for the application context.
        @EnableAutoConfiguration: Tells Spring Boot to start adding beans based on classpath settings, other beans, and various property settings.
            For example, if spring-webmvc is on the classpath, this annotation flags the application as a web application and setting up a DispatcherServlet.
        @ComponentScan: Tells Spring to look for other components, configurations, and services in the hello package, letting it find the controllers.
     */
    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(Application.class, args);
        GreetingClientFunc greetingClientFunc = context.getBean(GreetingClientFunc.class);
        // We need to block for the content here or the JVM might exit before the message is logged
        System.out.println(">> message = " + greetingClientFunc.getMessage().block());
    }
}