package org.example.api.game.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.example.api.common.dto.PageInfoResDto;
import org.example.api.common.exception.NotFoundException;
import org.example.api.game.controller.dto.ProductGameDetailRes;
import org.example.api.game.controller.dto.ProductGameItemRes;
import org.example.api.game.controller.dto.ProductGamePagingRes;
import org.example.api.game.repository.JpaWishlistRepository;
import org.example.api.game.repository.ProductGameRepository;
import org.example.core.domain.game.product.ProductGame;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class ProductGameQueryService {

    private final ProductGameRepository productGameRepository;
    private final JpaWishlistRepository wishlistRepository;

    public ProductGamePagingRes getProductGames(Long memberId, Pageable pageable) {
        Page<ProductGame> page = productGameRepository.findAll(pageable);
        List<ProductGame> items = page.getContent();

        if (items.isEmpty()) {
            return new ProductGamePagingRes(
                List.of(),
                PageInfoResDto.from(page)
            );
        }

        Set<Long> wishedIds = loadWishedIds(memberId, items);

        List<ProductGameItemRes> dtoList = items.stream()
            .map(pg -> ProductGameItemRes.toDto(pg, wishedIds.contains(pg.getId())))
            .toList();

        return new ProductGamePagingRes(
            dtoList,
            PageInfoResDto.from(page)
        );
    }

    public List<ProductGameItemRes> searchProductGameByKeyword(Long memberId, String keyword) {
        List<ProductGame> productGames = productGameRepository.findAllByNameContaining(keyword);

        if (productGames.isEmpty()) {
            return List.of();
        }

        Set<Long> wishedIds = loadWishedIds(memberId, productGames);

        return productGames.stream()
            .map(pg -> ProductGameItemRes.toDto(pg, wishedIds.contains(pg.getId())))
            .toList();
    }

    public ProductGameDetailRes getProductGameDetail(Long memberId, Long productGameId) {
        ProductGame productGame = getById(productGameId);

        boolean wished = (memberId != null)
            && wishlistRepository.existsByMemberIdAndProductGameId(memberId, productGameId);

        return ProductGameDetailRes.toDto(productGame, wished);
    }

    private Set<Long> loadWishedIds(Long memberId, List<ProductGame> items) {
        if (memberId == null) {
            return Set.of();
        }

        List<Long> ids = items.stream()
            .map(ProductGame::getId)
            .toList();

        if (ids.isEmpty()) {
            return Set.of();
        }

        return wishlistRepository.findProductGameIdsByMemberIdAndProductGameIdIn(memberId, ids)
            .stream()
            .collect(Collectors.toUnmodifiableSet());
    }

    public ProductGame getById(long productGameId) {
        return productGameRepository.findById(productGameId)
            .orElseThrow(() -> new NotFoundException("존재하지 않는 상품 게임 입니다."));
    }
}
