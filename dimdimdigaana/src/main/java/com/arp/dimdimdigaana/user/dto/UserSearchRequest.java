package com.arp.dimdimdigaana.user.dto;

import jakarta.validation.Valid;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserSearchRequest {

    /** All criteria are combined with AND logic */
    @Valid
    private List<SearchCriteria> criteria;
}

