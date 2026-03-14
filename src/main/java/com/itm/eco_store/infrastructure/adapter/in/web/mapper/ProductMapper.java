package com.itm.eco_store.infrastructure.adapter.in.web.mapper;

import com.itm.eco_store.domain.model.Product;
import com.itm.eco_store.infrastructure.adapter.in.web.dto.CreateProductDTO;
import com.itm.eco_store.infrastructure.adapter.in.web.dto.ProductResponse;
import com.itm.eco_store.infrastructure.adapter.in.web.dto.UpdateProductDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    /**
     * Construye el dominio; el descuento se obtiene de la categoría (Category.getDiscountPercent).
     */
    default Product toDomain(CreateProductDTO dto) {
        return Product.create(
                dto.getName(),
                dto.getDescription(),
                dto.getCategory(),
                dto.getOriginalPrice()
        );
    }

    /**
     * Construye el dominio para actualización; el descuento viene de la categoría.
     */
    default Product toDomain(UpdateProductDTO dto) {
        return Product.create(
                dto.getName(),
                dto.getDescription(),
                dto.getCategory(),
                dto.getOriginalPrice()
        );
    }

    @Mapping(source = "priceInfo.originalPrice", target = "originalPrice")
    @Mapping(source = "priceInfo.discountPercent", target = "discountPercent")
    @Mapping(source = "priceInfo.finalPrice", target = "finalPrice")
    ProductResponse toResponse(Product domain);
}
