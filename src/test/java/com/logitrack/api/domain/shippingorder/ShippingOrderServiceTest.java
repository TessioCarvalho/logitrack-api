package com.logitrack.api.domain.shippingorder;

import com.logitrack.api.domain.product.Product;
import com.logitrack.api.domain.product.ProductRepository;
import com.logitrack.api.domain.vehicle.Vehicle;
import com.logitrack.api.domain.vehicle.VehicleRepository;
import com.logitrack.api.domain.shippingorder.dto.ShippingOrderItemRequest;
import com.logitrack.api.domain.shippingorder.dto.ShippingOrderRequest;
import com.logitrack.api.exception.BusinessException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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
    @DisplayName("Should orchestrate shipping order creation successfully with PENDING status")
    void createShippingOrder_Success() {
        Vehicle vehicle = new Vehicle("Caminhão", "XYZ-9999", 5000.0, 50.0);
        // ORDEM DOS PARÂMETROS: nome, peso, volume, estoqueInicial
        Product product = new Product("Geladeira", 80.0, 0.8, 20);

        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(vehicle));
        when(productRepository.findById(10L)).thenReturn(Optional.of(product));
        when(shippingOrderRepository.save(any(ShippingOrder.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ShippingOrderItemRequest itemRequest = new ShippingOrderItemRequest(10L, 2, "Loja Central");
        ShippingOrderRequest orderRequest = new ShippingOrderRequest(1L, List.of(itemRequest));

        ShippingOrder result = shippingOrderService.createShippingOrder(orderRequest);

        assertThat(result).isNotNull();
        // ALINHAMENTO COM A MÁQUINA DE ESTADOS DO TMS: Estado inicial deve ser PENDING
        assertThat(result.getStatus()).isEqualTo(ShippingOrderStatus.PENDING);
        assertThat(result.getItems()).hasSize(1);
        assertThat(result.getTotalWeight()).isEqualTo(160.0); // 80kg * 2 unidades

        verify(productRepository, times(1)).save(product);
        verify(shippingOrderRepository, times(1)).save(any(ShippingOrder.class));
    }

    @Test
    @DisplayName("Should throw exception when vehicle is not found")
    void createShippingOrder_VehicleNotFound() {
        when(vehicleRepository.findById(1L)).thenReturn(Optional.empty());

        ShippingOrderRequest orderRequest = new ShippingOrderRequest(1L, List.of());

        assertThatThrownBy(() -> shippingOrderService.createShippingOrder(orderRequest))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Vehicle not found with ID: 1");

        verifyNoInteractions(productRepository);
        verifyNoInteractions(shippingOrderRepository);
    }

    @Test
    @DisplayName("Should throw BusinessException when payload exceeds vehicle capacity")
    void createShippingOrder_ExceedsVehicleCapacity() {
        // Veículo pequeno com capacidade máxima de 100 kg
        Vehicle smallVehicle = new Vehicle("Furgão", "ABC-1234", 100.0, 5.0);
        // Produto pesado
        Product heavyProduct = new Product("Maquinário", 200.0, 1.0, 10);

        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(smallVehicle));
        when(productRepository.findById(10L)).thenReturn(Optional.of(heavyProduct));

        ShippingOrderItemRequest itemRequest = new ShippingOrderItemRequest(10L, 1, "Fábrica");
        ShippingOrderRequest orderRequest = new ShippingOrderRequest(1L, List.of(itemRequest));

        assertThatThrownBy(() -> shippingOrderService.createShippingOrder(orderRequest))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Weight capacity exceeded");

        verify(shippingOrderRepository, never()).save(any());
    }
}