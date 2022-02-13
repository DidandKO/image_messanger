package org.example.web.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import javax.sql.DataSource;

@Configuration
public class DbConfig {

    @Bean
    public DataSource dataSource() {
        return new EmbeddedDatabaseBuilder()
                .generateUniqueName(false)
                .setName("messenger-db")
                .setType(EmbeddedDatabaseType.H2)
                .addScript("messageReference.sql")
                .addScript("messages.sql")
                .addScript("users_table.sql")
                .addScript("data.sql")
                .setScriptEncoding("UTF-8")
                .ignoreFailedDrops(true)
                .build();
    }

    @Bean(value = "jdbc_template")
    public NamedParameterJdbcTemplate namedParameterJdbcTemplate() {
        return new NamedParameterJdbcTemplate(dataSource());
    }
}