package com.flashdrop.productService.dto;

import com.flashdrop.productService.entity.Products;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-03-13T00:13:28+0530",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.8 (Oracle Corporation)"
)
@Component
public class ProductMapperImpl implements ProductMapper {

    @Override
    public List<ProductResponse> toProductResponseList(List<Products> products) {
        if ( products == null ) {
            return null;
        }

        List<ProductResponse> list = new ArrayList<ProductResponse>( products.size() );
        for ( Products products1 : products ) {
            list.add( productsToProductResponse( products1 ) );
        }

        return list;
    }

    protected ProductResponse productsToProductResponse(Products products) {
        if ( products == null ) {
            return null;
        }

        ProductResponse.ProductResponseBuilder productResponse = ProductResponse.builder();

        productResponse.productId( products.getProductId() );
        productResponse.productName( products.getProductName() );
        productResponse.productDescription( products.getProductDescription() );
        productResponse.productPrice( products.getProductPrice() );
        productResponse.productImageUrl( products.getProductImageUrl() );
        productResponse.productCategory( products.getProductCategory() );
        productResponse.productStatus( products.getProductStatus() );
        productResponse.rating( products.getRating() );

        return productResponse.build();
    }
}
