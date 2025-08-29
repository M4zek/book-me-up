package com.m4zek.backend.adapter;

import com.m4zek.backend.model.CompanyOffer;
import com.m4zek.backend.repository.CompanyOfferRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SqlCompanyOfferRepository extends CompanyOfferRepository, JpaRepository<CompanyOffer, Long> {
}
