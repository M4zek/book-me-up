package com.m4zek.backend.model.dto.write;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;



@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AddressRequest {

    @NotBlank(message = "City field must not be empty")
    private String city;

    @Pattern(regexp = "\\d{2}-\\d{3}", message = "Postal code must be in Poland pattern (xx-xxx)")
    private String postalCode;

    @NotBlank(message = "Street field must not be empty ")
    private String street;

    @Pattern(regexp = "^\\d+[A-Za-z]?(?:\\/\\d+[A-Za-z]?)?$", message = "Wrong building number field")
    private String buildingNumber;

}

