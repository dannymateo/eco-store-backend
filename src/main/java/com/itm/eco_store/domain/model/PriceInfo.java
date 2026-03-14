package com.itm.eco_store.domain.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * Value Object: trazabilidad de precios (original, % descuento, final).
 */
public final class PriceInfo {

    private static final int SCALE = 2;
    private static final RoundingMode ROUNDING = RoundingMode.HALF_UP;

    private final BigDecimal originalPrice;
    private final BigDecimal discountPercent;
    private final BigDecimal finalPrice;

    public static PriceInfo withoutDiscount(BigDecimal originalPrice) {
        if (originalPrice == null || originalPrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("El precio original debe ser >= 0");
        }
        BigDecimal orig = originalPrice.setScale(SCALE, ROUNDING);
        return new PriceInfo(orig, BigDecimal.ZERO.setScale(SCALE, ROUNDING), orig);
    }

    public static PriceInfo withDiscount(BigDecimal originalPrice, BigDecimal discountPercent) {
        if (originalPrice == null || originalPrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("El precio original debe ser >= 0");
        }
        if (discountPercent == null || discountPercent.compareTo(BigDecimal.ZERO) < 0
                || discountPercent.compareTo(BigDecimal.valueOf(100)) > 0) {
            throw new IllegalArgumentException("El descuento debe estar entre 0 y 100");
        }
        BigDecimal orig = originalPrice.setScale(SCALE, ROUNDING);
        BigDecimal pct = discountPercent.setScale(SCALE, ROUNDING);
        BigDecimal multiplier = BigDecimal.ONE.subtract(pct.divide(BigDecimal.valueOf(100), 4, ROUNDING));
        BigDecimal finalP = orig.multiply(multiplier).setScale(SCALE, ROUNDING);
        return new PriceInfo(orig, pct, finalP);
    }

    public PriceInfo(BigDecimal originalPrice, BigDecimal discountPercent, BigDecimal finalPrice) {
        this.originalPrice = Objects.requireNonNull(originalPrice).setScale(SCALE, ROUNDING);
        this.discountPercent = Objects.requireNonNull(discountPercent).setScale(SCALE, ROUNDING);
        this.finalPrice = Objects.requireNonNull(finalPrice).setScale(SCALE, ROUNDING);
        if (this.originalPrice.compareTo(BigDecimal.ZERO) < 0
                || this.discountPercent.compareTo(BigDecimal.ZERO) < 0
                || this.discountPercent.compareTo(BigDecimal.valueOf(100)) > 0) {
            throw new IllegalArgumentException("Precio o descuento inválido");
        }
    }

    public BigDecimal getOriginalPrice() { return originalPrice; }
    public BigDecimal getDiscountPercent() { return discountPercent; }
    public BigDecimal getFinalPrice() { return finalPrice; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PriceInfo that = (PriceInfo) o;
        return originalPrice.compareTo(that.originalPrice) == 0
                && discountPercent.compareTo(that.discountPercent) == 0
                && finalPrice.compareTo(that.finalPrice) == 0;
    }

    @Override
    public int hashCode() { return Objects.hash(originalPrice, discountPercent, finalPrice); }
}
