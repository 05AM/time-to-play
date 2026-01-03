package org.example.api.game.controller.dto;

import org.example.core.domain.game.common.MediaType;

public record GameMediaRes(
    MediaType mediaType,
    String url,
    int sortOrder
) {
}

