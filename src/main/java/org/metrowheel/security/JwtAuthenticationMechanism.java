package org.metrowheel.security;

import io.quarkus.security.identity.IdentityProviderManager;
import io.quarkus.security.identity.SecurityIdentity;
import io.quarkus.security.identity.request.AuthenticationRequest;
import io.quarkus.security.identity.request.TokenAuthenticationRequest;
import io.quarkus.vertx.http.runtime.security.ChallengeData;
import io.quarkus.vertx.http.runtime.security.HttpAuthenticationMechanism;
import io.quarkus.vertx.http.runtime.security.HttpCredentialTransport;
import io.smallrye.mutiny.Uni;
import io.vertx.core.http.HttpHeaders;
import io.vertx.ext.web.RoutingContext;
import jakarta.annotation.Priority;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.Collections;
import java.util.Set;

/**
 * Custom JWT authentication mechanism that works with our token implementation.
 */
@Priority(1)
@ApplicationScoped
public class JwtAuthenticationMechanism implements HttpAuthenticationMechanism {

    private static final String BEARER = "Bearer ";
    
    @Inject
    TokenUtils tokenUtils;

    @Override
    public Uni<SecurityIdentity> authenticate(RoutingContext context, IdentityProviderManager identityProviderManager) {
        String authorization = context.request().getHeader(HttpHeaders.AUTHORIZATION.toString());
        
        if (authorization == null || !authorization.startsWith(BEARER)) {
            // No token provided, continue with unauthenticated user
            return Uni.createFrom().nullItem();
        }
        
        // Extract token from Authorization header
        String token = authorization.substring(BEARER.length());
        
        // Create a token authentication request
        TokenAuthenticationRequest request = new TokenAuthenticationRequest(new JwtTokenCredential(token, "Bearer"));
        
        // Let the identity provider handle the authentication
        return identityProviderManager.authenticate(request);
    }

    @Override
    public Uni<ChallengeData> getChallenge(RoutingContext context) {
        ChallengeData challenge = new ChallengeData(401, "WWW-Authenticate", "Bearer");
        return Uni.createFrom().item(challenge);
    }

    @Override
    public Set<Class<? extends AuthenticationRequest>> getCredentialTypes() {
        return Collections.singleton(TokenAuthenticationRequest.class);
    }

    @Override
    public HttpCredentialTransport getCredentialTransport() {
        return new HttpCredentialTransport(HttpCredentialTransport.Type.AUTHORIZATION, "Bearer");
    }
}
