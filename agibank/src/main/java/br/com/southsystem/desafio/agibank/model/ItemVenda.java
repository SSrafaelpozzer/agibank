package br.com.southsystem.desafio.agibank.model;

import java.math.BigDecimal;

public class ItemVenda {

	private final long idItem;
	private final long quantidade;
	private final BigDecimal preco;
	
	public ItemVenda(long idItem, long quantidade, BigDecimal preco) {
		super();
		this.idItem = idItem;
		this.quantidade = quantidade;
		this.preco = preco;
	}

	public long getIdItem() {
		return idItem;
	}

	public long getQuantidade() {
		return quantidade;
	}

	public BigDecimal getPreco() {
		return preco;
	}
	
}
