package org.example.api.common.dto;

import org.springframework.data.domain.Page;

public record PageInfoResDto(
    int page,
    int size,
    long totalElements,
    int totalPages,
    boolean hasNext,
    boolean hasPrevious
) {

    public static PageInfoResDto from(Page<?> page) {
        return new PageInfoResDto(
            page.getNumber(),
            page.getSize(),
            page.getTotalElements(),
            page.getTotalPages(),
            page.hasNext(),
            page.hasPrevious()
        );
    }
}