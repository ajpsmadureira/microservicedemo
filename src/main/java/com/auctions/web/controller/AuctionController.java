package com.auctions.web.controller;

import com.auctions.exception.ControllerException;
import com.auctions.mapper.auction.AuctionCreateRequestToAuctionMapper;
import com.auctions.mapper.auction.AuctionToAuctionResponseMapper;
import com.auctions.mapper.auction.AuctionUpdateRequestToAuctionMapper;
import com.auctions.service.auction.AuctionService;
import com.auctions.service.auth.AuthService;
import com.auctions.web.api.auction.AuctionCreateRequest;
import com.auctions.web.api.auction.AuctionResponse;
import com.auctions.web.api.auction.AuctionUpdateRequest;
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
@RequestMapping("/api/auctions")
@RequiredArgsConstructor
@Validated
@Tag(name = "Auction Management", description = "APIs for auction management")
@ApiResponses({
        @ApiResponse(responseCode = "401", description = "Access denied", content = @Content)
})
public class AuctionController {

    private final AuctionService auctionService;
    private final AuthService authService;
    private final AuctionCreateRequestToAuctionMapper auctionCreateRequestToAuctionMapper;
    private final AuctionUpdateRequestToAuctionMapper auctionUpdateRequestToAuctionMapper;
    private final AuctionToAuctionResponseMapper auctionToAuctionResponseMapper;

    @Operation(summary = "Get all auctions", description = "Retrieve a list of all auctions in the system")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved auctions",
                    content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = AuctionResponse.class)))
            )
    })
    @GetMapping
    public List<AuctionResponse> getAllAuctions() {

        return auctionService
                .getAllAuctions()
                .stream()
                .map(auctionToAuctionResponseMapper::map)
                .toList();
    }

    @Operation(summary = "Get auction by ID", description = "Retrieve a specific auction by ID")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved auction",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = AuctionResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Auction not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @GetMapping("/{id}")
    public AuctionResponse getAuctionById(@Parameter(description = "Auction ID", required = true) @PathVariable Integer id) {

        return Optional.of(id)
                .map(auctionService::getAuctionById)
                .map(auctionToAuctionResponseMapper::map)
                .orElseThrow(ControllerException::new);
    }

    @Operation(summary = "Create new auction", description = "Create a new auction by providing lot ID, and start and stop times")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Auction successfully created",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = AuctionResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input data or incorrect request format",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AuctionResponse> createAuction(
            @Parameter(description = "Auction details", required = true) @RequestBody AuctionCreateRequest auctionCreateRequest
    ) {

        return new ResponseEntity<>(Optional.of(auctionCreateRequest)
                .map(auctionCreateRequestToAuctionMapper::map)
                .map(auction -> auctionService.createAuction(auction, authService.getCurrentUser()))
                .map(auctionToAuctionResponseMapper::map)
                .orElseThrow(ControllerException::new), HttpStatus.CREATED);
    }

    @Operation(summary = "Update auction details", description = "Update an existing auction's details.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Auction details successfully updated",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = AuctionResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Auction not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @PutMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public AuctionResponse updateAuction(
            @Parameter(description = "Auction ID", required = true) @PathVariable Integer id,
            @Parameter(description = "Updated auction details", required = true) @RequestBody AuctionUpdateRequest auctionUpdateRequest
    ) {

        return Optional.of(auctionUpdateRequest)
                .map(auctionUpdateRequestToAuctionMapper::map)
                .map(auction -> auctionService.updateAuctionDetails(id, auction, authService.getCurrentUser()))
                .map(auctionToAuctionResponseMapper::map)
                .orElseThrow(ControllerException::new);
    }

    @Operation(summary = "Delete auction", description = "Delete an auction")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Auction successfully deleted"),
            @ApiResponse(responseCode = "404", description = "Auction not found")
    })
    @DeleteMapping("/{id}")
    public void deleteAuction(@Parameter(description = "Auction ID", required = true) @PathVariable Integer id) {

        auctionService.deleteAuction(id);
    }

    @Operation(summary = "Start auction", description = "Starts an auction")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Auction successfully started"),
            @ApiResponse(responseCode = "404", description = "Auction not found")
    })
    @PostMapping("/{id}/start")
    public void startAuction(@Parameter(description = "Auction ID", required = true) @PathVariable Integer id) {

        auctionService.startAuction(id);
    }

    @Operation(summary = "Cancel auction", description = "Cancels an auction")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Auction successfully cancelled"),
            @ApiResponse(responseCode = "404", description = "Auction not found")
    })
    @PostMapping("/{id}/cancel")
    public void cancelAuction(@Parameter(description = "Auction ID", required = true) @PathVariable Integer id) {

        auctionService.cancelAuction(id);
    }
}