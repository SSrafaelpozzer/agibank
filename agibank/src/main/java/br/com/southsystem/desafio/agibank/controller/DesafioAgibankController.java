package br.com.southsystem.desafio.agibank.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import br.com.southsystem.desafio.agibank.service.DesafioAgibankService;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/desafioAgibank")
public class DesafioAgibankController {
	
	@Autowired
	private DesafioAgibankService service;
	
	@RequestMapping(value = "analisar")
    @ResponseStatus(HttpStatus.OK)
	public Mono<Void> analisarArquivos() {
		Mono<Void> just = Mono.just(service.readDirectory());
		return just;
	}

}
