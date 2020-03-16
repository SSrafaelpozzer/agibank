package br.com.southsystem.desafio.agibank.model;

import java.math.BigDecimal;
import java.util.List;

public class Venda {
	private final long saleId;
	private final String vendedor;
	private final List<ItemVenda> itens;
	
	
	public Venda(long saleId, String vendedor, List<ItemVenda> itens) {
		super();
		this.saleId = saleId;
		this.vendedor = vendedor;
		this.itens = itens;
	}

	public long getSaleId() {
		return saleId;
	}

	public String getVendedor() {
		return vendedor;
	}
	
	public List<ItemVenda> getItens() {
		return itens;
	}

	public BigDecimal getTotal() {
		BigDecimal total = BigDecimal.ZERO;
		for (ItemVenda item : itens) {
			total = total.add(item.getPreco().multiply(new BigDecimal(item.getQuantidade())));
		}
		return total;
	}

	

	
}
