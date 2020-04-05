package com.bloomberg.client.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.client.loadbalancer.reactive.ReactorLoadBalancerExchangeFilterFunction;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@RestController
public class ArithmeticController {
    private static Logger logger = LoggerFactory.getLogger(ArithmeticController.class);


    @Autowired
    private WebClient.Builder webClientBuilder;

    @RequestMapping("/arithmetic")
    public Mono<String> arithmetic(@RequestBody String test) {
        logger.info("Access /arithmetic");
        var result =  webClientBuilder.build().post().uri("http://arithmetic-client/operation").retrieve().bodyToMono(String.class);

        logger.info("Access /arithmetic2");
        return result;

    /*    return loadBalancedWebClientBuilder.build().post().uri("http://arithmetic-client/operation")
                .retrieve().bodyToMono(String.class)
                .map(operation -> String.format("%s, %s!", operation, name));*/
    }
}
