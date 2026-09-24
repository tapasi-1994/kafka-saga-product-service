package com.kafka.saga.ProductService.handler;

import com.kafka.saga.CoreService.dto.Product;
import com.kafka.saga.CoreService.dto.commands.CancelProductReservationCommand;
import com.kafka.saga.CoreService.dto.commands.ReserveProductCommand;
import com.kafka.saga.CoreService.events.ProductReservationCancelledEvent;
import com.kafka.saga.CoreService.events.ProductReservationFailedEvent;
import com.kafka.saga.CoreService.events.ProductReservedEvent;
import com.kafka.saga.ProductService.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@KafkaListener(
        topics = "${product.commands.topic.name}",
        groupId = "${spring.kafka.consumer.group-id}"
)
public class ProductCommandHandler {

    private static final Logger log = LoggerFactory.getLogger(ProductCommandHandler.class);
    private final ProductService productService;
    private final KafkaTemplate<String,Object> kafkaTemplate;

    @Value("${product.events.topic.name}")
    private String topicName;


    @KafkaHandler
    public void handleCommand(@Payload ReserveProductCommand command)
    {

        log.info("========== COMMAND RECEIVED ==========");
        log.info("Order ID: {}", command.getOrderId());
        log.info("Product ID: {}", command.getProductId());
        log.info("Quantity: {}", command.getProductQuantity());

        try {

            Product desiredProduct = Product.builder()
                    .productId(command.getProductId())
                    .quantity(command.getProductQuantity())
                    .build();
            Product reserveProduct = productService.reserve(desiredProduct, command.getOrderId());

            ProductReservedEvent productReservedEvent = ProductReservedEvent.builder()
                    .productId(command.getProductId())
                    .orderId(command.getOrderId())
                    .productPrice(reserveProduct.getPrice())
                    .productQuantity(command.getProductQuantity())
                    .build();
            kafkaTemplate.send(topicName,productReservedEvent);
        }
        catch(Exception e)
        {
            log.error(e.getMessage());
            ProductReservationFailedEvent reservationFailedEvent = ProductReservationFailedEvent.builder()
                    .productId(command.getProductId())
                    .productQuantity(command.getProductQuantity())
                    .orderId(command.getOrderId())
                    .build();
            kafkaTemplate.send(topicName,reservationFailedEvent);
        }
    }

    @KafkaHandler
    public void handleCommand(@Payload CancelProductReservationCommand cancelProductReservationCommand)
    {
        log.info("========== CANCEL PRODUCT RESERVATION COMMAND RECEIVED ==========");
        log.info("========== orderid:"+cancelProductReservationCommand.getOrderId());
        Product productToCancel = Product.builder()
                .productId(cancelProductReservationCommand.getProductId())
                .quantity(cancelProductReservationCommand.getProductQuantity())
                .build();
        productService.cancelReservation(productToCancel,cancelProductReservationCommand.getOrderId());

        ProductReservationCancelledEvent productReservationCancelledEvent = ProductReservationCancelledEvent.builder()
                .productId(cancelProductReservationCommand.getProductId())
                .orderId(cancelProductReservationCommand.getOrderId())
                .build();

        kafkaTemplate.send(topicName,productReservationCancelledEvent);
    }
}
