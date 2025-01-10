package hr.tvz.diplomski.webshop.dto.request;

import lombok.Data;

@Data
public class UpdatePersonalInformationRequest {
    private String firstName;
    private String lastName;
    private String password;
}
