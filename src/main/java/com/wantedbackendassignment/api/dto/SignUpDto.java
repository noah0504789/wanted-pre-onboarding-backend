package com.wantedbackendassignment.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.hibernate.validator.constraints.Length;

public record SignUpDto(

        @NotBlank
        @Pattern(regexp = "\\S+@\\S+")
        String email,

        @NotBlank
        @Length(min = 8)
        String password
) {
}
