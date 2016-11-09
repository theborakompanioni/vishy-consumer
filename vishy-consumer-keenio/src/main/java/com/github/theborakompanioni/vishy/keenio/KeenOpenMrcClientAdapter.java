package com.github.theborakompanioni.vishy.keenio;

import com.github.theborakompanioni.openmrc.OpenMrc;
import com.github.theborakompanioni.openmrc.OpenMrcRequestConsumer;
import com.github.theborakompanioni.vishy.consumer.OpenMrcRequestToMapFunction;
import com.google.common.collect.Maps;
import io.keen.client.java.KeenCallback;
import io.keen.client.java.KeenClient;
import io.keen.client.java.KeenProject;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import static java.util.Map.Entry;
import static java.util.Objects.requireNonNull;

@Slf4j
public class KeenOpenMrcClientAdapter implements OpenMrcRequestConsumer {
    private static Function<OpenMrc.Request, String> DEFAULT_EVENT_NAME_FUNC = request -> request.getType().name();

    private static KeenProject NULL_KEEN_PROJECT = null;
    private static Map<String, Object> NULL_KEEN_PROPERTIES = null;
    private static KeenCallback DEFAULT_KEEN_CALLBACK = new KeenCallback() {
        @Override
        public void onSuccess() {
        }

        @Override
        public void onFailure(Exception e) {
            log.warn("", e);
        }
    };

    private final KeenClient keenClient;
    private final OpenMrcRequestToMapFunction toMapFunction;
    private final Function<OpenMrc.Request, String> toEventNameFunction;

    public KeenOpenMrcClientAdapter(KeenClient keenClient, OpenMrcRequestToMapFunction toMapFunction) {
        this(keenClient, toMapFunction, DEFAULT_EVENT_NAME_FUNC);
    }

    public KeenOpenMrcClientAdapter(KeenClient keenClient, OpenMrcRequestToMapFunction toMapFunction, Function<OpenMrc.Request, String> toEventNameFunction) {
        this.keenClient = requireNonNull(keenClient);
        this.toMapFunction = requireNonNull(toMapFunction);
        this.toEventNameFunction = requireNonNull(toEventNameFunction);
    }

    @Override
    public void accept(OpenMrc.Request request) {
        Map<String, Object> event = Optional.ofNullable(request)
                .map(toMapFunction)
                .map(this::replaceDotsInKeysWithColon)
                .orElseThrow(IllegalArgumentException::new);

        String eventName = toEventNameFunction.apply(request);

        log.debug("received event for keenio {}", eventName);

        keenClient.addEvent(NULL_KEEN_PROJECT, eventName, event, NULL_KEEN_PROPERTIES, DEFAULT_KEEN_CALLBACK);
    }

    // keen does not like certain events
    // e.g. with "dots" in the key
    private <V> Map<String, V> replaceDotsInKeysWithColon(Map<String, V> event) {
        return newMapFromEntries(event, Collectors.toMap(e -> e.getKey().replace(".", ":"), Entry::getValue));
    }

    private <K, V> Map<K, V> newMapFromEntries(Map<K, V> target, Collector<Entry<K, V>, ?, Map<K, V>> collector) {
        Map<K, V> replacedKeysMap = Maps.newHashMapWithExpectedSize(target.size());

        replacedKeysMap.putAll(target.entrySet().stream()
                .collect(collector));

        return replacedKeysMap;
    }
}
