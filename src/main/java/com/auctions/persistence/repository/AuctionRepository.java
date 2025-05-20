package com.auctions.persistence.repository;

import com.auctions.domain.bid.BidState;
import com.auctions.persistence.entity.AuctionEntity;
import com.auctions.persistence.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuctionRepository extends JpaRepository<AuctionEntity, Integer> {

    List<AuctionEntity> findByCreatedBy(UserEntity userEntity);
    List<AuctionEntity> findByLastModifiedBy(UserEntity userEntity);

    @Modifying
    @Query(value = "UPDATE bids SET state = :#{#state.name()} WHERE state = 'CREATED' AND bids.auction = :id", nativeQuery = true)
    void updateAuctionCreatedBidsState(@Param("state") BidState bidState, @Param("id") Integer auctionId);

    // TODO
    /*@Modifying®
    @Query(value = """
            DEFINE now = now();
            UPDATE bids SET state = 'REJECTED'
                WHERE state = 'CREATED' AND auction IN
                    (SELECT id FROM auctions WHERE state = 'ONGOING' AND stop_time < &now);
            UPDATE auctions SET state = 'CLOSED'
                WHERE state = 'ONGOING' AND stop_time < &now;
            """, nativeQuery = true)
    int updateAuctionsStateToClosed();*/
}
