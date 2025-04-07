package org.metrowheel.auth.model;

import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegistrationRequest {
    
    // Basic Information
    @NotBlank(message = "Full name is required")
    private String fullName;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;
    
    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Phone number should be valid")
    private String phoneNumber;
    
    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;
    
    // Address Information
    @Valid
    private AddressDto address;
    
    // Payment Method
    @Valid
    private PaymentMethodDto paymentMethod;
    
    // Terms and Conditions
    @AssertTrue(message = "You must accept the terms and conditions")
    private boolean termsAccepted;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AddressDto {
        @NotBlank(message = "Street address is required")
        private String streetAddress;
        
        @NotBlank(message = "City is required")
        private String city;
        
        @NotBlank(message = "State is required")
        private String state;
        
        @NotBlank(message = "ZIP code is required")
        private String zipCode;
        
        @NotBlank(message = "Country is required")
        private String country;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PaymentMethodDto {
        @NotBlank(message = "Card number is required")
        @Pattern(regexp = "^[0-9]{16}$", message = "Card number must be 16 digits")
        private String cardNumber;
        
        @NotBlank(message = "Cardholder name is required")
        private String cardholderName;
        
        @NotBlank(message = "Expiry date is required")
        @Pattern(regexp = "^(0[1-9]|1[0-2])/[0-9]{2}$", message = "Expiry date must be in MM/YY format")
        private String expiryDate;
        
        @NotBlank(message = "CVV is required")
        @Pattern(regexp = "^[0-9]{3,4}$", message = "CVV must be 3 or 4 digits")
        private String cvv;
    }
}
