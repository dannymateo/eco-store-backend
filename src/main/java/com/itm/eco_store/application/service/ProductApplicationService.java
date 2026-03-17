package com.itm.eco_store.application.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.itm.eco_store.application.exception.DuplicateProductNameException;
import com.itm.eco_store.application.port.in.CreateProductUseCase;
import com.itm.eco_store.application.port.in.DeleteProductUseCase;
import com.itm.eco_store.application.port.in.GetProductUseCase;
import com.itm.eco_store.application.port.in.UpdateProductUseCase;
import com.itm.eco_store.application.port.out.DiscountRepository;
import com.itm.eco_store.application.port.out.ProductRepository;
import com.itm.eco_store.domain.model.Category;
import com.itm.eco_store.domain.model.DiscountRule;
import com.itm.eco_store.domain.model.Product;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductApplicationService implements
        CreateProductUseCase, GetProductUseCase, UpdateProductUseCase, DeleteProductUseCase {

    private final ProductRepository repository;
    private final DiscountRepository discountRepository;

    @Override
    @Transactional
    public Product create(Product product) {
        if (repository.existsByName(product.getName())) {
            throw new DuplicateProductNameException(product.getName());
        }
        
        // Obtener descuento desde configuración externa
        BigDecimal discountPercent = getDiscountForCategory(product.getCategory());
        
        // Crear producto con descuento aplicado
        Product productWithDiscount = Product.create(
                product.getName(),
                product.getDescription(),
                product.getCategory(),
                product.getPriceInfo().getOriginalPrice(),
                discountPercent
        );
        
        return repository.save(productWithDiscount);
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
        
        // Obtener descuento desde configuración externa
        BigDecimal discountPercent = getDiscountForCategory(product.getCategory());
        
        // Obtener producto existente y actualizarlo
        Product existing = repository.findById(id).orElseThrow();
        Product toSave = existing.updateWith(
                product.getName(),
                product.getDescription(),
                product.getCategory(),
                product.getPriceInfo().getOriginalPrice(),
                discountPercent
        );
        
        return repository.update(toSave);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new IllegalArgumentException("Producto no encontrado: " + id);
        }
        repository.deleteById(id);
    }
    
    /**
     * Obtiene el porcentaje de descuento para una categoría desde el repositorio de descuentos.
     * Si no hay regla definida, retorna 0%.
     */
    private BigDecimal getDiscountForCategory(Category category) {
        return discountRepository.findByCategory(category)
                .map(DiscountRule::getDiscountPercent)
                .orElse(BigDecimal.ZERO);
    }
}