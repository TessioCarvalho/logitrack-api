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
        Vehicle vehicle = vehicleRepository.findById(request.vehicleId())
                .orElseThrow(() -> new BusinessException("Vehicle not found with ID: " + request.vehicleId()));

        ShippingOrder shippingOrder = new ShippingOrder(vehicle);

        for (ShippingOrderItemRequest itemDto : request.items()) {
            Product product = productRepository.findById(itemDto.productId())
                    .orElseThrow(() -> new BusinessException("Product not found with ID: " + itemDto.productId()));

            // 1. Baixa o estoque do produto
            product.decreaseStock(itemDto.quantity());
            productRepository.save(product);

            // 2. Cria o item e adiciona na ordem (A própria ordem recalcula totais e valida a capacidade do veículo)
            ShippingOrderItem orderItem = new ShippingOrderItem(product, itemDto.quantity(), itemDto.customerName());
            shippingOrder.addItem(orderItem);
        }

        // 3. Transiciona o status usando o modelo de domínio rico
        shippingOrder.approve();

        return shippingOrderRepository.save(shippingOrder);
    }
}