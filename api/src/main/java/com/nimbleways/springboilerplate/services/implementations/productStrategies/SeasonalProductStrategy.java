package com.nimbleways.springboilerplate.services.implementations.productStrategies;

import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.repositories.ProductRepository;
import com.nimbleways.springboilerplate.services.implementations.NotificationService;

import java.time.LocalDate;

public class SeasonalProductStrategy implements ProductProcessingStrategy {
    @Override
    public boolean canProcess(Product product) {
        return "SEASONAL".equals(product.getType());
    }



    @Override
    public void processProduct(Product product, ProductRepository productRepository, NotificationService notificationService) {
        LocalDate now = LocalDate.now();
        if (now.isAfter(product.getSeasonStartDate()) &&
                now.isBefore(product.getSeasonEndDate()) &&
                product.getAvailable() > 0) {

            product.setAvailable(product.getAvailable() - 1);
            productRepository.save(product);
        } else {
            if (now.plusDays(product.getLeadTime()).isAfter(product.getSeasonEndDate())) {
                notificationService.sendOutOfStockNotification(product.getName());
                product.setAvailable(0);
                productRepository.save(product);
            } else if (product.getSeasonStartDate().isAfter(now)) {
                notificationService.sendOutOfStockNotification(product.getName());
                productRepository.save(product);
            } else {
                notificationService.sendDelayNotification(product.getLeadTime(), product.getName());
            }
        }
    }


}
