package org.example.api.game.repository;

import java.util.List;
import java.util.Optional;

import org.example.core.domain.game.product.ProductGame;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ProductGameRepositoryImpl implements ProductGameRepository {

    private final JpaProductGameRepository jpaProductGameRepository;

    @Override
    public Optional<ProductGame> findById(long id) {
        return jpaProductGameRepository.findById(id);
    }

    @Override
    public Page<ProductGame> findAll(Pageable pageable) {
        return jpaProductGameRepository.findAll(pageable);
    }

    @Override
    public List<ProductGame> findAllByNameContaining(String keyword) {
        return jpaProductGameRepository.findByNameContaining(keyword);
    }
}
