package com.example.algamoney.api.exceptionHandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice // Necessário para que a API entenda que essa classe controla os erros
public class AlgamoneyExceptionHandler extends ResponseEntityExceptionHandler {
	
	@Autowired
	private MessageSource messageSourse;
	
	@Override
	protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {
		
		String mensagemUsuario = messageSourse.getMessage("mensagem.invalida", null, LocaleContextHolder.getLocale()); 
		String mensagemDesenvolvedor = ex.getCause().toString();
		Erro mensagemBody = new Erro(mensagemUsuario, mensagemDesenvolvedor);
		status = HttpStatus.BAD_REQUEST;
		
		List<Erro> erros = Arrays.asList(mensagemBody);
		
		return handleExceptionInternal(ex, erros, headers, status, request); 
	}
	
	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {
		
		status = HttpStatus.BAD_REQUEST;
		List<Erro> erros = criarListaDeErros(ex.getBindingResult());
		
		return handleExceptionInternal(ex, erros, headers, status, request);
	}
	
	private List<Erro> criarListaDeErros(BindingResult bindingResult) {
		List<Erro> erros = new ArrayList<>();
		
		for (FieldError fieldError : bindingResult.getFieldErrors()) {
			String mensagemUsuario = messageSourse.getMessage(fieldError, LocaleContextHolder.getLocale());
			String mensagemDesenvolvedor = fieldError.toString();
			erros.add(new Erro(mensagemUsuario, mensagemDesenvolvedor));
		}
		
		return erros;
	}
	
	/**
	 * Classe criada para poder retornar as duas mensagems para o body
	 * mensagemUsuario
	 * mensagemDesenvolvedor
	 * @author rocr
	 *
	 */
	public static class Erro {
		private String mensagemUsuario;
		private String mensagemDesenvolvedor;
		
		public Erro(String mensagemUsuario, String mensagemDesenvolvedor) {
			this.mensagemUsuario = mensagemUsuario;
			this.mensagemDesenvolvedor = mensagemDesenvolvedor;
		}

		/**
		 * @return the mensagemUsuario
		 */
		public String getMensagemUsuario() {
			return mensagemUsuario;
		}

		/**
		 * @return the mensagemDesenvolvedor
		 */
		public String getMensagemDesenvolvedor() {
			return mensagemDesenvolvedor;
		}
	}
}
