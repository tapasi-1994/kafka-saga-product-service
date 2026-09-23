package com.kafka.saga.ProductService.service;

import com.kafka.saga.CoreService.dto.Product;

import java.util.List;

public interface ProductService {
    List<Product> findAll();
    Product reserve(Product desiredProduct, Long orderId);
    void cancelReservation(Product productToCancel, Long orderId);
    Product save(Product product);
}
