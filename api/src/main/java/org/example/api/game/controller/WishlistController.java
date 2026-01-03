package org.example.api.game.controller;

import java.util.List;

import org.example.api.common.model.BaseResponse;
import org.example.api.common.model.ResponseCode;
import org.example.api.game.controller.dto.AddWishlistReq;
import org.example.api.game.controller.dto.WishListRes;
import org.example.api.game.service.WishlistService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class WishlistController {

    private static final long MEMBER_ID = 1L;

    private final WishlistService wishlistService;

    // 멤버 위시리스트 조회
    @GetMapping("/wishlist")
    public ResponseEntity<BaseResponse<List<WishListRes>>> getMemberWishlist() {
        List<WishListRes> response = wishlistService.getMemberWishlist(MEMBER_ID);

        return ResponseEntity.ok(
            BaseResponse.of(ResponseCode.SUCCESS, response)
        );
    }

    // 멤버 위시리스트 추가
    @PostMapping("/wishlist")
    public ResponseEntity<BaseResponse<Void>> addMemberWishlist(
        @RequestBody AddWishlistReq request
    ) {
        wishlistService.addToWishlist(MEMBER_ID, request.productId(), request.discountRate());

        return ResponseEntity.ok(
            BaseResponse.of(ResponseCode.SUCCESS)
        );
    }
}
