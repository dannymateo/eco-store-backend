package com.itm.eco_store.application.port.in;

import com.itm.eco_store.domain.model.Product;

import java.util.List;
import java.util.Optional;

public interface GetProductUseCase {
    Optional<Product> getById(Long id);
    List<Product> getAll();
}
