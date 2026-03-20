package com.itm.eco_store.infrastructure.adapter.out.persistence;

import com.itm.eco_store.domain.model.Product;
import com.itm.eco_store.application.port.out.ProductRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Implementación en memoria del puerto. Sustituir por JPA (ProductJpaAdapter) en Fase 3.
 */
@Component
@Profile("!prod")
public class InMemoryProductRepository implements ProductRepository {

    private final ConcurrentHashMap<Long, Product> store = new ConcurrentHashMap<>();
    private final AtomicLong sequence = new AtomicLong(1);

    @Override
    public Product save(Product product) {
        if (product.getId() == null) {
            long id = sequence.getAndIncrement();
            Product withId = product.toBuilder().id(id).build();
            store.put(id, withId);
            return withId;
        }
        store.put(product.getId(), product);
        return product;
    }

    @Override
    public Optional<Product> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public List<Product> findAll() {
        return List.copyOf(store.values());
    }

    @Override
    public Product update(Product product) {
        store.put(product.getId(), product);
        return product;
    }

    @Override
    public void deleteById(Long id) {
        store.remove(id);
    }

    @Override
    public boolean existsById(Long id) {
        return store.containsKey(id);
    }

    @Override
    public boolean existsByName(String name) {
        if (name == null || name.isBlank()) return false;
        String lower = name.trim().toLowerCase();
        return store.values().stream()
                .anyMatch(p -> p.getName() != null && p.getName().trim().toLowerCase().equals(lower));
    }

    @Override
    public boolean existsByNameExcludingId(String name, Long excludeId) {
        if (name == null || name.isBlank()) return false;
        String lower = name.trim().toLowerCase();
        return store.values().stream()
                .anyMatch(p -> p.getId() != null && !p.getId().equals(excludeId)
                        && p.getName() != null && p.getName().trim().toLowerCase().equals(lower));
    }
}
