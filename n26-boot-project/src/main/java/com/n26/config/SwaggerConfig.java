package com.n26.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

@Configuration
public class SwaggerConfig {

	@Bean
	public Docket docket() {
		return new Docket(DocumentationType.SWAGGER_2).select()
				.apis(RequestHandlerSelectors.basePackage("com.n26.controller")).paths(PathSelectors.any()).build()
				.apiInfo(apiInfo());
	}

	private ApiInfo apiInfo() {
		return new ApiInfoBuilder().license("Apache 2.0").version("Version 1.0 - mw").termsOfServiceUrl("urn:tos")
				.title("N26 Statistics Application").licenseUrl("http://www.apache.org/licenses/LICENSE-2.0")
				.contact(new Contact("Aravind", null, "piratlaaru@gmail.com"))
				.description("Statistics API ")
				.build();
	}

}
