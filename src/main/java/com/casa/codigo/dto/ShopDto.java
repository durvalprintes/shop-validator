package com.casa.codigo.dto;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.casa.codigo.constants.Status;

import lombok.Data;

@Data
public class ShopDto {
  private String identifier;
  private LocalDate dateShop;
  private Status status;
  private List<ShopItemDto> items = new ArrayList<>();
}
