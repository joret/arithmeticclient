package com.bloomberg.client.controllers;

import com.bloomberg.client.models.Operation;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.time.Duration;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@RestController
public class ArithmeticController {
    private static Logger logger = LoggerFactory.getLogger(ArithmeticController.class);
    private WebClient webClient;
    private WebClient loadBalancedWeb;
    private ObjectMapper objectMapper = new ObjectMapper();

    //TODO set in config
    private final String uri = "http://arithmetic-service/operation";
    private final String uriFallback = "http://api.mathjs.org/v4/";
    private final int timeout = 20000;

    @Autowired
    public ArithmeticController(WebClient.Builder loadBalancedWebClientBuilder, WebClient.Builder webClientBuilder) {
        loadBalancedWeb = loadBalancedWebClientBuilder
                        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .filters(exchangeFilterFunctions -> {
                            exchangeFilterFunctions.add(logRequest());
                            exchangeFilterFunctions.add(logResponse());
                        })
                        .build();

        webClient = webClientBuilder
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .filters(exchangeFilterFunctions -> {
                    exchangeFilterFunctions.add(logRequest());
                    exchangeFilterFunctions.add(logResponse());
                })
                .build();
    }

    private ExchangeFilterFunction logRequest() {
        return ExchangeFilterFunction.ofRequestProcessor(clientRequest -> {

                StringBuilder sb = new StringBuilder();
                sb.append("FORWARDING:\t");
                sb.append(clientRequest.method());
                sb.append(" URL:");
                sb.append(clientRequest.url());
                clientRequest
                        .headers()
                        .forEach((name, values) -> values.forEach(value -> {sb.append("\tName:");sb.append(name);sb.append(value);} ));

                logger.info(sb.toString());

            return Mono.just(clientRequest);
        });
    }

    private ExchangeFilterFunction logResponse() {
        return ExchangeFilterFunction.ofResponseProcessor(clientResponse -> {

            StringBuilder sb = new StringBuilder();
            sb.append("RESPONSE:\t");

            sb.append(clientResponse.statusCode());
            clientResponse
                    .headers().asHttpHeaders()
                    .forEach((name, values) -> values.forEach(value -> {sb.append("\tName:");sb.append(name);sb
                    .append(value);} ));


            logger.info(sb.toString());

            return Mono.just(clientResponse);
        });
    }

    @RequestMapping("/arithmetic")
    public Mono<String> arithmetic(@RequestBody String body) {

        var result = loadBalancedWeb.post().uri(uri).body(BodyInserters.fromValue(body))
                .retrieve()
                .bodyToMono(String.class)
                .onErrorResume(WebClientResponseException.class,
                    //Fallback only on 500, 503 or Timeout
                    ex -> (ex.getStatusCode() == INTERNAL_SERVER_ERROR
                            || ex.getStatusCode() == HttpStatus.REQUEST_TIMEOUT
                            || ex.getStatusCode() == HttpStatus.SERVICE_UNAVAILABLE)
                            ?  fallback(body): Mono.error(ex))
                .timeout(Duration.ofMillis(timeout));


        return result;
    }

    private Mono<String> fallback(String body){
        logger.info("POST to Fallback----------");

        try {
            Operation operation = objectMapper.readValue(body, Operation.class);
            String fallbackExpression = operation.toFallbackJsonObject();
            var result = webClient.post().uri(uriFallback).body(BodyInserters.fromValue(fallbackExpression))
                    .retrieve()
                    .bodyToMono(String.class).timeout(Duration.ofMillis(timeout));

            return result;
        } catch (IOException e) {
            return Mono.error(e);
        }

    }
}
