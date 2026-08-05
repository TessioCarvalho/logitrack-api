package com.logitrack.api.domain.route;

import com.logitrack.api.domain.shippingorder.ShippingOrder;
import com.logitrack.api.domain.shippingorder.ShippingOrderStatus;
import com.logitrack.api.domain.vehicle.Vehicle;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
@Table(name = "delivery_routes")
public class DeliveryRoute {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;

    @OneToMany(mappedBy = "deliveryRoute", cascade = CascadeType.ALL)
    @OrderBy("deliverySequence ASC")
    private final List<ShippingOrder> shippingOrders = new ArrayList<>();

    @Column(name = "total_weight_kg", nullable = false)
    private Double totalWeightKg = 0.0;

    @Column(name = "total_volume_m3", nullable = false)
    private Double totalVolumeM3 = 0.0;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    protected DeliveryRoute() {
        // JPA Required
    }

    public DeliveryRoute(Vehicle vehicle) {
        if (vehicle == null) {
            throw new IllegalArgumentException("Vehicle cannot be null");
        }
        this.vehicle = vehicle;
        this.createdAt = LocalDateTime.now();
        this.vehicle.assignToRoute();
    }

    public void addOrder(ShippingOrder order, int sequence) {
        if (order == null) {
            throw new IllegalArgumentException("Order cannot be null");
        }

        double newWeight = this.totalWeightKg + order.getTotalWeightKg();
        double newVolume = this.totalVolumeM3 + order.getTotalVolumeM3();

        if (newWeight > vehicle.getMaxWeightCapacity()) {
            throw new IllegalArgumentException("Route weight limit exceeded for vehicle " + vehicle.getId());
        }
        if (newVolume > vehicle.getMaxCubicVolumeCapacity()) {
            throw new IllegalArgumentException("Route volume limit exceeded for vehicle " + vehicle.getId());
        }

        order.setDeliveryRoute(this);
        order.setDeliverySequence(sequence);
        order.transitionStatus(ShippingOrderStatus.ROUTED);

        this.shippingOrders.add(order);
        this.totalWeightKg = newWeight;
        this.totalVolumeM3 = newVolume;
    }

    // --- GETTERS SEGUROS ---

    public Long getId() {
        return id;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public List<ShippingOrder> getShippingOrders() {
        return Collections.unmodifiableList(shippingOrders);
    }

    public Double getTotalWeightKg() {
        return totalWeightKg;
    }

    public Double getTotalVolumeM3() {
        return totalVolumeM3;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}