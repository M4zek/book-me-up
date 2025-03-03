package com.m4zek.backend.model.projection;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CompanyWriteModel {

    private String name;
    private String description;
    private String logo;
    private AddressWriteModel address;
    private CategoryWriteModel category;

}
