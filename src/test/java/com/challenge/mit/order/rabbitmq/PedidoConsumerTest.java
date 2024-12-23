package com.challenge.mit.order.rabbitmq;

import com.challenge.mit.order.dto.PedidoDto;
import com.challenge.mit.order.rabbitmq.PedidoConsumer;
import com.challenge.mit.order.service.PedidoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PedidoConsumerTest {

    @Mock
    private PedidoService pedidoService;

    @InjectMocks
    private PedidoConsumer pedidoConsumer;


    @Test
    void testConsumirMensagem_Success() {
        // Arrange
        PedidoDto pedidoDto1 = new PedidoDto().setCodigoPedido("123");
        PedidoDto pedidoDto2 = new PedidoDto().setCodigoPedido("456");
        List<PedidoDto> pedidosDto = List.of(pedidoDto1, pedidoDto2);

        // Act
        pedidoConsumer.consumirMensagem(pedidosDto);

        // Assert
        verify(pedidoService, times(1)).salvarPedidosEmLote(pedidosDto);
    }

    @Test
    void testConsumirMensagem_EmptyList() {
        // Arrange
        List<PedidoDto> pedidosDto = List.of();

        // Act
        pedidoConsumer.consumirMensagem(pedidosDto);

        // Assert
        verify(pedidoService, times(1)).salvarPedidosEmLote(pedidosDto);
    }

    @Test
    void testConsumirMensagem_NullList() {
        // Act
        pedidoConsumer.consumirMensagem(null);

        // Assert
        verify(pedidoService, times(1)).salvarPedidosEmLote(null);
    }
}
