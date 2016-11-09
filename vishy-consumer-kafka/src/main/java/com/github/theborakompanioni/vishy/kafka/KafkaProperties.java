package com.github.theborakompanioni.vishy.kafka;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("vishy.kafka")
public class KafkaProperties {
    private static final int defaultRetries = 0;
    private static final int defaultBatchSize = 16384;
    private static final int defaultLingerMs = 1;
    private static final int defaultBufferMemory = 33554432;

    private boolean enabled;
    private String brokers;
    private int retries = 0;
    private int batchSize = 16384;
    private int lingerMs = 1;
    private int bufferMemory = 33554432;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public int getRetries() {
        return retries > 0 ? retries : defaultRetries;
    }

    public void setRetries(int retries) {
        this.retries = retries;
    }

    public int getBatchSize() {
        return batchSize > 0 ? batchSize : defaultBatchSize;
    }

    public void setBatchSize(int batchSize) {
        this.batchSize = batchSize;
    }

    public int getLingerMs() {
        return lingerMs > 0 ? lingerMs : defaultLingerMs;
    }

    public void setLingerMs(int lingerMs) {
        this.lingerMs = lingerMs;
    }

    public int getBufferMemory() {
        return bufferMemory > 0 ? bufferMemory : defaultBufferMemory;
    }

    public void setBufferMemory(int bufferMemory) {
        this.bufferMemory = bufferMemory;
    }

    public String getBrokers() {
        return brokers;
    }

    public void setBrokers(String brokers) {
        this.brokers = brokers;
    }
}
