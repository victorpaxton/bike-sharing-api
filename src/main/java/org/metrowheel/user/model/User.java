package org.metrowheel.user.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.metrowheel.common.model.BaseEntity;

@Entity
@Table(name = "users")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User extends BaseEntity {

    @NotBlank(message = "Full name is required")
    @Column(name = "full_name")
    private String fullName;

    @Email(message = "Email should be valid")
    @NotBlank(message = "Email is required")
    @Column(unique = true, nullable = false)
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    @Column(nullable = false)
    private String password;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Phone number should be valid")
    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "roles", length = 100)
    private String roles;

    @Enumerated(EnumType.STRING)
    @Column(name = "subscription_plan", nullable = false)
    @Builder.Default
    private SubscriptionPlan subscriptionPlan = SubscriptionPlan.STANDARD;

    @Column(name = "email_verified")
    @Builder.Default
    private boolean emailVerified = false;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private UserAddress address;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private PaymentMethod paymentMethod;

    @Column(name = "terms_accepted")
    @Builder.Default
    private boolean termsAccepted = false;
}
