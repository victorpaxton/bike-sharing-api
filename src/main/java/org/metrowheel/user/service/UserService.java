package org.metrowheel.user.service;

import at.favre.lib.crypto.bcrypt.BCrypt;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;
import org.metrowheel.auth.model.RegistrationRequest;
import org.metrowheel.user.model.PaymentMethod;
import org.metrowheel.user.model.User;
import org.metrowheel.user.model.UserAddress;
import org.metrowheel.user.repository.UserRepository;

import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class UserService {

    @Inject
    UserRepository userRepository;

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
                .termsAccepted(request.isTermsAccepted())
                .roles("user")
                .build();

        // Create address if provided
        if (request.getAddress() != null) {
            UserAddress address = UserAddress.builder()
                    .streetAddress(request.getAddress().getStreetAddress())
                    .city(request.getAddress().getCity())
                    .state(request.getAddress().getState())
                    .zipCode(request.getAddress().getZipCode())
                    .country(request.getAddress().getCountry())
                    .build();
            address.setUser(user);
            user.setAddress(address);
        }

        // Create payment method if provided
        if (request.getPaymentMethod() != null) {
            PaymentMethod paymentMethod = PaymentMethod.builder()
                    .cardNumber(request.getPaymentMethod().getCardNumber())
                    .cardholderName(request.getPaymentMethod().getCardholderName())
                    .expiryDate(request.getPaymentMethod().getExpiryDate())
                    .cvv(request.getPaymentMethod().getCvv())
                    .build();
            paymentMethod.setUser(user);
            user.setPaymentMethod(paymentMethod);
        }

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
}
