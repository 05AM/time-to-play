package org.example.api.game.repository;

import java.util.List;
import java.util.Optional;

import org.example.core.domain.game.product.ProductGame;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductGameRepository {

    Optional<ProductGame> findById(long id);

    Page<ProductGame> findAll(Pageable pageable);

    List<ProductGame> findAllByNameContaining(String keyword);
}
