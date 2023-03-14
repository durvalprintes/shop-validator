package com.casa.codigo.dto;

import lombok.Data;

@Data
public class ShopItemDto {
  private String productIdentifier;
  private Integer amount;
  private Float price;
}
