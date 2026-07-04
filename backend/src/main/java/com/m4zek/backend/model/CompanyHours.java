package com.m4zek.backend.model;

import jakarta.persistence.*;

@Entity(name = "CompaniesHours")
public class CompanyHours extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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


    public int getId() {
        return id;
    }

    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public String getOpenTime() {
        return openTime;
    }

    public String getCloseTime() {
        return closeTime;
    }

    public void setOpen(boolean open) {
        isOpen = open;
    }

    public void setOpenTime(String openTime) {
        this.openTime = openTime;
    }

    public void setCloseTime(String closeTime) {
        this.closeTime = closeTime;
    }

    public Company getCompany() {
        return company;
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
