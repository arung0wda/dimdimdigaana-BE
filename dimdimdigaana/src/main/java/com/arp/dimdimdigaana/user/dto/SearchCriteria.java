package com.arp.dimdimdigaana.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SearchCriteria {

    /** The entity field name: id, username, firstName, lastName, dob, age */
    @NotBlank(message = "field must not be blank")
    private String field;

    /** The operation to apply */
    @NotNull(message = "operation must not be null")
    private SearchOperation operation;

    /** The primary value (used by all operations) */
    @NotBlank(message = "value must not be blank")
    private String value;

    /** Secondary value (only used by BETWEEN) */
    private String valueTo;
}

