package com.m4zek.backend.model;

import com.m4zek.backend.model.dto.read.PortfolioImageResponse;
import jakarta.persistence.*;

@Entity(name = "PortfolioImages")
public class PortfolioImage extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Lob
    private byte[] image;

    private String filename;

    @ManyToOne
    @JoinColumn(name = "company_id")
    private Company company;

    public PortfolioImage() {
    }

    public PortfolioImage(byte[] image, String filename, Company company) {
        this.image = image;
        this.company = company;
        this.filename = filename;
    }

    public PortfolioImageResponse toReadModel(){
        return PortfolioImageResponse.builder()
                .id(this.id)
                .filename(this.filename)
                .image(this.image)
                .downloadUrl("/api/v1/portfolio-images/" + this.id + "/download")
                .build();
    }
}
