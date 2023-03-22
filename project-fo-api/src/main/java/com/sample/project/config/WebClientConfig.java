package com.sample.project.config;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.http.codec.FormHttpMessageReader;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Slf4j
@Configuration
public class WebClientConfig {

    @Value("${apple24.auth-code}")
    private String apple24AuthorizationCode;

    @Value("${apple24.api-uri}")
    private String apple24Uri;

    @Value("${apple24.api-path}")
    private String apple24ApiPath;

    @Value("${apple24.api-version}")
    private String apple24ApiVersion;

    @Value("${biztalk.uri}")
    private String bizTalkUri;

    @Value("${apple-delegate.delegatee-bot-id}")
    private String delegateeBotId; // 대행봇 아이디

    @Value("${apple-delegate.create-uri}")
    private String delegateUri; // 위임, 대행 URI

    @Value("${project-batch.url}")
    private String projectBatchUrl;

    @Bean
    public WebClient apple24WebClient() {
        ExchangeStrategies strategies = ExchangeStrategies.builder()
                .codecs(configurer -> {
                    configurer.registerDefaults(true);
                    configurer.customCodecs().register(new FormHttpMessageReader());
                })
                .build();

        return WebClient.builder()
                .clientConnector(httpClient())
//                .baseUrl(apple24Uri + apple24ApiPath)
                .defaultHeaders(httpHeaders ->  {
                    httpHeaders.setContentType(MediaType.APPLICATION_JSON);
                    httpHeaders.setBasicAuth(apple24AuthorizationCode); // base64_encode({client_id}:{client_secret}
                    httpHeaders.set("X-Apple24-Api-Version", apple24ApiVersion);
                })
//                .exchangeStrategies(strategies)
                .build();
    }

    @Bean
    public WebClient bizTalkWebClient() {
        return WebClient.builder()
                .clientConnector(httpClient())
                .baseUrl(bizTalkUri)
                .defaultHeaders(httpHeaders -> httpHeaders.setContentType(MediaType.APPLICATION_JSON))
                .build();
    }

    @Bean
    public WebClient delegateBotWebClient() {
        String uri = String.format(delegateUri, delegateeBotId);
        return WebClient.builder()
                .clientConnector(httpClient())
                .baseUrl(uri)
                .defaultHeaders(httpHeaders -> httpHeaders.setContentType(MediaType.APPLICATION_JSON))
                .build();
    }

    @Bean
    public WebClient batchWebClient() {

        return WebClient.builder()
                .clientConnector(httpClient())
                .baseUrl(projectBatchUrl)
                .defaultHeaders(httpHeaders -> httpHeaders.setContentType(MediaType.APPLICATION_JSON))
                .build();
    }

    private ReactorClientHttpConnector httpClient() {
        HttpClient httpClient = HttpClient.create()
                .proxyWithSystemProperties()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                .responseTimeout(Duration.ofMillis(5000))
                .doOnConnected(conn ->
                        conn.addHandlerLast(new ReadTimeoutHandler(5000, TimeUnit.MILLISECONDS))
                                .addHandlerLast(new WriteTimeoutHandler(5000, TimeUnit.MILLISECONDS)));

        return new ReactorClientHttpConnector(httpClient);
    }
}
