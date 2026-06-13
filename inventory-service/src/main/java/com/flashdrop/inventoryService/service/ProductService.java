package com.flashdrop.inventoryService.service;


import com.flashdrop.inventoryService.client.ProductServiceClient;
import com.flashdrop.inventoryService.dto.AddProductReq;
import com.flashdrop.inventoryService.dto.AddSkuReq;
import com.flashdrop.inventoryService.dto.KafkaContext;
import com.flashdrop.inventoryService.dto.ProductServiceReq;
import com.flashdrop.inventoryService.entity.*;
import com.flashdrop.inventoryService.repository.*;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
@Slf4j
public class ProductService {

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private InventoryRepository inventoryRepository;
    @Autowired
    private SkuRepository skuRepository;
    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private ProductServiceClient productServiceClient;




    @Transactional
    public void purchasedHot(String key,KafkaContext kafkaContext) {

        Inventory inventory = inventoryRepository.findBySku_SkuCode(kafkaContext.getSkuID())
                .orElseThrow(()-> new EntityNotFoundException("Sku did not found"));

        if (inventory.getAvailableQuantity() < kafkaContext.getQuantity()) {
            throw new IllegalArgumentException("Insufficient stock for SKU: " + kafkaContext.getSkuID() +
                    ". Available: " + inventory.getAvailableQuantity() + ", Requested: " + kafkaContext.getQuantity());
        }

        inventory.setAvailableQuantity(inventory.getAvailableQuantity() - kafkaContext.getQuantity());

        inventoryRepository.save(inventory);
        log.info("Inventory updated quantity in db"+inventory.getAvailableQuantity());

        Sku sku = skuRepository.findBySkuCode(kafkaContext.getSkuID()).orElseThrow(()-> new EntityNotFoundException("SKU: " + kafkaContext.getSkuID()));
        InventoryTransaction inventoryTransaction = InventoryTransaction.builder()
                .remarks(kafkaContext.getStatus())
                .orderId(kafkaContext.getOrderId())
                .userId(kafkaContext.getEmail())
                .quantity(kafkaContext.getQuantity())
                .sku(sku)
                .type(TransactionType.STOCK_OUT)
                .build();

        transactionRepository.save(inventoryTransaction);
    }

    public void addProduct(AddProductReq request) {
        Category category = categoryRepository
                .findById(request.getCategoryId())
                .orElseThrow(() ->
                        new RuntimeException("Category not found"));

        Product product = productRepository.findByProductCode(request.getProductCode());

        if(product == null) {
            product = Product.builder()
                    .productName(request.getProductName())
                    .productCode(request.getProductCode())
                    .description(request.getDescription())
                    .brand(request.getBrand())
                    .category(category)
                    .build();

            product = productRepository.save(product);
        }

        for (AddSkuReq skuRequest : request.getSkus()) {

            Optional<Sku> existingSku =
                    skuRepository.findBySkuCode(
                            skuRequest.getSkuCode());

            Sku sku;
                log.info(skuRequest.getProductImageUrl()+ " this is skurequest's imageurl ");
            if (existingSku.isPresent()) {

                sku = existingSku.get();

                Inventory inventory =
                        inventoryRepository
                                .findBySku_SkuCode(sku.getSkuCode())
                                .orElseThrow(() -> new RuntimeException("Inventory not found"));

                inventory.setAvailableQuantity(
                        inventory.getAvailableQuantity()
                                + skuRequest.getInitialStock());

                inventoryRepository.save(inventory);

                log.info(sku.getProductImageUrl() + " This is SKU's ImageURL");

            } else {

                sku = Sku.builder()
                        .skuCode(skuRequest.getSkuCode())
                        .color(skuRequest.getColor())
                        .size(skuRequest.getSize())
                        .material(skuRequest.getMaterial())
                        .sellingPrice(null)
                        .productImageUrl(skuRequest.getProductImageUrl())
                        .productImageUrlList(skuRequest.getProductImageUrlList())
                        .costPrice(skuRequest.getPrice())
                        .product(product)
                        .build();


                sku = skuRepository.save(sku);

                Inventory inventory = Inventory.builder()
                        .sku(sku)
                        .availableQuantity(
                                skuRequest.getInitialStock())
                        .reservedQuantity(0)
                        .build();

                inventoryRepository.save(inventory);
            }

            InventoryTransaction transaction =
                    InventoryTransaction.builder()
                            .sku(sku)
                            .type(TransactionType.STOCK_IN)
                            .quantity(
                                    skuRequest.getInitialStock())
                            .userId("INITIAL_STOCK")
                            .remarks("Product Creation")
                            .build();

            transactionRepository.save(transaction);

            String str1 = "";

            Inventory inventory = inventoryRepository.findBySku_SkuCode(skuRequest.getSkuCode()).orElseThrow(()-> new EntityNotFoundException("SKU: " + skuRequest.getSkuCode()));

            if(inventory.getAvailableQuantity()>1){
                str1 = "Available";
            }else if(inventory.getAvailableQuantity()==0) {
                str1 = "Not Available";
            }


            ProductServiceReq productServiceReq = ProductServiceReq.builder()
                    .productId(skuRequest.getSkuCode())
                    .productName(product.getProductName())
                    .productDescription(product.getDescription())
                    .productCategory(category.getName())
                    .productStatus(str1)
                    .productPrice(sku.getCostPrice())
                    .productImageUrl(skuRequest.getProductImageUrl())
                    .productImageUrlList(skuRequest.getProductImageUrlList())
                    .rating(4.5)
                    .build();
            log.info(productServiceReq.getProductImageUrl()  + " ImageUrl");
            String str = productServiceClient.addProduct(productServiceReq);
        }
    }
}
