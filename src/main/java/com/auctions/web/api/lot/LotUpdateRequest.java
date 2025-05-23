package com.auctions.web.api.lot;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LotUpdateRequest {

    @Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters")
    private String name;

    @Size(min = 2, max = 50, message = "Surname must be between 2 and 50 characters")
    private String surname;
} 