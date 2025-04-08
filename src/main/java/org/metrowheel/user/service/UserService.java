package org.metrowheel.user.service;

import at.favre.lib.crypto.bcrypt.BCrypt;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.SecurityContext;
import org.metrowheel.auth.model.RegistrationRequest;
import org.metrowheel.security.TokenUtils;
import org.metrowheel.user.model.PaymentMethod;
import org.metrowheel.user.model.User;
import org.metrowheel.user.model.UserAddress;
import org.metrowheel.user.repository.UserRepository;
import io.quarkus.security.identity.SecurityIdentity;

import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class UserService {

    @Inject
    UserRepository userRepository;
    
    @Inject
    TokenUtils tokenUtils;
    
    @Inject
    SecurityIdentity securityIdentity;

    /**
     * Find a user by their ID
     */
    public User findById(UUID id) {
        return userRepository.findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + id));
    }

    /**
     * Find a user by their email
     */
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    
    /**
     * Check if a user exists by their ID
     * 
     * @param id The user ID to check
     * @return True if the user exists, false otherwise
     */
    public boolean userExists(UUID id) {
        return userRepository.findByIdOptional(id).isPresent();
    }

    /**
     * Register a new user
     */
    @Transactional
    public User register(RegistrationRequest request) {

        // Check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already in use");
        }

        // Create user
        User user = User.builder()
                .email(request.getEmail())
                .password(hashPassword(request.getPassword()))
                .fullName(request.getFullName())
                .phoneNumber(request.getPhoneNumber())
                .roles("user")
                .emailVerified(false) // Default to not verified
                .termsAccepted(true) // Implicitly accepted if they register
                .build();

        // Save user
        userRepository.persist(user);
        return user;
    }

    /**
     * Verify if a password matches the stored hash for a user
     */
    public boolean verifyPassword(String email, String password) {
        Optional<User> userOpt = findByEmail(email);
        
        if (userOpt.isEmpty()) {
            return false;
        }
        
        User user = userOpt.get();
        return BCrypt.verifyer().verify(
                password.getBytes(StandardCharsets.UTF_8),
                user.getPassword().getBytes(StandardCharsets.UTF_8)
        ).verified;
    }
    
    /**
     * Hash a password using BCrypt
     */
    private String hashPassword(String password) {
        return BCrypt.withDefaults().hashToString(12, password.toCharArray());
    }

    /**
     * Get the currently authenticated user
     * 
     * @return The authenticated user
     * @throws NotFoundException if the user is not found
     */
    public User getCurrentUser() {
        try {
            if (securityIdentity == null) {
                throw new BadRequestException("No authenticated user found");
            }

            // Get the user ID directly from the security identity attributes
            String userId = securityIdentity.getAttribute("userId");
            if (userId == null) {
                throw new BadRequestException("Invalid security context: missing user ID");
            }
            
            return findById(UUID.fromString(userId));
        } catch (Exception e) {
            throw new BadRequestException("Failed to get current user: " + e.getMessage());
        }
    }
}
