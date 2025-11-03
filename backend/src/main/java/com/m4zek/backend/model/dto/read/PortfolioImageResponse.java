package com.m4zek.backend.model.dto.read;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PortfolioImageResponse {

    private int id;
    private String filename;
    private String downloadUrl;
    private byte[] image;
}
