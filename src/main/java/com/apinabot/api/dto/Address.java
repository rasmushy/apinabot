package com.apinabot.api.dto;

import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class Address {
    private String number;
    private String street;
    private String postcode;
    private String city;

    public Address(String number, String street, String postcode, String city) {
        this.number = number;
        this.street = street;
        this.postcode = postcode;
        this.city = city;
    }

    public Address() {
    }

    @Override
    public String toString() {
        return "Address{" +
               "number='" + number + '\'' +
               ", street='" + street + '\'' +
               ", postcode='" + postcode + '\'' +
               ", city='" + city + '\'' +
               '}';
    }
}
