package com.logitrack.api.domain.product;

import com.logitrack.api.exception.BusinessException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProductTest {

    @Test
    @DisplayName("Should decrease stock successfully when quantity is available")
    void decreaseStock_Success() {
        // Arrange
        Product product = new Product(1L, "Pneu Aptany", "APT123", 7.0, 100, 10, 0.05);

        // Act
        product.decreaseStock(10);

        // Assert
        assertEquals(90, product.getQuantityInStock(), "Stock should be decreased by the requested quantity");
    }

    @Test
    @DisplayName("Should throw BusinessException when trying to decrease more than available stock")
    void decreaseStock_InsufficientStock() {
        // Arrange
        Product product = new Product(1L, "Pneu Aptany", "APT123", 7.0, 10, 5, 0.05);

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            product.decreaseStock(11);
        });

        assertTrue(exception.getMessage().contains("Estoque insuficiente"), "Error message should mention insufficient stock");
        assertEquals(10, product.getQuantityInStock(), "Stock should remain unchanged after failed attempt");
    }

    @Test
    @DisplayName("Should throw BusinessException when requested quantity is zero or negative")
    void decreaseStock_InvalidQuantity() {
        // Arrange
        Product product = new Product(1L, "Pneu Aptany", "APT123", 7.0, 10, 5, 0.05);

        // Act & Assert
        assertThrows(BusinessException.class, () -> product.decreaseStock(0));
        assertThrows(BusinessException.class, () -> product.decreaseStock(-5));
        assertEquals(10, product.getQuantityInStock(), "Stock should remain unchanged after invalid requests");
    }
}