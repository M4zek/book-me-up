package com.m4zek.backend.service;


import com.m4zek.backend.exception.CompanyNotFoundException;
import com.m4zek.backend.mapper.CompanyHoursMapper;
import com.m4zek.backend.model.Company;
import com.m4zek.backend.model.CompanyHours;
import com.m4zek.backend.model.dto.read.CompanyHoursResponse;
import com.m4zek.backend.model.dto.write.CompanyHoursRequest;
import com.m4zek.backend.repository.CompanyHoursRepository;
import com.m4zek.backend.repository.CompanyRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class CompanyHoursService {

    private final CompanyHoursRepository companyHoursRepository;
    private final CompanyRepository companyRepository;

    public CompanyHoursService(CompanyHoursRepository companyHoursRepository, CompanyRepository companyRepository) {
        this.companyHoursRepository = companyHoursRepository;
        this.companyRepository = companyRepository;
    }


    public List<CompanyHoursResponse> setCompanyHours(Long companyId, List<CompanyHoursRequest> companyWorkingHours) {
        List<CompanyHours> companyHours = companyWorkingHours.stream()
                .map(writeModel ->
                        writeModel.toEntity(
                                companyRepository.findById(companyId).orElseThrow(
                                        () -> new CompanyNotFoundException(String.valueOf(companyId)))
                        )
                )
                .toList();

        companyHours.forEach(this.companyHoursRepository::save);

        return companyHours.stream().map(CompanyHoursMapper::companyHoursToCompanyHoursResponse).toList();
    }


    public List<CompanyHoursResponse> readCompanyHours(Long companyId) {
        List<CompanyHours> companyHours = this.companyHoursRepository.readAllByCompanyId(companyId);
        if (companyHours.isEmpty()) {
            throw new CompanyNotFoundException("The company's working hours could not be found");
        } else {
            return companyHours.stream()
                    .map(CompanyHoursMapper::companyHoursToCompanyHoursResponse)
                    .toList();
        }
    }

    /*
    * Method to update company opening hours
     */
    public List<CompanyHoursResponse> updateCompanyOpeningHours(Long companyId, List<CompanyHoursRequest> companyHours) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new CompanyNotFoundException("Company with given id not found"));


        if(companyHours.isEmpty()) {
            throw new IllegalArgumentException("New company's working hour list could not be empty");
        }

        List<CompanyHours> currentCompanyHours = company.getCompanyHoursList();

        Map<String, CompanyHours> currentMap =
                currentCompanyHours.stream().collect(
                        Collectors.toMap(
                                CompanyHours::getDayOfWeek,
                                Function.identity()
                        ));

        for (CompanyHoursRequest req : companyHours) {
            CompanyHours item = currentMap.get(req.getDayOfWeek());
            if (item != null) {
                item.setOpen(req.getIsOpen());
                item.setOpenTime(req.getOpenTime());
                item.setCloseTime(req.getCloseTime());
            }
        }

        this.companyRepository.save(company);
        return currentCompanyHours.stream()
                .map(CompanyHoursMapper::companyHoursToCompanyHoursResponse)
                .toList();
    }
}
