package com.auctions.task.bid;

import com.auctions.persistence.repository.BidRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class BidTaskTest {

    @Mock
    private BidRepository bidRepository;

    @InjectMocks
    private BidTask bidTask;

    @Test
    void test() {

        bidTask.updateStateToOutdated();

        verify(bidRepository).updateBidsStateToOutdated();
    }
}
