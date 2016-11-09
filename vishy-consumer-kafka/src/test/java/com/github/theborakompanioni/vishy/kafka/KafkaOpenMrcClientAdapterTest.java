package com.github.theborakompanioni.vishy.kafka;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.theborakompanioni.openmrc.OpenMrc;
import com.github.theborakompanioni.openmrc.mother.protobuf.InitialRequestProtobufMother;
import com.google.protobuf.ExtensionRegistry;
import com.googlecode.protobuf.format.JsonFormat;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.test.rule.KafkaEmbedded;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
public class KafkaOpenMrcClientAdapterTest {
    private static final String[] TOPICS = Arrays.stream(OpenMrc.RequestType.values())
            .map(Enum::name)
            .toArray(String[]::new);

    @ClassRule
    public static KafkaEmbedded embeddedKafka = new KafkaEmbedded(1, true, TOPICS);

    private static Consumer<String, String> consumer;

    private KafkaOpenMrcClientAdapter sut;

    @BeforeClass
    public static void beforeClass() throws Exception {
        Map<String, Object> consumerProps = KafkaTestUtils.consumerProps("testGroup", "true", embeddedKafka);
        consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);

        DefaultKafkaConsumerFactory<String, String> cf = new DefaultKafkaConsumerFactory<>(consumerProps);
        consumer = cf.createConsumer();

        embeddedKafka.consumeFromAnEmbeddedTopic(consumer, OpenMrc.RequestType.INITIAL.name());
    }

    @Before
    public void init() {
        Map<String, Object> producerProps = KafkaTestUtils.producerProps(embeddedKafka);
        producerProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        producerProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);

        DefaultKafkaProducerFactory<String, String> pf = new DefaultKafkaProducerFactory<>(producerProps);
        final Producer<String, String> producer = pf.createProducer();

        this.sut = new KafkaOpenMrcClientAdapter(producer);
    }

    @Test
    public void accept() throws Exception {
        final OpenMrc.Request initialRequest = new InitialRequestProtobufMother().standardInitialRequest().build();

        this.sut.accept(initialRequest);

        final ConsumerRecord<String, String> singleRecord = KafkaTestUtils.getSingleRecord(consumer, initialRequest.getType().name());

        assertThat(singleRecord, is(notNullValue()));

        final OpenMrc.Request.Builder builder = OpenMrc.Request.newBuilder();
        new JsonFormat().merge(singleRecord.value(), ExtensionRegistry.getEmptyRegistry(), builder);
        final OpenMrc.Request fromKafka = builder.build();

        assertThat(fromKafka, is(equalTo(initialRequest)));

        // sanity check: parse with jackson
        final ObjectMapper objectMapper = new ObjectMapper();
        final HashMap<String, Object> jsonAsMap = objectMapper.readValue(singleRecord.value(), new TypeReference<HashMap<String, Object>>() {
        });
        assertThat(jsonAsMap, hasEntry("type", initialRequest.getType().name()));
    }

}
