package com.challenge.mit.order.service;

import com.challenge.mit.order.dto.PedidoDto;
import com.challenge.mit.order.dto.ProdutoDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class PedidoJsonGenerator {

    public static void main(String[] args) throws Exception {
        List<PedidoDto> pedidos = new ArrayList<>();

        for (int i = 1; i <= 1000; i++) {
            PedidoDto pedido = new PedidoDto();
            pedido.setCodigoPedido("PEDIDO-" + i);

            List<ProdutoDto> produtos = List.of(
                new ProdutoDto("Produto A", new BigDecimal("50.00"), 2),
                new ProdutoDto("Produto B", new BigDecimal("30.00"), 1)
            );
            pedido.setProdutos(produtos);

            pedidos.add(pedido);
        }

        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(pedidos);
        System.out.println(json);
    }
}
