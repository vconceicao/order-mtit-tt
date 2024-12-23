package com.challenge.mit.order.controller;

import com.challenge.mit.order.dto.PedidoDto;
import com.challenge.mit.order.service.PedidoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class PedidoControllerTest {

    @Mock
    private PedidoService pedidoService;

    @InjectMocks
    private PedidoController pedidoController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testListarPedidos_Success() {
        // Arrange
        int page = 0;
        int size = 10;
        String sortBy = "id";
        String sortDirection = "ASC";
        String status = "PENDENTE";

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, sortBy));
        PedidoDto pedidoDto = new PedidoDto().setCodigoPedido("123");
        Page<PedidoDto> expectedPage = new PageImpl<>(List.of(pedidoDto));

        when(pedidoService.buscarPedidos(status, pageable)).thenReturn(expectedPage);

        // Act
        Page<PedidoDto> result = pedidoController.listarPedidos(page, size, sortBy, sortDirection, status);

        // Assert
        assertEquals(expectedPage, result);
        verify(pedidoService, times(1)).buscarPedidos(status, pageable);
    }

    @Test
    void testListarPedidos_DefaultParams() {
        // Arrange
        int defaultPage = 0;
        int defaultSize = 10;
        String defaultSortBy = "id";
        String defaultSortDirection = "ASC";
        String status = null;

        Pageable pageable = PageRequest.of(defaultPage, defaultSize, Sort.by(Sort.Direction.ASC, defaultSortBy));
        PedidoDto pedidoDto = new PedidoDto().setCodigoPedido("456");
        Page<PedidoDto> expectedPage = new PageImpl<>(List.of(pedidoDto));

        when(pedidoService.buscarPedidos(status, pageable)).thenReturn(expectedPage);

        // Act
        Page<PedidoDto> result = pedidoController.listarPedidos(defaultPage, defaultSize, defaultSortBy, defaultSortDirection, status);

        // Assert
        assertEquals(expectedPage, result);
        verify(pedidoService, times(1)).buscarPedidos(status, pageable);
    }

    @Test
    void testListarPedidos_InvalidSortDirection() {
        // Arrange
        int page = 0;
        int size = 10;
        String sortBy = "id";
        String invalidSortDirection = "INVALID";
        String status = "PROCESSADO";

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () ->
                pedidoController.listarPedidos(page, size, sortBy, invalidSortDirection, status)
        );


    }
}
