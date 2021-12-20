package top.mikecao.openchat.server.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * @author mike
 */

@Configuration
@PropertySource({"classpath:datasource.properties",
        "classpath:keystore.properties",
        "classpath:rabbit.properties"})
public class PropConfig {
}
