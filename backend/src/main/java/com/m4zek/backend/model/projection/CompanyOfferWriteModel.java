package com.m4zek.backend.model.projection;

import com.m4zek.backend.model.Company;
import com.m4zek.backend.model.CompanyOffer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CompanyOfferWriteModel {

    private String name;
    private String description;
    private double price;
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



