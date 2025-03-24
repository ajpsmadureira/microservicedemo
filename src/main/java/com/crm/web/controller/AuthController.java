package com.crm.web.controller;

import com.crm.service.auth.AuthService;
import com.crm.web.api.auth.LoginRequest;
import com.crm.web.api.auth.LoginResponse;
import com.crm.web.api.error.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Authentication management APIs")
@ApiResponses({
        @ApiResponse(
                responseCode = "400",
                description = "Bad request",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
                responseCode = "401",
                description = "Invalid credentials",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
        )
})
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "Login user", description = "Authenticate a user and return a JWT token")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Successfully authenticated",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = LoginResponse.class))
            )
    })
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Parameter(description = "Login credentials", required = true) @RequestBody LoginRequest loginRequest) {

        String token = authService.login(loginRequest.getUsername(), loginRequest.getPassword());

        return new ResponseEntity<>(LoginResponse.create(token), HttpStatus.CREATED);
    }
}