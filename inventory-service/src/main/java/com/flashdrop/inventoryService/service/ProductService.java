package com.flashdrop.inventoryService.service;


import com.flashdrop.inventoryService.dto.AddProductReq;
import com.flashdrop.inventoryService.dto.AddProductRes;
import com.flashdrop.inventoryService.dto.AddSkuReq;
import com.flashdrop.inventoryService.entity.*;
import com.flashdrop.inventoryService.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
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

    public void addProduct(AddProductReq addProductReq) {

        Category category = categoryRepository.findById(addProductReq.getCategoryId())
                .orElseThrow(()->
                        new RuntimeException("Category not found"));


        Product product = Product.builder()
                .productCode(addProductReq.getProductCode())
                .productName((addProductReq.getProductName()))
                .description((addProductReq.getDescription()))
                .brand((addProductReq.getBrand()))
                .category(category)
                .build();

        productRepository.save(product);

        for(AddSkuReq skuReq : addProductReq.getSkus()) {
            Sku sku = Sku.builder()
                    .skuCode(skuReq.getSkuCode())
                    .color(skuReq.getColor())
                    .size(skuReq.getSize())
                    .costPrice(skuReq.getPrice())
                    .product(product)
                    .build();

            skuRepository.save(sku);


            Inventory inventory = Inventory.builder()
                    .sku(sku)
                    .availableQuantity(skuReq.getInitialStock())
                    .reservedQuantity(0)
                    .build();

            inventoryRepository.save(inventory);

            InventoryTransaction inventoryTransaction = InventoryTransaction.builder()
                    .sku(sku)
                    .type(TransactionType.STOCK_IN)
                    .quantity(skuReq.getInitialStock())
                    .userId("Admin")
                    .remarks("Product Creation")
                    .build();

            transactionRepository.save(inventoryTransaction);
        }
    }
}
