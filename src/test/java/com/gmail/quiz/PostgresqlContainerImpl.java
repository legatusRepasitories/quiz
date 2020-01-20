package com.gmail.quiz;

import org.testcontainers.containers.PostgreSQLContainer;

public class PostgresqlContainerImpl extends PostgreSQLContainer<PostgresqlContainerImpl> {

    private static final String IMAGE_VERSION = "postgres:latest";
    private static PostgresqlContainerImpl container;

    public PostgresqlContainerImpl() {
        super(IMAGE_VERSION);
    }

    public static PostgresqlContainerImpl getInstance() {
        if (container == null) {
            container = new PostgresqlContainerImpl();
        }
        return container;
    }

    @Override
    public void start() {
        super.start();
        System.setProperty("DB_URL", container.getJdbcUrl());
        System.setProperty("DB_USERNAME", container.getUsername());
        System.setProperty("DB_PASSWORD", container.getPassword());
    }

    @Override
    public void stop() {
        //do nothing, JVM handles shut down
    }
}
