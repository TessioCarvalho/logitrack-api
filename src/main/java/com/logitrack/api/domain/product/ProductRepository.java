package com.logitrack.api.domain.product;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // Spring Data cria esta query automaticamente baseado no nome do método!
    // Essencial para o WMS: verificar se o SKU já existe antes de cadastrar.
    Optional<Product> findBySku(String sku);
}