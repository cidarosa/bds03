package com.devsuperior.bds03.config;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

@Configuration
@EnableResourceServer
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {

	//liberar o H2
	@Autowired
	private Environment env;
	
	
	@Autowired
	private JwtTokenStore tokenStore;
	
	//configurar as rotas
	//GET
	private static final String[] PUBLIC = {"/oauth/token", "/h2-console/**"}; //todos podem acessar
	
	//Rotas para operador ou admin
	private static final String[] OPERATOR_GET = {"/departments/**", "/employees/**"};
	
	//Rotas somente para admin
	//private static final String[] ADMIN = {"/users/**"};
	
	@Override
	public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
		resources.tokenStore(tokenStore);		
	}

	@Override
	public void configure(HttpSecurity http) throws Exception {
		
		//liberando o H2
		if(Arrays.asList(env.getActiveProfiles()).contains("test")) {
			http.headers().frameOptions().disable();
		}
		
		//configurando quem pode acessar o que
		http.authorizeRequests()
		.antMatchers(PUBLIC).permitAll()
		.antMatchers(HttpMethod.GET, OPERATOR_GET).hasAnyRole("OPERATOR", "ADMIN")//tá no DB
		.anyRequest().hasAnyRole("ADMIN"); //qq outra exige autenticação
	}
}
