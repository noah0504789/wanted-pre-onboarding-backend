package com.wantedbackendassignment.api.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wantedbackendassignment.api.auth.CustomExceptionHandlerFilter;
import com.wantedbackendassignment.api.auth.jwt.JwtAuthenticationFilter;
import com.wantedbackendassignment.api.auth.jwt.JwtProvider;
import com.wantedbackendassignment.api.auth.login.LoginFailureHandler;
import com.wantedbackendassignment.api.auth.login.LoginFilter;
import com.wantedbackendassignment.api.auth.login.LoginProvider;
import com.wantedbackendassignment.api.auth.login.LoginSuccessHandler;
import com.wantedbackendassignment.api.utils.HttpUtils;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.header.HeaderWriterFilter;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private static String[] AUTHENTICATION_WHITELIST = null;
    private final ObjectPostProcessor<Object> objectPostProcessor;
    private final LoginProvider loginProvider;
    private final ObjectMapper objectMapper;
    private final LocalValidatorFactoryBean validator;
    private final LoginSuccessHandler loginSuccessHandler;
    private final LoginFailureHandler loginFailureHandler;
    private final JwtProvider jwtProvider;
    private final HttpUtils httpUtils;

    @PostConstruct
    private void init() {
        AUTHENTICATION_WHITELIST = new String[]{
                "/api/auth/sign-up",
                "/api/post/list"
        };
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web.ignoring()
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations())
                .requestMatchers(AUTHENTICATION_WHITELIST)
                .requestMatchers(request -> isGETRequestAt(request, "/api/post"));
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.formLogin().disable();
        http.httpBasic().disable();
        http.csrf().disable();
        http.cors().disable();
        http.sessionManagement((session) -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http.authorizeHttpRequests(req ->  {
            req.anyRequest().permitAll();
        });

        LoginFilter loginFilter = loginFilter(authenticationManager(loginProvider));
        CustomExceptionHandlerFilter customExceptionHandlerFilter = customExceptionHandlerFilter();
        JwtAuthenticationFilter jwtAuthenticationFilter = jwtAuthenticationFilter();

        http.addFilterBefore(customExceptionHandlerFilter, HeaderWriterFilter.class)
            .addFilterAfter(loginFilter, customExceptionHandlerFilter.getClass())
            .addFilterAfter(jwtAuthenticationFilter, loginFilter.getClass());

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationProvider... authenticationProviders) throws Exception {
        AuthenticationManagerBuilder builder = new AuthenticationManagerBuilder(objectPostProcessor);

        for (AuthenticationProvider provider : authenticationProviders) {
            builder.authenticationProvider(provider);
        }

        return builder.build();
    }

    private CustomExceptionHandlerFilter customExceptionHandlerFilter() {
        return new CustomExceptionHandlerFilter(httpUtils);
    }

    private LoginFilter loginFilter(AuthenticationManager authenticationManager) {
        LoginFilter loginFilter = new LoginFilter(authenticationManager, objectMapper, validator);
        loginFilter.setFilterProcessesUrl("/api/login");
        loginFilter.setAuthenticationSuccessHandler(loginSuccessHandler);
        loginFilter.setAuthenticationFailureHandler(loginFailureHandler);
        loginFilter.afterPropertiesSet();

        return loginFilter;
    }

    private JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(jwtProvider, httpUtils);
    }

    private boolean isGETRequestAt(HttpServletRequest request, String requestUrl) {
        return request.getRequestURI().startsWith(requestUrl) &&
                request.getMethod().equalsIgnoreCase(HttpMethod.GET.toString());
    }
}
