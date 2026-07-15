package com.logitrack.api.domain.shippingorder;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShippingOrderRepository extends JpaRepository<ShippingOrder, Long> {
}