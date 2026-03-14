package com.itm.eco_store.infrastructure.adapter.in.web.dto;

import com.itm.eco_store.domain.model.Category;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

@Schema(description = "Datos para actualizar un producto. El descuento se aplica según la categoría (NORMAL=0%, TEMPORADA_PASADA=15%).")
public record UpdateProductDTO(
        @Schema(description = "Nombre del producto", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "El nombre es obligatorio")
        String name,

        @Schema(description = "Descripción del producto")
        String description,

        @Schema(description = "Categoría: NORMAL o TEMPORADA_PASADA", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "La categoría es obligatoria")
        Category category,

        @Schema(description = "Precio original", requiredMode = Schema.RequiredMode.REQUIRED, minimum = "0")
        @NotNull(message = "El precio original es obligatorio")
        @DecimalMin(value = "0", inclusive = true, message = "El precio debe ser >= 0")
        BigDecimal originalPrice
) {
    public String getName() { return name; }
    public String getDescription() { return description; }
    public Category getCategory() { return category; }
    public BigDecimal getOriginalPrice() { return originalPrice; }
}
