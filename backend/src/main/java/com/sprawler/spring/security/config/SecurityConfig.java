package com.sprawler.spring.security.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

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

    @Bean("filterChain")
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(Customizer.withDefaults())
                .httpBasic(Customizer.withDefaults())
                .authorizeHttpRequests(authorize -> authorize
                                .requestMatchers("/health")
                                .permitAll()
                                .requestMatchers("/spring/security/user")
                                .hasRole("USER")
                                .requestMatchers("/spring/security/admin")
                                .hasRole("ADMIN")
                                .anyRequest()
                                .permitAll())
                .logout(logout -> logout
                        .logoutUrl("/logout") // The URL to trigger logout
                        .logoutSuccessUrl("http://localhost:3000/login")
                        .invalidateHttpSession(true) // Invalidate session
                        .deleteCookies("JSESSIONID") // Delete cookies if needed
                        .permitAll())
                .build();
    }
}
