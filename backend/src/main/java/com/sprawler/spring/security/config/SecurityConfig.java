package com.sprawler.spring.security.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private static final Logger LOGGER = LogManager.getLogger(SecurityConfig.class);

    @Bean("passwordEncoder")
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean("userDetailsService")
    public InMemoryUserDetailsManager userDetailsService(
            @Qualifier("passwordEncoder") PasswordEncoder passwordEncoder) {
        InMemoryUserDetailsManager userManager = new InMemoryUserDetailsManager();

        userManager.createUser(User.withUsername("user")
                .password(passwordEncoder.encode("user"))
                .roles("USER")
                .build());

        userManager.createUser(User.withUsername("admin")
                .password(passwordEncoder.encode("admin"))
                .roles("USER", "ADMIN")
                .build());

        return userManager;
    }

    @Bean("corsConfig")
    public UrlBasedCorsConfigurationSource setCorsConfiguration() {
        CorsConfiguration corsConfig = new CorsConfiguration();
        corsConfig.addAllowedOrigin("http://localhost:3001");
        corsConfig.addAllowedOriginPattern("*");
        corsConfig.addAllowedMethod("POST");
        corsConfig.addAllowedMethod("GET");
        corsConfig.addAllowedHeader("*");
        corsConfig.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource sourceConfig = new UrlBasedCorsConfigurationSource();
        sourceConfig.registerCorsConfiguration("/**", corsConfig); // Apply to all paths

        return sourceConfig;
    }


    @Bean("filterChain")
    public SecurityFilterChain filterChain(HttpSecurity http,
                                           @Qualifier("corsConfig") UrlBasedCorsConfigurationSource corsSourceConfig) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(Customizer.withDefaults())
                .cors(cors -> cors.configurationSource(corsSourceConfig)) // Use custom CORS configuration
                .authorizeHttpRequests(authorize -> authorize
                                .requestMatchers("/health")
                                .permitAll()
                                .requestMatchers("/myinfo/token")
                                .permitAll()
                                .requestMatchers("/spring/security/user")
                                .hasRole("USER")
                                .requestMatchers("/spring/security/admin")
                                .hasRole("ADMIN")
                                .anyRequest()
                                .permitAll())
                .logout(logout -> logout
                        .logoutUrl("/logout") // The URL to trigger logout
                        .logoutSuccessUrl("http://localhost:3001/login")
                        .invalidateHttpSession(true) // Invalidate session
                        .deleteCookies("JSESSIONID") // Delete cookies if needed
                        .permitAll())
                .build();
    }
}
