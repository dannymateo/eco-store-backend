package com.itm.eco_store.domain.model;

import java.math.BigDecimal;

/**
 * Categoría del producto. El porcentaje de descuento está definido por categoría (no lo envía el cliente).
 * NORMAL: 0%. TEMPORADA_PASADA: 15%.
 */
public enum Category {
    NORMAL(BigDecimal.ZERO),
    TEMPORADA_PASADA(new BigDecimal("15"));

    private final BigDecimal discountPercent;

    Category(BigDecimal discountPercent) {
        this.discountPercent = discountPercent;
    }

    public BigDecimal getDiscountPercent() {
        return discountPercent;
    }

    public boolean requiresSeasonDiscount() {
        return this.discountPercent.compareTo(BigDecimal.ZERO) > 0;
    }
}
