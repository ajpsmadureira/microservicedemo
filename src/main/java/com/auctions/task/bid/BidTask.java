package com.auctions.task.bid;

import com.auctions.persistence.repository.BidRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class BidTask {

    private final BidRepository bidRepository;

    @Scheduled(cron = "${task.bid-updater.cron}")
    @Transactional
    public void updateStateToOutdated() {

        log.info("Updated {} bids to outdated", bidRepository.updateBidsStateToOutdated());
    }
}