package com.sample.project.config.http.proxy;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.*;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


@Slf4j
public class Selector extends ProxySelector {

    private final List<ProxyInfo> proxies;
    private final List<String>    proxyTargets;

    public Selector(List<ProxyInfo> proxies, List<String> proxyTargets) {
        this.proxies      = proxies;
        this.proxyTargets = proxyTargets;
    }

    @Override
    public List<Proxy> select(URI uri) {
        final String host = uri.getHost();
        if (proxyTargets.stream().anyMatch(s -> s.contains(host))) {
            log.info("REMOTE SERVER COMMUNICATION WITH PROXY : {}", host);
            return proxies.stream()
                    .map(proxy -> new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxy.getProxyHost(), proxy.getProxyPort())))
                    .collect(Collectors.toList());
        } else {
            log.info("REMOTE SERVER COMMUNICATION WITHOUT PROXY : {}", host);
            return Arrays.stream(new Proxy[]{Proxy.NO_PROXY})
                    .collect(Collectors.toList());
        }
    }

    @Override
    public void connectFailed(URI uri, SocketAddress sa, IOException ioe) {
        throw new UnsupportedOperationException(ioe.getMessage());
    }
}
