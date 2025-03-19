package com.crm.web.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import com.crm.exception.ErrorResponse;
import com.crm.mapper.lot.LotCreateRequestToLotMapper;
import com.crm.mapper.lot.LotUpdateRequestToLotMapper;
import com.crm.web.api.lot.LotCreateRequest;
import com.crm.exception.ControllerException;
import com.crm.mapper.lot.LotToLotResponseMapper;
import com.crm.web.api.lot.LotUpdateRequest;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.crm.web.api.lot.LotResponse;
import com.crm.service.AuthService;
import com.crm.service.LotService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/lots")
@RequiredArgsConstructor
@Validated
@Tag(name = "Lot Management", description = "APIs for lot management")
@ApiResponses({
        @ApiResponse(responseCode = "401", description = "Access denied", content = @Content)
})
public class LotController {

    private final LotService lotService;
    private final AuthService authService;
    private final LotCreateRequestToLotMapper lotCreateRequestToLotMapper;
    private final LotUpdateRequestToLotMapper lotUpdateRequestToLotMapper;
    private final LotToLotResponseMapper lotToLotResponseMapper;

    @Operation(summary = "Get all lots", description = "Retrieve a list of all lots in the system")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved lots",
                    content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = LotResponse.class)))
            )
    })
    @GetMapping
    public List<LotResponse> getAllLots() {

        return lotService
                .getAllLots()
                .stream()
                .map(lotToLotResponseMapper::map)
                .toList();
    }

    @Operation(summary = "Get lot by ID", description = "Retrieve a specific lot by ID")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved lot",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = LotResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Lot not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @GetMapping("/{id}")
    public LotResponse getLotById(@Parameter(description = "Lot ID", required = true) @PathVariable Integer id) {

        return Optional.of(id)
                .map(lotService::getLotById)
                .map(lotToLotResponseMapper::map)
                .orElseThrow(ControllerException::new);
    }

    @Operation(summary = "Create new lot", description = "Create a new lot by providing name and surname")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Lot successfully created",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = LotResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input data or incorrect request format",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LotResponse> createLot(
            @Parameter(description = "Lot details", required = true) @RequestBody LotCreateRequest lotCreateRequest
    ) {

        return new ResponseEntity<>(Optional.of(lotCreateRequest)
                .map(lotCreateRequestToLotMapper::map)
                .map(lot -> lotService.createLot(lot, authService.getCurrentUser()))
                .map(lotToLotResponseMapper::map)
                .orElseThrow(ControllerException::new), HttpStatus.CREATED);
    }

    @Operation(summary = "Update lot details", description = "Update an existing lot's details.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Lot details successfully updated",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = LotResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Lot not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @PutMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public LotResponse updateLot(
            @Parameter(description = "Lot ID", required = true) @PathVariable Integer id,
            @Parameter(description = "Updated lot details", required = true) @RequestBody LotUpdateRequest lotUpdateRequest
    ) {

        return Optional.of(lotUpdateRequest)
                .map(lotUpdateRequestToLotMapper::map)
                .map(lot -> lotService.updateLotDetails(id, lot, authService.getCurrentUser()))
                .map(lotToLotResponseMapper::map)
                .orElseThrow(ControllerException::new);
    }

    @Operation(summary = "Delete lot", description = "Delete a lot and its associated photo")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lot successfully deleted"),
            @ApiResponse(responseCode = "404", description = "Lot not found")
    })
    @DeleteMapping("/{id}")
    public void deleteLot(@Parameter(description = "Lot ID", required = true) @PathVariable Integer id) {

        lotService.deleteLot(id);
    }

    @Operation(summary = "Update lot photo", description = "Update an existing lot's photo.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lot photo successfully updated"),
            @ApiResponse(responseCode = "404", description = "Lot not found")
    })
    @PostMapping(path = "/{id}/photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void updateLotPhoto(
            @Parameter(description = "Lot ID", required = true) @PathVariable Integer id,
            @RequestParam("file") MultipartFile file
    ) {

        lotService.updateLotPhoto(id, file, authService.getCurrentUser());
    }

    @Operation(summary = "Get Lot photo", description = "Get an existing lot's photo.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lot photo"),
            @ApiResponse(responseCode = "404", description = "Lot not found")
    })
    @GetMapping(path = "/{id}/photo")
    public ResponseEntity<Resource> getLotPhoto(@Parameter(description = "Lot ID", required = true) @PathVariable Integer id) throws IOException {

        Path photoPath = lotService.getLotPhotoPath(id);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(Files.probeContentType(photoPath)))
                .body(new UrlResource(photoPath.toUri()));
    }
}