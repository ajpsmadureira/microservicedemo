package com.crm.web.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import com.crm.exception.ErrorResponse;
import com.crm.mapper.customer.CustomerCreateRequestToCustomerMapper;
import com.crm.mapper.customer.CustomerUpdateRequestToCustomerMapper;
import com.crm.web.api.customer.CustomerCreateRequest;
import com.crm.exception.ControllerException;
import com.crm.mapper.customer.CustomerToCustomerResponseMapper;
import com.crm.web.api.customer.CustomerUpdateRequest;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.crm.web.api.customer.CustomerResponse;
import com.crm.service.AuthService;
import com.crm.service.CustomerService;

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
@RequestMapping("/api/customers")
@RequiredArgsConstructor
@Validated
@Tag(name = "Customer Management", description = "APIs for customer management")
@ApiResponses({
        @ApiResponse(responseCode = "401", description = "Access denied", content = @Content)
})
public class CustomerController {

    private final CustomerService customerService;
    private final AuthService authService;
    private final CustomerCreateRequestToCustomerMapper customerCreateRequestToCustomerMapper;
    private final CustomerUpdateRequestToCustomerMapper customerUpdateRequestToCustomerMapper;
    private final CustomerToCustomerResponseMapper customerToCustomerResponseMapper;

    @Operation(summary = "Get all customers", description = "Retrieve a list of all customers in the system")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved customers",
                    content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = CustomerResponse.class)))
            )
    })
    @GetMapping
    public List<CustomerResponse> getAllCustomers() {

        return customerService
                .getAllCustomers()
                .stream()
                .map(customerToCustomerResponseMapper::map)
                .toList();
    }

    @Operation(summary = "Get customer by ID", description = "Retrieve a specific customer by their ID")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved customer",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CustomerResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Customer not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @GetMapping("/{id}")
    public CustomerResponse getCustomerById(@Parameter(description = "Customer ID", required = true) @PathVariable Integer id) {

        return Optional.of(id)
                .map(customerService::getCustomerById)
                .map(customerToCustomerResponseMapper::map)
                .orElseThrow(ControllerException::new);
    }

    @Operation(summary = "Create new customer", description = "Create a new customer by providing name and surname")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Customer successfully created",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CustomerResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input data or incorrect request format",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CustomerResponse> createCustomer(
            @Parameter(description = "Customer details", required = true) @RequestBody CustomerCreateRequest customerCreateRequest
    ) {

        return new ResponseEntity<>(Optional.of(customerCreateRequest)
                .map(customerCreateRequestToCustomerMapper::map)
                .map(customer -> customerService.createCustomer(customer, authService.getCurrentUser()))
                .map(customerToCustomerResponseMapper::map)
                .orElseThrow(ControllerException::new), HttpStatus.CREATED);
    }

    @Operation(summary = "Update customer details", description = "Update an existing customer's details.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Customer details successfully updated",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CustomerResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Customer not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @PutMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public CustomerResponse updateCustomer(
            @Parameter(description = "Customer ID", required = true) @PathVariable Integer id,
            @Parameter(description = "Updated customer details", required = true) @RequestBody CustomerUpdateRequest customerUpdateRequest
    ) {

        return Optional.of(customerUpdateRequest)
                .map(customerUpdateRequestToCustomerMapper::map)
                .map(customer -> customerService.updateCustomerDetails(id, customer, authService.getCurrentUser()))
                .map(customerToCustomerResponseMapper::map)
                .orElseThrow(ControllerException::new);
    }

    @Operation(summary = "Delete customer", description = "Delete a customer and their associated photo")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Customer successfully deleted"),
            @ApiResponse(responseCode = "404", description = "Customer not found")
    })
    @DeleteMapping("/{id}")
    public void deleteCustomer(@Parameter(description = "Customer ID", required = true) @PathVariable Integer id) {

        customerService.deleteCustomer(id);
    }

    @Operation(summary = "Update customer photo", description = "Update an existing customer's photo.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Customer photo successfully updated"),
            @ApiResponse(responseCode = "404", description = "Customer not found")
    })
    @PostMapping(path = "/{id}/photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void updateCustomerPhoto(
            @Parameter(description = "Customer ID", required = true) @PathVariable Integer id,
            @RequestParam("file") MultipartFile file
    ) {

        customerService.updateCustomerPhoto(id, file, authService.getCurrentUser());
    }

    @Operation(summary = "Get customer photo", description = "Get an existing customer's photo.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Customer photo"),
            @ApiResponse(responseCode = "404", description = "Customer not found")
    })
    @GetMapping(path = "/{id}/photo")
    public ResponseEntity<Resource> getCustomerPhoto(@Parameter(description = "Customer ID", required = true) @PathVariable Integer id) throws IOException {

        Path photoPath = customerService.getCustomerPhotoPath(id);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(Files.probeContentType(photoPath)))
                .body(new UrlResource(photoPath.toUri()));
    }
}