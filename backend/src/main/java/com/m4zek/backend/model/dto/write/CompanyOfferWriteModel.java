package com.m4zek.backend.model.dto.write;

import com.m4zek.backend.model.Company;
import com.m4zek.backend.model.CompanyOffer;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CompanyOfferWriteModel {

    @NotBlank(message = "Name is required")
    @Size(max = 255, message = "Name must not exceed 255 characters")
    private String name;

    @NotBlank(message = "Description is required")
    @Size(max = 2048, message = "Description must not exceed 2048 characters")
    private String description;

    @Positive(message = "Price must be greater than 0")
    @Digits(integer = 10, fraction = 2, message = "Price must be a valid amount with up to 2 decimal places")
    private double price;

    @Positive(message = "Duration must be greater than 0")
    @Max(value = 1440, message = "Duration cannot exceed 8 hours")
    private int duration;


    public CompanyOffer toEntity(Company company){
        return new CompanyOffer(
                this.name,
                this.description,
                this.price,
                this.duration,
                company);
    }
}



