package com.m4zek.backend.controller;

import com.m4zek.backend.model.Company;
import com.m4zek.backend.model.dto.read.CompanyReadModel;
import com.m4zek.backend.model.dto.write.CompanyWriteModel;
import com.m4zek.backend.model.dto.read.UserReadModel;
import com.m4zek.backend.service.CompanyService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/companies")
@Validated
public class CompanyController {

    private final CompanyService companyService;

    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }

    @PostMapping
    @Transactional
    public ResponseEntity<CompanyReadModel> createNewCompany(
            @RequestBody @Valid CompanyWriteModel companyWriteModel)
    {
        Company company = companyService.saveCompany(companyWriteModel);
        int companyId = company.toReadModel().getId();
        URI location = URI.create("/api/v1/companies/" + companyId);
        return ResponseEntity.created(location).body(company.toReadModel());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CompanyReadModel> readCompany(@PathVariable int id){
        return ResponseEntity.ok(companyService.readCompany(id));
    }

    @GetMapping
    public ResponseEntity<Page<CompanyReadModel>> readCompanies(Pageable pageable) {
        return ResponseEntity.ok(companyService.readAllCompanies(pageable));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteCompany(@PathVariable int id) {
        this.companyService.deleteCompany(id);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<CompanyReadModel> updateCompany(@PathVariable int id,
                                                          @RequestBody @Valid CompanyWriteModel companyWriteModel) {
        Company company = this.companyService.updateCompany(id, companyWriteModel);
        return ResponseEntity.ok(company.toReadModel());
    }

    @GetMapping("/employees/{companyId}")
    public ResponseEntity<List<UserReadModel>> readCompanyEmployee(@PathVariable int companyId) {
        List<UserReadModel> employees = this.companyService.readAllCompanyEmployees(companyId);
        return ResponseEntity.ok(employees);
    }
}
