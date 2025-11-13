package com.nano.clinicbooking.security;

import com.nano.clinicbooking.service.login.CustomOidcUserService;
import com.nano.clinicbooking.service.login.FacebookOAuth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.cors.CorsConfiguration;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
@CrossOrigin
public class SecurityConfig {

    private final JwtFilter jwtFilter; // ✅ inject filter
    private final CustomOidcUserService customOidcUserService;
    private final FacebookOAuth2UserService FacebookOAuth2UserService;
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(req -> {
                    var c = new CorsConfiguration();
                    c.setAllowedOriginPatterns(List.of("http://localhost:5173"));
                    c.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
                    c.setAllowedHeaders(List.of("*"));
                    c.setAllowCredentials(true);
                    return c;
                }))
                .csrf(csrf -> csrf.disable())
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((req, res, e) -> {
                            res.setStatus(401);
                            res.setContentType("application/json;charset=UTF-8");
                            res.getWriter().write("{\"error\":\"unauthorized\",\"message\":\"" + e.getMessage() + "\"}");
                        })
                        .accessDeniedHandler((req, res, e) -> {
                            res.setStatus(403);
                            res.setContentType("application/json;charset=UTF-8");
                            res.getWriter().write("{\"error\":\"forbidden\",\"message\":\"" + e.getMessage() + "\"}");
                        })
                )
                .authorizeHttpRequests(auth -> auth

                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        //  Cho phép public các API đăng nhập / đăng ký
                        .requestMatchers("/api/v1/auth/**").permitAll()
                        
                        //  Cho phép OAuth2 callback
                        .requestMatchers("/login/oauth2/code/**").permitAll()
                        .requestMatchers("/api/v1/auth/oauth2/user-info").authenticated()
                        .requestMatchers("/api/v1/auth/oauth2/logout").permitAll()

                        //  Cho phép tạo admin lần đầu (không cần token)
                        .requestMatchers("/api/v1/admin/register-admin").permitAll()

                        // Cho phép GET danh sách chuyên khoa (hiển thị công khai)
                        .requestMatchers(HttpMethod.GET, "/api/v1/specialties/all").permitAll()
                        //  Cho phép chatbot  cần đăng nhập
                        .requestMatchers(HttpMethod.POST, "/api/v1/chat/ask").authenticated()

                        .requestMatchers("/api/public/**").permitAll()

                        //  Các request khác yêu cầu JWT hợp lệ
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth -> oauth
                                .userInfoEndpoint(u -> u
                                                // Google (OIDC) sẽ chạy vào customOidcUserService
                                                .oidcUserService(customOidcUserService)
                                                .userService(FacebookOAuth2UserService)
                                        // Nếu sau này có provider OAuth2 thuần (không OIDC), vẫn hỗ trợ:
//                                .userService(customOAuth2UserService)
                                )

                                .successHandler((req, res, auth) -> {
                                    System.out.println(">> OAuth2/OIDC SUCCESS for: " + auth.getName());
                                    // Redirect về frontend callback page
                                    res.sendRedirect("http://localhost:5173/oauth2/callback");
                                })
                                .failureHandler((req, res, ex) -> {
                                    ex.printStackTrace();
                                    System.err.println(">> OAuth2/OIDC FAILURE: " + ex.getMessage());
                                    res.setStatus(401);
                                    res.setContentType("text/plain;charset=UTF-8");
                                    res.getWriter().write("OAuth2 ERROR: " + ex.getMessage());
                                })
                )
                .logout(logout -> logout
                        .logoutUrl("/api/v1/auth/oauth2/logout")
                        .deleteCookies("JSESSIONID")
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .logoutSuccessHandler((req, res, auth) -> {
                            res.setStatus(200);
                        })
                )


                .httpBasic(httpBasic -> httpBasic.disable())
                .formLogin(form -> form.disable());




        // ✅ Thêm JwtFilter trước UsernamePasswordAuthenticationFilter
        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
