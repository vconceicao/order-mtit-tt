package com.challenge.mit.order.repository;

import com.challenge.mit.order.model.Produto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProdutoRepository extends JpaRepository<Produto, Long> {
}
