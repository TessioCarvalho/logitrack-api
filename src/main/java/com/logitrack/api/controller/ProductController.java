package com.logitrack.api.controller;

import com.logitrack.api.domain.product.Product;
import com.logitrack.api.service.ProductService; // Ajuste o pacote se o seu service estiver em outro caminho
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/products")
public class ProductController {

    // Substituído o Repository pelo Service seguindo o padrão de arquitetura em camadas
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // Endpoint do WMS: Cadastrar um novo produto
    @PostMapping
    public ResponseEntity<Product> createProduct(@RequestBody @Valid Product product) {
        // Delegamos a verificação de duplicidade e o salvamento para o Service
        if (productService.existsBySku(product.getSku())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build(); // Retorna HTTP 409 Conflict
        }

        Product savedProduct = productService.save(product);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedProduct); // Retorna HTTP 201 Created
    }

    // Endpoint do WMS: Listar produtos no armazém (Paginado)
    @GetMapping
    public ResponseEntity<Page<Product>> listAll(
            @PageableDefault(page = 0, size = 10, sort = "name", direction = Sort.Direction.ASC) Pageable pageable) {

        Page<Product> productsPage = productService.findAll(pageable);
        return ResponseEntity.ok(productsPage);
    }
}