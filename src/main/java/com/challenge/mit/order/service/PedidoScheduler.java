package com.challenge.mit.order.service;

import com.challenge.mit.order.model.Pedido;
import com.challenge.mit.order.repository.PedidoRepository;
import jakarta.transaction.Transactional;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PedidoScheduler {

    private final PedidoRepository pedidoRepository;
    private final PedidoService pedidoService;

    public PedidoScheduler(PedidoRepository pedidoRepository, PedidoService pedidoService) {
        this.pedidoRepository = pedidoRepository;
        this.pedidoService = pedidoService;
    }

    @Scheduled(fixedRate = 60000) // Executa a cada 60 segundos
    @Transactional
    public void processarPedidosPendentes() {
        // Buscar pedidos com status "PENDENTE"
        List<Pedido> pedidosPendentes = pedidoRepository.findByStatus("PENDENTE");

        if (pedidosPendentes.isEmpty()) {
            System.out.println("Nenhum pedido pendente encontrado.");
            return;
        }

        System.out.println("Processando pedidos pendentes...");
        for (Pedido pedido : pedidosPendentes) {
            try {
                // Processar cada pedido
                pedidoService.processarPedido(pedido);
                System.out.println("Pedido processado: " + pedido.getCodigoPedido());
            } catch (Exception e) {
                System.err.println("Erro ao processar pedido " + pedido.getCodigoPedido() + ": " + e.getMessage());
            }
        }
    }

}
