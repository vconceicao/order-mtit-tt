package com.challenge.mit.order.service;

import com.challenge.mit.order.dto.PedidoDto;
import com.challenge.mit.order.dto.ProdutoDto;
import com.challenge.mit.order.model.Pedido;
import com.challenge.mit.order.model.Produto;
import com.challenge.mit.order.redis.Redis;
import com.challenge.mit.order.repository.PedidoRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class PedidoService {
    private static final int BATCH_SIZE = 100;
    public static final String STATUS_PROCESSADO = "PROCESSADO";
    public static final String STATUS_PENDENTE = "PENDENTE";
    public static final String STATUS_ERRO = "ERRO";
    private final PedidoRepository pedidoRepository;
    private final RedisTemplate<String, String> redisTemplate;

    private final Redis redis;

    private static final Logger logger = LoggerFactory.getLogger(PedidoService.class);


    public PedidoService(PedidoRepository pedidoRepository, RedisTemplate<String, String> redisTemplate, Redis redis) {
        this.pedidoRepository = pedidoRepository;
        this.redisTemplate = redisTemplate;
        this.redis = redis;
    }

    @Transactional
    public void processarPedido(Pedido pedido) {
        try{
        // Validar o pedido
        validarPedido(pedido);

        // Calcular valor total do pedido
        BigDecimal valorTotal = calcularValorTotal(pedido);

        // Atualizar o status e o valor total
        pedido.setValorTotal(valorTotal);
        pedido.setStatus(STATUS_PROCESSADO);

        // Salvar alterações no banco
        pedidoRepository.save(pedido);
        logger.error("Pedido processado com sucesso {}", pedido.getCodigoPedido());


        } catch (Exception e) {
        // Atualizar o status do pedido para ERRO
        pedido.setStatus(STATUS_ERRO);

        // Salvar o pedido com o status de erro no banco
        pedidoRepository.save(pedido);

        // Lançar a exceção novamente para tratamento superior, se necessário
        logger.error("Erro ao processar o pedido {}", pedido.getCodigoPedido());
        logger.error(e.getMessage());
    }
    }

    private void validarPedido(Pedido pedido) {
        if (pedido.getProdutos() == null || pedido.getProdutos().isEmpty()) {
            throw new IllegalArgumentException("Pedido deve conter ao menos um produto.");
        }

        for (Produto produto : pedido.getProdutos()) {
            if (produto.getQuantidade() <= 0) {
                throw new IllegalArgumentException("Produto '" + produto.getNome() + "' tem quantidade inválida.");
            }
            if (produto.getValor() == null || produto.getValor().compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("Produto '" + produto.getNome() + "' tem preço inválido.");
            }
        }
    }

    BigDecimal calcularValorTotal(Pedido pedido) {
        return pedido.getProdutos().stream()
                .map(produto -> produto.getValor().multiply(BigDecimal.valueOf(produto.getQuantidade())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // Busca paginada com filtro por status
    public Page<PedidoDto> buscarPedidos(String status, Pageable pageable) {
        Page<Pedido> pedidos;

        if (status != null && !status.isEmpty()) {
            pedidos = pedidoRepository.findByStatus(status, pageable);
        } else {
            pedidos = pedidoRepository.findAll(pageable);
        }

        // Convert Page<Pedido> to Page<PedidoDto>
        return pedidos.map(this::convertToDto);
    }

    private PedidoDto convertToDto(Pedido pedido) {
        return new PedidoDto()
                .setCodigoPedido(pedido.getCodigoPedido())
                .setProdutos(
                        pedido.getProdutos().stream()
                                .map(produto -> new ProdutoDto(produto.getNome(), produto.getValor() ,produto.getQuantidade()))
                                .toList()
                );
    }

    @Transactional
    public void salvarPedidosEmLote(List<PedidoDto> pedidosDto) {
        List<Pedido> pedidos = new ArrayList<>();
        List<String> redisKeys = new ArrayList<>();

        for (PedidoDto pedidoDto : pedidosDto) {
            String redisKey = "pedido:" + pedidoDto.getCodigoPedido();

            // Verificar duplicação no Redis
            if (Boolean.TRUE.equals(redis.hasKey(redisKey))) {
                logger.info("Pedido duplicado ignorado: {}", pedidoDto.getCodigoPedido());
                continue;
            }

            // Criar entidade Pedido
            Pedido pedido = new Pedido();
            pedido.setCodigoPedido(pedidoDto.getCodigoPedido());
            pedido.setStatus(STATUS_PENDENTE); // Mudar para enum
            pedido.setProdutos(pedidoDto.getProdutos().stream().map(p -> p.converterParaModel(pedido)).toList());

            pedidos.add(pedido);
            redisKeys.add(redisKey);

            // Inserir em lotes quando o limite for alcançado
            if (pedidos.size() >= BATCH_SIZE) {
                salvarLote(pedidos, redisKeys);
                pedidos.clear();
                redisKeys.clear();
            }
        }

        // Inserir os pedidos restantes
        if (!pedidos.isEmpty()) {
            salvarLote(pedidos, redisKeys);
        }
    }

    private void salvarLote(List<Pedido> pedidos, List<String> redisKeys) {
        // Salvar todos os pedidos no banco
        pedidoRepository.saveAll(pedidos);

        redis.addKeys(redisKeys);

      logger.info("Lote de {} pedidos processado.", pedidos.size());
    }
}
