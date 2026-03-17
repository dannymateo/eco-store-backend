package com.itm.eco_store.infrastructure.adapter.in.web.mapper;

import java.math.BigDecimal;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import com.itm.eco_store.domain.model.Category;
import com.itm.eco_store.domain.model.PriceInfo;
import com.itm.eco_store.domain.model.Product;
import com.itm.eco_store.infrastructure.adapter.in.web.dto.CreateProductDTO;
import com.itm.eco_store.infrastructure.adapter.in.web.dto.ProductResponse;
import com.itm.eco_store.infrastructure.adapter.in.web.dto.UpdateProductDTO;

/**
 * Mapper para convertir entre DTOs y entidades de dominio.
 * Nota: El descuento NO se aplica aquí, sino en ProductApplicationService.
 */
@Mapper(componentModel = "spring")
public interface ProductMapper {

    /**
     * Convierte CreateProductDTO a Product para crear.
     * El precio original se extrae del DTO y se crea un PriceInfo sin descuento.
     * El descuento se aplicará en ProductApplicationService.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "priceInfo", source = "originalPrice", qualifiedByName = "toPriceInfo")
    Product toDomain(CreateProductDTO dto);

    /**
     * Convierte UpdateProductDTO a Product para actualizar.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "priceInfo", source = "originalPrice", qualifiedByName = "toPriceInfo")
    Product toDomain(UpdateProductDTO dto);

    /**
     * Extrae la categoría del DTO como enum.
     */
    default Category map(String category) {
        if (category == null) return null;
        return Category.valueOf(category.trim().toUpperCase());
    }

    /**
     * Convierte BigDecimal a PriceInfo sin descuento.
     */
    @Named("toPriceInfo")
    default PriceInfo toPriceInfo(BigDecimal originalPrice) {
        if (originalPrice == null) return null;
        return PriceInfo.withoutDiscount(originalPrice);
    }

    /**
     * Convierte Product a ProductResponse.
     */
    @Mapping(source = "priceInfo.originalPrice", target = "originalPrice")
    @Mapping(source = "priceInfo.discountPercent", target = "discountPercent")
    @Mapping(source = "priceInfo.finalPrice", target = "finalPrice")
    ProductResponse toResponse(Product domain);
}
