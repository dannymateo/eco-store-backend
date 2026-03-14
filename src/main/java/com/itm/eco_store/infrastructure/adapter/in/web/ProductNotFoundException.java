package com.itm.eco_store.infrastructure.adapter.in.web;

public class ProductNotFoundException extends RuntimeException {

    public ProductNotFoundException(Long id) {
        super("Producto no encontrado: " + id);
    }
}
