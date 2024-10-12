package com.example.nginep.config;

import com.example.nginep.auth.service.impl.UserDetailsServiceImpl;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import jakarta.servlet.http.Cookie;
import lombok.extern.java.Log;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@Log
public class SecurityConfig {
    private final RsaKeyConfigProperties rsaKeyConfigProperties;
    private final UserDetailsServiceImpl userDetailsService;

   public SecurityConfig(RsaKeyConfigProperties rsaKeyConfigProperties, UserDetailsServiceImpl userDetailsService) {
       this.rsaKeyConfigProperties = rsaKeyConfigProperties;
       this.userDetailsService = userDetailsService;
   }

   @Bean
   public AuthenticationManager authenticationManager() {
       var authProvider = new DaoAuthenticationProvider();
       authProvider.setUserDetailsService(userDetailsService);
       authProvider.setPasswordEncoder(passwordEncoder());
       return new ProviderManager(authProvider);
   }

   @Bean
    PasswordEncoder passwordEncoder() {return new BCryptPasswordEncoder();
   }

   @Bean
    public JwtDecoder jwtDecoder() {
       return NimbusJwtDecoder.withPublicKey(rsaKeyConfigProperties.publicKey()).build();
   }

   @Bean
    public JwtEncoder jwtEncoder() {
       JWK jwk = new RSAKey.Builder(rsaKeyConfigProperties.publicKey()).privateKey(rsaKeyConfigProperties.privateKey()).build();
       JWKSource<SecurityContext> jwks = new ImmutableJWKSet<>(new JWKSet(jwk));
       return new NimbusJwtEncoder(jwks);
   }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, HandlerMappingIntrospector introspector) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
//                .cors(Customizer.withDefaults())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(auth -> {
                    auth.requestMatchers("/error/**").permitAll();
                    auth.requestMatchers("/api/v1/auth/**").permitAll();
                    auth.requestMatchers("/api/v1/home/**").permitAll();
                    auth.requestMatchers("/api/v1/users/**").permitAll();
                    auth.requestMatchers("/api/v1/review/**").permitAll();
                    auth.requestMatchers(HttpMethod.POST,"/api/v1/property/**").hasAuthority("SCOPE_tenant");
                    auth.requestMatchers("/api/v1/order/**").hasAuthority("SCOPE_guest");
                    auth.requestMatchers(HttpMethod.POST, "/api/v1/review/**").hasAuthority("SCOPE_guest");
                    auth.requestMatchers("/api/v1/languages").hasAuthority("SCOPE_tenant");
                    auth.requestMatchers("/api/v1/facility").hasAuthority("SCOPE_tenant");
                    auth.requestMatchers("/api/v1/bookings/user/**").hasAuthority("SCOPE_guest");
                    auth.requestMatchers("/api/v1/bookings/tenant/**").hasAuthority("SCOPE_tenant");
                    auth.requestMatchers("/api/v1/bookings/create/**").hasAuthority("SCOPE_guest");
                    auth.requestMatchers("/api/v1/bookings/check-existing-pending-booking/**").hasAuthority("SCOPE_guest");
                    auth.requestMatchers("/api/v1/bookings/**").permitAll();
                    auth.requestMatchers("/api/v1/payments/**").permitAll();
                    auth.requestMatchers("/api/v1/midtrans/**").permitAll();
                    auth.requestMatchers("/api/v1/category").hasAuthority("SCOPE_tenant");
                    auth.requestMatchers("/api/v1/peak-season-rates").hasAuthority("SCOPE_tenant");
                    auth.requestMatchers("/api/v1/property-facility").hasAuthority("SCOPE_tenant");
                    auth.requestMatchers("/api/v1/property-image").hasAuthority("SCOPE_tenant");
                    auth.requestMatchers("/api/v1/rooms").hasAuthority("SCOPE_tenant");
                    auth.requestMatchers(HttpMethod.GET,"/api/v1/property/**").permitAll();
                    auth.requestMatchers("/api/v1/review/user/**").hasAuthority("SCOPE_guest");
                    auth.requestMatchers("/api/v1/reviews/**").permitAll();
                    auth.requestMatchers("/api/v1/review-replies/create/**").hasAuthority("SCOPE_tenant");
                    auth.requestMatchers("/api/v1/review-replies/**").permitAll();
                    auth.requestMatchers(HttpMethod.POST,"/api/v1/rooms/availability").permitAll();
                    auth.requestMatchers("/api/v1/analytics/**").permitAll();
                    auth.requestMatchers(HttpMethod.PUT,"/api/v1/property/**").hasAuthority("SCOPE_tenant");
                    auth.requestMatchers(HttpMethod.DELETE,"/api/v1/property/**").hasAuthority("SCOPE_tenant");
                    auth.anyRequest().authenticated();
                })
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .oauth2ResourceServer((oauth2) -> {
                    oauth2.jwt((jwt) -> jwt
                            .decoder(jwtDecoder() ));
                    oauth2.bearerTokenResolver(request -> {
                        Cookie[] cookies = request.getCookies();
                        if (cookies != null) {
                            for (Cookie cookie : cookies) {
                                if ("sid".equals(cookie.getName())) {
                                    return cookie.getValue();
                                }
                            }
                        } else {
                          var header = request.getHeader("Authorization");
                          if (header != null) {
                            return header.replace("Bearer ", "");
                          }
                        }
                        return null;
                    });
                })
                .userDetailsService(userDetailsService)
                .httpBasic(Customizer.withDefaults())
                .build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList(
                "http://localhost:3000",
                "https://nginep-frontend-dev.vercel.app",
                "https://nginep-frontend.vercel.app",
                "https://www.nginep-frontend-dev.vercel.app",
                "https://www.nginep-frontend.vercel.app"
        ));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}
