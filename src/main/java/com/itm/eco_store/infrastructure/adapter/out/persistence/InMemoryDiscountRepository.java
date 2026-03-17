package com.itm.eco_store.infrastructure.adapter.out.persistence;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.itm.eco_store.application.port.out.DiscountRepository;
import com.itm.eco_store.domain.model.Category;
import com.itm.eco_store.domain.model.DiscountRule;

/**
 * Adaptador de salida en memoria para reglas de descuento.
 * Implementación por defecto con valores configurados.
 * En producción se sustituiría por una implementación JPA que lea de BD.
 */
@Component
@Profile("!prod")
public class InMemoryDiscountRepository implements DiscountRepository {

    private final List<DiscountRule> rules;

    public InMemoryDiscountRepository() {
        this.rules = List.of(
            new DiscountRule(Category.NORMAL, BigDecimal.ZERO),
            new DiscountRule(Category.TEMPORADA_PASADA, new BigDecimal("15.00"))
        );
    }

    @Override
    public Optional<DiscountRule> findByCategory(Category category) {
        return rules.stream()
                .filter(rule -> rule.appliesTo(category))
                .findFirst();
    }

    @Override
    public List<DiscountRule> findAll() {
        return rules;
    }
}