package com.itm.eco_store.domain.model;

/**
 * Categoría del producto.
 * El porcentaje de descuento se obtiene desde DiscountRepository (configuración externa).
 * Esto permite cambiar los descuentos sin recompilar el código.
 */
public enum Category {
    NORMAL,
    TEMPORADA_PASADA
}