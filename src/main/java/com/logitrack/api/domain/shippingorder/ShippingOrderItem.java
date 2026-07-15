package com.logitrack.api.domain.shippingorder;

import com.logitrack.api.domain.product.Product;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "shipping_order_items")
public class ShippingOrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shipping_order_id", nullable = false)
    private ShippingOrder shippingOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    @NotNull(message = "O produto é obrigatório")
    private Product product;

    @NotNull(message = "A quantidade é obrigatória")
    @Positive(message = "A quantidade deve ser maior que zero")
    private Integer quantity;

    @NotBlank(message = "O nome do cliente é obrigatório")
    @Column(name = "customer_name")
    private String customerName;

    public ShippingOrderItem() {}

    public ShippingOrderItem(Product product, Integer quantity, String customerName) {
        this.product = product;
        this.quantity = quantity;
        this.customerName = customerName;
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public ShippingOrder getShippingOrder() { return shippingOrder; }
    public void setShippingOrder(ShippingOrder shippingOrder) { this.shippingOrder = shippingOrder; }

    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
}
