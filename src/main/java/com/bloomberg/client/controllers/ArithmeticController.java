package com.bloomberg.client.controllers;

import io.netty.channel.ChannelOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.tcp.TcpClient;

import java.time.Duration;

@RestController
public class ArithmeticController {
    private static Logger logger = LoggerFactory.getLogger(ArithmeticController.class);
    private WebClient webClient;

    @Autowired
    public ArithmeticController(WebClient.Builder webClientBuilder) {

        //TODO set uri in config file
        webClient = webClientBuilder
                        .baseUrl("http://arithmetic-service/operation")
                        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE).build();
    }

    @RequestMapping("/arithmetic")
    public Mono<String> arithmetic(@RequestBody String body) {

        //TODO try to bind body as object maybe?
        var result = webClient.post().body(BodyInserters.fromValue(body))
                .retrieve()
                //TODO set timeout in config
                .bodyToMono(String.class).onErrorResume(e -> fallback()).timeout(Duration.ofMillis(2000));
        return result;
    }

    //TODO add proper fallback
    private Mono<String> fallback(){
        logger.info("Fallback");
        return Mono.just("fallback");
    }
}
