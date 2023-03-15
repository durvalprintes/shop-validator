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
import com.casa.codigo.dto.ShopStatusDto;
import com.casa.codigo.model.Product;
import com.casa.codigo.repository.ProductRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ShopListener {

  @Value(value = "${topic.shop.status}")
  private String topicStatus;

  private final KafkaTemplate<String, ShopStatusDto> kafkaTemplate;

  private final ProductRepository repository;

  @KafkaListener(topics = "${topic.shop.validator}", groupId = "group")
  public void listenShopTopic(ShopDto dto) {
    try {
      log.info("Compra recebida no tópico: {}.", dto.getIdentifier());
      List<ShopItemDto> validItems = dto.getItems().stream().takeWhile(this::validShop).toList();
      if (validItems.size() == dto.getItems().size())
        sendShopSuccess(dto);
      else
        sendShopError(dto);
    } catch (Exception e) {
      log.error("Error: ", e.getMessage());
      sendShopError(dto);
    }
  }

  private boolean validShop(ShopItemDto item) {
    Optional<Product> product = repository.findByIdentifier(item.getProductIdentifier());
    return product.isPresent() && product.get().getAmount() >= item.getAmount();
  }

  private void sendShopError(ShopDto dto) {
    log.info("Erro no processamento da compra {}.", dto.getIdentifier());
    kafkaTemplate.send(topicStatus, new ShopStatusDto(dto.getIdentifier(), Status.ERROR));
  }

  private void sendShopSuccess(ShopDto dto) {
    log.info("Compra {} efetuada com sucesso.", dto.getIdentifier());
    kafkaTemplate.send(topicStatus, new ShopStatusDto(dto.getIdentifier(), Status.SUCESS));
  }

}
