package com.demandlane.booklending.common.healthcheck;


import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

@Component
public class Database implements HealthIndicator {

    private final DataSource dataSource;

    public Database(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Health health() {
        Health.Builder healthBuilder = new Health.Builder();
        Health health = null;
        try {
            Connection connection = dataSource.getConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT 1");

            String[] data = (connection.getMetaData().getURL()).split(":");
            String dbHost = (data[2]).replaceAll("/", "");
            String[] hostElements = (dbHost.replace('.', '-')).split("-");

            if (resultSet.next()) {
                health = healthBuilder.up().withDetail("Description", "Successfully connected to database ✅")
                        .withDetail("DB Instance", "."+hostElements[hostElements.length-1]).build();
            }
            resultSet.close();
            statement.close();
            connection.close();
        } catch (Exception e) {
            health = healthBuilder.down().withException(new RuntimeException("Down Message: " + e.getMessage())).build();
        }
        return health;
    }
}