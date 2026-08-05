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
    @Column(unique = true, nullable = false)
    private String plate;

    @NotBlank(message = "O modelo do veículo é obrigatório")
    @Column(nullable = false)
    private String model;

    @NotNull(message = "A capacidade máxima de carga é obrigatória")
    @Positive(message = "A capacidade de carga deve ser um valor maior que zero")
    @Column(name = "max_capacity_kg", nullable = false)
    private Double maxCapacityKg;

    @NotNull(message = "A capacidade volumétrica máxima é obrigatória")
    @Positive(message = "A capacidade volumétrica máxima deve ser maior que zero")
    @Column(name = "max_cubic_volume", nullable = false)
    private Double maxCubicVolume;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VehicleStatus status = VehicleStatus.AVAILABLE;

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
        this.status = VehicleStatus.AVAILABLE;
    }

    // --- MÉTODOS E REGRAS DE NEGÓCIO DE DOMÍNIO (TMS) ---

    public boolean isAvailable() {
        return this.status == VehicleStatus.AVAILABLE;
    }

    public void assignToRoute() {
        if (!isAvailable()) {
            throw new IllegalStateException("Vehicle " + (id != null ? id : plate) + " is not available for dispatch. Current status: " + status);
        }
        this.status = VehicleStatus.ASSIGNED;
    }

    public void releaseVehicle() {
        this.status = VehicleStatus.AVAILABLE;
    }

    /**
     * Aliases semânticos com proteção contra NullPointerException
     */
    public Double getMaxWeightCapacity() {
        return this.maxCapacityKg != null ? this.maxCapacityKg : 0.0;
    }

    public Double getMaxCubicVolumeCapacity() {
        return this.maxCubicVolume != null ? this.maxCubicVolume : 0.0;
    }

    // --- GETTERS E SETTERS TRADICIONAIS ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPlate() {
        return plate;
    }

    public void setPlate(String plate) {
        this.plate = plate;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public Double getMaxCapacityKg() {
        return getMaxWeightCapacity();
    }

    public void setMaxCapacityKg(Double maxCapacityKg) {
        this.maxCapacityKg = maxCapacityKg != null ? maxCapacityKg : 0.0;
    }

    public Double getMaxCubicVolume() {
        return getMaxCubicVolumeCapacity();
    }

    public void setMaxCubicVolume(Double maxCubicVolume) {
        this.maxCubicVolume = maxCubicVolume != null ? maxCubicVolume : 0.0;
    }

    public VehicleStatus getStatus() {
        return status;
    }

    public void setStatus(VehicleStatus status) {
        this.status = status != null ? status : VehicleStatus.AVAILABLE;
    }
}