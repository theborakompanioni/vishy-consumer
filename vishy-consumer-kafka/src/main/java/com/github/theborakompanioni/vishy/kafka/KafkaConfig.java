package com.github.theborakompanioni.vishy.kafka;

import com.github.theborakompanioni.openmrc.OpenMrcRequestConsumer;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Configuration
@EnableConfigurationProperties(KafkaProperties.class)
@ConditionalOnProperty("vishy.kafka.enabled")
public class KafkaConfig {

    @Autowired
    private Environment environment;

    @Autowired
    private KafkaProperties properties;

    @Bean
    public OpenMrcRequestConsumer kafkaOpenMrcClientAdapter() {
        return new KafkaOpenMrcClientAdapter(kafkaProducer());
    }

    @Bean
    public Producer<String, String> kafkaProducer() {
        return kafkaProducerFactory().createProducer();
    }

    @Bean
    public DefaultKafkaProducerFactory<String, String> kafkaProducerFactory() {
        return new DefaultKafkaProducerFactory<>(producerProperties());
    }

    @Bean
    public Map<String, Object> producerProperties() {
        HashMap<String, Object> props = new HashMap<>();

        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);

        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, properties.getBrokers());
        props.put(ProducerConfig.RETRIES_CONFIG, properties.getRetries());
        props.put(ProducerConfig.BATCH_SIZE_CONFIG, properties.getBatchSize());
        props.put(ProducerConfig.LINGER_MS_CONFIG, properties.getLingerMs());
        props.put(ProducerConfig.BUFFER_MEMORY_CONFIG, properties.getBufferMemory());

        return props;
    }
}
