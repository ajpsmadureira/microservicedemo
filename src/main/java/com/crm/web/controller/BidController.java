package com.crm.web.controller;

import com.crm.exception.ControllerException;
import com.crm.exception.ErrorResponse;
import com.crm.mapper.lot.*;
import com.crm.service.AuthService;
import com.crm.service.BidService;
import com.crm.web.api.bid.BidCreateRequest;
import com.crm.web.api.bid.BidResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;

@RestController
@RequestMapping("/api/bids")
@RequiredArgsConstructor
@Validated
@Tag(name = "Bid Management", description = "APIs for bid management")
@ApiResponses({
        @ApiResponse(responseCode = "401", description = "Access denied", content = @Content)
})
public class BidController {

    private final BidService bidService;
    private final AuthService authService;
    private final BidCreateRequestToBidMapper bidCreateRequestToBidMapper;
    private final BidToBidResponseMapper bidToBidResponseMapper;

    @Operation(summary = "Create new bid", description = "Create a new bid")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Bid successfully created",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = BidResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input data or incorrect request format",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BidResponse> createBid(
            @Parameter(description = "Bid details", required = true) @RequestBody BidCreateRequest bidCreateRequest
    ) {

        return new ResponseEntity<>(Optional.of(bidCreateRequest)
                .map(bidCreateRequestToBidMapper::map)
                .map(bid -> bidService.createBid(bid, authService.getCurrentUser()))
                .map(bidToBidResponseMapper::map)
                .orElseThrow(ControllerException::new), HttpStatus.CREATED);
    }
}