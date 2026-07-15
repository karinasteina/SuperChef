package lv.superchef.app.config;

import jakarta.servlet.DispatcherType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)

                .httpBasic(Customizer.withDefaults())

                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .defaultSuccessUrl("/feed", true)
                        .failureUrl("/login?error")
                        .usernameParameter("username")
                        .passwordParameter("password")
                        .permitAll())

                .logout(logout -> logout
                        .logoutSuccessUrl("/login?logout")
                        .permitAll())

                .authorizeHttpRequests(authorize -> authorize

                        // Allow Spring Boot to render the real error page.
                        .dispatcherTypeMatchers(DispatcherType.ERROR)
                        .permitAll()

                        .requestMatchers("/register", "/error", "/css/**", "/js/**", "/images/**")
                        .permitAll()

                        .requestMatchers("/h2-console/**")
                        .hasRole("ADMIN")

                        // Protected recipe pages must come before /recipes/**.
                        .requestMatchers(HttpMethod.GET, "/recipes/create", "/recipes/favorites", "/recipes/favorites/**")
                        .authenticated()

                        // Adding, removing and creating recipes requires login.
                        .requestMatchers(HttpMethod.POST, "/recipes/**")
                        .authenticated()

                        // Public recipe list and recipe details.
                        .requestMatchers(HttpMethod.GET, "/recipes/**")
                        .permitAll()

                        .anyRequest()
                        .authenticated())

                .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin));

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}