package com.logitrack.api.domain.shippingorder;

import com.logitrack.api.domain.route.DeliveryRoute;
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
    @Column(nullable = false)
    private ShippingOrderStatus status = ShippingOrderStatus.PENDING;

    @Column(name = "total_weight")
    private Double totalWeight = 0.0;

    @Column(name = "total_cubic_volume")
    private Double totalCubicVolume = 0.0;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @OneToMany(mappedBy = "shippingOrder", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ShippingOrderItem> items = new ArrayList<>();

    // --- RELACIONAMENTO COM A ROTA DE ENTREGA (TMS) ---
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "delivery_route_id")
    private DeliveryRoute deliveryRoute;

    @Column(name = "delivery_sequence")
    private Integer deliverySequence;

    protected ShippingOrder() {}

    public ShippingOrder(Vehicle vehicle) {
        this.vehicle = Objects.requireNonNull(vehicle, "Vehicle cannot be null");
    }

    // --- REGRAS DE DOMÍNIO E OPERAÇÕES ---

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

    public void recalculateLoad() {
        if (this.items == null || this.items.isEmpty()) {
            this.totalWeight = 0.0;
            this.totalCubicVolume = 0.0;
            return;
        }

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

    /**
     * Valida e executa transições estritas do ciclo de vida da ordem no TMS.
     */
    public void transitionStatus(ShippingOrderStatus newStatus) {
        Objects.requireNonNull(newStatus, "New status cannot be null");
        if (!this.status.canTransitionTo(newStatus)) {
            throw new BusinessException(
                    String.format("Cannot transition shipping order %s from %s to %s",
                            id != null ? id.toString() : "[New]", status, newStatus)
            );
        }
        this.status = newStatus;
    }

    public void cancel() {
        transitionStatus(ShippingOrderStatus.CANCELLED);
    }

    // --- GETTERS, SETTERS E ALIASES DE DOMÍNIO ---

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
        return totalWeight != null ? totalWeight : 0.0;
    }

    public Double getTotalCubicVolume() {
        return totalCubicVolume != null ? totalCubicVolume : 0.0;
    }

    // Aliases semânticos exigidos pela DeliveryRoute
    public Double getTotalWeightKg() {
        return getTotalWeight();
    }

    public Double getTotalVolumeM3() {
        return getTotalCubicVolume();
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public List<ShippingOrderItem> getItems() {
        return Collections.unmodifiableList(items);
    }

    public DeliveryRoute getDeliveryRoute() {
        return deliveryRoute;
    }

    public void setDeliveryRoute(DeliveryRoute deliveryRoute) {
        this.deliveryRoute = deliveryRoute;
    }

    public Integer getDeliverySequence() {
        return deliverySequence;
    }

    public void setDeliverySequence(Integer deliverySequence) {
        this.deliverySequence = deliverySequence;
    }
}