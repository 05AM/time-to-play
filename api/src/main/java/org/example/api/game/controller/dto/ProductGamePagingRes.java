package org.example.api.game.controller.dto;

import java.util.List;

import org.example.api.common.dto.PageInfoResDto;

public record ProductGamePagingRes(
    List<ProductGameItemRes> productGames,
    PageInfoResDto pageInfoResDto
) {
}
