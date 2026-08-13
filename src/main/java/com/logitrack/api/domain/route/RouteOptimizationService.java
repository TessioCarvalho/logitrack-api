package com.logitrack.api.domain.route;

import com.logitrack.api.domain.shippingorder.ShippingOrder;
import com.logitrack.api.domain.shippingorder.ShippingOrderStatus;
import com.logitrack.api.domain.vehicle.Vehicle;
import com.logitrack.api.domain.vehicle.VehicleRepository;
import com.logitrack.api.exception.BusinessException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class RouteOptimizationService {

    private final VehicleRepository vehicleRepository;

    public RouteOptimizationService(VehicleRepository vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }

    /**
     * Generates an optimized DeliveryRoute for a list of pending shipping orders using a Best-Fit heuristic.
     * Selects the optimal available vehicle based on cumulative payload/volume and sequences orders.
     */
    public DeliveryRoute createOptimizedRoute(List<ShippingOrder> pendingOrders) {
        if (pendingOrders == null || pendingOrders.isEmpty()) {
            throw new BusinessException("Pending orders list cannot be null or empty for route generation.");
        }

        // 1. Enforce that all orders are in PENDING status prior to routing
        boolean hasInvalidStatus = pendingOrders.stream()
                .anyMatch(order -> order.getStatus() != ShippingOrderStatus.PENDING);
        if (hasInvalidStatus) {
            throw new BusinessException("Only orders with PENDING status can be routed.");
        }

        // 2. Calculate aggregated total payload weight and cubic volume
        double totalWeightNeeded = pendingOrders.stream().mapToDouble(ShippingOrder::getTotalWeightKg).sum();
        double totalVolumeNeeded = pendingOrders.stream().mapToDouble(ShippingOrder::getTotalVolumeM3).sum();

        // 3. Retrieve available vehicles and select the smallest suitable vehicle (Best-Fit strategy)
        List<Vehicle> availableVehicles = vehicleRepository.findAllAvailable();

        Vehicle selectedVehicle = availableVehicles.stream()
                .filter(v -> v.getMaxWeightCapacity() >= totalWeightNeeded && v.getMaxCubicVolumeCapacity() >= totalVolumeNeeded)
                .min(Comparator.comparingDouble(Vehicle::getMaxWeightCapacity))
                .orElseThrow(() -> new BusinessException(String.format(
                        "No available vehicle with sufficient capacity found for route. Required: %.2f kg, %.2f m³",
                        totalWeightNeeded, totalVolumeNeeded)));

        // 4. Instantiate the DeliveryRoute aggregate (marks vehicle as ASSIGNED)
        DeliveryRoute route = new DeliveryRoute(selectedVehicle);

        // 5. Sequence orders by cubic volume descending for optimal physical load efficiency
        List<ShippingOrder> sortedOrders = new ArrayList<>(pendingOrders);
        sortedOrders.sort(Comparator.comparingDouble(ShippingOrder::getTotalVolumeM3).reversed());

        int sequence = 1;
        for (ShippingOrder order : sortedOrders) {
            route.addOrder(order, sequence++);
        }

        return route;
    }
}