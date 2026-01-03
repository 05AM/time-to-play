package org.example.api.game.controller;

import java.util.List;

import org.example.api.common.model.BaseResponse;
import org.example.api.common.model.ResponseCode;
import org.example.api.game.controller.dto.ProductGameDetailRes;
import org.example.api.game.controller.dto.ProductGameItemRes;
import org.example.api.game.controller.dto.ProductGamePagingRes;
import org.example.api.game.service.ProductGameQueryService;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ProductGameController {

    private static final long MEMBER_ID = 1L;

    private final ProductGameQueryService productGameQueryService;

    // 게임 페이지네이션
    @GetMapping("/product-games")
    public ResponseEntity<BaseResponse<ProductGamePagingRes>> searchGame(
        Pageable pageable
    ) {
        ProductGamePagingRes response = productGameQueryService.getProductGames(MEMBER_ID, pageable);

        return ResponseEntity.ok(
            BaseResponse.of(ResponseCode.SUCCESS, response)
        );
    }

    // 게임 검색
    @GetMapping("/search/product-games")
    public ResponseEntity<BaseResponse<List<ProductGameItemRes>>> searchGame(
        @RequestParam String searchword
    ) {
        List<ProductGameItemRes> response = productGameQueryService.searchProductGameByKeyword(MEMBER_ID, searchword);

        return ResponseEntity.ok(
            BaseResponse.of(ResponseCode.SUCCESS, response)
        );
    }

    // 게임 상세 조회
    @GetMapping("/product-games/{productGameId}")
    public ResponseEntity<BaseResponse<ProductGameDetailRes>> getProductGameDetail(
        @PathVariable Long productGameId
    ) {
        ProductGameDetailRes response = productGameQueryService.getProductGameDetail(MEMBER_ID, productGameId);

        return ResponseEntity.ok(
            BaseResponse.of(ResponseCode.SUCCESS, response)
        );
    }
}
