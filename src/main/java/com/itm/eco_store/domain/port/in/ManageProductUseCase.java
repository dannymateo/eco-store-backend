package com.itm.eco_store.domain.port.in;

import com.itm.eco_store.domain.model.Product;

import java.util.List;
import java.util.Optional;

/**
 * Puerto de entrada (driving): contrato del caso de uso de gestión del catálogo.
 * Expresa operaciones en términos del dominio (Product). La capa de aplicación
 * convierte DTOs a Product y delega aquí.
 */
public interface ManageProductUseCase {

    Product create(Product product);

    Optional<Product> getById(Long id);

    List<Product> getAll();

    Product update(Long id, Product product);

    void deleteById(Long id);
}
