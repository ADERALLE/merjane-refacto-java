package com.nimbleways.springboilerplate.services.implementations;

import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.repositories.ProductRepository;


import com.nimbleways.springboilerplate.services.implementations.productStrategies.ExpirableProductStrategy;
import com.nimbleways.springboilerplate.services.implementations.productStrategies.NormalProductStrategy;
import com.nimbleways.springboilerplate.services.implementations.productStrategies.ProductProcessingStrategy;
import com.nimbleways.springboilerplate.services.implementations.productStrategies.SeasonalProductStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;

@Service
public class ProductService {
    private final List<ProductProcessingStrategy> strategies;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private NotificationService notificationService;




    public ProductService() {
        this.strategies = new ArrayList<>();
        this.strategies.add(new NormalProductStrategy());
        this.strategies.add(new SeasonalProductStrategy());
        this.strategies.add(new ExpirableProductStrategy());
    }

    public void processProduct(Product product) {
        for (ProductProcessingStrategy strategy : strategies) {
            if (strategy.canProcess(product)) {
                strategy.processProduct(product, productRepository, notificationService);
                return;
            }
        }
        throw new IllegalArgumentException("No strategy found for product type: " + product.getType());
    }
    public void notifyDelay(Integer leadTime, Product p) {
        p.setLeadTime(leadTime);
        productRepository.save(p);
        notificationService.sendDelayNotification(leadTime, p.getName());
    }

    public boolean canProcess(Product product) {
        return "SEASONAL".equals(product.getType());
    }
}