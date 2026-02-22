package com.arp.dimdimdigaana.user.dto;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRequestDto {

    private String username;
    private String firstName;
    private String lastName;
    private LocalDate dob;
}

