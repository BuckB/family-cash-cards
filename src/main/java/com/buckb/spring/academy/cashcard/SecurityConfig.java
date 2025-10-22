package com.buckb.spring.academy.cashcard;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer.FrameOptionsConfig;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // Allow access to H2 console and cashcards endpoints for testing.
        // Permit the H2 console, disable CSRF for the console path, and allow frames from same origin.
        http
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/h2-console/**").permitAll()
                        .requestMatchers("/cashcards/**").hasRole("CARD-OWNER")
                        .anyRequest().authenticated() //Ensures that all other requests not explicitly matched
                // by the above rules also require the user to be authenticated.
                )
                // Enable both form login and http basic so you can log in with a form (useful for H2 console)
                .formLogin(Customizer.withDefaults())
                .httpBasic(Customizer.withDefaults())
                .csrf(csrf -> csrf.ignoringRequestMatchers("/h2-console/**", "/cashcards/**"))
                .headers(headers -> headers.frameOptions(FrameOptionsConfig::sameOrigin))
                // 2. 💡 Configure exception handling to use Basic Authentication for unauthenticated requests
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        // For unauthenticated requests, use a BasicAuthenticationEntryPoint
                        .authenticationEntryPoint(
                                new org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint() {
                                    @Override
                                    public void commence(HttpServletRequest request, HttpServletResponse response,
                                            AuthenticationException authException) throws IOException {
                                        // Return 401 UNAUTHORIZED for API paths instead of redirecting
                                        response.setHeader("WWW-Authenticate",
                                                "Basic realm=\"" + this.getRealmName() + "\"");
                                        response.sendError(HttpServletResponse.SC_UNAUTHORIZED,
                                                authException.getMessage());
                                    }
                                }));
        return http.build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    UserDetailsService testOnlyUsers(PasswordEncoder passwordEncoder) {
        User.UserBuilder users = User.builder();
        UserDetails sarah = users
                .username("Sarah1")
                .password(passwordEncoder.encode("abc123"))
                .roles("CARD-OWNER")
                .build();

        UserDetails noCardsUser = users
                .username("john")
                .password(passwordEncoder.encode("def456"))
                .roles("NO-CARDS")
                .build();

        return new InMemoryUserDetailsManager(sarah, noCardsUser);
    }

}
