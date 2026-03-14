package com.itm.eco_store.domain.model;

/**
 * Categoría del producto. La regla de negocio establece que
 * TEMPORADA_PASADA debe recibir descuento automático.
 */
public enum Category {
    NORMAL,
    TEMPORADA_PASADA;

    public boolean requiresSeasonDiscount() {
        return this == TEMPORADA_PASADA;
    }
}
