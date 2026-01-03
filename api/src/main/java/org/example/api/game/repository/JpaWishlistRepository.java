package org.example.api.game.repository;

import java.util.List;

import org.example.core.domain.member.Member;
import org.example.core.domain.wishlist.Wishlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import io.lettuce.core.dynamic.annotation.Param;

public interface JpaWishlistRepository extends JpaRepository<Wishlist, Long> {
    List<Wishlist> findAllByMember(Member member);

    boolean existsByMemberIdAndProductGameId(Long memberId, Long productGameId);

    @Query("""
        select w.productGame.id
        from Wishlist w
        where w.member.id = :memberId
          and w.productGame.id in :productGameIds
    """)
    List<Long> findProductGameIdsByMemberIdAndProductGameIdIn(
        @Param("memberId") Long memberId,
        @Param("productGameIds") List<Long> productGameIds
    );
}
