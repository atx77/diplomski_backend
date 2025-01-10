package hr.tvz.diplomski.webshop.service;

import hr.tvz.diplomski.webshop.domain.Address;
import hr.tvz.diplomski.webshop.domain.User;
import hr.tvz.diplomski.webshop.enumeration.CountryEnum;

public interface AddressService {
    Address createNewAddressForUser(String firstName, String lastName, String street, String city, String postcode,
                                    CountryEnum country, User user);
}
