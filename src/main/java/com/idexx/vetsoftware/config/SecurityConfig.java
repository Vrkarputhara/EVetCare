package com.idexx.vetsoftware.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;

import com.idexx.vetsoftware.security.JwtService;
import com.idexx.vetsoftware.service.UserService;

@Configuration
@EnableMethodSecurity(prePostEnabled = true) // replaces @EnableGlobalMethodSecurity
public class SecurityConfig {
	
	@Bean
	public JwtAuthTokenFilter authenticationJwtTokenFilter(
	        JwtService jwtUtils,
	        UserService userService) {

	    return new JwtAuthTokenFilter(jwtUtils, userService);
	}
	
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http,
	                                       JwtAuthTokenFilter authenticationJwtTokenFilter,
	                                       DaoAuthenticationProvider authenticationProvider) throws Exception {

	    http
	        .csrf(csrf -> csrf.disable())
	        .cors().and()
	        .headers(headers -> headers.frameOptions().sameOrigin())
	        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
	        .authenticationProvider(authenticationProvider)   // ⭐ ADD THIS
	        .authorizeHttpRequests(auth -> auth
	            .requestMatchers(
	                "/",
	                "/index.html",
	                "/dashboard.html",
	                "/register.html",
	                "/patients.html",
	                "/appointments.html",
	                "/login.html",
	                "/images/**"
	            ).permitAll()
	            .requestMatchers("/api/vetusers/register").permitAll()
	            .requestMatchers("/api/vetusers/login").permitAll()
	            .requestMatchers("/api/**").authenticated()
	            .anyRequest().authenticated()
	        )
	        .addFilterBefore(authenticationJwtTokenFilter, UsernamePasswordAuthenticationFilter.class);

	    return http.build();
	}

	@Bean
	public DaoAuthenticationProvider authenticationProvider(UserService userService,
	                                                         PasswordEncoder passwordEncoder) {

	    DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
	    authProvider.setUserDetailsService(userService);
	    authProvider.setPasswordEncoder(passwordEncoder);

	    return authProvider;
	}
	
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                    .allowedOrigins("*")
                    .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                    .allowedHeaders("*")
                    .allowCredentials(false)
                    .maxAge(3600);
            }
        };
    }
}