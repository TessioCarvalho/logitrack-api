package com.logitrack.api.domain.shippingorder;

import com.logitrack.api.domain.product.Product;
import com.logitrack.api.domain.vehicle.Vehicle;
import com.logitrack.api.exception.BusinessException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ShippingOrderTest {

    @Test
    @DisplayName("Should add item and recalculate totals successfully")
    void addItem_Success() {
        Vehicle vehicle = new Vehicle("Furgão", "ABC-1234", 1000.0, 10.0);
        Product product = new Product("Notebook", 2.5, 0.01, 10);

        ShippingOrder order = new ShippingOrder(vehicle);
        ShippingOrderItem item = new ShippingOrderItem(product, 2, "Cliente A");

        order.addItem(item);

        assertThat(order.getItems()).hasSize(1);
        assertThat(order.getTotalWeight()).isEqualTo(5.0);
        assertThat(order.getTotalCubicVolume()).isEqualTo(0.02);
    }

    @Test
    @DisplayName("Should throw exception when weight capacity is exceeded")
    void addItem_WeightLimitExceeded() {
        Vehicle vehicle = new Vehicle("Furgão", "ABC-1234", 10.0, 10.0);
        Product heavyProduct = new Product("Carga Pesada", 15.0, 0.1, 10);

        ShippingOrder order = new ShippingOrder(vehicle);
        ShippingOrderItem item = new ShippingOrderItem(heavyProduct, 1, "Cliente B");

        assertThatThrownBy(() -> order.addItem(item))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Weight capacity exceeded");
    }

    @Test
    @DisplayName("Should throw exception when volume capacity is exceeded")
    void addItem_VolumeLimitExceeded() {
        Vehicle vehicle = new Vehicle("Furgão", "ABC-1234", 1000.0, 1.0);
        Product bulkyProduct = new Product("Caixa Grande", 1.0, 1.5, 10);

        ShippingOrder order = new ShippingOrder(vehicle);
        ShippingOrderItem item = new ShippingOrderItem(bulkyProduct, 1, "Cliente C");

        assertThatThrownBy(() -> order.addItem(item))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Cubic volume capacity exceeded");
    }
}