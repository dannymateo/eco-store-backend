package com.itm.eco_store.domain.model;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Entidad de dominio: producto del catálogo.
 * Encapsula la regla de negocio: aplicar descuento cuando la categoría es TEMPORADA_PASADA.
 */
public class Product {

    private Long id;
    private String name;
    private String description;
    private Category category;
    private PriceInfo priceInfo;

    public Product(Long id, String name, String description, Category category, PriceInfo priceInfo) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.category = category;
        this.priceInfo = priceInfo;
    }

    /**
     * Crea un producto nuevo (sin id) con precio inicial. Si la categoría es TEMPORADA_PASADA,
     * aplica el descuento indicado.
     */
    public static Product create(String name, String description, Category category,
                                BigDecimal originalPrice, BigDecimal seasonDiscountPercent) {
        PriceInfo info;
        if (category != null && category.requiresSeasonDiscount() && seasonDiscountPercent != null
                && seasonDiscountPercent.compareTo(BigDecimal.ZERO) > 0) {
            info = PriceInfo.withDiscount(originalPrice, seasonDiscountPercent);
        } else {
            info = PriceInfo.withoutDiscount(originalPrice);
        }
        return new Product(null, name, description, category, info);
    }

    /**
     * Aplica el descuento de temporada pasada al precio actual.
     * Solo tiene efecto si la categoría es TEMPORADA_PASADA.
     *
     * @param discountPercent porcentaje de descuento (0-100)
     */
    public void applySeasonDiscount(BigDecimal discountPercent) {
        if (category != null && category.requiresSeasonDiscount() && discountPercent != null
                && discountPercent.compareTo(BigDecimal.ZERO) > 0) {
            this.priceInfo = PriceInfo.withDiscount(priceInfo.getOriginalPrice(), discountPercent);
        }
    }

    /**
     * Actualiza datos editables (nombre, descripción, categoría, precio original).
     * Si la categoría es TEMPORADA_PASADA, aplica el descuento indicado; si no, sin descuento.
     */
    public void update(String name, String description, Category category,
                       BigDecimal originalPrice, BigDecimal seasonDiscountPercent) {
        this.name = name;
        this.description = description;
        this.category = category;
        if (category != null && category.requiresSeasonDiscount() && seasonDiscountPercent != null
                && seasonDiscountPercent.compareTo(BigDecimal.ZERO) > 0) {
            this.priceInfo = PriceInfo.withDiscount(originalPrice, seasonDiscountPercent);
        } else {
            this.priceInfo = PriceInfo.withoutDiscount(originalPrice);
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Category getCategory() {
        return category;
    }

    public PriceInfo getPriceInfo() {
        return priceInfo;
    }

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
