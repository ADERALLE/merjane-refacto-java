package com.nimbleways.springboilerplate.services.implementations.productStrategies;

import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.repositories.ProductRepository;
import com.nimbleways.springboilerplate.services.implementations.NotificationService;

public class NormalProductStrategy implements ProductProcessingStrategy {
    @Override
    public boolean canProcess(Product product) {
        return "NORMAL".equals(product.getType());
    }



    @Override
    public void processProduct(Product product, ProductRepository productRepository, NotificationService notificationService) {
        if (product.getAvailable() > 0) {
            product.setAvailable(product.getAvailable() - 1);
            productRepository.save(product);
        } else if (product.getLeadTime() > 0) {
            notificationService.sendDelayNotification(product.getLeadTime(), product.getName());
        }
    }


}