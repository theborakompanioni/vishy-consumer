package com.github.theborakompanioni.vishy.kafka;

import com.github.theborakompanioni.openmrc.OpenMrc;
import com.github.theborakompanioni.openmrc.OpenMrcRequestConsumer;
import com.googlecode.protobuf.format.JsonFormat;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import static java.util.Objects.requireNonNull;

@Slf4j
public class KafkaOpenMrcClientAdapter implements OpenMrcRequestConsumer {
    private static final JsonFormat DEFAULT_JSON_FORMAT = new JsonFormat();

    private final Producer<String, String> client;
    private final JsonFormat jsonFormat;

    public KafkaOpenMrcClientAdapter(Producer<String, String> client) {
        this(client, DEFAULT_JSON_FORMAT);
    }

    public KafkaOpenMrcClientAdapter(Producer<String, String> client, JsonFormat jsonFormat) {
        this.client = requireNonNull(client);
        this.jsonFormat = requireNonNull(jsonFormat);
    }

    @Override
    public void accept(OpenMrc.Request request) {
        String json = jsonFormat.printToString(request);
        String topic = request.getType().name();
        ProducerRecord<String, String> producerRecord = new ProducerRecord<>(topic, json);

        if (log.isDebugEnabled()) {
            log.debug("Sending request to kafka: {}", json);
        }

        client.send(producerRecord);
    }
}
