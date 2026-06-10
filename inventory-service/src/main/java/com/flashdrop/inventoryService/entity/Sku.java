package com.flashdrop.inventoryService.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "skus")
@Builder
public class Sku {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String skuCode;

    private String size;

    private String color;

    private String material;

    private String productImageUrl;

    private List<String> productImageUrlList;

    private Double rating;

    private String sellingPrice;

    private String costPrice;

    //In future if implemented warehouse detail then we will add location etc
    // private String location;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;
}
