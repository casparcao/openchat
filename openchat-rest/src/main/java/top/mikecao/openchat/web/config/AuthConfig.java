package top.mikecao.openchat.web.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import top.mikecao.openchat.core.auth.TokenGranter;
import top.mikecao.openchat.core.auth.granters.DefaultTokenGranter;

/**
 * @author caohailong
 */

@Configuration
public class AuthConfig {

    @Bean
    public TokenGranter tokenGranter(ObjectMapper mapper){
        return new DefaultTokenGranter(mapper);
    }
}
