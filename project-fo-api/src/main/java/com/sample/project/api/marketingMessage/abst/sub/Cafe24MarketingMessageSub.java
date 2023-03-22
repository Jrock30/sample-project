package com.sample.project.api.marketingMessage.abst.sub;

import com.sample.project.api.marketingMessage.abst.MarketingMessageAbstract;
import com.sample.project.api.marketingMessage.repository.*;
import com.sample.project.api.marketingMessage.util.MarketingUtil;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import javax.transaction.Transactional;

/**
 * @param : CampaignDto
 * @author : user
 * @desc : 마케팅 메시지의 기능중 카페24 기능 구현을 위한 전략 모듈
 * @since : 2022/11/23
 */
@Component
@Transactional
public class Apple24MarketingMessageSub extends MarketingMessageAbstract {

    public Apple24MarketingMessageSub(CampaignRepository campaignRepository, HolidayRepository holidayRepository, MarketingUtil marketingUtil, MarketingMessageRepository marketingMessageRepository, AttachFileRepository attachFileRepository, MarketingMessageResultRepository marketingMessageResultRepository, WebClient batchWebClient, SettlementManagementRepository settlementManagementRepository) {
        super(campaignRepository, holidayRepository, marketingUtil, marketingMessageRepository, attachFileRepository, marketingMessageResultRepository, batchWebClient, settlementManagementRepository);
    }
}
