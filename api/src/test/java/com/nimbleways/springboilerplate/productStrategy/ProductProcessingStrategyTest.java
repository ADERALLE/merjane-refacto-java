package com.nimbleways.springboilerplate.productStrategy;



import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.repositories.ProductRepository;
import com.nimbleways.springboilerplate.services.implementations.NotificationService;
import com.nimbleways.springboilerplate.services.implementations.productStrategies.ExpirableProductStrategy;
import com.nimbleways.springboilerplate.services.implementations.productStrategies.NormalProductStrategy;
import com.nimbleways.springboilerplate.services.implementations.productStrategies.SeasonalProductStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;

import static org.mockito.Mockito.*;

public class ProductProcessingStrategyTest {
    @Mock
    private ProductRepository productRepository;

    @Mock
    private NotificationService notificationService;

    private NormalProductStrategy normalStrategy;
    private SeasonalProductStrategy seasonalStrategy;
    private ExpirableProductStrategy expirableStrategy;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        normalStrategy = new NormalProductStrategy();
        seasonalStrategy = new SeasonalProductStrategy();
        expirableStrategy = new ExpirableProductStrategy();
    }

    @Test
    public void testNormalProductWithAvailableStock() {
        Product product = createNormalProduct(10);
        normalStrategy.processProduct(product, productRepository, notificationService);

        verify(productRepository).save(product);
        verify(notificationService, never()).sendDelayNotification(anyInt(), anyString());
        assert product.getAvailable() == 9;
    }

    @Test
    public void testNormalProductOutOfStock() {
        Product product = createNormalProduct(0);
        normalStrategy.processProduct(product, productRepository, notificationService);

        verify(productRepository, never()).save(product);
        verify(notificationService).sendDelayNotification(product.getLeadTime(), product.getName());
    }

    @Test
    public void testSeasonalProductDuringSeasonWithStock() {
        Product product = createSeasonalProduct(10, LocalDate.now(), LocalDate.now().plusMonths(1));
        seasonalStrategy.processProduct(product, productRepository, notificationService);

        verify(productRepository).save(product);
        verify(notificationService, never()).sendOutOfStockNotification(anyString());
        assert product.getAvailable() == 9;
    }

    @Test
    public void testSeasonalProductOutsideSeason() {
        Product product = createSeasonalProduct(10,
                LocalDate.now().plusMonths(1),
                LocalDate.now().plusMonths(2));
        seasonalStrategy.processProduct(product, productRepository, notificationService);

        verify(productRepository).save(product);
        verify(notificationService).sendOutOfStockNotification(product.getName());
        assert product.getAvailable() == 0;
    }

    @Test
    public void testExpirableProductBeforeExpiry() {
        Product product = createExpirableProduct(10, LocalDate.now().plusDays(10));
        expirableStrategy.processProduct(product, productRepository, notificationService);

        verify(productRepository).save(product);
        verify(notificationService, never()).sendExpirationNotification(anyString(), any());
        assert product.getAvailable() == 9;
    }

    @Test
    public void testExpirableProductAfterExpiry() {
        Product product = createExpirableProduct(10, LocalDate.now().minusDays(1));
        expirableStrategy.processProduct(product, productRepository, notificationService);

        verify(productRepository).save(product);
        verify(notificationService).sendExpirationNotification(product.getName(), product.getExpiryDate());
        assert product.getAvailable() == 0;
    }

    private Product createNormalProduct(int available) {
        return new Product(null, 15, available, "NORMAL", "Test Product", null, null, null);
    }

    private Product createSeasonalProduct(int available, LocalDate seasonStart, LocalDate seasonEnd) {
        return new Product(null, 15, available, "SEASONAL", "Seasonal Product", null, seasonStart, seasonEnd);
    }

    private Product createExpirableProduct(int available, LocalDate expiryDate) {
        return new Product(null, 15, available, "EXPIRABLE", "Expirable Product", expiryDate, null, null);
    }
}
