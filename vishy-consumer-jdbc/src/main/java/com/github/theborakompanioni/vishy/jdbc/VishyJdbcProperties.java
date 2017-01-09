package com.github.theborakompanioni.vishy.jdbc;

import com.google.common.base.Strings;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Optional;

@ConfigurationProperties("vishy.consumer.jdbc")
public class VishyJdbcProperties {

    private boolean enabled;
    private String jdbcUrl;
    private String username;
    private String password;
    private String driverClassName;
    private String flywayScriptsLocation;
    private boolean tableSetupEnabled = false;

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

    public boolean isTableSetupEnabled() {
        return tableSetupEnabled;
    }

    public void setTableSetupEnabled(boolean tableSetupEnabled) {
        this.tableSetupEnabled = tableSetupEnabled;
    }

    public String getFlywayScriptsLocation() {
        return flywayScriptsLocation;
    }

    public void setFlywayScriptsLocation(String flywayScriptsLocation) {
        this.flywayScriptsLocation = flywayScriptsLocation;
    }
}
