package ReactiveRestServiceAnnot;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.config.EnableWebFlux;

@EnableWebFlux
@Configuration
@ComponentScan
public class WebConfig { }