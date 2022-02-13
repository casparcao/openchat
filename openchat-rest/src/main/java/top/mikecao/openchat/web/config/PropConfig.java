package top.mikecao.openchat.web.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * @author mike
 */

@Configuration
@PropertySource({"classpath:datasource.properties"})
public class PropConfig {
}
