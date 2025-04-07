package org.metrowheel.user.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.metrowheel.common.model.BaseEntity;

@Entity
@Table(name = "user_addresses")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserAddress extends BaseEntity {

    @NotBlank(message = "Street address is required")
    @Column(name = "street_address")
    private String streetAddress;

    @NotBlank(message = "City is required")
    @Column(name = "city")
    private String city;

    @NotBlank(message = "State is required")
    @Column(name = "state")
    private String state;

    @NotBlank(message = "ZIP code is required")
    @Column(name = "zip_code")
    private String zipCode;

    @NotBlank(message = "Country is required")
    @Column(name = "country")
    private String country;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;
}
