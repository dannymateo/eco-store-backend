package com.itm.eco_store.domain.port.in;

import com.itm.eco_store.domain.model.Product;

public interface UpdateProductUseCase {
    Product update(Long id, Product product);
}
