package com.arp.dimdimdigaana.user.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SearchCriteria {

    /** The entity field name: id, username, firstName, lastName, dob, age */
    private String field;

    /** The operation to apply */
    private SearchOperation operation;

    /** The primary value (used by all operations) */
    private String value;

    /** Secondary value (only used by BETWEEN) */
    private String valueTo;
}

