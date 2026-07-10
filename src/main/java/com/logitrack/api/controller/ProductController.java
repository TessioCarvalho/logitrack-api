package com.logitrack.api.controller;

import com.logitrack.api.domain.product.Product;
import com.logitrack.api.domain.product.ProductRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {

    // Injeção de dependência clássica do Spring (Constructor Injection)
    // O mercado prefere isso em vez do @Autowired direto no atributo por questões de testes unitários.
    private final ProductRepository repository;

    public ProductController(ProductRepository repository) {
        this.repository = repository;
    }

    // Endpoint do WMS: Cadastrar um novo produto
    @PostMapping
    public ResponseEntity<Product> createProduct(@RequestBody @Valid Product product) {
        // Validação básica de regra de negócio: não permitir SKU duplicado
        if (repository.findBySku(product.getSku()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build(); // Retorna HTTP 409 Conflict
        }

        Product savedProduct = repository.save(product);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedProduct); // Retorna HTTP 201 Created
    }

    // Endpoint do WMS: Listar todos os produtos no armazém
    @GetMapping
    public ResponseEntity<List<Product>> listAllProducts() {
        List<Product> products = repository.findAll();
        return ResponseEntity.ok(products); // Retorna HTTP 200 OK
    }
}