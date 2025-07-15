package com.m4zek.backend.service;


import com.m4zek.backend.exception.CompanyNotFoundException;
import com.m4zek.backend.model.CompanyHours;
import com.m4zek.backend.model.projection.CompanyHoursReadModel;
import com.m4zek.backend.model.projection.CompanyHoursWriteModel;
import com.m4zek.backend.repository.CompanyHoursRepository;
import com.m4zek.backend.repository.CompanyRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CompanyHoursService {

    private final CompanyHoursRepository companyHoursRepository;
    private final CompanyRepository companyRepository;

    public CompanyHoursService(CompanyHoursRepository companyHoursRepository, CompanyRepository companyRepository) {
        this.companyHoursRepository = companyHoursRepository;
        this.companyRepository = companyRepository;
    }


    public List<CompanyHoursReadModel> setCompanyHours(Long companyId, List<CompanyHoursWriteModel> companyWorkingHours) {
        List<CompanyHours> companyHours = companyWorkingHours.stream()
                .map(writeModel ->
                        writeModel.toEntity(
                                companyRepository.findById(companyId).orElseThrow(
                                        () -> new CompanyNotFoundException(String.valueOf(companyId)))
                        )
                )
                .toList();

        companyHours.forEach(this.companyHoursRepository::save);

        return companyHours.stream().map(CompanyHours::toReadModel).toList();
    }


    public List<CompanyHoursReadModel> readCompanyHours(Long companyId) {
        List<CompanyHours> companyHours = this.companyHoursRepository.readAllByCompanyId(companyId);
        if (companyHours.isEmpty()) {
            throw new CompanyNotFoundException("The company's working hours could not be found");
        } else {
            return companyHours.stream()
                    .map(CompanyHours::toReadModel)
                    .toList();
        }
    }

}
