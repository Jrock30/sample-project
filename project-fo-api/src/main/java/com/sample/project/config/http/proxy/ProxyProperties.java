package com.sample.project.config.http.proxy;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "proxy")
public class ProxyProperties {
    private String host;
    private Integer port;
    private List<String> target;
}
