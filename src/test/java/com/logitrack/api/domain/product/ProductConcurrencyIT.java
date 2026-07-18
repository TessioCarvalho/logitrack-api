package com.logitrack.api.domain.product;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.OptimisticLockingFailureException;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ProductConcurrencyIT {

    @Autowired
    private ProductRepository productRepository;

    private Long productId;

    @BeforeEach
    void setUp() {
        productRepository.deleteAll();

        // Instancia usando o construtor real da sua entidade
        // Parametros: id (null), name, sku, weight, quantityInStock, minimumStock, cubicVolume
        Product product = new Product(
                null,
                "Componente Eletrônico",
                "COMP-123",
                1.5,
                100, // Estoque inicial alta o suficiente para aguentar as duas baixas
                10,
                0.02
        );

        Product savedProduct = productRepository.save(product);
        this.productId = savedProduct.getId();
    }

    @Test
    @DisplayName("Deve lançar OptimisticLockingFailureException quando duas threads tentarem abater o estoque simultaneamente")
    void shouldThrowOptimisticLockingExceptionOnConcurrentUpdate() throws InterruptedException {
        // Arrange: Prepara duas threads concorrentes
        ExecutorService executor = Executors.newFixedThreadPool(2);
        CountDownLatch latch = new CountDownLatch(1);

        AtomicReference<Exception> thread1Exception = new AtomicReference<>();
        AtomicReference<Exception> thread2Exception = new AtomicReference<>();

        // Thread 1: Tenta ler e abater 10 unidades do estoque
        executor.execute(() -> {
            try {
                latch.await(); // Aguarda o sinal de largada
                Product p1 = productRepository.findById(productId).orElseThrow();

                p1.decreaseStock(10); // Usa o método de negócio correto da sua entidade

                productRepository.save(p1);
            } catch (Exception e) {
                thread1Exception.set(e);
            }
        });

        // Thread 2: Exatamente ao mesmo tempo, tenta ler e abater 5 unidades do estoque
        executor.execute(() -> {
            try {
                latch.await(); // Aguarda o sinal de largada
                Product p2 = productRepository.findById(productId).orElseThrow();

                p2.decreaseStock(5); // Usa o método de negócio correto da sua entidade

                productRepository.save(p2);
            } catch (Exception e) {
                thread2Exception.set(e);
            }
        });

        // Act: Dá o sinal de largada para as duas threads correrem juntas
        latch.countDown();
        executor.shutdown();

        while (!executor.isTerminated()) {
            Thread.sleep(10);
        }

        // Assert: Uma das duas DEVE ter falhado com OptimisticLockingFailureException
        boolean thread1Failed = thread1Exception.get() instanceof OptimisticLockingFailureException;
        boolean thread2Failed = thread2Exception.get() instanceof OptimisticLockingFailureException;

        // Prova empírica que a colisão foi detectada e barrada pelo @Version
        assertThat(thread1Failed || thread2Failed)
                .as("Pelo menos uma das requisições concorrentes deve falhar por conflito de versão no Hibernate")
                .isTrue();

        // O estoque final deve refletir apenas o abate da thread vencedora (100-10=90 OU 100-5=95)
        Product finalProduct = productRepository.findById(productId).orElseThrow();
        System.out.println("[INFO] Versão final do produto no banco: " + finalProduct.getVersion());
        System.out.println("[INFO] Quantidade final de estoque real no banco: " + finalProduct.getQuantityInStock());

        assertThat(finalProduct.getQuantityInStock()).isIn(90, 95);
    }
}