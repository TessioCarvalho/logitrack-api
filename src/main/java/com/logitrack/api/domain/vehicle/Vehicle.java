package com.logitrack.api.domain.vehicle;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

@Entity
@Table(name = "vehicles")
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "A placa do veículo é obrigatória")
    @Column(unique = true)
    private String plate;

    @NotBlank(message = "O modelo do veículo é obrigatório")
    private String model;

    @NotNull(message = "A capacidade máxima de carga é obrigatória")
    @Positive(message = "A capacidade de carga deve ser um valor maior que zero")
    @Column(name = "max_capacity_kg")
    private BigDecimal maxCapacityKg;

    // Construtor padrão exigido pelo Hibernate
    public Vehicle() {}

    public Vehicle(String plate, String model, BigDecimal maxCapacityKg) {
        this.plate = plate;
        this.model = model;
        this.maxCapacityKg = maxCapacityKg;
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getPlate() { return plate; }
    public void setPlate(String plate) { this.plate = plate; }

    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    public BigDecimal getMaxCapacityKg() { return maxCapacityKg; }
    public void setMaxCapacityKg(BigDecimal maxCapacityKg) { this.maxCapacityKg = maxCapacityKg; }
}