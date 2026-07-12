package com.logitrack.api.domain.product;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Positive;

@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "O nome do produto é obrigatório.")
    private String name;

    @NotBlank(message = "O SKU (código único) é obrigatório.")
    @Column(unique = true)
    private String sku;

    @NotNull(message = "O peso do produto é obrigatório.")
    @PositiveOrZero(message = "O peso não pode ser negativo.")
    private Double weight; // peso em kg

    @NotNull(message = "A quantidade em estoque é obrigatória.")
    @PositiveOrZero(message = "A quantidade não pode ser negativa.")
    @Column(name = "quantity_in_stock")
    private Integer quantityInStock;

    @NotNull(message = "O estoque mínimo é obrigatório.")
    @PositiveOrZero(message = "O estoque mínimo não pode ser negativo.")
    @Column(name = "minimum_stock")
    private Integer minimumStock;

    // Novo campo técnico de cubagem para a distribuidora de pneus
    @NotNull(message = "O volume cúbico é obrigatório.")
    @Positive(message = "O volume cúbico deve ser maior que zero.")
    @Column(name = "cubic_volume")
    private Double cubicVolume; // volume em m³

    // --- CONSTRUTORES ---
    public Product() {
    }

    public Product(Long id, String name, String sku, Double weight, Integer quantityInStock, Integer minimumStock, Double cubicVolume) {
        this.id = id;
        this.name = name;
        this.sku = sku;
        this.weight = weight;
        this.quantityInStock = quantityInStock;
        this.minimumStock = minimumStock;
        this.cubicVolume = cubicVolume;
    }

    // --- GETTERS E SETTERS ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }

    public Double getWeight() { return weight; }
    public void setWeight(Double weight) { this.weight = weight; }

    public Integer getQuantityInStock() { return quantityInStock; }
    public void setQuantityInStock(Integer quantityInStock) { this.quantityInStock = quantityInStock; }

    public Integer getMinimumStock() { return minimumStock; }
    public void setMinimumStock(Integer minimumStock) { this.minimumStock = minimumStock; }

    public Double getCubicVolume() { return cubicVolume; }
    public void setCubicVolume(Double cubicVolume) { this.cubicVolume = cubicVolume; }
}