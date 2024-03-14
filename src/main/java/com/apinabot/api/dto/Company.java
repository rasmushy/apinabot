package com.apinabot.api.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;
@Getter
@Setter
public class Company {
    private String homePage;
    private String name;
    private Map<String, String> prices;

    public Company(String homePage, String name, Map<String, String> prices) {
        this.homePage = homePage;
        this.name = name;
        this.prices = prices;
    }

    public Company() {
    }

}

