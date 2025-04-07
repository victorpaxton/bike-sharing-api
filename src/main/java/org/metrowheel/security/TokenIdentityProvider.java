package org.metrowheel.security;

import io.quarkus.security.identity.AuthenticationRequestContext;
import io.quarkus.security.identity.IdentityProvider;
import io.quarkus.security.identity.SecurityIdentity;
import io.quarkus.security.identity.request.TokenAuthenticationRequest;
import io.quarkus.security.runtime.QuarkusSecurityIdentity;
import io.smallrye.mutiny.Uni;
import org.jboss.logging.Logger;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.HashSet;
import java.util.Set;

/**
 * Identity provider that validates JWT tokens extracted by JwtAuthenticationMechanism.
 */
@ApplicationScoped
public class TokenIdentityProvider implements IdentityProvider<TokenAuthenticationRequest> {

    private static final Logger LOG = Logger.getLogger(TokenIdentityProvider.class);

    @Inject
    TokenUtils tokenUtils;

    @Override
    public Class<TokenAuthenticationRequest> getRequestType() {
        return TokenAuthenticationRequest.class;
    }

    @Override
    public Uni<SecurityIdentity> authenticate(TokenAuthenticationRequest request, AuthenticationRequestContext context) {
        String token = request.getToken().getToken();

        // Validate the token and create a security identity
        try {
            io.jsonwebtoken.Claims claims = tokenUtils.parseToken(token);
            String email = claims.getSubject();
            
            // Extract roles from the token
            String rolesString = claims.get("roles", String.class);
            Set<String> roles = new HashSet<>();
            if (rolesString != null && !rolesString.isEmpty()) {
                for (String role : rolesString.split(",")) {
                    roles.add(role.trim());
                }
            }
            
            // Build a security identity with the roles
            QuarkusSecurityIdentity identity = QuarkusSecurityIdentity.builder()
                    .setPrincipal(() -> email)
                    .addRoles(roles)
                    .addAttribute("token", token)
                    .addAttribute("userId", claims.get("userId", String.class))
                    .addAttribute("email", claims.get("email", String.class))
                    .addAttribute("fullName", claims.get("fullName", String.class))
                    .build();
                    
            return Uni.createFrom().item(identity);
        } catch (Exception e) {
            LOG.error("Failed to authenticate JWT token", e);
            // Invalid token
            return Uni.createFrom().nullItem();
        }
    }
}
