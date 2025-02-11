package com.crm.web.controller;

import java.util.List;
import java.util.Optional;

import com.crm.exception.ErrorResponse;
import com.crm.web.api.user.UserCreateRequest;
import com.crm.web.api.user.UserResponse;
import com.crm.web.api.user.UserUpdateRequest;
import com.crm.exception.ControllerException;
import com.crm.mapper.user.UserCreateRequestToUserMapper;
import com.crm.mapper.user.UserToUserResponseMapper;
import com.crm.mapper.user.UserUpdateRequestToUserMapper;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.crm.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
@Tag(name = "User Management", description = "Admin APIs for user management")
@ApiResponses({
        @ApiResponse(responseCode = "401", description = "Access denied", content = @Content)
})
public class UserController {

    private final UserService userService;
    private final UserToUserResponseMapper userToUserResponseMapper;
    private final UserCreateRequestToUserMapper userCreateRequestToUserMapper;
    private final UserUpdateRequestToUserMapper userUpdateRequestToUserMapper;

    @Operation(summary = "Get all users", description = "Retrieve a list of all users in the system")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved users",
                    content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = UserResponse.class)))
            )
    })
    @GetMapping
    public List<UserResponse> getAllUsers() {

        return userService.getAllUsers().stream().map(userToUserResponseMapper::map).toList();
    }

    @Operation(summary = "Get user by ID", description = "Retrieve a specific user by their ID")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved user",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @GetMapping("/{id}")
    public UserResponse getUserById(@Parameter(description = "User ID", required = true) @PathVariable Integer id) {

        return Optional.of(id)
                .map(userService::getUserById)
                .map(userToUserResponseMapper::map)
                .orElseThrow(ControllerException::new);
    }

    @Operation(summary = "Create new user", description = "Create a new user in the system")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "User successfully created",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input data",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @PostMapping
    public ResponseEntity<UserResponse> createUser(@Parameter(description = "User details", required = true) @Valid @RequestBody UserCreateRequest userCreateRequest) {

        return new ResponseEntity<>(Optional.of(userCreateRequest)
                .map(userCreateRequestToUserMapper::map)
                .map(userService::createUser)
                .map(userToUserResponseMapper::map)
                .orElseThrow(ControllerException::new), HttpStatus.CREATED);
    }

    @Operation(summary = "Update user", description = "Update an existing user's information")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "User successfully updated",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input data",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @PutMapping("/{id}")
    public UserResponse updateUser(
            @Parameter(description = "User ID", required = true) @PathVariable Integer id,
            @Parameter(description = "Updated user details", required = true) @Valid @RequestBody UserUpdateRequest userUpdateRequest
    ) {

        return Optional.of(userUpdateRequest)
                .map(userUpdateRequestToUserMapper::map)
                .map(user -> userService.updateUser(id, user))
                .map(userToUserResponseMapper::map)
                .orElseThrow(ControllerException::new);
    }

    @Operation(summary = "Delete user", description = "Delete a user from the system")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User successfully deleted"),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @DeleteMapping("/{id}")
    public void deleteUser(@Parameter(description = "User ID", required = true) @PathVariable Integer id) {

        userService.deleteUser(id);
    }

    @Operation(summary = "Toggle admin status", description = "Toggle the admin status of a user")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Admin status successfully toggled",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @PutMapping("/{id}/toggle-admin")
    public UserResponse toggleAdminStatus(@Parameter(description = "User ID", required = true) @PathVariable Integer id) {

        return Optional.of(id)
                .map(userService::toggleAdminStatus)
                .map(userToUserResponseMapper::map)
                .orElseThrow(ControllerException::new);
    }
}