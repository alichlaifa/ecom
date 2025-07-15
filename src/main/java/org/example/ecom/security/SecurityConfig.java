package org.example.ecom.security;

import org.example.ecom.repository.UserRepo;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
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

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPublicKey;
import java.util.Arrays;
import java.util.Collections;
import java.util.UUID;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final UserRepo userRepository;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

    // Configures Spring Security's core HTTP request handling.
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable()) // Turns off CSRF protection entirely.
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(auth -> auth // Starts defining authorization rules for HTTP requests.
                        .requestMatchers( // Specifies one or more URL patterns that the following rule applies to.
                                "/api/v1/user/all",
                                "/api/v1/user/enable-disable/**",
                                "/api/v1/illustration/delete/**")
                        .hasAuthority("SCOPE_ROLE_ADMIN")
                        .requestMatchers(
                                "/api/v1/user/**",
                                "/api/v1/statrecapchaine/**",
                                "/api/v1/illustration/all",
                                "/content/**",
                                "/api/v1/illustration/by-id/**",
                                "/api/v1/reset-password/**",
                                "/api/student/**",
                                "/api/qualityManager/**",
                                "/api/administrator/**",
                                "/api/students/**",
                                "/api/departments/**",
                                "/api/forms/**",
                                "/api/modules/**",
                                "/api/questions/**",
                                "/api/responses/**",
                                "/api/teachers/**"
                        )
                        .permitAll()
                        .anyRequest().authenticated()) // For any other URL not matched above, the user must be authenticated (logged in)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))  // It tells Spring Security not to create or use an HTTP session to store authentication info.
                .exceptionHandling(handling -> handling.authenticationEntryPoint(customAuthenticationEntryPoint)) // Configures how Spring Security handles authentication errors.
                .authenticationProvider(authenticationProvider()) // Configures the authentication provider used to authenticate users.
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(withDefaults())); // Enable JWT token authentication for API endpoints

        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("user not found"));
    }

    // Used to handle authentication logic.
    @Bean
    public AuthenticationProvider authenticationProvider() {
        // “DAO” stands for Data Access Object. built-in implementation of AuthenticationProvider
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        //  Tells Spring to use BCrypt to check hashed passwords securely.
        return new BCryptPasswordEncoder();
    }

    // Generates a public-private RSA key pair. These keys are used to sign and verify JWTs
    // RSA is asymmetric: private key = sign, public key = verify.
    @Bean
    public KeyPair keyPair() {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048); // 2048-bit key size (secure)
            return keyPairGenerator.generateKeyPair();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // RSAKey is a class from the Nimbus JOSE + JWT library, which is used to represent an RSA public/private key pair in a format compliant with the JOSE (JSON Object Signing and Encryption) standard.
    @Bean
    public RSAKey rsaKey(KeyPair keyPair) {
        return new RSAKey.Builder((RSAPublicKey) keyPair.getPublic()) // It must be cast to RSAPublicKey because RSAKey is RSA-specific and doesn’t accept just any public key.
                .privateKey(keyPair.getPrivate())
                .keyID(UUID.randomUUID().toString()) // Assigns a unique ID to the key.
                .build();
    }

    // This method creates a JWKSource, which is how Spring Security will find the public RSA key to verify JWT tokens.
    @Bean
    public JWKSource<SecurityContext> jwkSource(RSAKey rsaKey) {
        JWKSet jwkSet = new JWKSet(rsaKey);
        return (jwkSelector, context) -> jwkSelector.select(jwkSet);
    }

    // This method creates a JwtDecoder bean that will be used by Spring Security to verify incoming JWT tokens.
    @Bean
    public JwtDecoder jwtDecoder(RSAKey rsaKey) throws Exception {
        return NimbusJwtDecoder.withPublicKey(rsaKey.toRSAPublicKey()).build();
    }

    // This method creates a JwtEncoder bean, which is used to generate (sign) JWT tokens.
    @Bean
    public JwtEncoder jwtEncoder(JWKSource<SecurityContext> jwkSource) {
        return new NimbusJwtEncoder(jwkSource);
    }

    // Configure CORS
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Collections.singletonList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
