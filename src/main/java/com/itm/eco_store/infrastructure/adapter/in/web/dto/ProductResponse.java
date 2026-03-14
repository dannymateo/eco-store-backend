package com.itm.eco_store.infrastructure.adapter.in.web.dto;

import com.itm.eco_store.domain.model.Category;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(description = "Producto del catálogo con trazabilidad de precios (original, descuento %, final).")
public record ProductResponse(
        @Schema(description = "Identificador único del producto")
        Long id,

        @Schema(description = "Nombre del producto")
        String name,

        @Schema(description = "Descripción del producto")
        String description,

        @Schema(description = "Categoría: NORMAL o TEMPORADA_PASADA")
        Category category,

        @Schema(description = "Precio original antes de descuentos")
        BigDecimal originalPrice,

        @Schema(description = "Porcentaje de descuento aplicado (0 si no aplica)")
        BigDecimal discountPercent,

        @Schema(description = "Precio final (persistido en BD)")
        BigDecimal finalPrice
) {
    public Long getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public Category getCategory() { return category; }
    public BigDecimal getOriginalPrice() { return originalPrice; }
    public BigDecimal getDiscountPercent() { return discountPercent; }
    public BigDecimal getFinalPrice() { return finalPrice; }
}
