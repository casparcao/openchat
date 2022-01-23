package top.mikecao.openchat.server.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import top.mikecao.openchat.core.serialize.Json;

/**
 * @author caohailong
 */

@Configuration
public class MapperConfig {

    @Bean
    public ObjectMapper objectMapper(){
        return Json.mapper();
    }
}
