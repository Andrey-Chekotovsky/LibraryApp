package net.chekotovsky.LibraryApp.Config;


import io.r2dbc.spi.ConnectionFactories;
import io.r2dbc.spi.ConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@Configuration
@EnableWebSecurity
public class ConnectionProvider extends AbstractR2dbcConfiguration {
    @Value("${connection.host}")
    private String host;
    @Value("${connection.database}")
    private String database;
    @Value("${connection.username}")
    private String username;
    @Value("${connection.password}")
    private String password;


        @Override
        public ConnectionFactory connectionFactory() {
            return ConnectionFactories.get("r2dbc:postgresql://localhost:5432/" + database);
        }
//    @Bean
//        public ConnectionFactory connectionFactory() {
//
//        // postgres
//        return new PostgresqlConnectionFactory(
//                PostgresqlConnectionConfiguration.builder()
//                        .host(host)
//                        .database(database)
//                        .username(username)
//                        .password(password)
//                        .build()
//        );
//    }
    @Bean
    public DatabaseClient databaseClient()
    {
        return DatabaseClient.create(connectionFactory());
    }
    @Bean
    public R2dbcEntityTemplate r2dbcEntityTemplate()
    {
        return new R2dbcEntityTemplate(connectionFactory());
    }
}
