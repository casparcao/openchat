package top.mikecao.openchat.server.config;

import com.mongodb.MongoClientSettings;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * @author caohailong
 */

@Configuration
public class MongoConfig {

    @Value("${spring.data.mongodb.custom.min-connections-per-host:20}")
    private int minConnectionsPerHost;
    @Value("${spring.data.mongodb.custom.max-connections-per-host:20}")
    private int maxConnectionsPerHost;
    @Value("${spring.data.mongodb.custom.max-wait-time:10000}")
    private int maxWaitTime;
    @Value("${spring.data.mongodb.custom.connect-timeout:10000}")
    private int connectTimeout;
    @Value("${spring.data.mongodb.custom.socket-timeout:10000}")
    private int socketTimeout;
    @Value("${spring.data.mongodb.custom.threads-multiplier:10}")
    private int multiplier;

    @Bean
    public MongoClientSettings settings(){
        return MongoClientSettings.builder()
                .applyToConnectionPoolSettings(builder ->
                        builder.maxWaitTime(maxWaitTime, TimeUnit.MILLISECONDS)
                        .maxSize(minConnectionsPerHost)
                        .minSize(maxConnectionsPerHost)
                        .maxConnectionIdleTime(0, TimeUnit.MILLISECONDS)
                        .maxConnectionLifeTime(0, TimeUnit.MILLISECONDS))
                .build();
    }

}
