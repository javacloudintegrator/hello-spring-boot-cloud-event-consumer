package com.jci.spring;

import java.net.URI;
import java.util.UUID;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.codec.CodecCustomizer;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.CodecConfigurer;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import io.cloudevents.CloudEvent;
import io.cloudevents.core.builder.CloudEventBuilder;
import io.cloudevents.spring.http.CloudEventHttpUtils;
import io.cloudevents.spring.webflux.CloudEventHttpMessageReader;
import io.cloudevents.spring.webflux.CloudEventHttpMessageWriter;
import reactor.core.publisher.Mono;

@SpringBootApplication
@RestController
// @Slf4j
public class KnativeHelloWorldApplication {

    public static void main(String[] args) {
        System.out.println("  -> in main");
        SpringApplication.run(KnativeHelloWorldApplication.class, args);
    }

    @PostMapping("/mono")
    // Use CloudEvent API and manual type conversion of request and response body
    public Mono<CloudEvent> hello(@RequestBody Mono<CloudEvent> body) {

        System.out.println("Received cloud event");
        String outEventData = "{\"value\": \"Event consumer says hello back!\"}";

        Mono<CloudEvent> outEvent = body.map(event -> CloudEventBuilder.from(event).withId(UUID.randomUUID().toString())
                .withSource(URI.create("hello-spring-boot-cloud-event-consumer")).withType("com.jci.cloud.event")
                .withData(outEventData.getBytes()).build());

        return outEvent;
    }

    @PostMapping("/event")
    // Let Spring do the type conversion of request and response body
    public ResponseEntity<CloudEvent> echo(@RequestBody CloudEvent cloudEvent, @RequestHeader HttpHeaders headers) {

        String inEventData = new String(cloudEvent.getData().toBytes());
        System.out.println("Incoming CloudEventData: " + inEventData);
        String outEventData = "{\"value\": \"Event consumer says hello back!\"}";

        CloudEvent cloudEventToReturn = CloudEventBuilder.from(cloudEvent).withId(UUID.randomUUID().toString())
                .withSource(URI.create("hello-spring-boot-cloud-event-consumer")).withType("com.jci.cloud.event")
                .withData(outEventData.getBytes()).build();

        HttpHeaders outgoing = CloudEventHttpUtils.toHttp(cloudEventToReturn);
        return ResponseEntity.ok().headers(outgoing).body(cloudEventToReturn);
    }

    @Configuration
    public static class CloudEventHandlerConfiguration implements CodecCustomizer {

        @Override
        public void customize(CodecConfigurer configurer) {
            configurer.customCodecs().register(new CloudEventHttpMessageReader());
            configurer.customCodecs().register(new CloudEventHttpMessageWriter());
        }

    }

}
