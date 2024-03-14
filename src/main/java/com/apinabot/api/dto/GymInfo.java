package com.apinabot.api.dto;

import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class GymInfo {

    private String id;
    private Company company;
    private Address address;
    private long createdAt;
    private OpeningTime openingTime;
    private ClosingTime closingTime;
    private String additionalInfo;

    public GymInfo(String id, Company company, Address address, long createdAt, OpeningTime openingTime, ClosingTime closingTime, String additionalInfo) {
        this.id = id;
        this.company = company;
        this.address = address;
        this.createdAt = createdAt;
        this.openingTime = openingTime;
        this.closingTime = closingTime;
        this.additionalInfo = additionalInfo;
    }

    public GymInfo() {
    }

    @Override
    public String toString() {
        return "GymInfo{" +
               ", company=" + company +
               ", address=" + address +
               ", createdAt=" + createdAt +
               ", openingTime=" + openingTime +
               ", closingTime=" + closingTime +
               ", additionalInfo='" + additionalInfo + '\'' +
               '}';
    }
    public String getDisplayName() {
        return this.getCompany().getName() + " - " + this.getAddress().getStreet();
    }
}

