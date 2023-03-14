package com.casa.codigo.listener;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.casa.codigo.constants.Status;
import com.casa.codigo.dto.ShopDto;
import com.casa.codigo.dto.ShopItemDto;
import com.casa.codigo.model.Product;
import com.casa.codigo.repository.ProductRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ShopListener {

  @Value(value = "${topic.result}")
  private String topicResult;

  private final KafkaTemplate<String, ShopDto> kafkaTemplate;

  private final ProductRepository repository;

  @KafkaListener(topics = "${topic.shop}", groupId = "group")
  public void listenShopTopic(ShopDto dto) {
    try {
      log.info("Compra recebida no tópico: {}.", dto.getIdentifier());
      List<ShopItemDto> validItems = dto.getItems().stream().takeWhile(item -> validShop(item)).toList();
      if (validItems.size() == dto.getItems().size())
        shopSuccess(dto);
      else
        shopError(dto);
    } catch (Exception e) {
      log.error("Error: ", e.getMessage());
      shopError(dto);
    }
  }

  private boolean validShop(ShopItemDto item) {
    Optional<Product> product = repository.findByIdentifier(item.getProductIdentifier());
    return product.isPresent() && product.get().getAmount() >= item.getAmount();
  }

  private void shopError(ShopDto dto) {
    log.info(" Erro no processamento da compra {}.", dto.getIdentifier());
    dto.setStatus(Status.ERROR);
    kafkaTemplate.send(topicResult, dto);
  }

  private void shopSuccess(ShopDto dto) {
    log.info(" Compra {} efetuada com sucesso.", dto.getIdentifier());
    dto.setStatus(Status.SUCESS);
    kafkaTemplate.send(topicResult, dto);
  }

}
