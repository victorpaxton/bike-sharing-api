package org.metrowheel.user.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponse {
    private UUID userId;
    private String email;
    private String token;
    private String refreshToken;
    private long expiresIn;
    private String fullName;
    private String roles;
}
