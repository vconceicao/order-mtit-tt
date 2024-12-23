package com.challenge.mit.order.controller;

import com.challenge.mit.order.dto.PedidoDto;
import com.challenge.mit.order.service.PedidoService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/pedidos")
public class PedidoController {

    private final PedidoService pedidoService;

    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    @GetMapping
    public Page<PedidoDto> listarPedidos(
            @RequestParam(defaultValue = "0") int page, // Número da página
            @RequestParam(defaultValue = "10") int size, // Tamanho da página
            @RequestParam(defaultValue = "id") String sortBy, // Campo de ordenação
            @RequestParam(defaultValue = "ASC") String sortDirection, // Direção da ordenação
            @RequestParam(required = false) String status // Filtro por status
    ) {
        // Configurar a ordenação
        Sort.Direction direction = Sort.Direction.fromString(sortDirection);
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        // Buscar pedidos
        return pedidoService.buscarPedidos(status, pageable);
    }
}
