package com.sample.project.config.http;

import com.sample.project.config.http.proxy.ProxyInfo;
import com.sample.project.config.http.proxy.ProxyProperties;
import com.sample.project.config.http.proxy.Selector;
import lombok.NonNull;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.SystemDefaultRoutePlanner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.stream.Collectors;

//@Configuration
//@RequiredArgsConstructor
public class RestTemplateConfig {

    @NonNull
    private ProxyProperties proxyProperties;


    @Profile("dev | cbt | prod")
    @Bean
    public RestTemplate restTemplate() {

        ProxyInfo proxyInfo = new ProxyInfo();
        proxyInfo.setProxyHost(proxyProperties.getHost());
        proxyInfo.setProxyPort(proxyProperties.getPort());

        HttpComponentsClientHttpRequestFactory factory =
                new HttpComponentsClientHttpRequestFactory(
                        HttpClientBuilder.create()
                                .setRoutePlanner(new SystemDefaultRoutePlanner(new Selector(Arrays.stream(new ProxyInfo[]{proxyInfo}).collect(Collectors.toList()), proxyProperties.getTarget())))
                                .build()
                );

        factory.setConnectionRequestTimeout(3000);  // 연결 요청 지연이 3초를 초과하면 Exception
        factory.setConnectTimeout(10000);           // 서버에 연결 지연이 10초를 초과하면 Exception
        factory.setReadTimeout(5000);               // 데이터 수신 지연이 5초를 초과하면 Exception
        return new RestTemplate(factory);
    }


    @Profile("local | pc")
    @Bean
    public RestTemplate restTemplateLocal() {
        HttpComponentsClientHttpRequestFactory factory =
                new HttpComponentsClientHttpRequestFactory(
                        HttpClientBuilder.create()
                                .build()
                );
        factory.setConnectionRequestTimeout(3000);  // 연결 요청 지연이 3초를 초과하면 Exception
        factory.setConnectTimeout(10000);           // 서버에 연결 지연이 10초를 초과하면 Exception
        factory.setReadTimeout(5000);               // 데이터 수신 지연이 5초를 초과하면 Exception
        return new RestTemplate(factory);
    }
}
