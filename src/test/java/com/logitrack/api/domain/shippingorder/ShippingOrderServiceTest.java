package com.logitrack.api.domain.shippingorder;

import com.logitrack.api.domain.product.Product;
import com.logitrack.api.domain.product.ProductRepository;
import com.logitrack.api.domain.product.event.LowStockEvent;
import com.logitrack.api.domain.shippingorder.dto.ShippingOrderItemRequest;
import com.logitrack.api.domain.shippingorder.dto.ShippingOrderRequest;
import com.logitrack.api.domain.vehicle.Vehicle;
import com.logitrack.api.domain.vehicle.VehicleRepository;
import com.logitrack.api.exception.BusinessException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ShippingOrderServiceTest {

    @Mock
    private ShippingOrderRepository shippingOrderRepository;

    @Mock
    private VehicleRepository vehicleRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ShippingOrderService shippingOrderService;

    @Test
    @DisplayName("Should create shipping order successfully and decrease product stock")
    void createShippingOrder_Success() {
        // Arrange
        Vehicle vehicle = new Vehicle(1L, "Scania R 450", "ABC-1234", 24000.0, 100.0);
        Product product = new Product(1L, "Pneu Aptany", "APT123", 10.0, 100, 10, 0.1); // 10kg, 0.1m³

        ShippingOrderItemRequest itemRequest = new ShippingOrderItemRequest(1L, 5, "Client A");
        ShippingOrderRequest request = new ShippingOrderRequest(1L, List.of(itemRequest));

        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(vehicle));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(shippingOrderRepository.save(any(ShippingOrder.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        ShippingOrder order = shippingOrderService.createShippingOrder(request);

        // Assert
        assertNotNull(order);
        assertEquals(ShippingOrderStatus.APPROVED, order.getStatus());
        assertEquals(50.0, order.getTotalWeight()); // 10kg * 5
        assertEquals(0.5, order.getTotalCubicVolume()); // 0.1m³ * 5
        assertEquals(95, product.getQuantityInStock(), "Stock should have decreased by 5");

        verify(shippingOrderRepository, times(1)).save(any(ShippingOrder.class));
    }

    @Test
    @DisplayName("Should throw BusinessException when vehicle capacity is exceeded")
    void createShippingOrder_WeightLimitExceeded() {
        // Arrange
        Vehicle vehicle = new Vehicle(1L, "Fiorino", "FIO-1234", 650.0, 3.0); // Limite de 650kg
        Product heavyProduct = new Product(1L, "Pneu de Carga", "CARGA1", 70.0, 20, 2, 0.5);

        ShippingOrderItemRequest itemRequest = new ShippingOrderItemRequest(1L, 10, "Heavy Client"); // Total: 700kg
        ShippingOrderRequest request = new ShippingOrderRequest(1L, List.of(itemRequest));

        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(vehicle));
        when(productRepository.findById(1L)).thenReturn(Optional.of(heavyProduct));

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            shippingOrderService.createShippingOrder(request);
        });

        assertTrue(exception.getMessage().contains("Capacidade de peso excedida"));
        verify(shippingOrderRepository, never()).save(any(ShippingOrder.class));
    }

    @Test
    @DisplayName("Should throw BusinessException when vehicle cubic volume is exceeded")
    void createShippingOrder_VolumeLimitExceeded() {
        // Arrange
        Vehicle vehicle = new Vehicle(1L, "Fiorino", "FIO-1234", 1000.0, 2.0); // Limite de 2m³
        Product bulkyProduct = new Product(1L, "Pneu Gigante", "GIG1", 10.0, 20, 2, 0.5); // 0.5m³ por item

        ShippingOrderItemRequest itemRequest = new ShippingOrderItemRequest(1L, 5, "Voluminous Client"); // Total: 2.5m³
        ShippingOrderRequest request = new ShippingOrderRequest(1L, List.of(itemRequest));

        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(vehicle));
        when(productRepository.findById(1L)).thenReturn(Optional.of(bulkyProduct));

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            shippingOrderService.createShippingOrder(request);
        });

        assertTrue(exception.getMessage().contains("Capacidade volumétrica (cubagem) excedida"));
        verify(shippingOrderRepository, never()).save(any(ShippingOrder.class));
    }

    @Test
    @DisplayName("Should register LowStockEvent when product stock falls below minimum after shipping order creation")
    void shouldRegisterLowStockEventWhenStockFallsBelowMinimum() {
        // Arrange
        Vehicle vehicle = new Vehicle(1L, "Scania R 450", "ABC-1234", 15000.0, 80.0);

        // Estoque Inicial = 10, Mínimo = 5. Pedindo 6 unidades, resta 4 (Gera Alerta).
        Product tire = new Product(
                1L,
                "Pneu Trator Agrícola",
                "PNEU-TRATOR-01",
                150.0,
                10,
                5,
                1.2
        );

        ShippingOrderItemRequest itemRequest = new ShippingOrderItemRequest(1L, 6, "Client A");
        ShippingOrderRequest request = new ShippingOrderRequest(1L, List.of(itemRequest));

        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(vehicle));
        when(productRepository.findById(1L)).thenReturn(Optional.of(tire));

        // Mock do save para retornar a própria entidade do parâmetro
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(shippingOrderRepository.save(any(ShippingOrder.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        shippingOrderService.createShippingOrder(request);

        // Assert
        // 1. Validando o decréscimo correto na camada do Repositório
        ArgumentCaptor<Product> productCaptor = ArgumentCaptor.forClass(Product.class);
        verify(productRepository).save(productCaptor.capture());

        Product savedProduct = productCaptor.getValue();
        assertEquals(4, savedProduct.getQuantityInStock());

        // 2. Extraindo o evento do Aggregate para garantir que foi acumulado para publicação
        var domainEvents = savedProduct.getAndClearDomainEvents();

        assertThat(domainEvents)
                .hasSize(1)
                .first()
                .isInstanceOf(LowStockEvent.class);

        LowStockEvent event = (LowStockEvent) domainEvents.iterator().next();
        assertEquals(1L, event.productId());
        assertEquals("PNEU-TRATOR-01", event.sku());
        assertEquals(4, event.currentStock());
        assertEquals(5, event.minimumStock());
    }
}