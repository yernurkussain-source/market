package kz.project.mymarket.configs;

import kz.project.mymarket.security.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.*;
import org.springframework.security.authentication.*;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserDetailsServiceImpl userDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/**").permitAll()
                        .requestMatchers("/ui/login", "/ui/register", "/css/**").permitAll()
                        .requestMatchers("/ui/users/**").hasRole("ADMIN")
                        .requestMatchers("/ui/orders/status/**", "/ui/orders/delete/**").hasRole("ADMIN")
                        .requestMatchers("/ui/products/add", "/ui/products/edit/**", "/ui/products/update/**", "/ui/products/delete/**").hasRole("ADMIN")
                        .requestMatchers("/ui/categories/add", "/ui/categories/edit/**", "/ui/categories/update/**", "/ui/categories/delete/**").hasRole("ADMIN")
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/products/**", "/categories/**").permitAll()
                        .requestMatchers("/products/**", "/categories/**", "/users/**").hasRole("ADMIN")
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/orders", "/orders/user/**").hasRole("ADMIN")
                        .requestMatchers(org.springframework.http.HttpMethod.PUT, "/orders/**").hasRole("ADMIN")
                        .requestMatchers(org.springframework.http.HttpMethod.DELETE, "/orders/**").hasRole("ADMIN")
                        .requestMatchers("/orders/**").authenticated()

                        .requestMatchers("/ui/**").authenticated()
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/ui/login")
                        .loginProcessingUrl("/ui/login")
                        .defaultSuccessUrl("/ui/dashboard", true)
                        .failureUrl("/ui/login?error=true")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/ui/logout")
                        .logoutSuccessUrl("/ui/login?logout=true")
                        .permitAll()
                );

        return http.build();
    }
}
