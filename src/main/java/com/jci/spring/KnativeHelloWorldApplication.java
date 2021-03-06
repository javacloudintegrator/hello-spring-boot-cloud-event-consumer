package com.jci.spring;

import java.net.URI;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
    public Mono<CloudEvent> mono(@RequestBody Mono<CloudEvent> body) {

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();

        System.out.println(dtf.format(now) + ": mono - Received cloud event");
        String outEventData = "{\"value\": \"mono Event consumer says hello back!\"}";

        Mono<CloudEvent> outEvent = body.map(event -> CloudEventBuilder.from(event).withId(UUID.randomUUID().toString())
                .withSource(URI.create("hello-spring-boot-cloud-event-consumer"))
                .withType("com.jci.cloud.event.out.mono").withData(outEventData.getBytes()).build());

        return outEvent;
    }

    @PostMapping("/event")
    // Let Spring do the type conversion of request and response body
    public ResponseEntity<CloudEvent> event(@RequestBody CloudEvent cloudEvent, @RequestHeader HttpHeaders headers) {

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();

        String inEventData = new String(cloudEvent.getData().toBytes());
        System.out.println(dtf.format(now) + ": event - Incoming CloudEventData: " + inEventData);
        String outEventData = "{\"value\": \"event Event consumer says hello back!\"}";

        CloudEvent cloudEventToReturn = CloudEventBuilder.from(cloudEvent).withId(UUID.randomUUID().toString())
                .withSource(URI.create("hello-spring-boot-cloud-event-consumer"))
                .withType("com.jci.cloud.event.out.event").withData(outEventData.getBytes()).build();

        HttpHeaders outgoing = CloudEventHttpUtils.toHttp(cloudEventToReturn);

        System.out.println(
                dtf.format(now) + ": Returning CloudEventData: " + new String(cloudEventToReturn.getData().toBytes()));
        return ResponseEntity.ok().headers(outgoing).body(cloudEventToReturn);
    }

    @PostMapping("/hello")
    // Let Spring do the type conversion of request and response body
    public ResponseEntity<Hello> hello(@RequestBody Hello hello, @RequestHeader HttpHeaders headers) {

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();

        System.out.println(dtf.format(now) + ": hello - Incoming CloudEventData: " + hello.toString());
        String outEventMsg = "hello Event consumer says hello back!";
        Hello hiBack = new Hello(outEventMsg);

        CloudEvent attributes = CloudEventHttpUtils.fromHttp(headers).withId(UUID.randomUUID().toString())
                .withSource(URI.create("hello-spring-boot-cloud-event-consumer"))
                .withType("com.jci.cloud.event.out.hello").build();

        HttpHeaders outgoing = CloudEventHttpUtils.toHttp(attributes);

        // System.out.println(dtf.format(now) + ": Returning CloudEventData: " + new
        // String(attributes.getData().toBytes()));

        return ResponseEntity.ok().headers(outgoing).body(hiBack);
    }

    //////////////////////////////
    @PostMapping("/listener")
    // Let Spring do the type conversion of request and response body
    public ResponseEntity<CloudEvent> listener(@RequestBody CloudEvent cloudEvent, @RequestHeader HttpHeaders headers) {

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();

        String inEventData = new String(cloudEvent.getData().toBytes());
        System.out.println(dtf.format(now) + ": listener - Incoming CloudEventData: " + inEventData);
        String outEventData = "{\"value\": \"listener Event consumer says hello back!\"}";

        CloudEvent cloudEventToReturn = CloudEventBuilder.from(cloudEvent).withId(UUID.randomUUID().toString())
                .withSource(URI.create("hello-spring-boot-cloud-event-consumer"))
                .withType("com.jci.cloud.event.out.listener").withData(outEventData.getBytes()).build();

        HttpHeaders outgoing = CloudEventHttpUtils.toHttp(cloudEventToReturn);

        System.out.println(
                dtf.format(now) + ": Returning CloudEventData: " + new String(cloudEventToReturn.getData().toBytes()));
        return ResponseEntity.ok().headers(outgoing).body(cloudEventToReturn);
    }

    @Configuration
    public static class CloudEventHandlerConfiguration implements CodecCustomizer {

        @Override
        public void customize(CodecConfigurer configurer) {
            System.err.println("In CloudEventHandlerConfiguration...");
            // if (true)
            // throw new RuntimeException("CloudEventHandlerConfiguration");
            configurer.customCodecs().register(new CloudEventHttpMessageReader());
            configurer.customCodecs().register(new CloudEventHttpMessageWriter());
        }

    }

}
