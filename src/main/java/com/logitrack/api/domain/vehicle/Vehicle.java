package com.logitrack.api.domain.vehicle;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

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
    private Double maxCapacityKg;

    // Novo campo técnico de capacidade de volumétrica para o baú do veículo
    @NotNull(message = "A capacidade volumétrica máxima é obrigatória")
    @Positive(message = "A capacidade volumétrica máxima deve ser maior que zero")
    @Column(name = "max_cubic_volume")
    private Double maxCubicVolume; // capacidade total em m³

    // Construtor padrão exigido pelo Hibernate
    public Vehicle() {}

    public Vehicle(Long id, String plate, String model, Double maxCapacityKg, Double maxCubicVolume) {
        this.id = id;
        this.plate = plate;
        this.model = model;
        this.maxCapacityKg = maxCapacityKg;
        this.maxCubicVolume = maxCubicVolume;
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getPlate() { return plate; }
    public void setPlate(String plate) { this.plate = plate; }

    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    public Double getMaxCapacityKg() { return maxCapacityKg; }
    public void setMaxCapacityKg(Double maxCapacityKg) { this.maxCapacityKg = maxCapacityKg; }

    public Double getMaxCubicVolume() { return maxCubicVolume; }
    public void setMaxCubicVolume(Double maxCubicVolume) { this.maxCubicVolume = maxCubicVolume; }
}