package com.auctions.web.controller;

import com.auctions.exception.ControllerException;
import com.auctions.mapper.bid.BidCreateRequestToBidMapper;
import com.auctions.mapper.bid.BidToBidResponseMapper;
import com.auctions.service.auth.AuthService;
import com.auctions.service.bid.BidService;
import com.auctions.web.api.bid.BidCreateRequest;
import com.auctions.web.api.bid.BidResponse;
import com.auctions.web.api.error.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
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

import java.util.List;
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

    @Operation(summary = "Get all bids", description = "Retrieve a list of all bids in the system")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved bids",
                    content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = BidResponse.class)))
            )
    })
    @GetMapping
    public List<BidResponse> getAllBids() {

        return bidService
                .getAllBids()
                .stream()
                .map(bidToBidResponseMapper::map)
                .toList();
    }

    @Operation(summary = "Get bid by ID", description = "Retrieve a specific bid by ID")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved bid",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = BidResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Bid not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @GetMapping("/{id}")
    public BidResponse getBidById(@Parameter(description = "Bid ID", required = true) @PathVariable Integer id) {

        return Optional.of(id)
                .map(bidService::getBidById)
                .map(bidToBidResponseMapper::map)
                .orElseThrow(ControllerException::new);
    }

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
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
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

    @Operation(summary = "Cancel bid", description = "Cancel a bid")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Bid successfully cancelled"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input data or incorrect request format",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(responseCode = "404", description = "Bid not found"),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @PostMapping("/{id}/cancel")
    public void cancelBid(@Parameter(description = "Bid ID", required = true) @PathVariable Integer id) {

        bidService.cancelBid(id);
    }

    @Operation(summary = "Accept bid", description = "Accept a bid")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Bid successfully accepted"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input data or incorrect request format",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(responseCode = "404", description = "Bid not found"),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @PostMapping("/{id}/accept")
    public void acceptBid(@Parameter(description = "Bid ID", required = true) @PathVariable Integer id) {

        bidService.acceptBid(id);
    }
}