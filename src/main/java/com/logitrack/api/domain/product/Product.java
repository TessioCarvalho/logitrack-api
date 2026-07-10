package com.logitrack.api.domain.product;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

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

    // --- CONSTRUTORES (Padrão e Completo) ---
    public Product() {
    }

    public Product(Long id, String name, String sku, Double weight, Integer quantityInStock, Integer minimumStock) {
        this.id = id;
        this.name = name;
        this.sku = sku;
        this.weight = weight;
        this.quantityInStock = quantityInStock;
        this.minimumStock = minimumStock;
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
}