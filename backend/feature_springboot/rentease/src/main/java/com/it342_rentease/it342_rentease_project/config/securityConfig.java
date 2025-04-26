package com.it342_rentease.it342_rentease_project.config;

import com.it342_rentease.it342_rentease_project.security.JwtFilter;
import com.it342_rentease.it342_rentease_project.service.OwnerDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
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

import java.util.List;

@Configuration
@EnableWebSecurity
public class securityConfig {

    @Autowired
    private JwtFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        return http
                .cors().configurationSource(corsConfigurationSource()).and()
                .csrf().disable()
                .authorizeHttpRequests(authorizeRequests -> authorizeRequests
                        .requestMatchers("/owners/register").permitAll()
                        .requestMatchers("/owners/login").permitAll()
                        .requestMatchers("/owners").permitAll()
                        .requestMatchers("/owners/current-user").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/renters").permitAll()
                        .requestMatchers("/api/renters/login").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/renters/current-user").authenticated()
                        .requestMatchers(HttpMethod.PATCH, "/api/renters/update-name").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/renters/delete").authenticated()
                        .requestMatchers("/rooms/**").permitAll()
                        .requestMatchers("/rented_units/**").permitAll()
                        .requestMatchers("/payments/**").permitAll()
                        .requestMatchers("/payment_reminders/**").permitAll()
                        .anyRequest().authenticated())
                .formLogin().disable()
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout=true")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll())
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public UrlBasedCorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowedOriginPatterns(List.of("*"));
        corsConfiguration.addAllowedMethod("*");
        corsConfiguration.addAllowedHeader("*");
        corsConfiguration.setAllowCredentials(true);

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
            DaoAuthenticationProvider daoAuthenticationProvider) throws Exception {
        return new ProviderManager(daoAuthenticationProvider);
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider(OwnerDetailsService ownerDetailsService) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(ownerDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}