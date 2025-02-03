package com.nimbleways.springboilerplate.services.implementations.productStrategies;

import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.repositories.ProductRepository;
import com.nimbleways.springboilerplate.services.implementations.NotificationService;

public interface ProductProcessingStrategy {
    boolean canProcess(Product product);
    void processProduct(Product product, ProductRepository productRepository, NotificationService notificationService);
}
