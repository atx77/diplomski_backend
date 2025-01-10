package hr.tvz.diplomski.webshop.dto;

import lombok.Data;

@Data
public class AddressDto {
    private String firstName;
    private String lastName;
    private String street;
    private String city;
    private String postcode;
    private String country;
}
