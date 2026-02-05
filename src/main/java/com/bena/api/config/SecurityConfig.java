package com.bena.api.config;

import com.bena.api.module.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

        private final JwtAuthenticationFilter jwtAuthFilter;
        private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
        private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
        private final UserRepository userRepository;

        @Bean
        public PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                http
                                .csrf(AbstractHttpConfigurer::disable)
                                .cors(cors -> cors.configure(http))
                                .sessionManagement(session -> session.sessionCreationPolicy(
                                                SessionCreationPolicy.STATELESS))
                                .authenticationProvider(authenticationProvider())
                                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                                .exceptionHandling(ex -> ex
                                                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                                                .accessDeniedHandler(jwtAccessDeniedHandler))
                                .authorizeHttpRequests(auth -> auth
                                                // Public endpoints - Authentication
                                                .requestMatchers(
                                                                "/v1/auth/login",
                                                                "/v1/auth/register",
                                                                "/v1/auth/google",
                                                                "/v1/auth/apple",
                                                                "/v1/auth/forgot-password",
                                                                "/v1/auth/reset-password",
                                                                "/v1/auth/verify-email",
                                                                "/v1/auth/resend-verification",
                                                                "/v1/auth/verify-reset-token")
                                                .permitAll()

                                                .requestMatchers(
                                                                "/ws",
                                                                "/ws/**")
                                                .permitAll()

                                                // Public endpoints - Documentation
                                                .requestMatchers(
                                                                "/swagger-ui/**",
                                                                "/swagger-ui.html",
                                                                "/api-docs/**",
                                                                "/v3/api-docs/**",
                                                                "/actuator/health")
                                                .permitAll()

                                                // Public endpoints - Read-only access
                                                .requestMatchers(HttpMethod.GET, "/v1/workers/**").permitAll()
                                                .requestMatchers(HttpMethod.GET, "/v1/cost/**").permitAll()
                                                .requestMatchers(HttpMethod.GET, "/v1/materials/**").permitAll()
                                                .requestMatchers(HttpMethod.GET, "/v1/consultation/**").permitAll()
                                                .requestMatchers(HttpMethod.GET, "/v1/building-steps/**").permitAll()
                                                .requestMatchers(HttpMethod.GET, "/v1/designs/**").permitAll()
                                                .requestMatchers(HttpMethod.GET, "/v1/images/**").permitAll()
                                                .requestMatchers(HttpMethod.GET, "/uploads/**").permitAll()
                                                .requestMatchers(HttpMethod.GET, "/v1/ads/**").permitAll()

                                                // Public endpoints - Contractor Offers (GET is public)
                                                .requestMatchers(HttpMethod.GET, "/api/offers").permitAll()
                                                .requestMatchers(HttpMethod.GET, "/api/offers/featured").permitAll()
                                                .requestMatchers(HttpMethod.GET, "/api/offers/search").permitAll()
                                                .requestMatchers(HttpMethod.GET, "/api/offers/types").permitAll()
                                                .requestMatchers(HttpMethod.GET, "/api/offers/{id}").permitAll()

                                                // Public endpoints - AI
                                                .requestMatchers(HttpMethod.POST, "/v1/ai/generate-text").permitAll()
                                                .requestMatchers(HttpMethod.POST, "/v1/ai/generate-image").permitAll()

                                                // Admin endpoints - ADMIN role required
                                                .requestMatchers("/v1/admin/**", "/admin/**", "/api/admin/**")
                                                .hasAnyRole("ADMIN", "SUPER_ADMIN")

                                                // Worker management - ADMIN only
                                                .requestMatchers(HttpMethod.POST, "/v1/workers")
                                                .hasAnyRole("ADMIN", "SUPER_ADMIN")
                                                .requestMatchers(HttpMethod.PUT, "/v1/workers/**")
                                                .hasAnyRole("ADMIN", "SUPER_ADMIN")
                                                .requestMatchers(HttpMethod.DELETE, "/v1/workers/**")
                                                .hasAnyRole("ADMIN", "SUPER_ADMIN")

                                                // Design management - ADMIN only
                                                .requestMatchers(HttpMethod.POST, "/v1/designs")
                                                .hasAnyRole("ADMIN", "SUPER_ADMIN", "DESIGNER")
                                                .requestMatchers(HttpMethod.PUT, "/v1/designs/**")
                                                .hasAnyRole("ADMIN", "SUPER_ADMIN", "DESIGNER")
                                                .requestMatchers(HttpMethod.DELETE, "/v1/designs/**")
                                                .hasAnyRole("ADMIN", "SUPER_ADMIN", "DESIGNER")

                                                // Building steps management - ADMIN only
                                                .requestMatchers(HttpMethod.POST, "/v1/building-steps/**")
                                                .hasAnyRole("ADMIN", "SUPER_ADMIN")
                                                .requestMatchers(HttpMethod.PUT, "/v1/building-steps/**")
                                                .hasAnyRole("ADMIN", "SUPER_ADMIN")
                                                .requestMatchers(HttpMethod.DELETE, "/v1/building-steps/**")
                                                .hasAnyRole("ADMIN", "SUPER_ADMIN")

                                                // File upload - Authenticated users only
                                                .requestMatchers("/v1/upload/**").authenticated()

                                                // Reviews - Authenticated users only
                                                .requestMatchers(HttpMethod.POST, "/v1/workers/*/reviews")
                                                .authenticated()

                                                // Job requests - Authenticated users only
                                                .requestMatchers("/v1/job-requests/**").authenticated()

                                                // Reports - Authenticated users only
                                                .requestMatchers("/v1/reports/**").authenticated()

                                                // User profile - Authenticated users only
                                                .requestMatchers("/v1/auth/me").authenticated()
                                                .requestMatchers("/v1/users/**").authenticated()

                                                // All other endpoints require authentication
                                                .anyRequest().authenticated());

                return http.build();
        }

        @Bean
        public UserDetailsService userDetailsService() {
                return email -> userRepository.findByEmail(email)
                                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));
        }

        @Bean
        public AuthenticationProvider authenticationProvider() {
                DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
                authProvider.setUserDetailsService(userDetailsService());
                authProvider.setPasswordEncoder(passwordEncoder());
                return authProvider;
        }

        @Bean
        public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
                return config.getAuthenticationManager();
        }
}
