package org.metrowheel.user.resource;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.metrowheel.exception.ApiError;
import org.metrowheel.security.TokenUtils;
import org.metrowheel.user.model.AuthResponse;
import org.metrowheel.user.model.LoginRequest;
import org.metrowheel.user.model.RegistrationRequest;
import org.metrowheel.user.model.User;
import org.metrowheel.user.service.UserService;

import java.util.Optional;

/**
 * Resource to handle authentication operations (login, registration, token refresh)
 */
@Path("/api/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthResource {

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
                    .entity(new ApiError("AUTH_ERROR", "Invalid credentials", Response.Status.UNAUTHORIZED.getStatusCode()))
                    .build();
        }

        // Get user
        Optional<User> userOpt = userService.findByEmail(request.getEmail());
        if (userOpt.isEmpty()) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(new ApiError("AUTH_ERROR", "Invalid credentials", Response.Status.UNAUTHORIZED.getStatusCode()))
                    .build();
        }

        User user = userOpt.get();

        // Create tokens
        String accessToken = tokenUtils.generateAccessToken(user);
        String refreshToken = tokenUtils.generateRefreshToken(user);

        // Create response
        AuthResponse response = AuthResponse.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .token(accessToken)
                .refreshToken(refreshToken)
                .expiresIn(tokenUtils.getAccessTokenDuration())
                .roles(user.getRoles())
                .build();

        return Response.ok(response).build();
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
            AuthResponse response = AuthResponse.builder()
                    .userId(user.getId())
                    .email(user.getEmail())
                    .fullName(user.getFullName())
                    .token(accessToken)
                    .refreshToken(refreshToken)
                    .expiresIn(tokenUtils.getAccessTokenDuration())
                    .roles(user.getRoles())
                    .build();

            return Response.status(Response.Status.CREATED).entity(response).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ApiError("REGISTRATION_ERROR", e.getMessage(), Response.Status.BAD_REQUEST.getStatusCode()))
                    .build();
        }
    }


}
