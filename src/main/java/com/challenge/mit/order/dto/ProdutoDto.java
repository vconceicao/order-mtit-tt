package com.challenge.mit.order.dto;

import com.challenge.mit.order.model.Pedido;
import com.challenge.mit.order.model.Produto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * DTO for {@link Produto}
 */
public class ProdutoDto implements Serializable {
    private String nome;
    private BigDecimal valor;
    private Integer quantidade;


    public ProdutoDto() {
    }

    public ProdutoDto(String nome, BigDecimal valor, Integer quantidade) {
        this.nome = nome;
        this.valor = valor;
        this.quantidade = quantidade;
    }

    public String getNome() {
        return nome;
    }

    public ProdutoDto setNome(String nome) {
        this.nome = nome;
        return this;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public ProdutoDto setValor(BigDecimal valor) {
        this.valor = valor;
        return this;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public ProdutoDto setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProdutoDto entity = (ProdutoDto) o;
        return Objects.equals(this.nome, entity.nome) &&
                Objects.equals(this.valor, entity.valor) &&
                Objects.equals(this.quantidade, entity.quantidade);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nome, valor, quantidade);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" +
                "nome = " + nome + ", " +
                "valor = " + valor + ", " +
                "quantidade = " + quantidade + ")";
    }


    public Produto converterParaModel(Pedido pedido){

        return new Produto(this.nome, this.valor, this.quantidade, pedido);
    }
}
