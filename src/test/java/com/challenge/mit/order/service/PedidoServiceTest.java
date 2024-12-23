package com.challenge.mit.order.service;

import com.challenge.mit.order.dto.PedidoDto;
import com.challenge.mit.order.dto.ProdutoDto;
import com.challenge.mit.order.model.Pedido;
import com.challenge.mit.order.model.Produto;
import com.challenge.mit.order.redis.Redis;
import com.challenge.mit.order.repository.PedidoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PedidoServiceTest {

    @Mock
    private PedidoRepository pedidoRepository;

    @Mock
    private Redis redisTemplate;

    @InjectMocks
    private PedidoService pedidoService;



    @Test
    void testProcessarPedido_Success() {
        // Arrange
        Pedido pedido = new Pedido();
        pedido.setCodigoPedido("123");
        pedido.setProdutos(List.of(new Produto("Produto A", BigDecimal.TEN, 2, pedido)));

        when(pedidoRepository.save(pedido)).thenReturn(pedido);

        // Act
        pedidoService.processarPedido(pedido);

        // Assert
        assertEquals(PedidoService.STATUS_PROCESSADO, pedido.getStatus());
        verify(pedidoRepository, times(1)).save(pedido);
    }

    @Test
    void testProcessarPedido_Error() {
        Pedido pedido = spy(new Pedido());
        pedido.setCodigoPedido("123");
        pedido.setProdutos(Collections.emptyList()); // No products to trigger validation error

        doNothing().when(pedido).setStatus(anyString()); // Spy on setStatus calls

        // Act
        pedidoService.processarPedido(pedido);

        // Assert
        verify(pedido, times(1)).setStatus(PedidoService.STATUS_ERRO);
        verify(pedidoRepository, times(1)).save(pedido);


    }

    @Test
    void testSalvarPedidosEmLote_Success() {
        // Arrange
        PedidoDto pedidoDto = new PedidoDto();
        pedidoDto.setCodigoPedido("123");
        pedidoDto.setProdutos(List.of(new ProdutoDto("Produto A", BigDecimal.TEN, 1)));

        List<PedidoDto> pedidosDto = Collections.singletonList(pedidoDto);

        when(redisTemplate.hasKey("pedido:123")).thenReturn(false);

        // Act
        pedidoService.salvarPedidosEmLote(pedidosDto);

        // Assert
        verify(pedidoRepository, times(1)).saveAll(anyList());
   }

    @Test
    void testSalvarPedidosEmLote_Duplicate() {
        // Arrange
        PedidoDto pedidoDto = new PedidoDto();
        pedidoDto.setCodigoPedido("123");

        List<PedidoDto> pedidosDto = Collections.singletonList(pedidoDto);

        when(redisTemplate.hasKey("pedido:123")).thenReturn(true);

        // Act
        pedidoService.salvarPedidosEmLote(pedidosDto);

        // Assert
        verify(pedidoRepository, never()).saveAll(anyList());
    }

    @Test
    void testBuscarPedidos() {
        // Arrange
        Pedido pedido = new Pedido();
        pedido.setCodigoPedido("123");
        pedido.setProdutos(List.of(new Produto("Produto A", BigDecimal.TEN, 1, pedido)));


        Page<Pedido> page = new PageImpl<>(List.of(pedido));
        when(pedidoRepository.findByStatus(eq("PENDENTE"), any(Pageable.class))).thenReturn(page);

        // Act
        Page<PedidoDto> result = pedidoService.buscarPedidos("PENDENTE", Pageable.unpaged());

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("123", result.getContent().get(0).getCodigoPedido());
    }

    @Test
    void testCalcularValorTotal() {
        // Arrange
        Pedido pedido = new Pedido();
        pedido.setProdutos(List.of(
                new Produto("Produto A", BigDecimal.TEN, 2, pedido),
                new Produto("Produto B", BigDecimal.valueOf(5), 3, pedido)
        ));

        // Act
        BigDecimal valorTotal = pedidoService.calcularValorTotal(pedido);

        // Assert
        assertEquals(BigDecimal.valueOf(35), valorTotal);
    }

}
