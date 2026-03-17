package com.itm.eco_store.application.port.out;

import java.util.List;
import java.util.Optional;

import com.itm.eco_store.domain.model.Category;
import com.itm.eco_store.domain.model.DiscountRule;

/**
 * Puerto de salida para obtener reglas de descuento.
 * Permite que el origen de los descuentos sea configurable (BD, properties, etc.)
 */
public interface DiscountRepository {

    /**
     * Busca la regla de descuento para una categoría específica.
     */
    Optional<DiscountRule> findByCategory(Category category);

    /**
     * Obtiene todas las reglas de descuento configuradas.
     */
    List<DiscountRule> findAll();
}