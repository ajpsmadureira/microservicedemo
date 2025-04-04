package com.auctions.service.auction.component;

import com.auctions.exception.BusinessException;
import com.auctions.exception.ResourceNotFoundException;
import com.auctions.mapper.auction.AuctionEntityToAuctionMapper;
import com.auctions.persistence.repository.AuctionRepository;
import com.auctions.persistence.repository.LotRepository;
import com.auctions.persistence.repository.UserRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DeleteAuctionServiceComponent extends AuctionServiceComponent {

    public DeleteAuctionServiceComponent(UserRepository userRepository, AuctionRepository auctionRepository, LotRepository lotRepository, AuctionEntityToAuctionMapper auctionEntityToAuctionMapper) {
        super(userRepository, auctionRepository, lotRepository, auctionEntityToAuctionMapper);
    }

    @Transactional
    public void deleteAuction(Integer id) {

        try {

            findAuctionByIdOrThrowException(id);

        } catch(ResourceNotFoundException e) {

            return;
        }

        try {

            auctionRepository.deleteById(id);

        } catch (Exception e) {

            throw new BusinessException("Failed to delete auction: " + e.getMessage());
        }
    }
}
