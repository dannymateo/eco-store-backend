package com.itm.eco_store.domain.model;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Value Object: Regla de descuento por categoría.
 * Encapsula la relación entre una categoría y su porcentaje de descuento.
 */
public final class DiscountRule {

    private final Category category;
    private final BigDecimal discountPercent;

    public DiscountRule(Category category, BigDecimal discountPercent) {
        this.category = Objects.requireNonNull(category, "La categoría es obligatoria");
        if (discountPercent == null || discountPercent.compareTo(BigDecimal.ZERO) < 0 
                || discountPercent.compareTo(BigDecimal.valueOf(100)) > 0) {
            throw new IllegalArgumentException("El descuento debe estar entre 0 y 100");
        }
        this.discountPercent = discountPercent;
    }

    public Category getCategory() {
        return category;
    }

    public BigDecimal getDiscountPercent() {
        return discountPercent;
    }

    public boolean appliesTo(Category category) {
        return this.category.equals(category);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DiscountRule that = (DiscountRule) o;
        return category == that.category;
    }

    @Override
    public int hashCode() {
        return Objects.hash(category);
    }
}