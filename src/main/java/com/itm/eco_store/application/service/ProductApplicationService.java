package com.itm.eco_store.application.service;

import com.itm.eco_store.application.exception.DuplicateProductNameException;
import com.itm.eco_store.application.port.out.NotificationPort;
import com.itm.eco_store.domain.model.Product;
import com.itm.eco_store.application.port.in.CreateProductUseCase;
import com.itm.eco_store.application.port.in.DeleteProductUseCase;
import com.itm.eco_store.application.port.in.GetProductUseCase;
import com.itm.eco_store.application.port.in.UpdateProductUseCase;
import com.itm.eco_store.application.port.out.ProductRepositoryPort;
import com.itm.eco_store.infrastructure.adapter.in.web.ProductNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductApplicationService implements
        CreateProductUseCase, GetProductUseCase, UpdateProductUseCase, DeleteProductUseCase {

    private final ProductRepositoryPort repository;
    private final NotificationPort notificationPort;

    @Override
    @Transactional
    public Product create(Product product) {
        if (repository.existsByName(product.getName())) {
            throw new DuplicateProductNameException(product.getName());
        }
        return repository.save(product);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Product> getById(Long id) {
        return repository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Product> getAll() {
        return repository.findAll();
    }

    @Override
    @Transactional
    public Product update(Long id, Product product) {
        if (!repository.existsById(id)) {
            throw new IllegalArgumentException("Producto no encontrado: " + id);
        }
        if (repository.existsByNameExcludingId(product.getName(), id)) {
            throw new DuplicateProductNameException(product.getName());
        }
        Product existing = repository.findById(id).orElseThrow();
        Product toSave = existing.toBuilder()
                .name(product.getName())
                .description(product.getDescription())
                .category(product.getCategory())
                .priceInfo(product.getPriceInfo())
                .build();
        return repository.update(toSave);
    }

    @Override
    @Transactional
    public void delete(Long id) {

        try {
            Product toDelete = repository.findById(id).orElseThrow();

            if (toDelete == null) {
                throw new IllegalArgumentException("Producto no encontrado: " + id);
            }

            repository.deleteById(id);

            notificationPort.send("El producto " + toDelete.getName() + " se ha eliminado");

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}