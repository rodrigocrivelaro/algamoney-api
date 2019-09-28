package com.example.algamoney.api.resource;

import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.algamoney.api.event.RecursoCriadoEvent;
import com.example.algamoney.api.model.Categoria;
import com.example.algamoney.api.repository.CategoriaRepository;

@RestController // Informo que essa classe é um Controller do REST
@RequestMapping("/categorias") // Todos os métodos serão executados quando na URI tiver /categorias
public class CategoriaResource {
	
	@Autowired // Injeção de Dependência
	private CategoriaRepository categoriaRepository;
	
	@Autowired
	private ApplicationEventPublisher publisher;
	
	/**
	 * Retorna todas as categorias utilizando o método findAll() do JpaRepository 
	 * 
	 * @return List<Categoria> - uma lista de categorias
	 */
	@GetMapping // Método GET
	public List<Categoria> listar() {
		return categoriaRepository.findAll();
	}
	
	/**
	 * Quando o recurso é salvo, retorna o status 201 Created com o ResponseEntity.created(uri), conforme as regras do REST
	 * Se passar o retorno ResponseEntity<Categoria> não precisa da notação @ResponseStatus(HttpStatus.CREATED)
	 * 
	 * @param categoria, response 
	 * @return ResponseEntity.created(uri).body(categoriaSalva) - categoriaSalva para o body e o status 201 Created
	 */
	@PostMapping // Método POST
//	@ResponseStatus(HttpStatus.CREATED) 
	public ResponseEntity<Categoria> criar(@Valid @RequestBody Categoria categoria, HttpServletResponse response) {
		// Executa o método save do JpaRepository e retorna a categoria salva
		Categoria categoriaSalva = categoriaRepository.save(categoria);
		
		// Antes da implementação do evento
		// Retorna a uri da categoria salva
//		URI uri = ServletUriComponentsBuilder.fromCurrentRequestUri().path("/{codigo}")
//			.buildAndExpand(categoriaSalva.getCodigo()).toUri();
//		
//		// Pelas regras do REST, no POST, é necessário devolver no HEADER da resposta a "Location" com a URI do recurso criado
//		response.setHeader("Location", uri.toASCIIString());
		
		
		// Depois da implementação do evento
		publisher.publishEvent(new RecursoCriadoEvent(this, response, categoriaSalva.getCodigo()));
		
		return ResponseEntity.status(HttpStatus.CREATED).body(categoriaSalva);
	}

	/**
	 * Retorna uma categoria informado um ID utilizando o método findOne(codigo) do JpaRepository 
	 * @param codigo (ID)
	 * @return Categoria
	 */
	@GetMapping("/{codigo}") // Ja existe o método GET para /categorias, por isso é necessário mapear outros métodos GET com um novo PATH
	public ResponseEntity<Categoria> buscarPeloCodigo(@PathVariable Long codigo, HttpServletResponse response) {
		if (categoriaRepository.findOne(codigo) != null) {
			return ResponseEntity.ok().body(categoriaRepository.findOne(codigo));
		} else {
			return ResponseEntity.notFound().build();
		}
			
		
	}
}
