package com.it342_rentease.it342_rentease_project.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.it342_rentease.it342_rentease_project.security.JwtFilter;
import com.it342_rentease.it342_rentease_project.service.OwnerDetailsService;

@Configuration
@EnableWebSecurity
public class securityConfig {

    @Autowired
    private JwtFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        return http
            .cors().configurationSource(corsConfigurationSource()).and()
            .csrf().disable()  // Disable CSRF for stateless JWT authentication
            .authorizeHttpRequests(authorizeRequests -> 
                authorizeRequests
                .requestMatchers("/owners/register").permitAll()
                .requestMatchers("/api/renters/register", "/api/renters/login","/api/renters").permitAll() 
                .requestMatchers("/owners/login").permitAll()               
                    .requestMatchers("/rooms/**").permitAll()
                    .requestMatchers("/rented_units/**").permitAll()
                    .requestMatchers("/payments/**").permitAll()
                    .requestMatchers("/payment_reminders/**").permitAll()
                    .requestMatchers("/owners").permitAll()
                    .requestMatchers("/owners/current-user").authenticated()  // Explicitly protect this endpoint
                    .requestMatchers("/owners/current").authenticated()  // Explicitly protect this endpoint
                    .anyRequest().authenticated()
            )
            .formLogin().disable()
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout=true")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)  // Use stateless sessions for JWT
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)  // Add JWT filter
            .build();
    }

    @Bean
    public UrlBasedCorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.addAllowedOrigin("http://localhost:5173");
        corsConfiguration.addAllowedMethod("*");
        corsConfiguration.addAllowedHeader("*");
        corsConfiguration.setAllowCredentials(true);  // Allow cookies/credentials
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration);
        return source;
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

   @Bean
public AuthenticationManager authenticationManager(
        AuthenticationConfiguration config,
        DaoAuthenticationProvider daoAuthenticationProvider
) throws Exception {
    return new ProviderManager(daoAuthenticationProvider);
}

    @Bean
public DaoAuthenticationProvider daoAuthenticationProvider(OwnerDetailsService ownerDetailsService) {
    DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
    provider.setUserDetailsService(ownerDetailsService);
    provider.setPasswordEncoder(passwordEncoder());  // use your existing password encoder
    return provider;
}

@Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

}