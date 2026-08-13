package com.logitrack.api.domain.route;

import com.logitrack.api.domain.shippingorder.ShippingOrder;
import com.logitrack.api.domain.shippingorder.ShippingOrderItem;
import com.logitrack.api.domain.shippingorder.ShippingOrderStatus;
import com.logitrack.api.domain.product.Product;
import com.logitrack.api.domain.vehicle.Vehicle;
import com.logitrack.api.domain.vehicle.VehicleRepository;
import com.logitrack.api.domain.vehicle.VehicleStatus;
import com.logitrack.api.exception.BusinessException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RouteOptimizationServiceTest {

    @Mock
    private VehicleRepository vehicleRepository;

    @InjectMocks
    private RouteOptimizationService routeOptimizationService;

    @Test
    @DisplayName("Should select optimal vehicle using Best-Fit strategy and sequence orders correctly")
    void createOptimizedRoute_BestFitSuccess() {
        // Arrange: Carga necessária -> Peso Total: 300kg, Volume Total: 3.0m³
        Product productA = new Product("Cadeira", 100.0, 1.0, 10);
        Product productB = new Product("Mesa", 200.0, 2.0, 10);

        Vehicle dummyVehicle = new Vehicle("Dummy", "AAA-0000", 5000.0, 50.0);

        ShippingOrder order1 = new ShippingOrder(dummyVehicle);
        order1.addItem(new ShippingOrderItem(productA, 1, "Cliente A")); // 100kg, 1.0m³

        ShippingOrder order2 = new ShippingOrder(dummyVehicle);
        order2.addItem(new ShippingOrderItem(productB, 1, "Cliente B")); // 200kg, 2.0m³

        // Frota disponível no pátio
        Vehicle van = new Vehicle(1L, "VAN-100", "Fiorino", 500.0, 4.0);     // Cabe! (Menor veículo que atende)
        Vehicle truck = new Vehicle(2L, "TRK-200", "Caminhão", 5000.0, 30.0); // Cabe, mas é grande demais
        Vehicle motorcycle = new Vehicle(3L, "MOTO-1", "Moto", 50.0, 0.5);   // Não cabe

        when(vehicleRepository.findAllAvailable()).thenReturn(List.of(truck, van, motorcycle));

        // Act
        DeliveryRoute route = routeOptimizationService.createOptimizedRoute(List.of(order1, order2));

        // Assert
        assertThat(route).isNotNull();
        // 1. Deve selecionar a Van pelo algoritmo Best-Fit (capacidade útil mais próxima da carga)
        assertThat(route.getVehicle().getId()).isEqualTo(1L);
        assertThat(route.getVehicle().getStatus()).isEqualTo(VehicleStatus.ASSIGNED);

        // 2. Os totais da rota devem bater com a soma das ordens
        assertThat(route.getTotalWeightKg()).isEqualTo(300.0);
        assertThat(route.getTotalVolumeM3()).isEqualTo(3.0);

        // 3. As ordens foram sequenciadas e tiveram o status atualizado para ROUTED
        assertThat(route.getShippingOrders()).hasSize(2);
        assertThat(order1.getStatus()).isEqualTo(ShippingOrderStatus.ROUTED);
        assertThat(order2.getStatus()).isEqualTo(ShippingOrderStatus.ROUTED);

        // 4. Sequenciamento por volume descendente (order2 tem 2.0m³, order1 tem 1.0m³)
        assertThat(route.getShippingOrders().get(0)).isEqualTo(order2);
        assertThat(route.getShippingOrders().get(0).getDeliverySequence()).isEqualTo(1);
        assertThat(route.getShippingOrders().get(1)).isEqualTo(order1);
        assertThat(route.getShippingOrders().get(1).getDeliverySequence()).isEqualTo(2);
    }

    @Test
    @DisplayName("Should throw BusinessException when no available vehicle can carry the payload")
    void createOptimizedRoute_NoVehicleAvailable() {
        // Carga pesada -> 1000kg, 10.0m³
        Product heavyProduct = new Product("Carga Pesada", 1000.0, 10.0, 5);
        Vehicle dummyVehicle = new Vehicle("Dummy", "AAA-0000", 5000.0, 50.0);

        ShippingOrder order = new ShippingOrder(dummyVehicle);
        order.addItem(new ShippingOrderItem(heavyProduct, 1, "Cliente C"));

        // Apenas uma van pequena disponível
        Vehicle smallVan = new Vehicle(1L, "VAN-100", "Fiorino", 500.0, 4.0);
        when(vehicleRepository.findAllAvailable()).thenReturn(List.of(smallVan));

        assertThatThrownBy(() -> routeOptimizationService.createOptimizedRoute(List.of(order)))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("No available vehicle with sufficient capacity found for route");
    }

    @Test
    @DisplayName("Should throw BusinessException when order list contains non-PENDING orders")
    void createOptimizedRoute_InvalidOrderStatus() {
        Vehicle dummyVehicle = new Vehicle("Dummy", "AAA-0000", 5000.0, 50.0);
        ShippingOrder order = new ShippingOrder(dummyVehicle);

        // Força transição antecipada do status
        order.transitionStatus(ShippingOrderStatus.ROUTED);

        assertThatThrownBy(() -> routeOptimizationService.createOptimizedRoute(List.of(order)))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Only orders with PENDING status can be routed.");
    }
}