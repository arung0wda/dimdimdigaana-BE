package com.arp.dimdimdigaana.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRequestDto {

    @NotBlank(message = "username must not be blank")
    @Size(max = 50, message = "username must be at most 50 characters")
    private String username;

    @NotBlank(message = "firstName must not be blank")
    @Size(max = 100, message = "firstName must be at most 100 characters")
    private String firstName;

    @NotBlank(message = "lastName must not be blank")
    @Size(max = 100, message = "lastName must be at most 100 characters")
    private String lastName;

    @NotNull(message = "dob must not be null")
    @Past(message = "dob must be a past date")
    private LocalDate dob;
}

