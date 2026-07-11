package com.logitrack.api.service;

import com.logitrack.api.domain.product.Product;
import com.logitrack.api.domain.product.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    // Injeção por construtor (melhor prática que @Autowired)
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    // Verifica se já existe um produto com o SKU informado
    public boolean existsBySku(String sku) {
        return productRepository.findBySku(sku).isPresent();
    }

    // Executa a persistência do produto no banco de dados
    public Product save(Product product) {
        return productRepository.save(product);
    }

    // Retorna a listagem de produtos com paginação e ordenação
    public Page<Product> findAll(Pageable pageable) {
        return productRepository.findAll(pageable);
    }
}