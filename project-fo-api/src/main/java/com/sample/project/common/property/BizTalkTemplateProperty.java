package com.sample.project.common.property;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class BizTalkTemplateProperty {

    @Value("${biztalk.template.auth.template-code}")
    private String tizTalkAuthTemplateCode;

    @Value("${biztalk.template.auth.message}")
    private String tizTalkAuthTemplateMessage;

    @Value("${biztalk.template.auth.subject}")
    private String tizTalkAuthTemplateSubject;

    @Value("${biztalk.template.auth.sender}")
    private String tizTalkAuthTemplateSender;

    @Value("${biztalk.template.auth.path}")
    private String tizTalkAuthTemplatePath;
}
