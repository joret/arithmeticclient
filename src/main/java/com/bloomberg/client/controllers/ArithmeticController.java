package com.bloomberg.client.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@RestController
public class ArithmeticController {
    private static Logger logger = LoggerFactory.getLogger(ArithmeticController.class);
    private WebClient webClient;

    @Autowired
    public ArithmeticController(WebClient.Builder webClientBuilder) {
        //TODO set uri in config file
        webClient = webClientBuilder
                        .baseUrl("http://arithmetic-client/operation")
                        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE).build();
    }

    @RequestMapping("/arithmetic")
    public Mono<String> arithmetic(@RequestBody String body) {
        //TODO try to bind body as object maybe?
        var result = webClient.post().body(BodyInserters.fromValue(body)).retrieve().bodyToMono(String.class);
        return result;

    /*    return loadBalancedWebClientBuilder.build().post().uri("http://arithmetic-client/operation")
                .retrieve().bodyToMono(String.class)
                .map(operation -> String.format("%s, %s!", operation, name));*/
    }
}
