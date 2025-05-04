package com.example.stock.repository;

import com.example.stock.domain.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface StockRepository extends JpaRepository<Stock, Long> {
	@Modifying
	@Query("UPDATE Stock s SET s.quantity = s.quantity - :quantity WHERE s.id = :id")
	int decreaseStock(@Param("id") Long id, @Param("quantity") Long quantity);
}
