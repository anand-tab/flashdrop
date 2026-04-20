package com.flashdrop.productService.dto;


import com.flashdrop.productService.entity.Products;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductMapper {



    ProductResponse toProductResponse(Products products);
    List<ProductResponse> toProductResponseList(List<Products> products);
}
