package org.example.api.game.repository;

import java.util.List;

import org.example.core.domain.game.product.ProductGame;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaProductGameRepository extends JpaRepository<ProductGame, Long> {
    List<ProductGame> findByNameContaining(String name);
}
