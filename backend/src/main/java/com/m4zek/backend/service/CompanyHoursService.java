package com.m4zek.backend.service;


import com.m4zek.backend.exception.BadRequestException;
import com.m4zek.backend.model.Company;
import com.m4zek.backend.model.CompanyHours;
import com.m4zek.backend.model.dto.write.CompanyHoursRequest;
import com.m4zek.backend.repository.CompanyHoursRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class CompanyHoursService {

    private final static Logger logger = LoggerFactory.getLogger(CompanyHoursService.class);

    private final CompanyHoursRepository companyHoursRepository;


    public CompanyHoursService(CompanyHoursRepository companyHoursRepository) {
        this.companyHoursRepository = companyHoursRepository;
    }




    @Transactional
    public List<CompanyHours> createAndSaveCompanyHours(Company company, List<CompanyHoursRequest> requests){

        validateNoDuplicateDays(requests);

        List<CompanyHours> companyHours = requests.stream()
                .map(day -> new CompanyHours(
                        day.getDayOfWeek(),
                        day.getOpenTime(),
                        day.getCloseTime(),
                        day.getOpen(),
                        company
                )).toList();

        // Saved all companyHours
        companyHours = this.saveAll(companyHours);
        return companyHours;
    }


    public List<CompanyHours> findCompanyHours(long companyId){
        return this.companyHoursRepository.readAllByCompanyId(companyId);
    }

    public List<CompanyHours> saveAll(List<CompanyHours> hours){
        List<CompanyHours> savedHours = hours
                .stream()
                .map(this.companyHoursRepository::save)
                        .toList();
        logger.info("Company Hours {} has been saved",
                Arrays.toString(
                        savedHours.stream()
                            .map(CompanyHours::getDayOfWeek).toArray())
                );
        return savedHours;
    }

    public CompanyHours save(CompanyHours hours){
        CompanyHours savedHours = this.companyHoursRepository.save(hours);
        logger.info("Day [{}] has been saved to Company [{}]", savedHours.getDayOfWeek(), savedHours.getCompany().getId());
        return savedHours;
    }

    public List<CompanyHours> updateHours(Company company, List<CompanyHoursRequest> requests){
        // Throw exception if request body is empty
        if(requests.isEmpty()) {
            throw new IllegalArgumentException("New company's working hour list could not be empty");
        }

        validateNoDuplicateDays(requests);

        List<CompanyHours> currentHours = this.findCompanyHours(company.getId());

        // Create map DAY -> Opening hours
        Map<String, CompanyHours> currentMap =
                currentHours.stream().collect(
                        Collectors.toMap(
                                CompanyHours::getDayOfWeek,
                                Function.identity()
                        ));

        // Create list for updated entities
        List<CompanyHours> changedHours = new ArrayList<>();

        // Iterable for new hours request and update if exist
        for (CompanyHoursRequest req : requests) {
            CompanyHours item = currentMap.get(req.getDayOfWeek());
            if(item != null){
                item = this.update(item, req);
                changedHours.add(item);
            }
        }

        // Save all changed hours
        changedHours = this.saveAll(changedHours);

        return changedHours;
    }



    public void validateNoDuplicateDays(List<CompanyHoursRequest> list) {
        Set<String> days = new HashSet<>();

        for (CompanyHoursRequest item : list) {
            String day = item.getDayOfWeek();

            if (!days.add(day)) {
                throw new BadRequestException("Duplicate days found: " + day);
            }
        }
    }

    public void assignOpeningHours(Company company, List<CompanyHoursRequest> openingHours) {
        validateNoDuplicateDays(openingHours);

        for (CompanyHoursRequest request : openingHours) {

            CompanyHours companyHours =
                    new CompanyHours(
                            request.getDayOfWeek(),
                            request.getOpenTime(),
                            request.getCloseTime(),
                            request.getOpen(),
                            company
                    );

            companyHours = this.save(companyHours);
            company.addCompanyHour(companyHours);
        }
    }

    //*****************************************
    // Private methods

    private CompanyHours update(CompanyHours hours, CompanyHoursRequest request){

        String updated = "";

        // If day open value was change update entity filed open
        if(request.getOpen() != null && hours.isOpen() != request.getOpen()){
            updated = String.format("Open [%s] -> [%s]  ", hours.isOpen(), request.getOpen());
            hours.setOpen(request.getOpen());
        }

        // If day close time was change update entity field close time
        if(request.getCloseTime() != null && !hours.getCloseTime().equals(request.getCloseTime())){
            updated += String.format("Close time [%s] -> [%s] ", hours.getCloseTime(), request.getCloseTime());
            hours.setCloseTime(request.getCloseTime());
        }

        // If day open time was change update entity field open time
        if(request.getOpenTime() != null && !hours.getOpenTime().equals(request.getOpenTime())){
            updated += String.format("Open time [%s] -> [%s]", hours.getOpenTime(), request.getOpenTime());
            hours.setOpenTime(request.getOpenTime());
        }

        // If any field has been changed show log with changes
        if(!updated.isEmpty()){
            logger.info("Day [{}] in Company [{}], changes: {}",
                    hours.getDayOfWeek(), hours.getCompany().getId(), updated);
        }

        return hours;
    }
}
