package com.challenge.mit.order.service;

import com.challenge.mit.order.model.Pedido;
import com.challenge.mit.order.repository.PedidoRepository;
import com.challenge.mit.order.service.PedidoScheduler;
import com.challenge.mit.order.service.PedidoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.mockito.Mockito.*;

class PedidoSchedulerTest {

    @Mock
    private PedidoRepository pedidoRepository;

    @Mock
    private PedidoService pedidoService;

    @InjectMocks
    private PedidoScheduler pedidoScheduler;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testProcessarPedidosPendentes_WithPendingOrders() {
        // Arrange
        Pedido pedido1 = new Pedido();
        pedido1.setCodigoPedido("123");
        pedido1.setStatus("PENDENTE");

        Pedido pedido2 = new Pedido();
        pedido2.setCodigoPedido("456");
        pedido2.setStatus("PENDENTE");

        List<Pedido> pedidosPendentes = List.of(pedido1, pedido2);

        when(pedidoRepository.findByStatus("PENDENTE")).thenReturn(pedidosPendentes);

        // Act
        pedidoScheduler.processarPedidosPendentes();

        // Assert
        verify(pedidoRepository, times(1)).findByStatus("PENDENTE");
        verify(pedidoService, times(1)).processarPedido(pedido1);
        verify(pedidoService, times(1)).processarPedido(pedido2);
    }

    @Test
    void testProcessarPedidosPendentes_NoPendingOrders() {
        // Arrange
        when(pedidoRepository.findByStatus("PENDENTE")).thenReturn(List.of());

        // Act
        pedidoScheduler.processarPedidosPendentes();

        // Assert
        verify(pedidoRepository, times(1)).findByStatus("PENDENTE");
        verify(pedidoService, never()).processarPedido(any(Pedido.class));
    }

    @Test
    void testProcessarPedidosPendentes_ExceptionWhileProcessing() {
        // Arrange
        Pedido pedido1 = new Pedido();
        pedido1.setCodigoPedido("123");
        pedido1.setStatus("PENDENTE");

        List<Pedido> pedidosPendentes = List.of(pedido1);

        when(pedidoRepository.findByStatus("PENDENTE")).thenReturn(pedidosPendentes);
        doThrow(new RuntimeException("Simulated exception")).when(pedidoService).processarPedido(pedido1);

        // Act
        pedidoScheduler.processarPedidosPendentes();

        // Assert
        verify(pedidoRepository, times(1)).findByStatus("PENDENTE");
        verify(pedidoService, times(1)).processarPedido(pedido1);
    }
}
