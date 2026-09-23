package com.kafka.saga.ProductService.service;

import com.kafka.saga.CoreService.dto.Product;
import com.kafka.saga.CoreService.exception.ProductInsufficientQuantityException;
import com.kafka.saga.ProductService.entity.ProductEntity;
import com.kafka.saga.ProductService.repo.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;

    @Override
    public Product reserve(Product desiredProduct, Long orderId) {
        ProductEntity productEntity = productRepository.findById(desiredProduct.getProductId()).orElseThrow(()->new RuntimeException("Product Not Found"));
        if (desiredProduct.getQuantity() > productEntity.getQuantity()) {
            throw new ProductInsufficientQuantityException(productEntity.getId(), orderId);
        }

        productEntity.setQuantity(productEntity.getQuantity() - desiredProduct.getQuantity());
        ProductEntity updatedProduct = productRepository.save(productEntity);

        Product reservedProduct = Product.builder()
                        .productId(updatedProduct.getId())
                        .name(updatedProduct.getName())
                        .price(updatedProduct.getPrice())
                        .quantity(desiredProduct.getQuantity())
                        .build();
        return reservedProduct;
    }

    @Override
    public void cancelReservation(Product productToCancel, Long orderId) {
        ProductEntity productEntity = productRepository.findById(productToCancel.getProductId()).orElseThrow(()->new RuntimeException("Product Not Found"));
        productEntity.setQuantity(productEntity.getQuantity() + productToCancel.getQuantity());
        productRepository.save(productEntity);
    }

    @Override
    public Product save(Product product) {
        ProductEntity productEntity = ProductEntity.builder()
                .name(product.getName())
                .price(product.getPrice())
                .quantity(product.getQuantity())
                .build();
        productRepository.save(productEntity);

        return new Product(productEntity.getId(), product.getName(), product.getPrice(), product.getQuantity());
    }

    @Override
    public List<Product> findAll() {
        return productRepository.findAll().stream()
                .map(entity -> new Product(entity.getId(), entity.getName(), entity.getPrice(), entity.getQuantity()))
                .collect(Collectors.toList());
    }
}
