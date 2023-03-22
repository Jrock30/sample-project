package com.sample.project.common.service.web;

import com.sample.project.api.access.dto.RequestTokenDto;
import com.sample.project.api.access.dto.ResponseAgencyTokenDto;
import com.sample.project.api.access.dto.ResponseBizTalkTokenDto;
import com.sample.project.api.access.entity.AgencyAccessHistoryEntity;
import com.sample.project.api.access.entity.AgencyTokenRedisEntity;
import com.sample.project.api.access.entity.BizTalkTokenRedisEntity;
import com.sample.project.api.access.repository.AgencyAccessRepository;
import com.sample.project.api.access.repository.TokenBizTalkRedisRepository;
import com.sample.project.api.access.repository.TokenApple24RedisRepository;
import com.sample.project.api.apple.dto.response.ResponseDelegatorBot;
import com.sample.project.api.marketingMessage.dto.WebClientUrlCond;
import com.sample.project.common.dto.ResponseBizTalkBaseDto;
import com.sample.project.common.exception.Apple24AccessTokenExpiresException;
import com.sample.project.common.exception.CustomException;
import com.sample.project.common.property.WebClientProperty;
import com.sample.project.common.type.ResponseErrorCode;
import com.sample.project.common.utils.CommonUtils;
import com.sample.project.common.utils.StringUtils;
import com.sample.project.security.SecurityUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ObjectUtils;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.io.IOException;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static com.sample.project.common.type.ResponseErrorCode.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class WebClientService {

    private final TokenApple24RedisRepository tokenApple24RedisRepository;

    private final TokenBizTalkRedisRepository tokenBizTalkRedisRepository;

    private final WebClient apple24WebClient;

    private final WebClient bizTalkWebClient;

    private final WebClient delegateBotWebClient;

    private final AgencyAccessRepository agencyAccessRepository;

    private final ObjectMapper objectMapper;

    private final WebClientProperty webClientProperty;

    /**
     * Apple24 토큰 요청 및 Redis 저장
     */
    @Transactional
    public void getApple24AccessToken(RequestTokenDto requestTokenDto) {
        log.debug("requestTokenDto >>>> {} ", requestTokenDto.toString());
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("grant_type", "authorization_code");
        formData.add("code", requestTokenDto.getCode());
        formData.add("redirect_uri", webClientProperty.getApple24RedirectUri());

        AgencyAccessHistoryEntity agencyAccessHistoryEntity = agencyAccessRepository.getById(requestTokenDto.getState());

        String result = apple24WebClient.post()
//                .uri(webClientProperty.getApple24TokenPath())
                .uri(uriBuilder ->
                        uriBuilder.scheme("https")
                                .host(agencyAccessHistoryEntity.getMallId() + ".apple24api.com")
                                .path(webClientProperty.getApple24ApiPath() + webClientProperty.getApple24TokenPath())
                                .build()
                )
                .headers(httpHeaders -> httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED))
                .body((BodyInserters.fromFormData(formData)))
                .retrieve()
                .bodyToMono(String.class)
                .onErrorMap(e -> {
                    log.debug("[[ WebClient Error Response Body ]]  >> {}", ((WebClientResponseException) e).getResponseBodyAsString());
                    log.debug(CommonUtils.getPrintStackTrace(e));
                    return new CustomException(FAIL_500.message(), FAIL_500.status());
                })
                .block();

        try {
            ResponseAgencyTokenDto responseAgencyTokenDto;
            responseAgencyTokenDto = objectMapper.readValue(result, ResponseAgencyTokenDto.class);
            saveAgencyRedisToken(responseAgencyTokenDto);
        } catch (JsonProcessingException e) {
            throw new CustomException(FAIL_500.message(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Apple24 Refresh 토큰 요청 및 Redis 저장
     */
    @Transactional
    public void getRefreshToken() {
        AgencyTokenRedisEntity agencyTokenRedisEntity =
                tokenApple24RedisRepository.findById(Objects.requireNonNull(SecurityUtils.getMallId().orElse(null)))
                        .orElseThrow(() -> new CustomException(FAIL_4003.message(), HttpStatus.BAD_REQUEST));

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("grant_type", "refresh_token");
        formData.add("refresh_token", agencyTokenRedisEntity.getRefreshToken());

        String result = apple24WebClient.post()
                .uri(uriBuilder ->
                        uriBuilder.scheme("https")
                                .host(agencyTokenRedisEntity.getId() + ".apple24api.com")
                                .path(webClientProperty.getApple24ApiPath() + webClientProperty.getApple24TokenPath())
                                .build()
                )
                .headers(httpHeaders -> httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED))
                .body((BodyInserters.fromFormData(formData)))
                .retrieve()
                .bodyToMono(String.class)
                .onErrorMap(e -> {
                    log.debug("[[ WebClient Error Response Body ]]  >> {}", ((WebClientResponseException) e).getResponseBodyAsString());
                    log.debug(CommonUtils.getPrintStackTrace(e));
                    return new CustomException(FAIL_500.message(), HttpStatus.INTERNAL_SERVER_ERROR);
                })
                .block();
        try {
            ResponseAgencyTokenDto responseAgencyTokenDto;
            responseAgencyTokenDto = objectMapper.readValue(result, ResponseAgencyTokenDto.class);
            saveAgencyRedisToken(responseAgencyTokenDto);
        } catch (JsonProcessingException e) {
            throw new CustomException(FAIL_500.message(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public void saveAgencyRedisToken(ResponseAgencyTokenDto responseAgencyTokenDto) {
        AgencyTokenRedisEntity agencyTokenRedisEntity = AgencyTokenRedisEntity.builder()
                .id(responseAgencyTokenDto.getMallId())
                .accessToken(responseAgencyTokenDto.getAccessToken())
                .accessTokenExpiresAt(responseAgencyTokenDto.getExpiresAt())
                .refreshToken(responseAgencyTokenDto.getRefreshToken())
                .refreshTokenExpiresAt(responseAgencyTokenDto.getRefreshTokenExpiresAt())
                .blackListYn("0")
                .build();

        tokenApple24RedisRepository.save(agencyTokenRedisEntity);
        log.debug("responseAgencyTokenDto >>> {}", responseAgencyTokenDto);
    }

    /**
     * @author: user
     * @desc  : Apple24 GET 요청
     * @param : String lastPath
     * @return JsonNode
     * @throws IOException
     */
    public JsonNode getApple24JsonAuth(String lastPath, String mallId) throws IOException {

        if (ObjectUtils.isEmpty(mallId)) {
            mallId = SecurityUtils.getMallId().orElseThrow(
                () -> new CustomException(FAIL_4003.message(), HttpStatus.UNAUTHORIZED)); // 몰 정보를 찾을 수 없습니다.
        }

        AgencyTokenRedisEntity agencyTokenRedisEntity = tokenApple24RedisRepository.findById(mallId)
                .orElseThrow(() -> new CustomException(FAIL_4003.message(), HttpStatus.BAD_REQUEST));

        /**
         * @user 2022/12/06
         * 자동으로 Access Token 을 재발급 받기 위해서 예외처리를 통해 재발급 그리고 해당 API 를 한번 더 콜 한다.
         */
        String result;
        try {
            result = searchGetApple24Api(lastPath, agencyTokenRedisEntity);
        } catch (Apple24AccessTokenExpiresException e) {
            log.debug("[[ Apple24AccessTokenExpiresException ]] : {}", e.getMessage());
            this.getRefreshToken(); // ** Access 토큰 재발급 **
            AgencyTokenRedisEntity refreshAgencyTokenRedisEntity = // 참고 - 재발급 된 토큰을 재 조회한다! ( 재 조회 안하고 위 메서드에서 리턴 받아도 되긴 함 )
                    tokenApple24RedisRepository.findById(mallId)
                    .orElseThrow(() -> new CustomException(FAIL_4003.message(), HttpStatus.BAD_REQUEST));
            result = searchGetApple24Api(lastPath, refreshAgencyTokenRedisEntity);
        }
        return jsonNode(result);
    }

    private String searchGetApple24Api(String lastPath, AgencyTokenRedisEntity agencyTokenRedisEntity) {
        return apple24WebClient.get()

                .uri(uriBuilder ->
                        uriBuilder.scheme("https")
                                .host(agencyTokenRedisEntity.getId() + ".apple24api.com")
                                .path(lastPath)
                                .build()
                )
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + agencyTokenRedisEntity.getAccessToken())
                .retrieve()
                .onStatus(HttpStatus::is4xxClientError,
                        clientResponse -> Mono.error(new Apple24AccessTokenExpiresException(FAIL_4001.message())))
                .bodyToMono(String.class)
                .onErrorMap(e -> {
                    log.debug("[[ WebClient Error Response Body ]]  >> {}", e.getMessage());
                    log.debug(CommonUtils.getPrintStackTrace(e));
                    return new Apple24AccessTokenExpiresException(ResponseErrorCode.FAIL_4001.message());
//                    return new CustomException(FAIL_500.message(), HttpStatus.INTERNAL_SERVER_ERROR);
                })
                .block();
    }


    /**
     * @desc  : Apple24 GET 요청(기간 조건)
     * @param : String lastPath
     * @return JsonNode
     * @throws IOException
     */
    public JsonNode getApple24JsonAuthPeriod(String mallId, WebClientUrlCond urlPathParam) throws IOException {
        if (ObjectUtils.isEmpty(mallId)) {
            mallId = SecurityUtils.getMallId().orElseThrow(
                    () -> new CustomException(FAIL_4003.message(), HttpStatus.UNAUTHORIZED)); // 몰 정보를 찾을 수 없습니다.
        }

        AgencyTokenRedisEntity agencyTokenRedisEntity = tokenApple24RedisRepository.findById(mallId)
                .orElseThrow(() -> new CustomException(FAIL_4003.message(), HttpStatus.BAD_REQUEST));

        /**
         * @user 2022/12/06
         * 자동으로 Access Token 을 재발급 받기 위해서 예외처리를 통해 재발급 그리고 해당 API 를 한번 더 콜 한다.
         */
        String result;
        try {
            result = searchGetApple24ApiPeriod(urlPathParam, agencyTokenRedisEntity);
        } catch (Apple24AccessTokenExpiresException e) {
            log.debug("[[ Apple24AccessTokenExpiresException ]] : {}", e.getMessage());
            this.getRefreshToken(); // ** Access 토큰 재발급 **
            AgencyTokenRedisEntity refreshAgencyTokenRedisEntity = // 참고 - 재발급 된 토큰을 재 조회한다! ( 재 조회 안하고 위 메서드에서 리턴 받아도 되긴 함 )
                    tokenApple24RedisRepository.findById(mallId)
                            .orElseThrow(() -> new CustomException(FAIL_4003.message(), HttpStatus.BAD_REQUEST));
            result = searchGetApple24ApiPeriod(urlPathParam, refreshAgencyTokenRedisEntity);
        }
        return jsonNode(result);
    }

    private String searchGetApple24ApiPeriod(WebClientUrlCond urlPathParam, AgencyTokenRedisEntity agencyTokenRedisEntity) {
        String lastPath = urlPathParam.getLastPath();

        return apple24WebClient.get()
                .uri(uriBuilder -> {
                    if (lastPath.contains("/admin/products/")) {
                        return uriBuilder.scheme("https").host(agencyTokenRedisEntity.getId()+".apple24api.com").path("/api/v2"+urlPathParam.getLastPath())
                                .queryParam("embed", urlPathParam.getEmbed())
                                .build();
                    } else if (lastPath.contains("/admin/orders")) {
                        return uriBuilder.scheme("https").host(agencyTokenRedisEntity.getId()+".apple24api.com").path("/api/v2"+urlPathParam.getLastPath())
                                .queryParam("start_date", urlPathParam.getStartDate())
                                .queryParam("end_date", urlPathParam.getEndDate())
                                .queryParam("embed", urlPathParam.getEmbed())
                                .queryParam("inflow_path", urlPathParam.getInflowPath())
                                .queryParam("limit", urlPathParam.getLimit())
                                .build();
                    }
                    return uriBuilder.scheme("https").host(agencyTokenRedisEntity.getId()+".apple24api.com").path("/api/v2"+urlPathParam.getLastPath()).build();
                })
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + agencyTokenRedisEntity.getAccessToken())
                .retrieve()
                .onStatus(HttpStatus::is4xxClientError,
                        clientResponse -> Mono.error(new Apple24AccessTokenExpiresException(ResponseErrorCode.FAIL_4001.message())))
                .bodyToMono(String.class)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(1))
                        .filter(ex -> HttpStatus.TOO_MANY_REQUESTS.is4xxClientError()))
                .onErrorMap(e -> {
                    log.debug("[[ WebClient Error Response Body ]]  >> {}", e.getMessage());
                    log.debug(CommonUtils.getPrintStackTrace(e));
                    return new Apple24AccessTokenExpiresException(ResponseErrorCode.FAIL_4001.message());
                })
                .block();
    }







    /**
     * @author: user
     * @desc  : Apple24 POST 요청
     * @param : String lastPath
     * @return JsonNode
     * @throws IOException
     */
    public JsonNode postApple24JsonAuth(String lastPath, JsonNode body) throws IOException {
        AgencyTokenRedisEntity agencyTokenRedisEntity = tokenApple24RedisRepository.findById(SecurityUtils.getMallId().orElse(""))
                .orElseThrow(() -> new CustomException(FAIL_4003.message(), HttpStatus.BAD_REQUEST));

        /**
         * @user 2022/12/06
         * 자동으로 Access Token 을 재발급 받기 위해서 예외처리를 통해 재발급 그리고 해당 API 를 한번 더 콜 한다.
         */
        String result;
        try {
            result = searchPostApple24Api(lastPath, body, agencyTokenRedisEntity);
        } catch (Apple24AccessTokenExpiresException e) {
            log.debug("[[ Apple24AccessTokenExpiresException ]] : {}", e.getMessage());
            this.getRefreshToken(); // ** Access 토큰 재발급 **
            result = searchPostApple24Api(lastPath, body, agencyTokenRedisEntity);
        }
        return jsonNode(result);
    }

    private String searchPostApple24Api(String lastPath, JsonNode body, AgencyTokenRedisEntity agencyTokenRedisEntity) {
        return apple24WebClient.post()
//                .uri(lastPath)
                .uri(uriBuilder ->
                        uriBuilder.scheme("https")
                                .host(agencyTokenRedisEntity.getId() + ".apple24api.com")
                                .path(lastPath)
                                .build()
                )
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + agencyTokenRedisEntity.getAccessToken())
                .bodyValue(body)
                .retrieve()
                .onStatus(HttpStatus::is4xxClientError,
                        clientResponse -> Mono.error(new Apple24AccessTokenExpiresException(FAIL_4001.message())))
                .bodyToMono(String.class)
                .block();
    }

    /**
     * 비즈톡 토큰요청 (Redis 에 있으면 가져오고 없으면 토큰 발급 저장)
     */
    @Transactional
    public String getBizTalkToken() {

        Optional<BizTalkTokenRedisEntity> bizTalkTokenRedisEntity = tokenBizTalkRedisRepository.findById(webClientProperty.getBizTalkId());

        if (bizTalkTokenRedisEntity.isPresent()) {
            if (StringUtils.isNotEmpty(bizTalkTokenRedisEntity.get().getToken())) {
                return bizTalkTokenRedisEntity.get().getToken();
            }
        }

        Map<String, String> param = new HashMap<>();
        param.put("bsid", webClientProperty.getBizTalkBsid());
        param.put("passwd", webClientProperty.getBizTalkPasswd());

        ObjectMapper mapper = new ObjectMapper();
        JsonNode body = mapper.valueToTree(param);

        String result = bizTalkWebClient.post()
                .uri(webClientProperty.getBizTalkTokenPath())
                .headers(httpHeaders -> httpHeaders.setContentType(MediaType.APPLICATION_JSON))
                .bodyValue(body)
                .retrieve()
//                .onStatus(HttpStatus::is4xxClientError,
//                        clientResponse -> Mono.error(RuntimeException::new))
                .bodyToMono(String.class)
                .onErrorMap(e -> {
                    log.debug("[[ WebClient Error Response Body ]]  >> {}", ((WebClientResponseException) e).getResponseBodyAsString());
                    log.debug(CommonUtils.getPrintStackTrace(e));
                    return new CustomException(FAIL_500.message(), HttpStatus.INTERNAL_SERVER_ERROR);
                })
                .block();

        try {
            ResponseBizTalkTokenDto responseBizTalkTokenDto =
                    objectMapper.readValue(result, ResponseBizTalkTokenDto.class);
            BizTalkTokenRedisEntity newBizTalkTokenEntity = BizTalkTokenRedisEntity.builder()
                    .id(webClientProperty.getBizTalkId())
                    .token(responseBizTalkTokenDto.getToken())
                    .expireDate(responseBizTalkTokenDto.getExpireDate())
                    .build();
            tokenBizTalkRedisRepository.save(newBizTalkTokenEntity);
            return newBizTalkTokenEntity.getToken();

        } catch (JsonProcessingException e) {
            log.debug("BizTalk Get Token Error -> {}", CommonUtils.getPrintStackTrace(e));
            throw new CustomException(FAIL_500.message(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 프록시 아이피가 다수인 관계로 Redis에 따로 저장하지 않고 알림톡 발송할 때 마다 토큰 얻는 프로세스
     */
    public String getNotRedisBizTalkToken() {

        Map<String, String> param = new HashMap<>();
        param.put("bsid", webClientProperty.getBizTalkBsid());
        param.put("passwd", webClientProperty.getBizTalkPasswd());

        ObjectMapper mapper = new ObjectMapper();
        JsonNode body = mapper.valueToTree(param);

        String result = bizTalkWebClient.post()
                .uri(webClientProperty.getBizTalkTokenPath())
                .headers(httpHeaders -> httpHeaders.setContentType(MediaType.APPLICATION_JSON))
                .bodyValue(body)
                .retrieve()
//                .onStatus(HttpStatus::is4xxClientError,
//                        clientResponse -> Mono.error(RuntimeException::new))
                .bodyToMono(String.class)
                .onErrorMap(e -> {
                    log.debug("[[ WebClient Error Response Body ]]  >> {}", ((WebClientResponseException) e).getResponseBodyAsString());
                    log.debug(CommonUtils.getPrintStackTrace(e));
                    return new CustomException(FAIL_500.message(), HttpStatus.INTERNAL_SERVER_ERROR);
                })
                .block();

        try {
            ResponseBizTalkTokenDto responseBizTalkTokenDto =
                    objectMapper.readValue(result, ResponseBizTalkTokenDto.class);

            return responseBizTalkTokenDto.getToken();
        } catch (JsonProcessingException e) {
            log.debug("BizTalk Get Token Error -> {}", CommonUtils.getPrintStackTrace(e));
            throw new CustomException(FAIL_500.message(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * @author: user
     * @desc: 비즈톡 POST 요청
     * @param : String lastPath
     * @return JsonNode
     * @throws IOException
     */
    public ResponseBizTalkBaseDto postBizTalkJsonAuth(String lastPath, JsonNode body) throws IOException {
//        String bizTalkToken = accessService.getBizTalkToken(); // Redis 이용
        String bizTalkToken = this.getNotRedisBizTalkToken(); // 매번 토큰 요청(proxy ip 로 인해 현재 이 것 사용)

        String result = bizTalkWebClient.post()
                .uri(lastPath)
                .header("bt-token", bizTalkToken)
                .bodyValue(body)
                .retrieve()
//                .onStatus(HttpStatus::is4xxClientError,
//                        clientResponse -> Mono.error(RuntimeException::new))
                .bodyToMono(String.class)
                .onErrorMap(e -> {
                    log.debug("[[ WebClient Error Response Body ]]  >> {}", ((WebClientResponseException) e).getResponseBodyAsString());
                    log.debug(CommonUtils.getPrintStackTrace(e));
                    return new CustomException(FAIL_500.message(), HttpStatus.INTERNAL_SERVER_ERROR);
                })
                .block();

        log.debug("BizTalk Send Result >> {}", result);
        return objectMapper.readValue(result, ResponseBizTalkBaseDto.class);
    }

    /**
     * @author: user
     * @desc: 카카오 위임-대행 POST 요청
     * @param : String lastPath
     * @return JsonNode
     * @throws IOException
     */
    public ResponseDelegatorBot postAppleDelegateJsonAuth(String lastPath, JsonNode body) throws IOException {
        log.debug("APPLE Delegate Request Payload > {}", body);
        String result = delegateBotWebClient.post()
                .uri(lastPath)
                .bodyValue(body)
                .retrieve()
                .onStatus(HttpStatus::is4xxClientError,
                        clientResponse -> Mono.error(RuntimeException::new))
                .bodyToMono(String.class)
                .block();

        log.debug("Apple Delegate Send Result >> {}", result);
        return objectMapper.readValue(result, ResponseDelegatorBot.class);
    }

    public JsonNode jsonNode(String result) throws JsonProcessingException {
        return objectMapper.readTree(result);
    }
}
