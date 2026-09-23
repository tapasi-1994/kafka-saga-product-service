package com.kafka.saga.ProductService.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;


@Table(name = "products")
@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "name")
    private String name;

    @Column(name = "price")
    private BigDecimal price;
}