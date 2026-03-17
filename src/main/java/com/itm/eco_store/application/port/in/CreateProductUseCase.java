package com.itm.eco_store.application.port.in;

import com.itm.eco_store.domain.model.Product;

public interface CreateProductUseCase {
    Product create(Product product);
}