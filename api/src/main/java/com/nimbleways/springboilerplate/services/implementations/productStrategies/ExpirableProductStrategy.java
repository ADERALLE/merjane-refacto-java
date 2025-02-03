package com.nimbleways.springboilerplate.services.implementations.productStrategies;

import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.repositories.ProductRepository;
import com.nimbleways.springboilerplate.services.implementations.NotificationService;

import java.time.LocalDate;

public class ExpirableProductStrategy implements ProductProcessingStrategy {
    @Override
    public boolean canProcess(Product product) {
        return "EXPIRABLE".equals(product.getType());
    }



    @Override
    public void processProduct(Product product, ProductRepository productRepository, NotificationService notificationService) {
        LocalDate now = LocalDate.now();
        if (product.getAvailable() > 0 && product.getExpiryDate().isAfter(now)) {
            product.setAvailable(product.getAvailable() - 1);
            productRepository.save(product);
        } else {
            notificationService.sendExpirationNotification(product.getName(), product.getExpiryDate());
            product.setAvailable(0);
            productRepository.save(product);
        }
    }


}
