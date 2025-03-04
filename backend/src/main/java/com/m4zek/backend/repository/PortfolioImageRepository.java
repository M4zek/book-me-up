package com.m4zek.backend.repository;

import com.m4zek.backend.model.Company;
import com.m4zek.backend.model.PortfolioImage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface PortfolioImageRepository {

    Page<PortfolioImage> findAllByCompany(Company company, Pageable pageable);

    Optional<PortfolioImage> findById(int id);

    PortfolioImage save(PortfolioImage portfolioImage);

    void delete(PortfolioImage portfolioImage);

}
