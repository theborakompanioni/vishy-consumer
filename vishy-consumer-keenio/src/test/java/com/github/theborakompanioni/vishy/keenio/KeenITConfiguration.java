package com.github.theborakompanioni.vishy.keenio;

import com.github.theborakompanioni.openmrc.OpenMrc;
import com.github.theborakompanioni.openmrc.OpenMrcRequestConsumer;
import com.github.theborakompanioni.vishy.consumer.RequestToMapFunction;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.function.Function;

import static org.mockito.Mockito.spy;

@Configuration
@EnableConfigurationProperties(KeenIoProperties.class)
class KeenITConfiguration extends KeenIoConfig {

    @Override
    @Bean
    @Primary
    public OpenMrcRequestConsumer keenOpenMrcClientAdapter() {
        String testEventPrefix = "test_";
        Function<OpenMrc.Request, String> toEventNameFunction = request -> testEventPrefix + request.getType().name();
        final KeenOpenMrcClientAdapter client = new KeenOpenMrcClientAdapter(keenClient(), new RequestToMapFunction(), toEventNameFunction);

        final KeenOpenMrcClientAdapter clientSpy = spy(client);

        return clientSpy;
    }
}
