package com.itm.eco_store.application.port.out;

import com.itm.eco_store.domain.model.Product;

import java.util.List;
import java.util.Optional;

public interface ProductRepository {
    Product save(Product product);
    Optional<Product> findById(Long id);
    List<Product> findAll();
    Product update(Product product);
    void deleteById(Long id);
    boolean existsById(Long id);
    boolean existsByName(String name);
    boolean existsByNameExcludingId(String name, Long excludeId);
}
