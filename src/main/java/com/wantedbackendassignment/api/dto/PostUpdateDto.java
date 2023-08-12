package com.wantedbackendassignment.api.dto;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

public record PostUpdateDto(

        @NotBlank
        @Length(max = 100, message = "cannot exceed 100 characters")
        String title,

        @NotBlank
        @Length(max = 500, message = "cannot exceed 500 characters")
        String description
) {
}
