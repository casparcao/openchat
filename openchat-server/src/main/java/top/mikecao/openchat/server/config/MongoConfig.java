package top.mikecao.openchat.server.config;

import com.mongodb.MongoClientOptions;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.mongodb.MongoMetricsCommandListener;
import io.micrometer.core.instrument.binder.mongodb.MongoMetricsConnectionPoolListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.convert.DbRefResolver;
import org.springframework.data.mongodb.core.convert.DefaultDbRefResolver;
import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;

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
    public MongoClientOptions mongoClientOptions(MeterRegistry registry){
        // 配置连接池
        MongoClientOptions.Builder builder = new MongoClientOptions.Builder();
        builder.connectionsPerHost(maxConnectionsPerHost);
        builder.minConnectionsPerHost(minConnectionsPerHost);
        builder.maxWaitTime(maxWaitTime);
        builder.connectTimeout(connectTimeout);
        builder.socketTimeout(socketTimeout);
        builder.threadsAllowedToBlockForConnectionMultiplier(multiplier);
        builder.addConnectionPoolListener(new MongoMetricsConnectionPoolListener(registry));
        builder.addCommandListener(new MongoMetricsCommandListener(registry));
        return builder.build();
    }

    @Bean
    public MappingMongoConverter mappingMongoConverter(MongoDbFactory mongoDbFactory,
                                                       MongoMappingContext mongoMappingContext) {

        DbRefResolver dbRefResolver = new DefaultDbRefResolver(mongoDbFactory);
        MappingMongoConverter converter = new MappingMongoConverter(dbRefResolver, mongoMappingContext);
        converter.setTypeMapper(new DefaultMongoTypeMapper(null));

        return converter;
    }
}
