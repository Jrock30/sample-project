package com.sample.project.config.openapi;

import com.sample.project.common.type.ResponseErrorCode;
import com.sample.project.common.wrapper.CommonErrorResponse;
import com.sample.project.common.wrapper.CommonSuccessResponse;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.SpringDocUtils;
import org.springdoc.core.customizers.OpenApiCustomiser;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.util.Map;

/**
 * Swagger 설정
 *
 * @author user
 * @since 2022/11/01
 */
@Profile({"local","dev"})
@EnableWebMvc
@Configuration
public class OpenApiConfig {

    // 토큰인증헤더 키
    public static final String HEADER_NAME_AUTHORIZATION = "authorization";
    // 토큰인증헤더 키 (Basic)
    public static final String HEADER_NAME_BASIC         = "basic";
    // API 헤더 키
    public static final String HEADER_NAME_APIKEY        = "apiKey";

    static {
        // ingnore swagger parameter
        // https://springdoc.org/faq.html#how-can-i-hide-a-parameter-from-the-documentation-
        SpringDocUtils.getConfig()
                .addSimpleTypesForParameterObject()
                .addRequestWrapperToIgnore(
                        Pageable.class,
                        Map.class
//                        Specification.class, // jpa
                );
    }

    @Bean
    public OpenAPI api() {
        return new OpenAPI()
                .components(generateComponents())
                .info(new Info()
                        .version("v1")
                        .title("챗봇나우 관리자 시스템 API")
                        .description("챗봇나우 관리자에서 제공하는 REST API 명세 문서입니다.")
                        .contact(new Contact().name("DKT").email("dami@applecorp.com").url("https://"))
                        .license(new License().name("Copyrightⓒ2022 sample All rights reserved").url("https://"))
                )
                .addSecurityItem(new SecurityRequirement().addList(OpenApiConfig.HEADER_NAME_AUTHORIZATION))
                ;
    }

    private Components generateComponents() {
        return new Components()
                // 인증헤더 설정
                .addSecuritySchemes(OpenApiConfig.HEADER_NAME_AUTHORIZATION,
                        new SecurityScheme()
                                .name(OpenApiConfig.HEADER_NAME_AUTHORIZATION)

                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                )
                // BASIC
//                .addSecuritySchemes(OpenApiConfig.HEADER_NAME_BASIC,
//                        new SecurityScheme()
//                                .type(SecurityScheme.Type.HTTP)
//                                .scheme("Basic")
//                )
                // API 키 헤더 설정
//                .addSecuritySchemes(OpenApiConfig.HEADER_NAME_APIKEY,
//                        new SecurityScheme()
//                                .in(SecurityScheme.In.HEADER)
//                                .name("keyName")
//                                .type(SecurityScheme.Type.APIKEY)
//                )
                ;
    }

    /*
     * 전역설정
     */
    @Bean
    public OpenApiCustomiser globalOpenApiResponses() {
        return openApi -> openApi.getPaths().values().forEach(path -> path.readOperations()
                .forEach(operation -> {
                    ApiResponses responses = operation.getResponses();
//                    responses.addApiResponse("200", makeApiSuccessResponse("성공 응답", new CommonSuccessResponse<>(new ObjectMapper())));
                    responses.addApiResponse("400", makeApiErrorResponse("사용자 요청 에러 응답", new CommonErrorResponse(ResponseErrorCode.FAIL_400.code(), ResponseErrorCode.FAIL_400.message())));
                    responses.addApiResponse("401", makeApiErrorResponse("인증 에러 응답", new CommonErrorResponse(ResponseErrorCode.FAIL_401.code(), ResponseErrorCode.FAIL_401.message())));
                    responses.addApiResponse("403", makeApiErrorResponse("인가 에러 응답", new CommonErrorResponse(ResponseErrorCode.FAIL_403.code(), ResponseErrorCode.FAIL_403.message())));
                    responses.addApiResponse("404", makeApiErrorResponse("찾을 수 없는 리소스 에러 응답", new CommonErrorResponse(ResponseErrorCode.FAIL_404.code(), ResponseErrorCode.FAIL_404.message())));
                    responses.addApiResponse("500", makeApiErrorResponse("서버 에러 응답", new CommonErrorResponse(ResponseErrorCode.FAIL_500.code(), ResponseErrorCode.FAIL_500.message())));
                }));
    }

    /*
     * 기본응답 데이터 확인
     * @param desc
     * @param message
     * @return
     */
    private ApiResponse makeApiSuccessResponse(String desc, CommonSuccessResponse<?> data) {
        return new ApiResponse()
                .description(desc)
                .content(new Content().addMediaType(MediaType.APPLICATION_JSON_VALUE, new io.swagger.v3.oas.models.media.MediaType().schema(new Schema<CommonSuccessResponse<?>>().example(data))));
    }

    private ApiResponse makeApiErrorResponse(String desc, CommonErrorResponse data) {
        return new ApiResponse()
                .description(desc)
                .content(new Content().addMediaType(MediaType.APPLICATION_JSON_VALUE, new io.swagger.v3.oas.models.media.MediaType().schema(new Schema<CommonErrorResponse>().example(data))));
    }
}