package com.m4zek.backend.model.dto.read;


import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class UserToHiredResponse {

    private int id;
    private String firstName;
    private String lastName;
    private List<Integer> companyIds;
    private String avatar;

}
