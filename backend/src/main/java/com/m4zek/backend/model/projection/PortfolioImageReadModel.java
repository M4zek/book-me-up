package com.m4zek.backend.model.projection;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PortfolioImageReadModel {

    private int id;
    private String filename;
    private String downloadUrl;
    private byte[] image;
}
