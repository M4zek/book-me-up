package com.m4zek.backend.model;

import com.m4zek.backend.model.projection.CompanyHoursReadModel;
import jakarta.persistence.*;

@Entity(name = "CompaniesHours")
public class CompanyHours extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    private String dayOfWeek;
    private boolean isOpen;
    private String openTime;
    private String closeTime;

    @ManyToOne
    @JoinColumn(name = "company_id")
    private Company company;

    public CompanyHours() {}


    public CompanyHours(String dayOfWeek, String openTime, String closeTime, boolean isOpen,Company company) {
        this.dayOfWeek = dayOfWeek;
        this.openTime = openTime;
        this.closeTime = closeTime;
        this.isOpen = isOpen;
        this.company = company;
    }


    public CompanyHoursReadModel toReadModel(){
        return CompanyHoursReadModel.builder()
                .dayOfWeek(dayOfWeek)
                .openTime(openTime)
                .closeTime(closeTime)
                .isOpen(isOpen)
                .build();
    }

    @Override
    public String toString() {
        return "CompanyHours{" +
                "closeTime='" + closeTime + '\'' +
                ", openTime='" + openTime + '\'' +
                ", dayOfWeek='" + dayOfWeek + '\'' +
                ", id=" + id +
                '}';
    }

}
