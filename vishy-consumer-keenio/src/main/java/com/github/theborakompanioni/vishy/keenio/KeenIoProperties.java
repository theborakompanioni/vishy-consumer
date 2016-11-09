package com.github.theborakompanioni.vishy.keenio;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("vishy.keenio")
public class KeenIoProperties {

    private boolean enabled;
    private String projectId;
    private String writeKey;
    private String readKey;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getWriteKey() {
        return writeKey;
    }

    public void setWriteKey(String writeKey) {
        this.writeKey = writeKey;
    }

    public String getReadKey() {
        return readKey;
    }

    public void setReadKey(String readKey) {
        this.readKey = readKey;
    }
}
