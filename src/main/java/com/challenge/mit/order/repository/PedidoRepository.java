package com.challenge.mit.order.repository;

import com.challenge.mit.order.model.Pedido;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    List<Pedido> findByStatus(String status);

    Page<Pedido> findByStatus(String status, Pageable pageable);

}
