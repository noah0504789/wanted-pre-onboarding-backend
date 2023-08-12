package com.wantedbackendassignment.api.dto;

public record PostInfoDto(
        Long id,
        String title,
        String description,
        String authorEmail
) {
}
