package com.example.backend.dtos.address;

import com.example.backend.validators.OnCreate;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddressRequestDTO {
    @NotBlank(message = "Street information is required", groups = OnCreate.class)
    private String street;

    @NotBlank(message = "Mention your city", groups = OnCreate.class)
    private String city;

    @NotBlank(message = "State information is required", groups = OnCreate.class)
    private String state;

    @NotBlank(message = "Provide your areas zip code.", groups = OnCreate.class)
    private String zip;

    @NotBlank(message = "Please mention country of living", groups = OnCreate.class)
    private String country;

    @NotBlank(message = "Phone number is required", groups = OnCreate.class)
    @Size(min = 4, max = 17, message = "Valid phone number range between 4 to 17")
    private String phone;
    private Boolean isDefault;
}
