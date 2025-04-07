package org.metrowheel.security;

import io.smallrye.jwt.build.Jwt;
import io.smallrye.jwt.build.JwtClaimsBuilder;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;

/**
 * Utility class to generate RSA keys for JWT signing and verification.
 * Run this class as a standalone application to generate private and public key files.
 */
public class GenerateJwtKeys {

    public static void main(String[] args) throws NoSuchAlgorithmException, IOException {
        // Generate RSA key pair
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
        kpg.initialize(2048);
        KeyPair pair = kpg.generateKeyPair();
        PrivateKey privateKey = pair.getPrivate();
        PublicKey publicKey = pair.getPublic();

        // Save private key to file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("src/main/resources/privateKey.pem"))) {
            writer.write("-----BEGIN PRIVATE KEY-----\n");
            writer.write(Base64.getEncoder().encodeToString(privateKey.getEncoded()));
            writer.write("\n-----END PRIVATE KEY-----\n");
        }

        // Save public key to file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("src/main/resources/publicKey.pem"))) {
            writer.write("-----BEGIN PUBLIC KEY-----\n");
            writer.write(Base64.getEncoder().encodeToString(publicKey.getEncoded()));
            writer.write("\n-----END PUBLIC KEY-----\n");
        }

        System.out.println("Public and private keys have been generated in the src/main/resources directory.");
        
        // Test the keys by creating a JWT
        String token = createTestJwt(privateKey);
        System.out.println("Test JWT token: " + token);
    }
    
    /**
     * Create a test JWT token using the generated private key
     */
    private static String createTestJwt(PrivateKey privateKey) {
        JwtClaimsBuilder claims = Jwt.claims();
        claims.subject("test-subject");
        claims.issuer("https://metrowheel.org");
        claims.groups("user");
        claims.expiresIn(86400);
        
        return claims.jws().keyId("test-key").sign(privateKey);
    }
}
