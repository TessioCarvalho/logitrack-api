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

    @NotNull(message = "A capacidade volumétrica máxima é obrigatória")
    @Positive(message = "A capacidade volumétrica máxima deve ser maior que zero")
    @Column(name = "max_cubic_volume")
    private Double maxCubicVolume;

    // Construtor sem argumentos EXIGIDO pelo JPA/Hibernate
    protected Vehicle() {
    }

    /**
     * Construtor para testes unitários e instanciação rápida sem ID.
     */
    public Vehicle(String model, String plate, Double maxCapacityKg, Double maxCubicVolume) {
        this(null, plate, model, maxCapacityKg, maxCubicVolume);
    }

    /**
     * Construtor completo do Domínio.
     */
    public Vehicle(Long id, String plate, String model, Double maxCapacityKg, Double maxCubicVolume) {
        this.id = id;
        this.plate = plate;
        this.model = model;
        this.maxCapacityKg = maxCapacityKg != null ? maxCapacityKg : 0.0;
        this.maxCubicVolume = maxCubicVolume != null ? maxCubicVolume : 0.0;
    }

    // --- ALINHAMENTO COM AS REGRAS DE NEGÓCIO DO TMS ---

    /**
     * Aliases semânticos para expor as capacidades do veículo ao validador da ShippingOrder
     */
    public Double getMaxWeightCapacity() {
        return this.maxCapacityKg != null ? this.maxCapacityKg : 0.0;
    }

    public Double getMaxCubicVolumeCapacity() {
        return this.maxCubicVolume != null ? this.maxCubicVolume : 0.0;
    }

    // --- GETTERS E SETTERS TRADICIONAIS ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getPlate() { return plate; }
    public void setPlate(String plate) { this.plate = plate; }

    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    public Double getMaxCapacityKg() { return getMaxWeightCapacity(); }
    public void setMaxCapacityKg(Double maxCapacityKg) { this.maxCapacityKg = maxCapacityKg; }

    public Double getMaxCubicVolume() { return getMaxCubicVolumeCapacity(); }
    public void setMaxCubicVolume(Double maxCubicVolume) { this.maxCubicVolume = maxCubicVolume; }
}