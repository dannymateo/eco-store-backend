package com.itm.eco_store.domain.model;

import java.math.BigDecimal;
import java.util.Objects;

import lombok.AllArgsConstructor;
import lombok.Builder;

@Builder(toBuilder = true)
@AllArgsConstructor
public class Product {

    private final Long id;
    private final String name;
    private final String description;
    private final Category category;
    private final PriceInfo priceInfo;

    /**
     * Factory method para crear un producto.
     * El porcentaje de descuento se obtiene externamente (via DiscountRepository).
     */
    public static Product create(String name, String description, Category category,
                                 BigDecimal originalPrice, BigDecimal discountPercent) {
        BigDecimal discountPct = discountPercent != null ? discountPercent : BigDecimal.ZERO;
        PriceInfo info = discountPct.compareTo(BigDecimal.ZERO) > 0
                ? PriceInfo.withDiscount(originalPrice, discountPct)
                : PriceInfo.withoutDiscount(originalPrice);
        return Product.builder()
                .name(name)
                .description(description)
                .category(category)
                .priceInfo(info)
                .build();
    }

    /**
     * Factory method para actualizar un producto existente.
     * El porcentaje de descuento se obtiene externamente (via DiscountRepository).
     */
    public Product updateWith(String name, String description, Category category,
                              BigDecimal originalPrice, BigDecimal discountPercent) {
        BigDecimal discountPct = discountPercent != null ? discountPercent : BigDecimal.ZERO;
        PriceInfo info = discountPct.compareTo(BigDecimal.ZERO) > 0
                ? PriceInfo.withDiscount(originalPrice, discountPct)
                : PriceInfo.withoutDiscount(originalPrice);
        return this.toBuilder()
                .name(name)
                .description(description)
                .category(category)
                .priceInfo(info)
                .build();
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public Category getCategory() { return category; }
    public PriceInfo getPriceInfo() { return priceInfo; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return Objects.equals(id, product.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}