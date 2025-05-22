package com.auctions.task.bid;

import com.auctions.service.bid.BidService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class BidTask {

    private final BidService bidService;

    @Scheduled(cron = "${task.bid-updater.cron}")
    @Transactional
    public void updateStateToOutdated() {

        log.info("Updated {} bids to outdated", bidService.updateBidsStateToOutdated());
    }
}