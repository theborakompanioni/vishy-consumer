package com.github.theborakompanioni.vishy.jdbc;

import com.google.common.base.CharMatcher;
import com.google.common.base.Strings;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Optional;

import static java.util.Objects.requireNonNull;

@ConfigurationProperties("vishy.jdbc")
public class JdbcProperties {
    private final static String DEFAULT_TABLE_NAME = "vishy_openmrc_request";

    private boolean enabled;
    private String jdbcUrl;
    private String username;
    private String password;
    private String driverClassName;
    private String tableName;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getJdbcUrl() {
        return jdbcUrl;
    }

    public void setJdbcUrl(String jdbcUrl) {
        this.jdbcUrl = jdbcUrl;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Optional<String> getDriverClassName() {
        return Optional.ofNullable(driverClassName)
                .map(Strings::emptyToNull);
    }

    public void setDriverClassName(String driverClassName) {
        this.driverClassName = driverClassName;
    }

    public String getTableName() {
        return Optional.ofNullable(tableName)
                .map(Strings::emptyToNull)
                .orElse(DEFAULT_TABLE_NAME);
    }

    public void setTableName(String tableName) {
        requireNonNull(tableName);

        if (!CharMatcher.javaLetterOrDigit().or(CharMatcher.anyOf("._")).matchesAllOf(tableName)) {
            throw new IllegalArgumentException("Invalid table name provided: " + tableName);
        }

        this.tableName = tableName;
    }
}
