package lv.superchef.app.config;

import jakarta.servlet.DispatcherType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
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
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/api/**"))

                .httpBasic(Customizer.withDefaults())

                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .defaultSuccessUrl("/recipes/favorites", true)
                        .failureUrl("/login?error")
                        .usernameParameter("username")
                        .passwordParameter("password")
                        .permitAll())

                .logout(logout -> logout
                        .logoutSuccessUrl("/login?logout")
                        .permitAll())

                .authorizeHttpRequests(authorize -> authorize


                        .dispatcherTypeMatchers(DispatcherType.ERROR)
                        .permitAll()

                        .requestMatchers("/", "/register", "/error", "/css/**", "/js/**", "/images/**")
                        .permitAll()

                        .requestMatchers("/.well-known/**")
                        .permitAll()

                        .requestMatchers("/login")
                        .permitAll()

                        .requestMatchers("/h2-console/**")
                        .hasRole("ADMIN")

                        .requestMatchers(HttpMethod.GET, "/feed").authenticated()

                        .requestMatchers(HttpMethod.GET, "/recipes/*/edit").authenticated()
                        .requestMatchers(HttpMethod.GET, "/recipes/create", "/recipes/favorites", "/recipes/favorites/**")
                        .authenticated()


                        .requestMatchers(HttpMethod.POST, "/recipes/**")
                        .authenticated()


                        .requestMatchers(HttpMethod.GET, "/recipes", "/recipes/**")
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
