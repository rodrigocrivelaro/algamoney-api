package com.example.algamoney.api.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.InMemoryTokenStore;

@Configuration
@EnableAuthorizationServer
public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {
	
	@Autowired
	private AuthenticationManager authenticationManager;
	
	@Override
	public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
		clients.inMemory()
			.withClient("angular") // Usuário e senha da aplicação cliente
			.secret("@ngul@r0") // Usuário e senha da aplicação cliente
			.scopes("read", "write")
			.authorizedGrantTypes("password")
			.accessTokenValiditySeconds(1800); // Tempo de vida do Access Token 30min
	}
	
	/**
	 * Nota sobre o Access Token no Postman:
	 * Para fazer a requisição do access token é preciso criar um POST em localhost:8080/oauth/token, 
	 * na aba Authorization escolher o tipo Basic Auth passando o usuário e senha de cliente 
	 * e não de usuário e na aba Body escolher x-www-form-urlencoded e criar as propriedades
	 * client = nome do cliente, no caso angular
	 * username = usuário da API, no caso admim
	 * password = senha do usuário da API, no caso admim
	 * grant_type = password
	 * ao clicar em enviar retornará um json com o accsess_token e o token_type a ser utilizado
	 * lembrando que o access_tokem dura o tempo definido acima em .accessTokenValiditySeconds(1800);
	 * Após fazer a requisição do access token deverá ser criado na rotina que esta tentando acessar
	 * a API um Header Authorization = Beare e o access token gerado.
	 * Lembrar que na guia Authorization o tipo de autenticação deve ser No Auth, pois a validação
	 * já está sendo feita no Header.
	 */
	
	@Override
	public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
		endpoints
			.tokenStore(tokenStore())
			.authenticationManager(authenticationManager);
	}

	@Bean
	public TokenStore tokenStore() {
		return new InMemoryTokenStore();
	}
}
