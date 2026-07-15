package com.logitrack.api.domain.shippingorder;

import com.logitrack.api.domain.vehicle.Vehicle;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "shipping_orders")
public class ShippingOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id", nullable = false)
    @NotNull(message = "O veículo é obrigatório")
    private Vehicle vehicle;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "O status da ordem é obrigatório")
    private ShippingOrderStatus status = ShippingOrderStatus.PENDING;

    @Column(name = "total_weight")
    private Double totalWeight = 0.0;

    @Column(name = "total_cubic_volume")
    private Double totalCubicVolume = 0.0;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @OneToMany(mappedBy = "shippingOrder", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ShippingOrderItem> items = new ArrayList<>();

    public ShippingOrder() {}

    // Método utilitário para adicionar itens garantindo a consistência bidirecional
    public void addItem(ShippingOrderItem item) {
        items.add(item);
        item.setShippingOrder(this);
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Vehicle getVehicle() { return vehicle; }
    public void setVehicle(Vehicle vehicle) { this.vehicle = vehicle; }

    public ShippingOrderStatus getStatus() { return status; }
    public void setStatus(ShippingOrderStatus status) { this.status = status; }

    public Double getTotalWeight() { return totalWeight; }
    public void setTotalWeight(Double totalWeight) { this.totalWeight = totalWeight; }

    public Double getTotalCubicVolume() { return totalCubicVolume; }
    public void setTotalCubicVolume(Double totalCubicVolume) { this.totalCubicVolume = totalCubicVolume; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public List<ShippingOrderItem> getItems() { return items; }
    public void setItems(List<ShippingOrderItem> items) { this.items = items; }
}