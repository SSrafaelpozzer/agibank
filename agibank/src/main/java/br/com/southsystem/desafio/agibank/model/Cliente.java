package br.com.southsystem.desafio.agibank.model;

public class Cliente {
	private final long cnpj;
	private final String name;
	private final String businessArea;
	
	public Cliente(long cnpj, String name, String businessArea) {
		this.cnpj = cnpj;
		this.name = name;
		this.businessArea = businessArea;
	}

	public long getCnpj() {
		return cnpj;
	}

	public String getName() {
		return name;
	}

	public String getBusinessArea() {
		return businessArea;
	}

	public Cliente withName(String name) {
		return new Cliente(cnpj, name, businessArea);
	}
	
	public Cliente witBusinessArea(String businessArea) {
		return new Cliente(cnpj, name, businessArea);
	}
	
}
