package com.auctions.functional;

import com.auctions.web.api.lot.LotCreateRequest;
import org.junit.jupiter.api.Test;

public class UserJourneyIT extends UserJourneyAbstractIT {

    @Test
    void userJourneyTest() {

        // 1. Login

        String jwt = login();

        // 2. Create lot

        LotCreateRequest lotCreateRequest = new LotCreateRequest();
        lotCreateRequest.setName("");
        lotCreateRequest.setSurname("");

        Integer lotId = createLot(jwt, lotCreateRequest);

        // 3. Delete lot

        deleteLot(jwt, lotId);
    }
}