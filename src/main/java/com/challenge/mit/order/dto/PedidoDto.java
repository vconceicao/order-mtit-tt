package com.challenge.mit.order.dto;

import com.challenge.mit.order.model.Pedido;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * DTO for {@link Pedido}
 */
public class PedidoDto implements Serializable {
    private String codigoPedido;
    private List<ProdutoDto> produtos;

    public String getCodigoPedido() {
        return codigoPedido;
    }

    public PedidoDto setCodigoPedido(String codigoPedido) {
        this.codigoPedido = codigoPedido;
        return this;
    }


    public List<ProdutoDto> getProdutos() {
        return produtos;
    }

    public PedidoDto setProdutos(List<ProdutoDto> produtos) {
        this.produtos = produtos;
        return this;
    }

}
