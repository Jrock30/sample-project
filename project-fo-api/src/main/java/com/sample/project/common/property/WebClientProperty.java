package com.sample.project.common.property;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class WebClientProperty {

    @Value("${apple24.redirect-uri}")
    private String apple24RedirectUri;

    @Value("${apple24.client-id}")
    private String apple24ClientId;

    @Value("${apple24.auth-path}")
    private String apple24AuthPath;

    @Value("${apple24.scope}")
    private String apple24Scope;

    @Value("${apple24.state}")
    private String apple24State;

    @Value("${apple24.api-path}")
    private String apple24ApiPath;

    @Value("${apple24.token-path}")
    private String apple24TokenPath;

    @Value("${biztalk.id}")
    private String bizTalkId;

    @Value("${biztalk.expire}")
    private String bizTalkExpire;

    @Value("${biztalk.token-path}")
    private String bizTalkTokenPath;

    @Value("${biztalk.bsid}")
    private String bizTalkBsid;

    @Value("${biztalk.passwd}")
    private String bizTalkPasswd;

    @Value("${biztalk.sender-key}")
    private String bizTalkSenderKey;

}
