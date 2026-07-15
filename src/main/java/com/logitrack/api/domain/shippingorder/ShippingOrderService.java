package com.logitrack.api.domain.shippingorder;

import com.logitrack.api.domain.product.Product;
import com.logitrack.api.domain.product.ProductRepository;
import com.logitrack.api.domain.vehicle.Vehicle;
import com.logitrack.api.domain.vehicle.VehicleRepository;
import com.logitrack.api.domain.shippingorder.dto.ShippingOrderRequest;
import com.logitrack.api.domain.shippingorder.dto.ShippingOrderItemRequest;
import com.logitrack.api.exception.BusinessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ShippingOrderService {

    private final ShippingOrderRepository shippingOrderRepository;
    private final VehicleRepository vehicleRepository;
    private final ProductRepository productRepository;

    public ShippingOrderService(ShippingOrderRepository shippingOrderRepository,
                                VehicleRepository vehicleRepository,
                                ProductRepository productRepository) {
        this.shippingOrderRepository = shippingOrderRepository;
        this.vehicleRepository = vehicleRepository;
        this.productRepository = productRepository;
    }

    @Transactional
    public ShippingOrder createShippingOrder(ShippingOrderRequest request) {
        // 1. Buscar o veículo associado
        Vehicle vehicle = vehicleRepository.findById(request.vehicleId())
                .orElseThrow(() -> new BusinessException("Veículo não encontrado com o ID: " + request.vehicleId()));

        ShippingOrder shippingOrder = new ShippingOrder();
        shippingOrder.setVehicle(vehicle);

        double accumulatedWeight = 0.0;
        double accumulatedVolume = 0.0;

        // 2. Processar, somar e calcular pesos/cubagens parciais
        for (ShippingOrderItemRequest itemDto : request.items()) {
            Product product = productRepository.findById(itemDto.productId())
                    .orElseThrow(() -> new BusinessException("Produto não encontrado com o ID: " + itemDto.productId()));

            double itemWeight = product.getWeight() * itemDto.quantity();
            double itemVolume = product.getCubicVolume() * itemDto.quantity();

            accumulatedWeight += itemWeight;
            accumulatedVolume += itemVolume;

            ShippingOrderItem orderItem = new ShippingOrderItem(product, itemDto.quantity(), itemDto.customerName());
            shippingOrder.addItem(orderItem);
        }

        // 3. Validar Limite de Peso
        if (accumulatedWeight > vehicle.getMaxCapacityKg()) {
            throw new BusinessException(String.format(
                    "Capacidade de peso excedida! Carga atual: %.2f kg | Limite do veículo (%s): %.2f kg",
                    accumulatedWeight, vehicle.getModel(), vehicle.getMaxCapacityKg()
            ));
        }

        // 4. Validar Limite de Cubagem
        if (accumulatedVolume > vehicle.getMaxCubicVolume()) {
            throw new BusinessException(String.format(
                    "Capacidade volumétrica (cubagem) excedida! Volume da carga: %.3f m³ | Limite do veículo (%s): %.3f m³",
                    accumulatedVolume, vehicle.getModel(), vehicle.getMaxCubicVolume()
            ));
        }

        // 5. Consolidar dados e aprovar
        shippingOrder.setTotalWeight(accumulatedWeight);
        shippingOrder.setTotalCubicVolume(accumulatedVolume);
        shippingOrder.setStatus(ShippingOrderStatus.APPROVED);

        return shippingOrderRepository.save(shippingOrder);
    }
}