package org.example.api.game.service;

import java.util.List;

import org.example.api.game.controller.dto.WishListRes;
import org.example.api.game.repository.JpaWishlistRepository;
import org.example.api.member.service.MemberQueryService;
import org.example.core.domain.game.product.ProductGame;
import org.example.core.domain.member.Member;
import org.example.core.domain.wishlist.Wishlist;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Transactional
@Service
@RequiredArgsConstructor
public class WishlistService {

    private final MemberQueryService memberQueryService;
    private final ProductGameQueryService productGameQueryService;

    private final JpaWishlistRepository wishlistRepository;

    // 위시 리스트 추가
    public void addToWishlist(long memberId, long productId, short discountRate) {
        Member member = memberQueryService.getById(memberId);
        ProductGame productGame = productGameQueryService.getById(productId);

        Wishlist wishlist = new Wishlist(member, productGame, discountRate);
        wishlistRepository.save(wishlist);
    }

    // 위시 리스트 조회
    @Transactional(readOnly = true)
    public List<WishListRes> getMemberWishlist(long memberId) {
        Member member = memberQueryService.getById(memberId);
        List<Wishlist> wishlists = wishlistRepository.findAllByMember(member);

        return wishlists.stream()
            .map(WishListRes::toDto)
            .toList();
    }

    @Transactional(readOnly = true)
    public List<Long> getWishlistProductIds(long memberId) {
        Member member = memberQueryService.getById(memberId);
        List<Wishlist> wishlists = wishlistRepository.findAllByMember(member);

        return wishlists.stream()
            .map(wishlist -> wishlist.getProductGame().getId())
            .toList();
    }

}
