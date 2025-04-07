package org.metrowheel.security;

import io.quarkus.security.credential.TokenCredential;

/**
 * Simple extension of the TokenCredential class to provide JWT token credentials.
 * This is needed for the authentication mechanism.
 */
public class JwtTokenCredential extends TokenCredential {
    
    public JwtTokenCredential(String token, String tokenType) {
        super(token, tokenType);
    }
}
