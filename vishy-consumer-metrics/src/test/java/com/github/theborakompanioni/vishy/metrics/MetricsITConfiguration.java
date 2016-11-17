package com.github.theborakompanioni.vishy.metrics;

import com.github.theborakompanioni.openmrc.OpenMrcRequestConsumer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;

import static org.mockito.Mockito.spy;

@Configuration
@Import(VishyMetricsAutoConfiguration.class)
class MetricsITConfiguration {

    @Bean
    @Primary
    public OpenMrcRequestConsumer testMetricsOpenMrcClientAdapter(OpenMrcRequestConsumer sut) {
        return spy(sut);
    }
}
