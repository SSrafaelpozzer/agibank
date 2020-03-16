package br.com.southsystem.desafio.agibank.model;

import java.math.BigDecimal;

public class Vendedor {
	private final long cpf;
	private final String name;
	private final BigDecimal salary;
	
	public Vendedor(long cpf, String name, BigDecimal salary) {
		super();
		this.cpf = cpf;
		this.name = name;
		this.salary = salary;
	}

	public long getCpf() {
		return cpf;
	}

	public String getName() {
		return name;
	}

	public BigDecimal getSalary() {
		return salary;
	}

	public Vendedor withSalary(BigDecimal salary) {
		return new Vendedor(cpf, name, salary);
	}
	
}
