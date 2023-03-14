package com.casa.codigo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.casa.codigo.model.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {

  Optional<Product> findByIdentifier(String identifier);
}
