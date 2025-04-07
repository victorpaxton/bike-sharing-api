package org.metrowheel.auth.controller;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.metrowheel.auth.model.AuthResponse;
import org.metrowheel.auth.model.LoginRequest;
import org.metrowheel.auth.model.RegistrationRequest;
import org.metrowheel.common.model.ApiResponse;
import org.metrowheel.security.TokenUtils;
import org.metrowheel.user.model.User;
import org.metrowheel.user.service.UserService;

import java.util.Optional;

/**
 * Controller to handle authentication operations (login, registration, token refresh)
 */
@Path("/api/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthController {

    @Inject
    UserService userService;

    @Inject
    TokenUtils tokenUtils;

    /**
     * Login endpoint
     */
    @POST
    @Path("/login")
    public Response login(@Valid LoginRequest request) {
        // Verify credentials
        if (!userService.verifyPassword(request.getEmail(), request.getPassword())) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(ApiResponse.error("Invalid credentials"))
                    .build();
        }

        // Get user
        Optional<User> userOpt = userService.findByEmail(request.getEmail());
        if (userOpt.isEmpty()) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(ApiResponse.error("Invalid credentials"))
                    .build();
        }

        User user = userOpt.get();

        // Create tokens
        String accessToken = tokenUtils.generateAccessToken(user);
        String refreshToken = tokenUtils.generateRefreshToken(user);

        // Create response
        AuthResponse authResponse = AuthResponse.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .token(accessToken)
                .refreshToken(refreshToken)
                .expiresIn(tokenUtils.getAccessTokenDuration())
                .roles(user.getRoles())
                .build();

        return Response.ok(ApiResponse.success("Login successful", authResponse)).build();
    }

    /**
     * Registration endpoint
     */
    @POST
    @Path("/register")
    public Response register(@Valid RegistrationRequest request) {
        try {
            // Register user
            User user = userService.register(request);

            // Create tokens
            String accessToken = tokenUtils.generateAccessToken(user);
            String refreshToken = tokenUtils.generateRefreshToken(user);

            // Create response
            AuthResponse authResponse = AuthResponse.builder()
                    .userId(user.getId())
                    .email(user.getEmail())
                    .fullName(user.getFullName())
                    .token(accessToken)
                    .refreshToken(refreshToken)
                    .expiresIn(tokenUtils.getAccessTokenDuration())
                    .roles(user.getRoles())
                    .build();

            return Response.status(Response.Status.CREATED)
                    .entity(ApiResponse.success("Registration successful", authResponse))
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(ApiResponse.error(e.getMessage()))
                    .build();
        }
    }
}
