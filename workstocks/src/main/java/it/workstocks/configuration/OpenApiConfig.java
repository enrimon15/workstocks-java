package it.workstocks.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

@Configuration
public class OpenApiConfig {
	
	@Value("${spring.application.name}")
	private String moduleName;
	
	@Value("${spring.application.version}")
	private String apiVersion;
	
	private final String BEARER_FORMAT = "JWT";
	private final String SECURITY_SCHEME_NAME = "bearerAuth";
	private final String SECURITY_SCHEME = "bearer";
	
	@Bean
	  public OpenAPI customOpenAPI() {
	    final String securitySchemeName = SECURITY_SCHEME_NAME;
	    final String apiTitle = String.format("%s API", moduleName);
	    return new OpenAPI()
	        .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
	        .components(
	            new Components()
	                .addSecuritySchemes(securitySchemeName,
	                    new SecurityScheme()
	                        .name(securitySchemeName)
	                        .type(SecurityScheme.Type.HTTP)
	                        .scheme(SECURITY_SCHEME)
	                        .bearerFormat(BEARER_FORMAT)
	                )
	        )
	        .info(new Info().title(apiTitle).version(apiVersion).description("REST API for finding company and job offers"));
	  }

}
