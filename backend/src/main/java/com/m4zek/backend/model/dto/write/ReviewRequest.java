package com.m4zek.backend.model.dto.write;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReviewRequest {

    @NotBlank(message = "Comment cannot be blank")
    private String comment;

    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating cannot be greater than 5")
    private int rating;

    @NotNull(message = "Company offer ID cannot be null")
    private int company_offer_id;

    @NotNull(message = "Author ID cannot be null")
    private int author_id;

}
