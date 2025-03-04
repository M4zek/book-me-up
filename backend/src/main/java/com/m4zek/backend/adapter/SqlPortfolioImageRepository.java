package com.m4zek.backend.adapter;

import com.m4zek.backend.model.PortfolioImage;
import com.m4zek.backend.repository.PortfolioImageRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SqlPortfolioImageRepository extends PortfolioImageRepository, JpaRepository<PortfolioImage, Integer> {
}
