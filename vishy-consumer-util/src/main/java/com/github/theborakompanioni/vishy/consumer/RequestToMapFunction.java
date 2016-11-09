package com.github.theborakompanioni.vishy.consumer;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.theborakompanioni.openmrc.OpenMrc;
import com.googlecode.protobuf.format.JsonFormat;

import java.io.IOException;
import java.util.Map;

import static java.util.Objects.requireNonNull;

public class RequestToMapFunction implements OpenMrcRequestToMapFunction {

    private static final ObjectMapper DEFAULT_MAPPER = new ObjectMapper();
    private static final JsonFormat DEFAULT_JSON_FORMAT = new JsonFormat();

    private final ObjectMapper mapper;
    private final JsonFormat jsonFormat;

    public RequestToMapFunction() {
        this(DEFAULT_MAPPER);
    }

    public RequestToMapFunction(ObjectMapper mapper) {
        this(mapper, DEFAULT_JSON_FORMAT);
    }

    public RequestToMapFunction(ObjectMapper mapper, JsonFormat jsonFormat) {
        this.mapper = requireNonNull(mapper);
        this.jsonFormat = requireNonNull(jsonFormat);
    }

    @Override
    public Map<String, Object> apply(OpenMrc.Request request) {
        try {
            String json = jsonFormat.printToString(request);
            return mapper.readValue(json, new TypeReference<Map<String, Object>>() {
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
