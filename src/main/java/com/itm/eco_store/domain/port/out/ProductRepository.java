package com.itm.eco_store.domain.port.out;

import com.itm.eco_store.domain.model.Product;

import java.util.List;
import java.util.Optional;

/**
 * Puerto de salida (driven): contrato para persistencia de productos.
 * El dominio no conoce la implementación (JPA, etc.).
 */
public interface ProductRepository {

    Product save(Product product);

    Optional<Product> findById(Long id);

    List<Product> findAll();

    Product update(Product product);

    void deleteById(Long id);

    boolean existsById(Long id);
}
