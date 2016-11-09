package com.github.theborakompanioni.vishy.elasticsearch.repository;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Collections;
import java.util.Map;

@Document(indexName = "request", type = "request")
public class RequestDocument {

    @Id
    private String id;
    @Version
    @JsonIgnore
    private Long version;

    @CreatedBy
    private String createdBy;

    @CreatedDate
    private Long createdAt;

    @Field(type = FieldType.Nested)
    private Map<String, Object> request = Collections.emptyMap();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Long createdAt) {
        this.createdAt = createdAt;
    }

    public Map<String, Object> getRequest() {
        return Collections.unmodifiableMap(request);
    }

    public void setRequest(Map<String, Object> request) {
        this.request = request;
    }
}
