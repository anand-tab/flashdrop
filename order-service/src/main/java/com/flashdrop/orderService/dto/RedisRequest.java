package com.flashdrop.orderService.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RedisRequest {
    private List<String> key;
    private List<String> value;
 }
