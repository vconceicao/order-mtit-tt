package com.challenge.mit.order.rabbitmq;

import com.challenge.mit.order.dto.PedidoDto;
import com.challenge.mit.order.service.PedidoService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PedidoConsumer {

    private final PedidoService pedidoService;

    public PedidoConsumer(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    @RabbitListener(queues = RabbitMQConfig.QUEUE)
    public void consumirMensagem(List<PedidoDto> pedidosDTO) {
        pedidoService.salvarPedidosEmLote(pedidosDTO);
    }
}
