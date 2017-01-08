package com.github.theborakompanioni.vishy.elasticsearch;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("vishy.consumer.elasticsearch")
public class VishyElasticsearchProperties {

    private boolean enabled;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
