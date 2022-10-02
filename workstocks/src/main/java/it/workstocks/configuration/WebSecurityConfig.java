package it.workstocks.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import it.workstocks.exception.handler.CustomAccessDeniedHandler;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
	
	@Autowired
	private UserDetailsService userDetailsService;

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
	}
	
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Bean
	public CustomAccessDeniedHandler deniedHandler() {
		return new CustomAccessDeniedHandler();
	}
	
	@Override
	protected void configure(HttpSecurity httpSecurity) throws Exception {
		httpSecurity.headers().disable().csrf().disable()
				.formLogin()
					.permitAll()
					.loginPage("/login")
					.loginProcessingUrl("/login")
					.usernameParameter("email")
					.failureUrl("/login?error=invalidlogin")
					.defaultSuccessUrl("/", false)
				.and()
				.logout()
					.permitAll()
					.logoutSuccessUrl("/")
					.invalidateHttpSession(true)
				.and().exceptionHandling().accessDeniedHandler(deniedHandler())
				.and().authorizeRequests()
				// Specificare le url che sono soggette ad autenticazione ed autorizzazione
				.antMatchers("/", "/register/**", "/admin-register/**", "/reset-password/**", "/public/**", "/error/**", "/css/**", "/js/**", "/img/**", "/fonts/**", "/favicon.ico", "/**/*.css", "/**/*.js").permitAll()
				.antMatchers("/common/**").hasAnyRole("APPLICANT", "COMPANY_OWNER","ADMIN")
				.antMatchers("/applicant/**").hasRole("APPLICANT")
				.antMatchers("/company/**").hasRole("COMPANY_OWNER")
				.antMatchers("/admin/**").hasRole("ADMIN")
				.anyRequest().authenticated();
	}

}
