package com.logitrack.api.domain.shippingorder;

import com.logitrack.api.domain.vehicle.Vehicle;
import com.logitrack.api.exception.BusinessException;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "shipping_orders")
public class ShippingOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id", nullable = false)
    @NotNull(message = "Vehicle is required")
    private Vehicle vehicle;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Status is required")
    private ShippingOrderStatus status = ShippingOrderStatus.PENDING;

    @Column(name = "total_weight")
    private Double totalWeight = 0.0;

    @Column(name = "total_cubic_volume")
    private Double totalCubicVolume = 0.0;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @OneToMany(mappedBy = "shippingOrder", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ShippingOrderItem> items = new ArrayList<>();

    protected ShippingOrder() {}

    public ShippingOrder(Vehicle vehicle) {
        this.vehicle = Objects.requireNonNull(vehicle, "Vehicle cannot be null");
    }

    // --- REGRAS DE NEGÓCIO DE DOMÍNIO ---

    public void addItem(ShippingOrderItem item) {
        if (this.status != ShippingOrderStatus.PENDING) {
            throw new BusinessException("Cannot add items to an order that is not pending.");
        }

        Objects.requireNonNull(item, "Item cannot be null");
        this.items.add(item);
        item.setShippingOrder(this);

        recalculateLoad();
        validateVehicleCapacity();
    }

    private void recalculateLoad() {
        this.totalWeight = this.items.stream()
                .mapToDouble(ShippingOrderItem::calculateTotalWeight)
                .sum();

        this.totalCubicVolume = this.items.stream()
                .mapToDouble(ShippingOrderItem::calculateTotalCubicVolume)
                .sum();
    }

    private void validateVehicleCapacity() {
        if (this.vehicle != null) {
            if (this.totalWeight > this.vehicle.getMaxWeightCapacity()) {
                throw new BusinessException(String.format(
                        "Weight capacity exceeded. Max: %.2f kg | Current: %.2f kg",
                        this.vehicle.getMaxWeightCapacity(), this.totalWeight));
            }
            if (this.totalCubicVolume > this.vehicle.getMaxCubicVolumeCapacity()) {
                throw new BusinessException(String.format(
                        "Cubic volume capacity exceeded. Max: %.2f m³ | Current: %.2f m³",
                        this.vehicle.getMaxCubicVolumeCapacity(), this.totalCubicVolume));
            }
        }
    }

    public void confirm() {
        if (this.status != ShippingOrderStatus.PENDING) {
            throw new BusinessException("Only PENDING orders can be confirmed.");
        }
        if (this.items.isEmpty()) {
            throw new BusinessException("Cannot confirm an order without items.");
        }
        this.status = ShippingOrderStatus.APPROVED;
    }

    public void cancel() {
        if (this.status == ShippingOrderStatus.DELIVERED) {
            throw new BusinessException("Cannot cancel an order that has already been delivered.");
        }
        this.status = ShippingOrderStatus.CANCELLED;
    }

    public void approve() {
        if (this.status == ShippingOrderStatus.CANCELLED) {
            throw new BusinessException("Cannot approve a cancelled order.");
        }
        this.status = ShippingOrderStatus.APPROVED;
    }

    // --- GETTERS E SETTERS SEGUROS ---

    public Long getId() {
        return id;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = Objects.requireNonNull(vehicle, "Vehicle cannot be null");
        validateVehicleCapacity();
    }

    public ShippingOrderStatus getStatus() {
        return status;
    }

    public Double getTotalWeight() {
        return totalWeight;
    }

    public Double getTotalCubicVolume() {
        return totalCubicVolume;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public List<ShippingOrderItem> getItems() {
        return Collections.unmodifiableList(items);
    }
}